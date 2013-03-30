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
package com.nostra13.universalimageloader.utils;

import java.lang.reflect.Field;

import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;

/**
 * Provides calculations with image sizes, scales
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.3
 */
public final class ImageSizeUtils {

	private ImageSizeUtils() {
	}

	/**
	 * Defines target size for image. Size is defined by target {@link ImageView view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 * Size computing algorithm:<br />
	 * 1) Get the actual drawn <b>getWidth()</b> and <b>getHeight()</b> of the View. If view haven't drawn yet then go
	 * to step #2.<br />
	 * 2) Get <b>layout_width</b> and <b>layout_height</b>. If both of them haven't exact value then go to step #3.<br />
	 * 3) Get <b>maxWidth</b> and <b>maxHeight</b>. If both of them are not set then go to step #4.<br />
	 * 4) Get <b>maxImageWidth</b> param (<b>maxImageWidthForMemoryCache</b>) and <b>maxImageHeight</b> param
	 * (<b>maxImageHeightForMemoryCache</b>). If both of them are not set (equal 0) then go to step #5.<br />
	 * 5) Get device screen dimensions.
	 */
	public static ImageSize defineTargetSizeForView(ImageView imageView, int maxImageWidth, int maxImageHeight) {
		final DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

		final LayoutParams params = imageView.getLayoutParams();
		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth(); // Get actual image width
		if (width <= 0) width = params.width; // Get layout width parameter
		if (width <= 0) width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check maxWidth parameter
		if (width <= 0) width = maxImageWidth;
		if (width <= 0) width = displayMetrics.widthPixels;

		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView.getHeight(); // Get actual image height
		if (height <= 0) height = params.height; // Get layout height parameter
		if (height <= 0) height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check maxHeight parameter
		if (height <= 0) height = maxImageHeight;
		if (height <= 0) height = displayMetrics.heightPixels;

		return new ImageSize(width, height);
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

	/**
	 * Computes sample size for downscaling image size (<b>srcSize</b>) to view size (<b>targetSize</b>). This sample
	 * size is used during
	 * {@linkplain BitmapFactory#decodeStream(java.io.InputStream, android.graphics.Rect, android.graphics.BitmapFactory.Options)
	 * decoding image} to bitmap.<br />
	 * <br />
	 * <b>Examples:</b><br />
	 * 
	 * <pre>
	 * srcSize(100x100), targetSize(10x10), powerOf2Scale = true -> sampleSize = 8
	 * srcSize(100x100), targetSize(10x10), powerOf2Scale = false -> sampleSize = 10
	 * 
	 * srcSize(100x100), targetSize(20x40), viewScaleType = FIT_INSIDE -> sampleSize = 5
	 * srcSize(100x100), targetSize(20x40), viewScaleType = CROP       -> sampleSize = 2
	 * </pre>
	 * 
	 * <br />
	 * The sample size is the number of pixels in either dimension that correspond to a single pixel in the decoded
	 * bitmap. For example, inSampleSize == 4 returns an image that is 1/4 the width/height of the original, and 1/16
	 * the number of pixels. Any value <= 1 is treated the same as 1.
	 * 
	 * @param srcSize Original (image) size
	 * @param targetSize Target (view) size
	 * @param viewScaleType {@linkplain ViewScaleType Scale type} for placing image in view
	 * @param powerOf2Scale <i>true</i> - if sample size be a power of 2 (1, 2, 4, 8, ...)
	 * @return Computed sample size
	 */
	public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType, boolean powerOf2Scale) {
		int srcWidth = srcSize.getWidth();
		int srcHeight = srcSize.getHeight();
		int targetWidth = targetSize.getWidth();
		int targetHeight = targetSize.getHeight();

		int scale = 1;

		int widthScale = srcWidth / targetWidth;
		int heightScale = srcHeight / targetHeight;

		switch (viewScaleType) {
			case FIT_INSIDE:
				if (powerOf2Scale) {
					while (srcWidth / 2 >= targetWidth || srcHeight / 2 >= targetHeight) { // ||
						srcWidth /= 2;
						srcHeight /= 2;
						scale *= 2;
					}
				} else {
					scale = Math.max(widthScale, heightScale); // max
				}
				break;
			case CROP:
				if (powerOf2Scale) {
					while (srcWidth / 2 >= targetWidth && srcHeight / 2 >= targetHeight) { // &&
						srcWidth /= 2;
						srcHeight /= 2;
						scale *= 2;
					}
				} else {
					scale = Math.min(widthScale, heightScale); // min
				}
				break;
		}

		if (scale < 1) {
			scale = 1;
		}

		return scale;
	}

	/**
	 * Computes scale of target size (<b>targetSize</b>) to source size (<b>srcSize</b>).<br />
	 * <br />
	 * <b>Examples:</b><br />
	 * 
	 * <pre>
	 * srcSize(40x40), targetSize(10x10) -> scale = 0.25
	 * 
	 * srcSize(10x10), targetSize(20x20), stretch = false -> scale = 1
	 * srcSize(10x10), targetSize(20x20), stretch = true  -> scale = 2
	 * 
	 * srcSize(100x100), targetSize(20x40), viewScaleType = FIT_INSIDE -> scale = 0.2
	 * srcSize(100x100), targetSize(20x40), viewScaleType = CROP       -> scale = 0.4
	 * </pre>
	 * 
	 * @param srcSize Source (image) size
	 * @param targetSize Target (view) size
	 * @param viewScaleType {@linkplain ViewScaleType Scale type} for placing image in view
	 * @param stretch Whether source size should be stretched if target size is larger than source size. If <b>false</b>
	 *            then result scale value can't be greater than 1.
	 * @return Computed scale
	 */
	public static float computeImageScale(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType, boolean stretch) {
		int srcWidth = srcSize.getWidth();
		int srcHeight = srcSize.getHeight();
		int targetWidth = targetSize.getWidth();
		int targetHeight = targetSize.getHeight();

		float widthScale = (float) srcWidth / targetWidth;
		float heightScale = (float) srcHeight / targetHeight;

		int destWidth;
		int destHeight;
		if ((viewScaleType == ViewScaleType.FIT_INSIDE && widthScale >= heightScale) || (viewScaleType == ViewScaleType.CROP && widthScale < heightScale)) {
			destWidth = targetWidth;
			destHeight = (int) (srcHeight / widthScale);
		} else {
			destWidth = (int) (srcWidth / heightScale);
			destHeight = targetHeight;
		}

		float scale = 1;
		if ((!stretch && destWidth < srcWidth && destHeight < srcHeight) || (stretch && destWidth != srcWidth && destHeight != srcHeight)) {
			scale = (float) destWidth / srcWidth;
		}

		return scale;
	}
}
