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

import android.graphics.BitmapFactory;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

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
	 * Defines target size for image aware view. Size is defined by target
	 * {@link com.nostra13.universalimageloader.core.imageaware.ImageAware view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 */
	public static ImageSize defineTargetSizeForView(ImageAware imageAware, ImageSize maxImageSize) {
		int width = imageAware.getWidth();
		if (width <= 0) width = maxImageSize.getWidth();

		int height = imageAware.getHeight();
		if (height <= 0) height = maxImageSize.getHeight();

		return new ImageSize(width, height);
	}

	/**
	 * Computes sample size for downscaling image size (<b>srcSize</b>) to view size (<b>targetSize</b>). This sample
	 * size is used during
	 * {@linkplain BitmapFactory#decodeStream(java.io.InputStream, android.graphics.Rect, android.graphics.BitmapFactory.Options)
	 * decoding image} to bitmap.<br />
	 * <br />
	 * <b>Examples:</b><br />
	 * <p/>
	 * <pre>
	 * srcSize(100x100), targetSize(10x10), powerOf2Scale = true -> sampleSize = 8
	 * srcSize(100x100), targetSize(10x10), powerOf2Scale = false -> sampleSize = 10
	 *
	 * srcSize(100x100), targetSize(20x40), viewScaleType = FIT_INSIDE -> sampleSize = 5
	 * srcSize(100x100), targetSize(20x40), viewScaleType = CROP       -> sampleSize = 2
	 * </pre>
	 * <p/>
	 * <br />
	 * The sample size is the number of pixels in either dimension that correspond to a single pixel in the decoded
	 * bitmap. For example, inSampleSize == 4 returns an image that is 1/4 the width/height of the original, and 1/16
	 * the number of pixels. Any value <= 1 is treated the same as 1.
	 *
	 * @param srcSize       Original (image) size
	 * @param targetSize    Target (view) size
	 * @param viewScaleType {@linkplain ViewScaleType Scale type} for placing image in view
	 * @param powerOf2Scale <i>true</i> - if sample size be a power of 2 (1, 2, 4, 8, ...)
	 * @return Computed sample size
	 */
	public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType,
											 boolean powerOf2Scale) {
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
	 * <p/>
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
	 * @param srcSize       Source (image) size
	 * @param targetSize    Target (view) size
	 * @param viewScaleType {@linkplain ViewScaleType Scale type} for placing image in view
	 * @param stretch       Whether source size should be stretched if target size is larger than source size. If <b>false</b>
	 *                      then result scale value can't be greater than 1.
	 * @return Computed scale
	 */
	public static float computeImageScale(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType,
										  boolean stretch) {
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
