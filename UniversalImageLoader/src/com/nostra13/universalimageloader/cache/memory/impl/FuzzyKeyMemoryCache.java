package com.nostra13.universalimageloader.cache.memory.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;

/**
 * Decorator for {@link MemoryCacheAware}. Provides special feature for cache: some different keys are considered as
 * equals (using {@link Comparator comparator}). And when you try to put some value into cache by key so entries with
 * "equals" keys will be removed from cache before.<br />
 * <b>NOTE:</b> Used for internal needs. Normally you don't need to use this class.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class FuzzyKeyMemoryCache<K, V> implements MemoryCacheAware<K, V> {

	private final MemoryCacheAware<K, V> cache;
	private final Comparator<K> keyComparator;

	public FuzzyKeyMemoryCache(MemoryCacheAware<K, V> cache, Comparator<K> keyComparator) {
		this.cache = cache;
		this.keyComparator = keyComparator;
	}

	@Override
	public synchronized boolean put(K key, V value) {
		// Search equal key and remove this entry
		K keyToRemove = null;
		for (Iterator<K> it = cache.keys().iterator(); it.hasNext();) {
			K cacheKey = it.next();
			if (keyComparator.compare(key, cacheKey) == 0) {
				keyToRemove = cacheKey;
			}
		}
		cache.remove(keyToRemove);

		return cache.put(key, value);
	}

	@Override
	public synchronized V get(K key) {
		return cache.get(key);
	}

	@Override
	public synchronized void remove(K key) {
		cache.remove(key);
	}

	@Override
	public synchronized void clear() {
		cache.clear();
	}

	@Override
	public synchronized Collection<K> keys() {
		return cache.keys();
	}
}
