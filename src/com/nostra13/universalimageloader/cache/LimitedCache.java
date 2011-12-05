package com.nostra13.universalimageloader.cache;

import java.lang.ref.Reference;
import java.util.LinkedList;

/**
 * Limited cache. Provides objects storing. Object has size ({@link #getSize(Object)}), size of all stored object will
 * not to exceed size limit ({@link #getSizeLimit()}).<br />
 * Not thread-safe.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public abstract class LimitedCache<K, V> extends Cache<K, V> {

	private int cacheSize = 0;

	/**
	 * Contains strong references to stored objects. Each next object is added first. If hard cache size will exceed
	 * limit then last object is deleted (but it continue exist at {@link #softMap} and can be collected by GC at any
	 * time)
	 */
	private final LinkedList<V> hardCache = new LinkedList<V>();

	public void put(K key, V value) {
		int valueSize = getSize(value);
		int sizeLimit = getSizeLimit();
		// add to hard cache
		if (valueSize < sizeLimit) {
			while (cacheSize + valueSize > sizeLimit) {
				cacheSize -= getSize(hardCache.removeLast());
			}
			hardCache.addFirst(value);
			cacheSize += valueSize;
		}
		// add to soft cache
		super.put(key, value);
	}

	public void clear() {
		hardCache.clear();
		cacheSize = 0;
		super.clear();
	}

	protected abstract Reference<V> createReference(V value);

	protected abstract int getSize(V value);

	protected abstract int getSizeLimit();
}
