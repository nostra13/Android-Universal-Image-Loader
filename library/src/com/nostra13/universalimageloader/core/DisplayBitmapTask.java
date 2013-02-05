package com.nostra13.universalimageloader.core;

import static com.nostra13.universalimageloader.core.ImageLoader.LOG_DISPLAY_IMAGE_IN_IMAGEVIEW;
import static com.nostra13.universalimageloader.core.ImageLoader.LOG_TASK_CANCELLED;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

/**
 * Displays bitmap in {@link ImageView}. Must be called on UI thread.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoadingListener
 * @see BitmapDisplayer
 */
final class DisplayBitmapTask implements Runnable {

	private final Bitmap bitmap;
	private final ImageView imageView;
	private final String memoryCacheKey;
	private final BitmapDisplayer bitmapDisplayer;
	private final ImageLoadingListener listener;
	private final ImageLoaderEngine engine;

	private boolean loggingEnabled;

	public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine) {
		this.bitmap = bitmap;
		imageView = imageLoadingInfo.imageView;
		memoryCacheKey = imageLoadingInfo.memoryCacheKey;
		bitmapDisplayer = imageLoadingInfo.options.getDisplayer();
		listener = imageLoadingInfo.listener;
		this.engine = engine;
	}

	public void run() {
		if (isViewWasReused()) {
			if (loggingEnabled) L.i(LOG_TASK_CANCELLED, memoryCacheKey);
			listener.onLoadingCancelled();
		} else {
			if (loggingEnabled) L.i(LOG_DISPLAY_IMAGE_IN_IMAGEVIEW, memoryCacheKey);
			Bitmap displayedBitmap = bitmapDisplayer.display(bitmap, imageView);
			listener.onLoadingComplete(displayedBitmap);
			engine.cancelDisplayTaskFor(imageView);
		}
	}

	/** Checks whether memory cache key (image URI) for current ImageView is actual */
	private boolean isViewWasReused() {
		String currentCacheKey = engine.getLoadingUriForView(imageView);
		return !memoryCacheKey.equals(currentCacheKey);
	}

	void setLoggingEnabled(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}
}
