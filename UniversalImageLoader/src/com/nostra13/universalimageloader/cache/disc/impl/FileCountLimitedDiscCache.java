package com.nostra13.universalimageloader.cache.disc.impl;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.LimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;

/**
 * Disc cache limited by file count. If file count in cache directory exceeds specified limit then file with the most
 * oldest last usage date will be deleted.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see LimitedDiscCache
 */
public class FileCountLimitedDiscCache extends LimitedDiscCache {

	/**
	 * @param cacheDir
	 *            Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's needed
	 *            for right cache limit work.
	 * @param maxFileCount
	 *            Maximum file count for cache. If file count in cache directory exceeds this limit then file with the
	 *            most oldest last usage date will be deleted.
	 */
	public FileCountLimitedDiscCache(File cacheDir, int maxFileCount) {
		this(cacheDir, DefaultConfigurationFactory.createFileNameGenerator(), maxFileCount);
	}

	/**
	 * @param cacheDir
	 *            Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's needed
	 *            for right cache limit work.
	 * @param fileNameGenerator
	 *            Name generator for cached files
	 * @param maxFileCount
	 *            Maximum file count for cache. If file count in cache directory exceeds this limit then file with the
	 *            most oldest last usage date will be deleted.
	 */
	public FileCountLimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, int maxFileCount) {
		super(cacheDir, fileNameGenerator, maxFileCount);
	}

	@Override
	protected int getSize(File file) {
		return 1;
	}
}
