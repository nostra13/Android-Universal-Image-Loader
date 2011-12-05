package com.nostra13.universalimageloader.cache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;

/**
 * Image cache limited by size. Contains Bitmaps.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageCache extends LimitedCache<String, Bitmap> {

	private int sizeLimit;

	public ImageCache(int sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	@Override
	protected int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected int getSizeLimit() {
		return sizeLimit;
	}

	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
