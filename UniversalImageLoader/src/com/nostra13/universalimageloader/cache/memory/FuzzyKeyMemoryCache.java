package com.nostra13.universalimageloader.cache.memory;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Decorator for {@link MemoryCacheAware}. Provides special feature for cache: some different keys are considered as
 * equals (using {@link Comparator comparator}). And when you try to put some value into cache by key so entries with
 * "equals" keys will be removed from cache before.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class FuzzyKeyMemoryCache<K, V> implements MemoryCacheAware<K, V> {

	private MemoryCacheAware<K, V> cache;
	private Comparator<K> keyComparator;

	public FuzzyKeyMemoryCache(MemoryCacheAware<K, V> cache, Comparator<K> keyComparator) {
		this.cache = cache;
		this.keyComparator = keyComparator;
	}

	@Override
	public boolean put(K key, V value) {
		// Search equal key and remove this entry
		K keyToRemove = null;
		synchronized (cache) {
			for (Iterator<K> it = cache.keys().iterator(); it.hasNext();) {
				K cacheKey = it.next();
				if (keyComparator.compare(key, cacheKey) == 0) {
					keyToRemove = cacheKey;
				}
			}
			cache.remove(keyToRemove);
		}

		return cache.put(key, value);
	}

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public void remove(K key) {
		cache.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public Collection<K> keys() {
		return cache.keys();
	}
}
