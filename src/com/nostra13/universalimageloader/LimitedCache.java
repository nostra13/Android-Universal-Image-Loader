package com.nostra13.universalimageloader;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Limited cache. Provides objects storing. Object has size ({@link #getSize(Object)}), size of all stored object will
 * not to exceed size limit ({@link #getSizeLimit()}).<br />
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public abstract class LimitedCache<K, V> {

	/**
	 * Contains strong references to stored objects. Each next object is added first. If hard cache size will exceed
	 * limit then last object is deleted (but it continue exist at {@link #softMap} and can be collected by GC at any
	 * time)
	 */
	private final LinkedList<V> hardCache = new LinkedList<V>();
	/**
	 * Stores not strong references to objects. If hard cache contains a stored object from this map then this object
	 * will not be collected by GC.
	 */
	private final Map<K, Reference<V>> softMap = new HashMap<K, Reference<V>>();

	public V get(K key) {
		if (containsKey(key)) {
			Reference<V> reference = softMap.get(key);
			return reference.get();
		} else {
			return null;
		}
	}

	public void put(K key, V value) {
		int valueSize = getSize(value);
		int sizeLimit = getSizeLimit();
		// add to hard cache
		if (valueSize < sizeLimit) {
			while (getMapSize() + valueSize > sizeLimit) {
				hardCache.removeLast();
			}
			hardCache.addFirst(value);
		}
		// add to soft cache
		softMap.put(key, createReference(value));
	}

	private int getMapSize() {
		int size = 0;
		for (V v : hardCache) {
			size += getSize(v);
		}
		return size;
	}

	public void clear() {
		hardCache.clear();
		softMap.clear();
	}

	public boolean containsKey(K key) {
		return softMap.containsKey(key);
	}

	protected abstract Reference<V> createReference(V value);

	protected abstract int getSize(V value);

	protected abstract int getSizeLimit();
}
