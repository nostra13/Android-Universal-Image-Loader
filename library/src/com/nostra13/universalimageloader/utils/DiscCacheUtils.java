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
package com.nostra13.universalimageloader.utils;

import com.nostra13.universalimageloader.cache.disc.DiskCache;

import java.io.File;

/**
 * Utility for convenient work with disk cache.<br />
 * <b>NOTE:</b> This utility works with file system so avoid using it on application main thread.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
public final class DiscCacheUtils {

	private DiscCacheUtils() {
	}

	/** Returns {@link File} of cached image or <b>null</b> if image was not cached in disk cache */
	public static File findInCache(String imageUri, DiskCache diskCache) {
		File image = diskCache.get(imageUri);
		return image != null && image.exists() ? image : null;
	}

	/**
	 * Removed cached image file from disk cache (if image was cached in disk cache before)
	 *
	 * @return <b>true</b> - if cached image file existed and was deleted; <b>false</b> - otherwise.
	 */
	public static boolean removeFromCache(String imageUri, DiskCache diskCache) {
		File image = diskCache.get(imageUri);
		return image != null && image.exists() && image.delete();
	}
}
