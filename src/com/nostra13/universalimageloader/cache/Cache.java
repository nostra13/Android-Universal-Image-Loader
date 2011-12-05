package com.nostra13.universalimageloader.cache;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache. Provides object references (not strong) storing.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public abstract class Cache<K, V> {

	/**
	 * Stores not strong references to objects.
	 */
	protected final Map<K, Reference<V>> softMap = new HashMap<K, Reference<V>>();

	public V get(K key) {
		if (containsKey(key)) {
			Reference<V> reference = softMap.get(key);
			return reference.get();
		} else {
			return null;
		}
	}

	public void put(K key, V value) {
		softMap.put(key, createReference(value));
	}

	public void clear() {
		softMap.clear();
	}

	public boolean containsKey(K key) {
		return softMap.containsKey(key);
	}

	protected abstract Reference<V> createReference(V value);
}
