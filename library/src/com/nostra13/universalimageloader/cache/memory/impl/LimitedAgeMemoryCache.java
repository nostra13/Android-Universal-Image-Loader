/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
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

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.MemoryCache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Decorator for {@link MemoryCache}. Provides special feature for cache: if some cached object age exceeds defined
 * value then this object will be removed from cache.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see MemoryCache
 * @since 1.3.1
 */
public class LimitedAgeMemoryCache implements MemoryCache {

	private final MemoryCache cache;

	private final long maxAge;
	private final Map<String, Long> loadingDates = Collections.synchronizedMap(new HashMap<String, Long>());

	/**
	 * @param cache  Wrapped memory cache
	 * @param maxAge Max object age <b>(in seconds)</b>. If object age will exceed this value then it'll be removed from
	 *               cache on next treatment (and therefore be reloaded).
	 */
	public LimitedAgeMemoryCache(MemoryCache cache, long maxAge) {
		this.cache = cache;
		this.maxAge = maxAge * 1000; // to milliseconds
	}

	@Override
	public boolean put(String key, Bitmap value) {
		boolean putSuccesfully = cache.put(key, value);
		if (putSuccesfully) {
			loadingDates.put(key, System.currentTimeMillis());
		}
		return putSuccesfully;
	}

	@Override
	public Bitmap get(String key) {
		Long loadingDate = loadingDates.get(key);
		if (loadingDate != null && System.currentTimeMillis() - loadingDate > maxAge) {
			cache.remove(key);
			loadingDates.remove(key);
		}

		return cache.get(key);
	}

	@Override
	public Bitmap remove(String key) {
		loadingDates.remove(key);
		return cache.remove(key);
	}

	@Override
	public Collection<String> keys() {
		return cache.keys();
	}

	@Override
	public void clear() {
		cache.clear();
		loadingDates.clear();
	}
}
