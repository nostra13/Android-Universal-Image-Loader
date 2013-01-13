package com.nostra13.universalimageloader.cache.memory;

import java.util.Collection;

/**
 * Interface for memory cache
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface MemoryCacheAware<K, V> {

	/**
	 * Puts value into cache by key
	 * 
	 * @return <b>true</b> - if value was put into cache successfully, <b>false</b> - if value was <b>not</b> put into
	 *         cache
	 */
	boolean put(K key, V value);

	/** Returns value by key. If there is no value for key then null will be returned. */
	V get(K key);

	/** Removes item by key */
	void remove(K key);

	/** Returns all keys of cache */
	Collection<K> keys();

	/** Remove all items from cache */
	void clear();
}
