package com.nostra13.universalimageloader.cache.disc;

import java.io.File;

/**
 * Base disc cache. Implements common functionality for disc cache.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see DiscCacheAware
 */
public abstract class BaseDiscCache implements DiscCacheAware {

	private File cacheDir;

	public BaseDiscCache(File cacheDir) {
		this.cacheDir = cacheDir;
	}

	@Override
	public File get(String key) {
		String fileName = keyToFileName(key);
		return new File(cacheDir, fileName);
	}

	@Override
	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}
	}

	protected File getCacheDir() {
		return cacheDir;
	}

	/** Generates unique file name for incoming key */
	protected abstract String keyToFileName(String key);
}