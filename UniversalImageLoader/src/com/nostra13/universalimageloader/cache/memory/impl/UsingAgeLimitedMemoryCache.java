package com.nostra13.universalimageloader.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache;

import android.graphics.Bitmap;

/**
 * Limited {@link Bitmap bitmap} cache. Provides {@link Bitmap bitmaps} storing. Size of all stored bitmaps will not to
 * exceed size limit. When cache reaches limit size then the bitmap which has the oldest usage date is deleted from
 * cache.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UsingAgeLimitedMemoryCache extends LimitedMemoryCache<String, Bitmap> {

	/**
	 * Contains strong references to stored objects (keys) and last object usage date (in milliseconds). If hard cache
	 * size will exceed limit then object with the oldest last usage date is deleted (but it continue exist at
	 * {@link #softMap} and can be collected by GC at any time)
	 */
	private final Map<Bitmap, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<Bitmap, Long>());

	public UsingAgeLimitedMemoryCache(int sizeLimit) {
		super(sizeLimit);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		if (super.put(key, value)) {
			lastUsageDates.put(value, System.currentTimeMillis());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Bitmap get(String key) {
		Bitmap value = super.get(key);
		// Save current usage date for value if value is contained in hardCahe
		if (value != null) {
			Long lastUsage = lastUsageDates.get(value);
			if (lastUsage != null) {
				lastUsageDates.put(value, System.currentTimeMillis());
			}
		}
		return value;
	}

	@Override
	public void remove(String key) {
		Bitmap value = super.get(key);
		if (value != null) {
			lastUsageDates.remove(value);
		}
		super.remove(key);
	}

	@Override
	public void clear() {
		lastUsageDates.clear();
		super.clear();
	}

	@Override
	protected int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected Bitmap removeNext() {
		Long oldestUsage = null;
		Bitmap mostLongUsedValue = null;
		Set<Entry<Bitmap, Long>> entries = lastUsageDates.entrySet();
		synchronized (lastUsageDates) {
			for (Entry<Bitmap, Long> entry : entries) {
				if (mostLongUsedValue == null) {
					mostLongUsedValue = entry.getKey();
					oldestUsage = entry.getValue();
				} else {
					Long lastValueUsage = entry.getValue();
					if (lastValueUsage < oldestUsage) {
						oldestUsage = lastValueUsage;
						mostLongUsedValue = entry.getKey();
					}
				}
			}
		}
		lastUsageDates.remove(mostLongUsedValue);
		return mostLongUsedValue;
	}

	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
