package com.nostra13.universalimageloader.core.assist;

import android.graphics.Bitmap;

/**
 * A convenience class to extend when you only want to listen for a subset of all the image loading events. This
 * implements all methods in the {@link ImageLoadingListener} but does nothing.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class SimpleImageLoadingListener implements ImageLoadingListener {
	@Override
	public void onLoadingStarted() {
		// Empty implementation
	}

	@Override
	public void onLoadingFailed(FailReason failReason) {
		// Empty implementation
	}

	@Override
	public void onLoadingComplete(Bitmap loadedImage) {
		// Empty implementation
	}

	@Override
	public void onLoadingCancelled() {
		// Empty implementation
	}
}
