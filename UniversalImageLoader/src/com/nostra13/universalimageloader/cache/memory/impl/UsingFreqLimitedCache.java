package com.nostra13.universalimageloader.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nostra13.universalimageloader.cache.memory.LimitedCache;

import android.graphics.Bitmap;

/**
 * Limited {@link Bitmap bitmap} cache. Provides {@link Bitmap bitmaps} storing. Size of all stored bitmaps will not to
 * exceed size limit ( {@link #getSizeLimit()}). When cache reaches limit size then the bitmap which used the least
 * frequently is deleted from cache.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UsingFreqLimitedCache extends LimitedCache<String, Bitmap> {

	/**
	 * Contains strong references to stored objects (keys) and last object usage date (in milliseconds). If hard cache
	 * size will exceed limit then object with the least frequently usage is deleted (but it continue exist at
	 * {@link #softMap} and can be collected by GC at any time)
	 */
	private final Map<Bitmap, Integer> hardCache = Collections.synchronizedMap(new HashMap<Bitmap, Integer>());

	public UsingFreqLimitedCache(int sizeLimit) {
		super(sizeLimit);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		if (super.put(key, value)) {
			hardCache.put(value, 0);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Bitmap get(String key) {
		Bitmap value = super.get(key);
		// Increment usage count for value if value is contained in hardCahe
		if (value != null) {
			Integer usageCount = hardCache.get(value);
			if (usageCount != null) {
				hardCache.put(value, usageCount + 1);
			}
		}
		return value;
	}

	@Override
	public void clear() {
		super.clear();
		hardCache.clear();
	}

	@Override
	protected int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected Bitmap removeNext() {
		Integer minUsageCount = null;
		Bitmap leastUsedValue = null;
		Set<Entry<Bitmap, Integer>> entries = hardCache.entrySet();
		synchronized (hardCache) {
			for (Entry<Bitmap, Integer> entry : entries) {
				if (leastUsedValue == null) {
					leastUsedValue = entry.getKey();
					minUsageCount = entry.getValue();
				} else {
					Integer lastValueUsage = entry.getValue();
					if (lastValueUsage < minUsageCount) {
						minUsageCount = lastValueUsage;
						leastUsedValue = entry.getKey();
					}
				}
			}
		}
		hardCache.remove(leastUsedValue);
		return leastUsedValue;
	}

	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
