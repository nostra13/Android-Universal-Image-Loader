package com.nostra13.universalimageloader.cache.memory;

import java.util.Collection;

/**
 * Interface for memory cache
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface MemoryCacheable<K, V> {

	boolean put(K key, V value);

	V get(K key);

	void remove(K key);

	Collection<K> keys();

	void clear();
}
