package com.nostra13.universalimageloader.cache.disc.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nostra13.universalimageloader.cache.disc.BaseDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;

/**
 * Cache which deletes files which were loaded more than defined time. Cache size is unlimited.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see BaseDiscCache
 */
public class LimitedAgeDiscCache extends BaseDiscCache {

	private final long maxFileAge;

	private final Map<File, Long> loadingDates = Collections.synchronizedMap(new HashMap<File, Long>());

	/**
	 * @param cacheDir Directory for file caching
	 * @param maxAge Max file age (in seconds). If file age will exceed this value then it'll be removed on next
	 *            treatment (and therefore be reloaded).
	 */
	public LimitedAgeDiscCache(File cacheDir, long maxAge) {
		this(cacheDir, DefaultConfigurationFactory.createFileNameGenerator(), maxAge);
	}

	/**
	 * @param cacheDir Directory for file caching
	 * @param fileNameGenerator Name generator for cached files
	 * @param maxAge Max file age (in seconds). If file age will exceed this value then it'll be removed on next
	 *            treatment (and therefore be reloaded).
	 */
	public LimitedAgeDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, long maxAge) {
		super(cacheDir, fileNameGenerator);
		this.maxFileAge = maxAge * 1000; // to milliseconds
	}

	@Override
	public void put(String key, File file) {
		long currentTime = System.currentTimeMillis();
		file.setLastModified(currentTime);
		loadingDates.put(file, currentTime);
	}

	@Override
	public File get(String key) {
		File file = super.get(key);
		if (file.exists()) {
			boolean cached;
			Long loadingDate = loadingDates.get(file);
			if (loadingDate == null) {
				cached = false;
				loadingDate = file.lastModified();
			} else {
				cached = true;
			}

			if (System.currentTimeMillis() - loadingDate > maxFileAge) {
				file.delete();
				loadingDates.remove(file);
			} else if (!cached) {
				loadingDates.put(file, loadingDate);
			}
		}
		return file;
	}
}