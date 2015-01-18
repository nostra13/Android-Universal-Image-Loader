/*******************************************************************************
 * Copyright 2013-2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;

/**
 * ImageAware which provides needed info for processing of original image but do nothing for displaying image. It's
 * used when user need just load and decode image and get it in {@linkplain
 * com.nostra13.universalimageloader.core.listener.ImageLoadingListener#onLoadingComplete(String, android.view.View,
 * android.graphics.Bitmap) callback}.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.0
 */
public class NonViewAware implements ImageAware {

	protected final String imageUri;
	protected final ImageSize imageSize;
	protected final ViewScaleType scaleType;

	public NonViewAware(ImageSize imageSize, ViewScaleType scaleType) {
		this(null, imageSize, scaleType);
	}

	public NonViewAware(String imageUri, ImageSize imageSize, ViewScaleType scaleType) {
		if (imageSize == null) throw new IllegalArgumentException("imageSize must not be null");
		if (scaleType == null) throw new IllegalArgumentException("scaleType must not be null");

		this.imageUri = imageUri;
		this.imageSize = imageSize;
		this.scaleType = scaleType;
	}

	@Override
	public int getWidth() {
		return imageSize.getWidth();
	}

	@Override
	public int getHeight() {
		return imageSize.getHeight();
	}

	@Override
	public ViewScaleType getScaleType() {
		return scaleType;
	}

	@Override
	public View getWrappedView() {
		return null;
	}

	@Override
	public boolean isCollected() {
		return false;
	}

	@Override
	public int getId() {
		return TextUtils.isEmpty(imageUri) ? super.hashCode() : imageUri.hashCode();
	}

	@Override
	public boolean setImageDrawable(Drawable drawable) { // Do nothing
		return true;
	}

	@Override
	public boolean setImageBitmap(Bitmap bitmap) { // Do nothing
		return true;
	}
}