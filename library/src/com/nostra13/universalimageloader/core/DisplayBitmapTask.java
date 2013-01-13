package com.nostra13.universalimageloader.core;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Displays bitmap in {@link ImageView}. Must be called on UI thread.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoadingListener
 * @see BitmapDisplayer
 */
final class DisplayBitmapTask implements Runnable {

	private static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView [%s]";
	private static final String LOG_TASK_CANCELLED = "ImageView is reused for another image. Task is cancelled. [%s]";

	private final Bitmap bitmap;
	private final ImageView imageView;
	private final String memoryCacheKey;
	private final BitmapDisplayer bitmapDisplayer;
	private final ImageLoadingListener listener;

	private boolean loggingEnabled;

	public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo) {
		this.bitmap = bitmap;
		imageView = imageLoadingInfo.imageView;
		memoryCacheKey = imageLoadingInfo.memoryCacheKey;
		bitmapDisplayer = imageLoadingInfo.options.getDisplayer();
		listener = imageLoadingInfo.listener;
	}

	public void run() {
		if (isViewWasReused()) {
			if (loggingEnabled) L.i(LOG_TASK_CANCELLED, memoryCacheKey);
			listener.onLoadingCancelled();
		} else {
			if (loggingEnabled) L.i(LOG_DISPLAY_IMAGE_IN_IMAGEVIEW, memoryCacheKey);
			Bitmap displayedBitmap = bitmapDisplayer.display(bitmap, imageView);
			listener.onLoadingComplete(displayedBitmap);
			ImageLoader.getInstance().cancelDisplayTask(imageView);
		}
	}

	/** Checks whether memory cache key (image URI) for current ImageView is actual */
	private boolean isViewWasReused() {
		String currentCacheKey = ImageLoader.getInstance().getLoadingUriForView(imageView);
		return !memoryCacheKey.equals(currentCacheKey);
	}

	void setLoggingEnabled(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}
}
