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
 * @see MemoryCacheAware
 */
public class LimitedAgeMemoryCache<K, V> implements MemoryCacheAware<K, V> {

	private final MemoryCacheAware<K, V> cache;

	private final long maxAge;
	private final Map<K, Long> loadingDates = Collections.synchronizedMap(new HashMap<K, Long>());

	/**
	 * @param cache
	 *            Wrapped memory cache
	 * @param maxAge
	 *            Max object age <b>(in seconds)</b>. If object age will exceed this value then it'll be removed from
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
