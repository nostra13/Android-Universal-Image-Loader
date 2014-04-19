/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
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
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.utils.L;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Wrapper for Android {@link android.view.View View}. Keeps weak reference of View to prevent memory leaks.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.2
 */
public abstract class ViewAware implements ImageAware {

	public static final String WARN_CANT_SET_DRAWABLE = "Can't set a drawable into view. You should call ImageLoader on UI thread for it.";
	public static final String WARN_CANT_SET_BITMAP = "Can't set a bitmap into view. You should call ImageLoader on UI thread for it.";

	protected Reference<View> viewRef;
	protected boolean checkActualViewSize;

	/**
	 * Constructor. <br />
	 * References {@link #ViewAware(android.view.View, boolean) ImageViewAware(imageView, true)}.
	 *
	 * @param view {@link android.view.View View} to work with
	 */
	public ViewAware(View view) {
		this(view, true);
	}

	/**
	 * Constructor
	 *
	 * @param view                {@link android.view.View View} to work with
	 * @param checkActualViewSize <b>true</b> - then {@link #getWidth()} and {@link #getHeight()} will check actual
	 *                            size of View. It can cause known issues like
	 *                            <a href="https://github.com/nostra13/Android-Universal-Image-Loader/issues/376">this</a>.
	 *                            But it helps to save memory because memory cache keeps bitmaps of actual (less in
	 *                            general) size.
	 *                            <p/>
	 *                            <b>false</b> - then {@link #getWidth()} and {@link #getHeight()} will <b>NOT</b>
	 *                            consider actual size of View, just layout parameters. <br /> If you set 'false'
	 *                            it's recommended 'android:layout_width' and 'android:layout_height' (or
	 *                            'android:maxWidth' and 'android:maxHeight') are set with concrete values. It helps to
	 *                            save memory.
	 */
	public ViewAware(View view, boolean checkActualViewSize) {
		if (view == null) throw new IllegalArgumentException("view must not be null");

		this.viewRef = new WeakReference<View>(view);
		this.checkActualViewSize = checkActualViewSize;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Width is defined by target {@link android.view.View view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 * Size computing algorithm (go by steps until get non-zero value):<br />
	 * 1) Get the actual drawn <b>getWidth()</b> of the View<br />
	 * 2) Get <b>layout_width</b>
	 */
	@Override
	public int getWidth() {
		View view = viewRef.get();
		if (view != null) {
			final ViewGroup.LayoutParams params = view.getLayoutParams();
			int width = 0;
			if (checkActualViewSize && params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
				width = view.getWidth(); // Get actual image width
			}
			if (width <= 0 && params != null) width = params.width; // Get layout width parameter
			return width;
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Height is defined by target {@link android.view.View view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 * Size computing algorithm (go by steps until get non-zero value):<br />
	 * 1) Get the actual drawn <b>getHeight()</b> of the View<br />
	 * 2) Get <b>layout_height</b>
	 */
	@Override
	public int getHeight() {
		View view = viewRef.get();
		if (view != null) {
			final ViewGroup.LayoutParams params = view.getLayoutParams();
			int height = 0;
			if (checkActualViewSize && params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
				height = view.getHeight(); // Get actual image height
			}
			if (height <= 0 && params != null) height = params.height; // Get layout height parameter
			return height;
		}
		return 0;
	}

	@Override
	public ViewScaleType getScaleType() {
		return ViewScaleType.CROP;
	}

	@Override
	public View getWrappedView() {
		return viewRef.get();
	}

	@Override
	public boolean isCollected() {
		return viewRef.get() == null;
	}

	@Override
	public int getId() {
		View view = viewRef.get();
		return view == null ? super.hashCode() : view.hashCode();
	}

	@Override
	public boolean setImageDrawable(Drawable drawable) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			View view = viewRef.get();
			if (view != null) {
				setImageDrawableInto(drawable, view);
				return true;
			}
		} else {
			L.w(WARN_CANT_SET_DRAWABLE);
		}
		return false;
	}

	@Override
	public boolean setImageBitmap(Bitmap bitmap) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			View view = viewRef.get();
			if (view != null) {
				setImageBitmapInto(bitmap, view);
				return true;
			}
		} else {
			L.w(WARN_CANT_SET_BITMAP);
		}
		return false;
	}

	/**
	 * Should set drawable into incoming view. Incoming view is guaranteed not null.<br />
	 * This method is called on UI thread.
	 */
	protected abstract void setImageDrawableInto(Drawable drawable, View view);

	/**
	 * Should set Bitmap into incoming view. Incoming view is guaranteed not null.< br />
	 * This method is called on UI thread.
	 */
	protected abstract void setImageBitmapInto(Bitmap bitmap, View view);
}
