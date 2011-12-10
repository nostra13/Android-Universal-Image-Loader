package com.nostra13.universalimageloader.imageloader;

import android.widget.ImageView;

/**
 * Listener for image loading process
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface ImageLoadingListener {

	/** Is called when image loading put into queue for loading */
	void onLoadingStarted();

	/** Is called when image is loaded and displyed in {@link ImageView} */
	void onLoadingComplete();
}
