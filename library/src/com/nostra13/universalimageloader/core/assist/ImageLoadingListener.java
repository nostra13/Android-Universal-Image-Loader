package com.nostra13.universalimageloader.core.assist;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Listener for image loading process.<br />
 * You can use {@link SimpleImageLoadingListener} for implementing only needed methods.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see SimpleImageLoadingListener
 * @see FailReason
 */
public interface ImageLoadingListener {

	/** Is called when image loading task was started */
	void onLoadingStarted();

	/** Is called when an error was occurred during image loading */
	void onLoadingFailed(FailReason failReason);

	/** Is called when image is loaded successfully and displayed in {@link ImageView} */
	void onLoadingComplete(Bitmap loadedImage);

	/** Is called when image loading task was cancelled because {@link ImageView} was reused in newer task */
	void onLoadingCancelled();
}
