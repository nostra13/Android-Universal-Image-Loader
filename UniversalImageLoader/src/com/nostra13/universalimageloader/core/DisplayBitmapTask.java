package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

/**
 * Used to display bitmap in {@link ImageView}. Must be called on UI thread.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoaderConfiguration
 * @see ImageLoadingInfo
 */
final class DisplayBitmapTask implements Runnable {

	private static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView [%s]";

	private final ImageLoaderConfiguration configuration;
	private final Bitmap bitmap;
	private final ImageLoadingInfo imageLoadingInfo;

	public DisplayBitmapTask(ImageLoaderConfiguration configuration, ImageLoadingInfo imageLoadingInfo, Bitmap bitmap) {
		this.configuration = configuration;
		this.bitmap = bitmap;
		this.imageLoadingInfo = imageLoadingInfo;
	}

	public void run() {
		if (imageLoadingInfo.isConsistent()) {
			if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_DISPLAY_IMAGE_IN_IMAGEVIEW, imageLoadingInfo.memoryCacheKey));
			imageLoadingInfo.imageView.setImageBitmap(bitmap);
			imageLoadingInfo.listener.onLoadingComplete();
		}
	}
}
