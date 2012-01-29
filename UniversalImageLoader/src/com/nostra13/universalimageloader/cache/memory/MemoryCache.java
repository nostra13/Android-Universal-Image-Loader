package com.nostra13.universalimageloader.cache.memory;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory cache. Provides object references ({@linkplain Reference not strong}) storing.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class MemoryCache<K, V> implements MemoryCacheable<K, V> {

	/** Stores not strong references to objects */
	private final Map<K, Reference<V>> softMap = Collections.synchronizedMap(new HashMap<K, Reference<V>>());

	/** Returns value by key. If value was collected by GC then <b>null</b> was got. */
	public V get(K key) {
		V result = null;
		Reference<V> reference = softMap.get(key);
		if (reference != null) {
			result = reference.get();
		}
		return result;
	}

	/**
	 * Puts value into cache by key. Always calls <b>super.put()</b> in overridden method.
	 * 
	 * @return <b>true</b> - if value was put into cache successfully, <b>false</b> - if value was <b>not</b> put into
	 *         cache
	 */
	public boolean put(K key, V value) {
		softMap.put(key, createReference(value));
		return true;
	}

	public void remove(K key) {
		softMap.remove(key);
	}

	public Collection<K> keys() {
		return softMap.keySet();
	}

	public void clear() {
		softMap.clear();
	}

	/** Creates {@linkplain Reference not strong} reference of value */
	protected abstract Reference<V> createReference(V value);
}
