package com.nostra13.universalimageloader.cache.disc.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nostra13.universalimageloader.cache.disc.BaseDiscCache;

/**
 * Cache which deletes files which were loaded more than defined time . Cache size is unlimited. Names file as cache key
 * {@linkplain String#hashCode() hashcode}.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see BaseDiscCache
 */
public class LimitedAgeDiscCache extends BaseDiscCache {

	private final long maxFileAge;

	private final Map<File, Long> loadingDates = Collections.synchronizedMap(new HashMap<File, Long>());

	/**
	 * @param cacheDir
	 *            Directory for file caching
	 * @param maxAge
	 *            Max file age (in seconds). If file age will exceed this value then it'll be removed on next treatment
	 *            (and therefore be reloaded).
	 */
	public LimitedAgeDiscCache(File cacheDir, long maxAge) {
		super(cacheDir);
		this.maxFileAge = maxAge * 1000; // to milliseconds
		readLoadingDates();
	}

	private void readLoadingDates() {
		File[] cachedFiles = getCacheDir().listFiles();
		for (File cachedFile : cachedFiles) {
			loadingDates.put(cachedFile, cachedFile.lastModified());
		}
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
			Long loadingDate = loadingDates.get(file);
			if (loadingDate == null) {
				loadingDate = file.lastModified();
			}
			if (System.currentTimeMillis() - loadingDate > maxFileAge) {
				file.delete();
				loadingDates.remove(file);
			}
		}
		return file;
	}

	@Override
	protected String keyToFileName(String key) {
		return String.valueOf(key.hashCode());
	}
}