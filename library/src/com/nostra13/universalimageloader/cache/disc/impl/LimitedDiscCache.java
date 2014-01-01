/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.cache.disc.impl;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract disc cache limited by some parameter. If cache exceeds specified limit then file with the most oldest last
 * usage date will be deleted.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see BaseDiscCache
 * @see FileNameGenerator
 * @since 1.0.0
 */
public abstract class LimitedDiscCache extends BaseDiscCache {

	private static final int INVALID_SIZE = -1;

	private final AtomicInteger cacheSize;

	private final int sizeLimit;

	/** File path - Last usage date timestamp */
	private final Map<String, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<String, Long>());

	/**
	 * @param cacheDir  Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's
	 *                  needed for right cache limit work.
	 * @param sizeLimit Cache limit value. If cache exceeds this limit then file with the most oldest last usage date
	 *                  will be deleted.
	 */
	public LimitedDiscCache(File cacheDir, int sizeLimit) {
		this(cacheDir, null, DefaultConfigurationFactory.createFileNameGenerator(), sizeLimit);
	}

	/**
	 * @param cacheDir        Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's
	 *                        needed for right cache limit work.
	 * @param reserveCacheDir null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
	 * @param sizeLimit       Cache limit value. If cache exceeds this limit then file with the most oldest last usage date
	 *                        will be deleted.
	 */
	public LimitedDiscCache(File cacheDir, File reserveCacheDir, int sizeLimit) {
		this(cacheDir, reserveCacheDir, DefaultConfigurationFactory.createFileNameGenerator(), sizeLimit);
	}

	/**
	 * @param cacheDir          Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's
	 *                          needed for right cache limit work.
	 * @param reserveCacheDir   null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
	 * @param fileNameGenerator Name generator for cached files
	 * @param sizeLimit         Cache limit value. If cache exceeds this limit then file with the most oldest last usage date
	 *                          will be deleted.
	 */
	public LimitedDiscCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, int sizeLimit) {
		super(cacheDir, reserveCacheDir, fileNameGenerator);
		this.sizeLimit = sizeLimit;
		cacheSize = new AtomicInteger();
		calculateCacheSizeAndFillUsageMap();
	}

	private void calculateCacheSizeAndFillUsageMap() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int size = 0;
				File[] cachedFiles = cacheDir.listFiles();
				if (cachedFiles != null) { // rarely but it can happen, don't know why
					for (File cachedFile : cachedFiles) {
						size += getSize(cachedFile);
						lastUsageDates.put(cachedFile.getAbsolutePath(), cachedFile.lastModified());
					}
					cacheSize.set(size);
				}
			}
		}).start();
	}

	@Override
	public File get(String imageUri) {
		File file = super.get(imageUri);

		if (file != null && file.exists()) {
			Long currentTime = System.currentTimeMillis();
			file.setLastModified(currentTime);
			lastUsageDates.put(imageUri, currentTime);
		}

		return file;
	}

	@Override
	public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
		boolean saved = super.save(imageUri, imageStream, listener);
		if (saved) {
			rememberUsage(imageUri);
			trimCacheSize();
		}
		return saved;
	}

	@Override
	public boolean save(String imageUri, Bitmap bitmap) throws IOException {
		boolean saved = super.save(imageUri, bitmap);
		if (saved) {
			rememberUsage(imageUri);
			trimCacheSize();
		}
		return saved;
	}

	@Override
	public boolean remove(String uri) {
		File file = getFile(uri);
		int valueSize = getSize(file);
		boolean removed = super.remove(uri);
		if (removed) {
			cacheSize.addAndGet(-valueSize);
			lastUsageDates.remove(uri);
		}
		return removed;
	}

	@Override
	public void clear() {
		lastUsageDates.clear();
		cacheSize.set(0);
		super.clear();
	}

	private void rememberUsage(String imageUri) {
		File file = getFile(imageUri);
		int valueSize = getSize(file);

		cacheSize.addAndGet(valueSize);
		Long currentTime = System.currentTimeMillis();
		file.setLastModified(currentTime);
		lastUsageDates.put(file.getAbsolutePath(), currentTime);
	}

	private void trimCacheSize() {
		int curCacheSize = cacheSize.get();
		while (curCacheSize > sizeLimit) {
			int freedSize = removeNext();
			if (freedSize == INVALID_SIZE) break; // cache is empty (have nothing to delete)
			curCacheSize = cacheSize.addAndGet(-freedSize);
		}
	}

	/** Remove next file and returns it's size */
	private int removeNext() {
		if (lastUsageDates.isEmpty()) {
			return INVALID_SIZE;
		}
		Long oldestUsage = null;
		String mostLongUsedFilePath = null;
		Set<Entry<String, Long>> entries = lastUsageDates.entrySet();
		synchronized (lastUsageDates) {
			for (Entry<String, Long> entry : entries) {
				if (mostLongUsedFilePath == null) {
					mostLongUsedFilePath = entry.getKey();
					oldestUsage = entry.getValue();
				} else {
					Long lastValueUsage = entry.getValue();
					if (lastValueUsage < oldestUsage) {
						oldestUsage = lastValueUsage;
						mostLongUsedFilePath = entry.getKey();
					}
				}
			}
		}

		int fileSize = 0;
		if (mostLongUsedFilePath != null) {
			File mostLongUsedFile = new File(mostLongUsedFilePath);
			if (mostLongUsedFile.exists()) {
				fileSize = getSize(mostLongUsedFile);
				if (mostLongUsedFile.delete()) {
					lastUsageDates.remove(mostLongUsedFilePath);
				}
			} else {
				lastUsageDates.remove(mostLongUsedFilePath);
			}
		}
		return fileSize;
	}

	protected abstract int getSize(File file);
}