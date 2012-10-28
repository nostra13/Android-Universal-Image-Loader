package com.nostra13.universalimageloader.core;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Displays bitmap in {@link ImageView}. Must be called on UI thread.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoadingListener
 */
final class DisplayBitmapTask implements Runnable {

	private final Bitmap bitmap;
	private final ImageView imageView;
	private final BitmapDisplayer bitmapDisplayer;
	private final ImageLoadingListener listener;
	private final String memoryCacheKey;

	public DisplayBitmapTask(Bitmap bitmap, ImageView imageView, BitmapDisplayer bitmapDisplayer, ImageLoadingListener listener, String memoryCacheKey) {
		this.bitmap = bitmap;
		this.imageView = imageView;
		this.bitmapDisplayer = bitmapDisplayer;
		this.listener = listener;
		this.memoryCacheKey = memoryCacheKey;
	}

	public void run() {
		String currentCacheKey = ImageLoader.getInstance().getLoadingUriForView(imageView);
		if (memoryCacheKey.equals(currentCacheKey)) {
			Bitmap displayedBitmap = bitmapDisplayer.display(bitmap, imageView);
			listener.onLoadingComplete(displayedBitmap);
		} else {
			listener.onLoadingCancelled();
		}
	}
}
