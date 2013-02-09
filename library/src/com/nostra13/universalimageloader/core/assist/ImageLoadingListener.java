package com.nostra13.universalimageloader.core.assist;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Listener for image loading process.<br />
 * You can use {@link SimpleImageLoadingListener} for implementing only needed methods.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see SimpleImageLoadingListener
 * @see FailReason
 */
public interface ImageLoadingListener {

	/**
	 * Is called when image loading task was started
	 * 
	 * @param imageUri Loading image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForListener(Object)
	 *            DisplayImageOptions.extraForListener(Object)}
	 */
	void onLoadingStarted(String imageUri, Object extra);

	/**
	 * Is called when an error was occurred during image loading
	 * 
	 * @param imageUri Loading image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForListener(Object)
	 *            DisplayImageOptions.extraForListener(Object)}
	 * @param failReason {@linkplain FailReason The reason} why image loading was failed
	 */
	void onLoadingFailed(String imageUri, Object extra, FailReason failReason);

	/**
	 * Is called when image is loaded successfully (and displayed in {@link ImageView} if one was specified)
	 * 
	 * @param imageUri Loaded image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForListener(Object)
	 *            DisplayImageOptions.extraForListener(Object)}
	 * @param loadedImage Bitmap of loaded and decoded image
	 */
	void onLoadingComplete(String imageUri, Object extra, Bitmap loadedImage);

	/**
	 * Is called when image loading task was cancelled because {@link ImageView} was reused in newer task
	 * 
	 * @param imageUri Loading image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForListener(Object)
	 *            DisplayImageOptions.extraForListener(Object)}
	 */
	void onLoadingCancelled(String imageUri, Object extra);
}
