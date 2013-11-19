/*******************************************************************************
 * Copyright 2013 Sergey Tarasevich
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
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.utils.L;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Wrapper for Android {@link android.widget.ImageView ImageView}. Keeps weak reference of ImageView to prevent memory
 * leaks.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.0
 */
public class ImageViewAware implements ImageAware {

	private Reference<ImageView> imageViewRef;

	public ImageViewAware(ImageView imageView) {
		this.imageViewRef = new WeakReference<ImageView>(imageView);
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Width is defined by target {@link ImageView view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 * Size computing algorithm:<br />
	 * 1) Get the actual drawn <b>getWidth()</b> of the View. If view haven't drawn yet then go
	 * to step #2.<br />
	 * 2) Get <b>layout_width</b>. If it hasn't exact value then go to step #3.<br />
	 * 3) Get <b>maxWidth</b>.
	 */
	@Override
	public int getWidth() {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			final ViewGroup.LayoutParams params = imageView.getLayoutParams();
			int width = (params != null && params.width == ViewGroup.LayoutParams.WRAP_CONTENT) ? 0 :
					imageView.getWidth(); // Get actual image width
			if (width <= 0 && params != null) width = params.width; // Get layout width parameter
			if (width <= 0) width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check maxWidth parameter
			L.w("width = " + width);
			return width;
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Height is defined by target {@link ImageView view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 * Size computing algorithm:<br />
	 * 1) Get the actual drawn <b>getHeight()</b> of the View. If view haven't drawn yet then go
	 * to step #2.<br />
	 * 2) Get <b>layout_height</b>. If it hasn't exact value then go to step #3.<br />
	 * 3) Get <b>maxHeight</b>.
	 */
	@Override
	public int getHeight() {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			final ViewGroup.LayoutParams params = imageView.getLayoutParams();
			int height = (params != null && params.height == ViewGroup.LayoutParams.WRAP_CONTENT) ? 0 :
					imageView.getHeight(); // Get actual image height
			if (height <= 0 && params != null) height = params.height; // Get layout height parameter
			if (height <= 0) height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check maxHeight parameter
			L.w("height = " + height);
			return height;
		}
		return 0;
	}

	@Override
	public ViewScaleType getScaleType() {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			return ViewScaleType.fromImageView(imageView);
		}
		return null;
	}

	@Override
	public ImageView getWrappedView() {
		return imageViewRef.get();
	}

	@Override
	public boolean isCollected() {
		return imageViewRef.get() == null;
	}

	@Override
	public int getId() {
		ImageView imageView = imageViewRef.get();
		return imageView == null ? super.hashCode() : imageView.hashCode();
	}

	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			L.e(e);
		}
		return value;
	}

	@Override
	public boolean setImageDrawable(Drawable drawable) {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			imageView.setImageDrawable(drawable);
			return true;
		}
		return false;
	}

	@Override
	public boolean setImageBitmap(Bitmap bitmap) {
		ImageView imageView = imageViewRef.get();
		if (imageView != null) {
			imageView.setImageBitmap(bitmap);
			return true;
		}
		return false;
	}
}
