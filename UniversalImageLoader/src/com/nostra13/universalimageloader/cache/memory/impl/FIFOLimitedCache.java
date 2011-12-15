package com.nostra13.universalimageloader.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.cache.memory.LimitedCache;

import android.graphics.Bitmap;

/**
 * Limited {@link Bitmap bitmap} cache. Provides {@link Bitmap bitmaps} storing. Size of all stored bitmaps will not to
 * exceed size limit ({@link #getSizeLimit()}). When cache reaches limit size then cache clearing is processed by FIFO
 * principle.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class FIFOLimitedCache extends LimitedCache<String, Bitmap> {

	private final List<Bitmap> queue = Collections.synchronizedList(new LinkedList<Bitmap>());

	public FIFOLimitedCache(int sizeLimit) {
		super(sizeLimit);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		if (super.put(key, value)) {
			queue.add(value);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void clear() {
		super.clear();
		queue.clear();
	}

	@Override
	protected int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected Bitmap removeNext() {
		return queue.remove(0);
	}

	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
