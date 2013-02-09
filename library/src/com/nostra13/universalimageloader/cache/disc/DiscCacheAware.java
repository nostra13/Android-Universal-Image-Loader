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
package com.nostra13.universalimageloader.cache.disc;

import java.io.File;

/**
 * Interface for disc cache
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public interface DiscCacheAware {
	/**
	 * This method must not to save file on file system in fact. It is called after image was cached in cache directory
	 * and it was decoded to bitmap in memory. Such order is required to prevent possible deletion of file after it was
	 * cached on disc and before it was tried to decode to bitmap.
	 */
	void put(String key, File file);

	/**
	 * Returns {@linkplain File file object} appropriate incoming key.<br />
	 * <b>NOTE:</b> Must <b>not to return</b> a null. Method must return specific {@linkplain File file object} for
	 * incoming key whether file exists or not.
	 */
	File get(String key);

	/** Clears cache directory */
	void clear();
}
