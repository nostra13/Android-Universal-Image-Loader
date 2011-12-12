package com.nostra13.universalimageloader.cache;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	 * Contains strong references to stored objects (keys) and last object usage date (in milliseconds). If hard cache
	 * size will exceed limit then object with the oldest last usage date is deleted (but it continue exist at
	 * {@link #softMap} and can be collected by GC at any time)
	 */
	private final Map<V, Long> hardCache = new HashMap<V, Long>();

	public void put(K key, V value) {
		int valueSize = getSize(value);
		int sizeLimit = getSizeLimit();
		// add to hard cache
		if (valueSize < sizeLimit) {
			while (cacheSize + valueSize > sizeLimit) {
				cacheSize -= getSize(removeMostLongUsed());
			}
			hardCache.put(value, System.currentTimeMillis());
			cacheSize += valueSize;
		}
		// add to soft cache
		super.put(key, value);
	}

	public V get(K key) {
		V value = super.get(key);
		// Save current usage date for value if value is contained in hardCahe
		if (value != null) {
			Long lastUsage = hardCache.get(value);
			if (lastUsage != null) {
				hardCache.put(value, System.currentTimeMillis());
			}
		}
		return value;
	}

	public void clear() {
		hardCache.clear();
		cacheSize = 0;
		super.clear();
	}

	// TODO : Implement different logic variants for element removing (FIFO, remove the biggest)
	private V removeMostLongUsed() {
		Long oldestUsage = null;
		V leastUsedValue = null;
		for (Entry<V, Long> entry : hardCache.entrySet()) {
			if (leastUsedValue == null) {
				leastUsedValue = entry.getKey();
				oldestUsage = entry.getValue();
			} else {
				Long lastValueUsage = entry.getValue();
				if (lastValueUsage < oldestUsage) {
					oldestUsage = lastValueUsage;
					leastUsedValue = entry.getKey();
				}
			}
		}
		hardCache.remove(leastUsedValue);
		return leastUsedValue;
	}

	protected abstract Reference<V> createReference(V value);

	protected abstract int getSize(V value);

	protected abstract int getSizeLimit();
}
