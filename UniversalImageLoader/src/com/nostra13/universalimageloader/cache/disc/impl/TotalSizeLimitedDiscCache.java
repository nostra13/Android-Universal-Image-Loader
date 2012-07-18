package com.nostra13.universalimageloader.cache.disc.impl;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.LimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

/**
 * Disc cache limited by total cache size. If cache size exceeds specified limit then file with the most oldest last
 * usage date will be deleted.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see LimitedDiscCache
 */
public class TotalSizeLimitedDiscCache extends LimitedDiscCache {

	/**
	 * @param cacheDir
	 *            Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's needed
	 *            for right cache limit work.
	 * @param maxCacheSize
	 *            Maximum cache directory size (in bytes). If cache size exceeds this limit then file with the most
	 *            oldest last usage date will be deleted.
	 */
	public TotalSizeLimitedDiscCache(File cacheDir, int maxCacheSize) {
		this(cacheDir, FileNameGenerator.createDefault(), maxCacheSize);
	}

	/**
	 * @param cacheDir
	 *            Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's needed
	 *            for right cache limit work.
	 * @param fileNameGenerator
	 *            Name generator for cached files
	 * @param maxCacheSize
	 *            Maximum cache directory size (in bytes). If cache size exceeds this limit then file with the most
	 *            oldest last usage date will be deleted.
	 */
	public TotalSizeLimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, int maxCacheSize) {
		super(cacheDir, fileNameGenerator, maxCacheSize);
	}

	@Override
	protected int getSize(File file) {
		return (int) file.length();
	}
}
