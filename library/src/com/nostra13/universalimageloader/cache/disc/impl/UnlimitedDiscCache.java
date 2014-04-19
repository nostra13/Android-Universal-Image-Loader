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

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

import java.io.File;

/**
 * Default implementation of {@linkplain com.nostra13.universalimageloader.cache.disc.DiskCache disk cache}.
 * Cache size is unlimited.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class UnlimitedDiscCache extends BaseDiscCache {
	/** @param cacheDir Directory for file caching */
	public UnlimitedDiscCache(File cacheDir) {
		super(cacheDir);
	}

	/**
	 * @param cacheDir        Directory for file caching
	 * @param reserveCacheDir null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
	 */
	public UnlimitedDiscCache(File cacheDir, File reserveCacheDir) {
		super(cacheDir, reserveCacheDir);
	}

	/**
	 * @param cacheDir          Directory for file caching
	 * @param reserveCacheDir   null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
	 * @param fileNameGenerator {@linkplain com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator
	 *                          Name generator} for cached files
	 */
	public UnlimitedDiscCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
		super(cacheDir, reserveCacheDir, fileNameGenerator);
	}
}
