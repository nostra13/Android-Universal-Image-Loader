/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

/**
 * Displays bitmap in {@link ImageView}. Must be called on UI thread.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoadingListener
 * @see BitmapDisplayer
 * @since 1.3.1
 */
final class DisplayBitmapTask implements Runnable {

	private static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView (loaded from %1$s) [%2$s]";
	private static final String LOG_TASK_CANCELLED = "ImageView is reused for another image. Task is cancelled. [%s]";

	private final Bitmap bitmap;
	private final String imageUri;
	private final ImageView imageView;
	private final String memoryCacheKey;
	private final BitmapDisplayer displayer;
	private final ImageLoadingListener listener;
	private final ImageLoaderEngine engine;
	private final LoadedFrom loadedFrom;

	private boolean loggingEnabled;

	public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine, LoadedFrom loadedFrom) {
		this.bitmap = bitmap;
		imageUri = imageLoadingInfo.uri;
		imageView = imageLoadingInfo.imageView;
		memoryCacheKey = imageLoadingInfo.memoryCacheKey;
		displayer = imageLoadingInfo.options.getDisplayer();
		listener = imageLoadingInfo.listener;
		this.engine = engine;
		this.loadedFrom = loadedFrom;
	}

	public void run() {
		if (isViewWasReused()) {
			if (loggingEnabled) L.i(LOG_TASK_CANCELLED, memoryCacheKey);
			listener.onLoadingCancelled(imageUri, imageView);
		} else {
			if (loggingEnabled) L.i(LOG_DISPLAY_IMAGE_IN_IMAGEVIEW, loadedFrom, memoryCacheKey);
			Bitmap displayedBitmap = displayer.display(bitmap, imageView, loadedFrom);
			listener.onLoadingComplete(imageUri, imageView, displayedBitmap);
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
