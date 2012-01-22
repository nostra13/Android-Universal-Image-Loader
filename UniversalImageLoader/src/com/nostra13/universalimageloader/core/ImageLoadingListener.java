package com.nostra13.universalimageloader.core;

import android.widget.ImageView;

/**
 * Listener for image loading process
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface ImageLoadingListener {

	/** Is called when image loading task was put into thread execution pool */
	void onLoadingStarted();

	/** Is called when an error was occurred during image loading */
	void onLoadingFailed(FailReason failReason);

	/** Is called when image is loaded successfully and displayed in {@link ImageView} */
	void onLoadingComplete();
}
