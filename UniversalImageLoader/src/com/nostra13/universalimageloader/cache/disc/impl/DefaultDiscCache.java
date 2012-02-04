package com.nostra13.universalimageloader.cache.disc.impl;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.DiscCache;

/**
 * Default implementation of {@linkplain DiscCache disc cache}. Names file as file URL {@linkplain String#hashCode() hashcode}.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see DiscCache
 */
public class DefaultDiscCache extends DiscCache {

	public DefaultDiscCache(File cacheDir) {
		super(cacheDir);
	}

	@Override
	public File getFile(String url) {
		String fileName = String.valueOf(url.hashCode());
		return new File(getCacheDir(), fileName);
	}
}
