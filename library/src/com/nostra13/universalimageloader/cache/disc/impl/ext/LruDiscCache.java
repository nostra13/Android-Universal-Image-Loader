/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.cache.disc.impl.ext;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Disk cache based on "Least-Recently Used" principle. Adapter pattern, adapts
 * {@link com.nostra13.universalimageloader.cache.disc.impl.ext.DiskLruCache DiskLruCache} to
 * {@link com.nostra13.universalimageloader.cache.disc.DiskCache DiskCache}
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see FileNameGenerator
 * @since 1.9.2
 */
public class LruDiscCache implements DiskCache {
	/** {@value */
	public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 Kb
	/** {@value */
	public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
	/** {@value */
	public static final int DEFAULT_COMPRESS_QUALITY = 100;

	private static final String ERROR_ARG_NULL = " argument must be not null";
	private static final String ERROR_ARG_NEGATIVE = " argument must be positive number";

	protected DiskLruCache cache;
	private File reserveCacheDir;

	protected final FileNameGenerator fileNameGenerator;

	protected int bufferSize = DEFAULT_BUFFER_SIZE;

	protected Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
	protected int compressQuality = DEFAULT_COMPRESS_QUALITY;

	/**
	 * @param cacheDir          Directory for file caching
	 * @param fileNameGenerator {@linkplain com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator
	 *                          Name generator} for cached files. Generated names must match the regex
	 *                          <strong>[a-z0-9_-]{1,64}</strong>
	 * @param cacheMaxSize      Max cache size in bytes. <b>0</b> means cache size is unlimited.
	 */
	public LruDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize) {
		this(cacheDir, fileNameGenerator, cacheMaxSize, 0);
	}

	/**
	 * @param cacheDir          Directory for file caching
	 * @param fileNameGenerator {@linkplain com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator
	 *                          Name generator} for cached files. Generated names must match the regex
	 *                          <strong>[a-z0-9_-]{1,64}</strong>
	 * @param cacheMaxSize      Max cache size in bytes. <b>0</b> means cache size is unlimited.
	 * @param cacheMaxFileCount Max file count in cache. <b>0</b> means file count is unlimited.
	 */
	public LruDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize, int cacheMaxFileCount) {
		if (cacheDir == null) {
			throw new IllegalArgumentException("cacheDir" + ERROR_ARG_NULL);
		}
		if (cacheMaxSize < 0) {
			throw new IllegalArgumentException("cacheMaxSize" + ERROR_ARG_NEGATIVE);
		}
		if (cacheMaxFileCount < 0) {
			throw new IllegalArgumentException("cacheMaxFileCount" + ERROR_ARG_NEGATIVE);
		}
		if (fileNameGenerator == null) {
			throw new IllegalArgumentException("fileNameGenerator" + ERROR_ARG_NULL);
		}

		if (cacheMaxSize == 0) {
			cacheMaxSize = Long.MAX_VALUE;
		}
		if (cacheMaxFileCount == 0) {
			cacheMaxFileCount = Integer.MAX_VALUE;
		}

		this.fileNameGenerator = fileNameGenerator;
		initCache(cacheDir, reserveCacheDir, cacheMaxSize, cacheMaxFileCount);
	}

	private void initCache(File cacheDir, File reserveCacheDir, long cacheMaxSize, int cacheMaxFileCount) {
		try {
			cache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize, cacheMaxFileCount);
		} catch (IOException e) {
			L.e(e);
			if (reserveCacheDir != null) {
				initCache(reserveCacheDir, null, cacheMaxSize, cacheMaxFileCount);
			}
		}
	}

	@Override
	public File getDirectory() {
		return cache.getDirectory();
	}

	@Override
	public File get(String imageUri) {
		try {
			DiskLruCache.Snapshot snapshot = cache.get(getKey(imageUri));
			return snapshot == null ? null : snapshot.getFile(0);
		} catch (IOException e) {
			L.e(e);
			return null;
		}
	}

	@Override
	public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
		DiskLruCache.Editor editor = cache.edit(getKey(imageUri));
		if (editor == null) {
			return false;
		}

		OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), bufferSize);
		boolean copied = false;
		try {
			copied = IoUtils.copyStream(imageStream, os, listener, bufferSize);
		} finally {
			IoUtils.closeSilently(os);
			if (copied) {
				editor.commit();
			} else {
				editor.abort();
			}
		}
		return copied;
	}

	@Override
	public boolean save(String imageUri, Bitmap bitmap) throws IOException {
		DiskLruCache.Editor editor = cache.edit(getKey(imageUri));
		if (editor == null) {
			return false;
		}

		OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), bufferSize);
		boolean savedSuccessfully = false;
		try {
			savedSuccessfully = bitmap.compress(compressFormat, compressQuality, os);
		} finally {
			IoUtils.closeSilently(os);
		}
		if (savedSuccessfully) {
			editor.commit();
		} else {
			editor.abort();
		}
		return savedSuccessfully;
	}

	@Override
	public boolean remove(String imageUri) {
		try {
			return cache.remove(getKey(imageUri));
		} catch (IOException e) {
			L.e(e);
			return false;
		}
	}

	@Override
	public void close() {
		try {
			cache.close();
		} catch (IOException e) {
			L.e(e);
		}
		cache = null;
	}

	@Override
	public void clear() {
		try {
			cache.delete();
		} catch (IOException e) {
			L.e(e);
		} finally {
			initCache(cache.getDirectory(), reserveCacheDir, cache.getMaxSize(), cache.getMaxFileCount());
		}
	}

	private String getKey(String imageUri) {
		return fileNameGenerator.generate(imageUri);
	}

	/**
	 * @param reserveCacheDir null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
	 */
	public void setReserveCacheDir(File reserveCacheDir) {
		this.reserveCacheDir = reserveCacheDir;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
		this.compressFormat = compressFormat;
	}

	public void setCompressQuality(int compressQuality) {
		this.compressQuality = compressQuality;
	}
}
