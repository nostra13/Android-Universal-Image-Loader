/**
 * ****************************************************************************
 * Copyright 2014 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package com.nostra13.universalimageloader.cache.memory;

import android.graphics.Bitmap;

import java.util.Set;

/**
 * Interface for memory cache
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.2
 */
public interface MemoryCache {
	/** Returns value by key. If there is no value for key then null will be returned. */
	Bitmap get(String key);

	/** Puts value into cache by key */
	void put(String key, Bitmap value);

	/** Removes item by key */
	Bitmap remove(String key);

	/** Returns max size of the cache in bytes. */
	int maxSize();

	/** Returns the current size of the cache in bytes. */
	int size();

	/** Remove all items from cache */
	void clear();

	/** Returns all keys of cache */
	Set<String> keys();

	void addMemoryCacheListener(MemoryCacheListener listener);

	void removeMemoryCacheListener(MemoryCacheListener listener);

	interface MemoryCacheListener {

		void onEntryRemoved(String key, Bitmap removed);
	}
}
