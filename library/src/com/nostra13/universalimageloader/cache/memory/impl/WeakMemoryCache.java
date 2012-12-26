package com.nostra13.universalimageloader.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.BaseMemoryCache;

/**
 * Memory cache with {@linkplain WeakReference weak references} to {@linkplain android.graphics.Bitmap bitmaps}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class WeakMemoryCache extends BaseMemoryCache<String, Bitmap> {
	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
