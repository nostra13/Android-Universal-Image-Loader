package com.nostra13.universalimageloader.core;

import android.widget.ImageView;

/**
 * Listener for image loading process
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface ImageLoadingListener {

	/** Is called when image loading task was started */
	void onLoadingStarted();

	/** Is called when an error was occurred during image loading */
	void onLoadingFailed();

	/** Is called when image is loaded successfully and displayed in {@link ImageView} */
	void onLoadingComplete();
}
