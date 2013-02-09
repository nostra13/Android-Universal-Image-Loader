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

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.LimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;

/**
 * Disc cache limited by file count. If file count in cache directory exceeds specified limit then file with the most
 * oldest last usage date will be deleted.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 * @see LimitedDiscCache
 */
public class FileCountLimitedDiscCache extends LimitedDiscCache {
	/**
	 * @param cacheDir Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's
	 *            needed for right cache limit work.
	 * @param maxFileCount Maximum file count for cache. If file count in cache directory exceeds this limit then file
	 *            with the most oldest last usage date will be deleted.
	 */
	public FileCountLimitedDiscCache(File cacheDir, int maxFileCount) {
		this(cacheDir, DefaultConfigurationFactory.createFileNameGenerator(), maxFileCount);
	}

	/**
	 * @param cacheDir Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's
	 *            needed for right cache limit work.
	 * @param fileNameGenerator Name generator for cached files
	 * @param maxFileCount Maximum file count for cache. If file count in cache directory exceeds this limit then file
	 *            with the most oldest last usage date will be deleted.
	 */
	public FileCountLimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, int maxFileCount) {
		super(cacheDir, fileNameGenerator, maxFileCount);
	}

	@Override
	protected int getSize(File file) {
		return 1;
	}
}
