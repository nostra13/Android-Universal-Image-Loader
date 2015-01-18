/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.cache.memory;

import android.graphics.Bitmap;

import java.util.Collection;

/**
 * Interface for memory cache
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.2
 */
public interface MemoryCache {
	/**
	 * Puts value into cache by key
	 *
	 * @return <b>true</b> - if value was put into cache successfully, <b>false</b> - if value was <b>not</b> put into
	 * cache
	 */
	boolean put(String key, Bitmap value);

	/** Returns value by key. If there is no value for key then null will be returned. */
	Bitmap get(String key);

	/** Removes item by key */
	Bitmap remove(String key);

	/** Returns all keys of cache */
	Collection<String> keys();

	/** Remove all items from cache */
	void clear();
}
