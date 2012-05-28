package com.nostra13.universalimageloader.core;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.MemoryCacheKeyUtil;

/**
 * Information for load'n'display image task
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see MemoryCacheKeyUtil
 * @see DisplayImageOptions
 * @see ImageLoadingListener
 */
final class ImageLoadingInfo {

	final String url;
	final String memoryCacheKey;
	final ImageView imageView;
	final ImageSize targetSize;
	final DisplayImageOptions options;
	final ImageLoadingListener listener;

	public ImageLoadingInfo(String url, ImageView imageView, ImageSize targetSize, DisplayImageOptions options, ImageLoadingListener listener) {
		this.url = url;
		this.imageView = imageView;
		this.targetSize = targetSize;
		this.options = options;
		this.listener = listener;
		memoryCacheKey = MemoryCacheKeyUtil.generateKey(url, targetSize);
	}
}
