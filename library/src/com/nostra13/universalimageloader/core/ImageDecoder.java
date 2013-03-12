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

import static com.nostra13.universalimageloader.core.ImageLoader.LOG_CANT_DECODE_IMAGE;
import static com.nostra13.universalimageloader.core.ImageLoader.LOG_IMAGE_SCALED;
import static com.nostra13.universalimageloader.core.ImageLoader.LOG_IMAGE_SUBSAMPLING;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

/**
 * Decodes images to {@link Bitmap}, scales them to needed size
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 * @see ImageScaleType
 * @see ViewScaleType
 * @see ImageDownloader
 * @see DisplayImageOptions
 */
class ImageDecoder {

	private final String imageUri;
	private final ImageDownloader imageDownloader;
	private final DisplayImageOptions displayOptions;

	private boolean loggingEnabled;

	/**
	 * @param imageUri Image URI (<b>i.e.:</b> "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageDownloader Image downloader
	 * 
	 */
	ImageDecoder(String imageUri, ImageDownloader imageDownloader, DisplayImageOptions options) {
		this.imageUri = imageUri;
		this.imageDownloader = imageDownloader;
		this.displayOptions = options;
	}

	/**
	 * Decodes image from URI into {@link Bitmap}. Image is scaled close to incoming {@link ImageSize image size} during
	 * decoding (depend on incoming image scale type).
	 * 
	 * @param targetSize Image size to scale to during decoding
	 * @param scaleType {@link ImageScaleType Image scale type}
	 * @param viewScaleType {@link ViewScaleType View scale type}
	 * 
	 * @return Decoded bitmap
	 * @throws IOException
	 * @throws UnsupportedOperationException
	 */
	public Bitmap decode(ImageSize targetSize, ImageScaleType scaleType, ViewScaleType viewScaleType) throws IOException {
		Options decodeOptions = getBitmapOptionsForImageDecoding(targetSize, scaleType, viewScaleType);
		InputStream imageStream = imageDownloader.getStream(imageUri, displayOptions.getExtraForDownloader());
		Bitmap subsampledBitmap;
		try {
			subsampledBitmap = BitmapFactory.decodeStream(imageStream, null, decodeOptions);
		} finally {
			IoUtils.closeSilently(imageStream);
		}
		if (subsampledBitmap == null) {
			log(LOG_CANT_DECODE_IMAGE, imageUri);
			return null;
		}

		// Scale to exact size if need
		if (scaleType == ImageScaleType.EXACTLY || scaleType == ImageScaleType.EXACTLY_STRETCHED) {
			subsampledBitmap = scaleImageExactly(subsampledBitmap, targetSize, scaleType, viewScaleType);
		}

		return subsampledBitmap;
	}

	private Options getBitmapOptionsForImageDecoding(ImageSize targetSize, ImageScaleType scaleType, ViewScaleType viewScaleType) throws IOException {
		Options decodeOptions = new Options();
		decodeOptions.inSampleSize = scaleType == ImageScaleType.NONE ? 1 : computeImageScale(targetSize, scaleType, viewScaleType);
		decodeOptions.inPreferredConfig = displayOptions.getBitmapConfig();
		return decodeOptions;
	}

	private int computeImageScale(ImageSize targetSize, ImageScaleType scaleType, ViewScaleType viewScaleType) throws IOException {
		int targetWidth = targetSize.getWidth();
		int targetHeight = targetSize.getHeight();

		// decode image size
		Options options = new Options();
		options.inJustDecodeBounds = true;
		InputStream imageStream = imageDownloader.getStream(imageUri, displayOptions.getExtraForDownloader());
		try {
			BitmapFactory.decodeStream(imageStream, null, options);
		} finally {
			IoUtils.closeSilently(imageStream);
		}

		int scale = 1;
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		int widthScale = imageWidth / targetWidth;
		int heightScale = imageHeight / targetHeight;

		if (viewScaleType == ViewScaleType.FIT_INSIDE) {
			if (scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2) {
				while (imageWidth / 2 >= targetWidth || imageHeight / 2 >= targetHeight) { // ||
					imageWidth /= 2;
					imageHeight /= 2;
					scale *= 2;
				}
			} else {
				scale = Math.max(widthScale, heightScale); // max
			}
		} else { // ViewScaleType.CROP
			if (scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2) {
				while (imageWidth / 2 >= targetWidth && imageHeight / 2 >= targetHeight) { // &&
					imageWidth /= 2;
					imageHeight /= 2;
					scale *= 2;
				}
			} else {
				scale = Math.min(widthScale, heightScale); // min
			}
		}

		if (scale < 1) {
			scale = 1;
		}

		log(LOG_IMAGE_SUBSAMPLING, imageWidth, imageHeight, targetWidth, targetHeight, scale);
		return scale;
	}

	private Bitmap scaleImageExactly(Bitmap subsampledBitmap, ImageSize targetSize, ImageScaleType scaleType, ViewScaleType viewScaleType) {
		float srcWidth = subsampledBitmap.getWidth();
		float srcHeight = subsampledBitmap.getHeight();

		float widthScale = srcWidth / targetSize.getWidth();
		float heightScale = srcHeight / targetSize.getHeight();

		int destWidth;
		int destHeight;
		if ((viewScaleType == ViewScaleType.FIT_INSIDE && widthScale >= heightScale) || (viewScaleType == ViewScaleType.CROP && widthScale < heightScale)) {
			destWidth = targetSize.getWidth();
			destHeight = (int) (srcHeight / widthScale);
		} else {
			destWidth = (int) (srcWidth / heightScale);
			destHeight = targetSize.getHeight();
		}

		Bitmap scaledBitmap;
		if ((scaleType == ImageScaleType.EXACTLY && destWidth < srcWidth && destHeight < srcHeight)
				|| (scaleType == ImageScaleType.EXACTLY_STRETCHED && destWidth != srcWidth && destHeight != srcHeight)) {
			scaledBitmap = Bitmap.createScaledBitmap(subsampledBitmap, destWidth, destHeight, true);
			if (scaledBitmap != subsampledBitmap) {
				subsampledBitmap.recycle();
			}
			log(LOG_IMAGE_SCALED, (int) srcWidth, (int) srcHeight, destWidth, destHeight);
		} else {
			scaledBitmap = subsampledBitmap;
		}

		return scaledBitmap;
	}

	void setLoggingEnabled(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

	private void log(String message, Object... args) {
		if (loggingEnabled) L.i(message, args);
	}
}