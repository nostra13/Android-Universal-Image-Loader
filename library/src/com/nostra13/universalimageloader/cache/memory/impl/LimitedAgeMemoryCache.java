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
package com.nostra13.universalimageloader.cache.memory.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;

/**
 * Decorator for {@link MemoryCacheAware}. Provides special feature for cache: if some cached object age exceeds defined
 * value then this object will be removed from cache.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.3.1
 * @see MemoryCacheAware
 */
public class LimitedAgeMemoryCache<K, V> implements MemoryCacheAware<K, V> {

	private final MemoryCacheAware<K, V> cache;

	private final long maxAge;
	private final Map<K, Long> loadingDates = Collections.synchronizedMap(new HashMap<K, Long>());

	/**
	 * @param cache Wrapped memory cache
	 * @param maxAge Max object age <b>(in seconds)</b>. If object age will exceed this value then it'll be removed from
	 *            cache on next treatment (and therefore be reloaded).
	 */
	public LimitedAgeMemoryCache(MemoryCacheAware<K, V> cache, long maxAge) {
		this.cache = cache;
		this.maxAge = maxAge * 1000; // to milliseconds
	}

	@Override
	public boolean put(K key, V value) {
		boolean putSuccesfully = cache.put(key, value);
		if (putSuccesfully) {
			loadingDates.put(key, System.currentTimeMillis());
		}
		return putSuccesfully;
	}

	@Override
	public V get(K key) {
		Long loadingDate = loadingDates.get(key);
		if (loadingDate != null && System.currentTimeMillis() - loadingDate > maxAge) {
			cache.remove(key);
			loadingDates.remove(key);
		}

		return cache.get(key);
	}

	@Override
	public void remove(K key) {
		cache.remove(key);
		loadingDates.remove(key);
	}

	@Override
	public Collection<K> keys() {
		return cache.keys();
	}

	@Override
	public void clear() {
		cache.clear();
		loadingDates.clear();
	}
}
