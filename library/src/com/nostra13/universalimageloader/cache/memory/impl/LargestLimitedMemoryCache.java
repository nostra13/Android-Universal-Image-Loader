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
 * exceed size limit. When cache reaches limit size then the bitmap which used the least frequently is deleted from
 * cache.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class LargestLimitedMemoryCache extends LimitedMemoryCache<String, Bitmap> {

	/**
	 * Contains strong references to stored objects (keys) and last object usage date (in milliseconds). If hard cache
	 * size will exceed limit then object with the least frequently usage is deleted (but it continue exist at
	 * {@link #softMap} and can be collected by GC at any time)
	 */
	private final Map<Bitmap, Integer> valueSizes = Collections.synchronizedMap(new HashMap<Bitmap, Integer>());

	public LargestLimitedMemoryCache(int sizeLimit) {
		super(sizeLimit);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		if (super.put(key, value)) {
			valueSizes.put(value, getSize(value));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void remove(String key) {
		Bitmap value = super.get(key);
		if (value != null) {
			valueSizes.remove(value);
		}
		super.remove(key);
	}

	@Override
	public void clear() {
		valueSizes.clear();
		super.clear();
	}

	@Override
	protected int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected Bitmap removeNext() {
		Integer maxSize = null;
		Bitmap largestValue = null;
		Set<Entry<Bitmap, Integer>> entries = valueSizes.entrySet();
		synchronized (valueSizes) {
			for (Entry<Bitmap, Integer> entry : entries) {
				if (largestValue == null) {
					largestValue = entry.getKey();
					maxSize = entry.getValue();
				} else {
					Integer size = entry.getValue();
					if (size > maxSize) {
						maxSize = size;
						largestValue = entry.getKey();
					}
				}
			}
		}
		valueSizes.remove(largestValue);
		return largestValue;
	}

	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
