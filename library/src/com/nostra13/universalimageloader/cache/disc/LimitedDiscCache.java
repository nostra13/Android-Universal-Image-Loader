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
package com.nostra13.universalimageloader.cache.disc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;

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

	private final int minSizeLimitReduction;
	
	private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new LinkedHashMap<File, Long>(16, 0.75f, true));

	/**
	 * @param cacheDir  Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's
	 *                  needed for right cache limit work.
	 * @param sizeLimit Cache limit value. If cache exceeds this limit then file with the most oldest last usage date
	 *                  will be deleted.
	 */
	public LimitedDiscCache(File cacheDir, int sizeLimit) {
		this(cacheDir, DefaultConfigurationFactory.createFileNameGenerator(), sizeLimit);
	}

	/**
	 * @param cacheDir          Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's
	 *                          needed for right cache limit work.
	 * @param fileNameGenerator Name generator for cached files
	 * @param sizeLimit         Cache limit value. If cache exceeds this limit then file with the most oldest last usage date
	 *                          will be deleted.
	 */
	public LimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, int sizeLimit, float minSizeLimitReductionPercent) {
		super(cacheDir, fileNameGenerator);
		this.sizeLimit = sizeLimit;
		this.minSizeLimitReduction = (int) ((float)sizeLimit * Math.min(1.0, minSizeLimitReductionPercent));
		cacheSize = new AtomicInteger();
		calculateCacheSizeAndFillUsageMap();
		
	}
	public LimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, int sizeLimit) {
		this(cacheDir, fileNameGenerator, sizeLimit, 1);

	}

	private void calculateCacheSizeAndFillUsageMap() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int size = 0;
				File[] cachedFiles = cacheDir.listFiles();
				if (cachedFiles != null) { // rarely but it can happen, don't know why
					
					// sort the files by oldest files are first, so when they are inserted into LinkedHashMap they will be the oldest files.
					Arrays.sort(cachedFiles, new Comparator<File>(){
					    public int compare(File f1, File f2)
					    {
					        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
					    } });
					// order by last usage time.
					for (File cachedFile : cachedFiles) {
						size += getSize(cachedFile);
						lastUsageDates.put(cachedFile, cachedFile.lastModified());
					}
					cacheSize.set(size);
				}
			}
		}).start();
	}

	@Override
	public void put(String key, File file) {
		int valueSize = getSize(file);
		int curCacheSize = cacheSize.get();

		while (curCacheSize + valueSize > sizeLimit) {
			int freedSize = removeMinAmount(minSizeLimitReduction);
			if (freedSize == INVALID_SIZE) break; // cache is empty (have nothing to delete)
			curCacheSize = cacheSize.addAndGet(-freedSize);
		}
		cacheSize.addAndGet(valueSize);

		Long currentTime = System.currentTimeMillis();
		file.setLastModified(currentTime);
		lastUsageDates.put(file, currentTime);
	}

	@Override
	public File get(String key) {
		File file = super.get(key);

		Long currentTime = System.currentTimeMillis();
		file.setLastModified(currentTime);
		lastUsageDates.put(file, currentTime);

		return file;
	}

	@Override
	public void clear() {
		lastUsageDates.clear();
		cacheSize.set(0);
		super.clear();
	}

	/** Remove next file and returns it's size */
	private int removeMinAmount(int minByteAmount) {
		if (lastUsageDates.isEmpty()) {
			return INVALID_SIZE;
		}

		Set<Entry<File, Long>> entries = lastUsageDates.entrySet();
		int deletedFileSize = 0;
		List<File> removeFiles = new ArrayList<File>();
		synchronized (lastUsageDates) {
			for (Entry<File, Long> entry : entries) {
				
				File oldestFile = entry.getKey();
				if(oldestFile != null)
				{
					int fileSize = getSize(oldestFile);
					removeFiles.add(oldestFile);
						
					deletedFileSize += fileSize;
					if(deletedFileSize >= minByteAmount)
						break;
				}
				else
				{
					lastUsageDates.remove(oldestFile);
				}

			}
		}
		int actualDeletedFileSize = 0;
		for(File removeFile : removeFiles)
		{
			if(removeFile != null && removeFile.exists())
			{
				int fileSize = getSize(removeFile);
				if(removeFile.delete())
				{
					actualDeletedFileSize += fileSize;
					lastUsageDates.remove(removeFile);
				}
				
			}
		}

		return actualDeletedFileSize;
	}
	

	protected abstract int getSize(File file);
}