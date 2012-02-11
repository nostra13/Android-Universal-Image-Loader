package com.nostra13.universalimageloader.cache.disc.impl;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.BaseDiscCache;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;

/**
 * Default implementation of {@linkplain DiscCacheAware disc cache}. Cache size is unlimited. Names file as cache key
 * {@linkplain String#hashCode() hashcode}.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see BaseDiscCache
 */
public class UnlimitedDiscCache extends BaseDiscCache {

	public UnlimitedDiscCache(File cacheDir) {
		super(cacheDir);
	}

	@Override
	public void put(String key, File file) {
		// Do nothing
	}

	@Override
	protected String keyToFileName(String key) {
		return String.valueOf(key.hashCode());
	}
}
