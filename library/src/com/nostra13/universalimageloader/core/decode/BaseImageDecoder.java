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
package com.nostra13.universalimageloader.core.decode;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

/**
 * Decodes images to {@link Bitmap}, scales them to needed size
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.3
 * @see ImageDecodingInfo
 */
public class BaseImageDecoder implements ImageDecoder {

	protected static final String LOG_SABSAMPLE_IMAGE = "Subsample original image (%1$s) to %2$s (scale = %3$d) [%4$s]";
	protected static final String LOG_SCALE_IMAGE = "Scale subsampled image (%1$s) to %2$s (scale = %3$.5f) [%4$s]";
	protected static final String LOG_ROTATE_IMAGE = "Rotate image on %1$d\u00B0 [%2$s]";
	protected static final String LOG_FLIP_IMAGE = "Flip image horizontally [%s]";
	protected static final String ERROR_CANT_DECODE_IMAGE = "Image can't be decoded [%s]";

	protected boolean loggingEnabled;

	public BaseImageDecoder() {
	}

	public BaseImageDecoder(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

	/**
	 * Decodes image from URI into {@link Bitmap}. Image is scaled close to incoming {@linkplain ImageSize target size}
	 * during decoding (depend on incoming parameters).
	 * 
	 * @param decodingInfo Needed data for decoding image
	 * 
	 * @return Decoded bitmap
	 * @throws IOException if some I/O exception occurs during image reading
	 * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
	 */
	public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {
		InputStream imageStream = getImageStream(decodingInfo);
		ImageFileInfo imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo.getImageUri());
		Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo);
		imageStream = getImageStream(decodingInfo);
		Bitmap decodedBitmap = decodeStream(imageStream, decodingOptions);
		if (decodedBitmap == null) {
			L.e(ERROR_CANT_DECODE_IMAGE, decodingInfo.getImageKey());
		} else {
			decodedBitmap = considerExactScaleAndOrientaiton(decodedBitmap, decodingInfo, imageInfo.exif.rotation, imageInfo.exif.flipHorizontal);
		}
		return decodedBitmap;
	}

	protected InputStream getImageStream(ImageDecodingInfo decodingInfo) throws IOException {
		return decodingInfo.getDownloader().getStream(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());
	}

	protected ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, String imageUri) throws IOException {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(imageStream, null, options);
		} finally {
			IoUtils.closeSilently(imageStream);
		}

		ExifInfo exif;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			exif = defineExifOrientation(imageUri, options.outMimeType);
		} else {
			exif = new ExifInfo();
		}
		return new ImageFileInfo(new ImageSize(options.outWidth, options.outHeight, exif.rotation), exif);
	}

	protected ExifInfo defineExifOrientation(String imageUri, String mimeType) {
		int rotation = 0;
		boolean flip = false;
		if ("image/jpeg".equalsIgnoreCase(mimeType) && Scheme.ofUri(imageUri) == Scheme.FILE) {
			try {
				ExifInterface exif = new ExifInterface(Scheme.FILE.crop(imageUri));
				int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				switch (exifOrientation) {
					case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
						flip = true;
					case ExifInterface.ORIENTATION_NORMAL:
						rotation = 0;
						break;
					case ExifInterface.ORIENTATION_TRANSVERSE:
						flip = true;
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotation = 90;
						break;
					case ExifInterface.ORIENTATION_FLIP_VERTICAL:
						flip = true;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotation = 180;
						break;
					case ExifInterface.ORIENTATION_TRANSPOSE:
						flip = true;
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotation = 270;
						break;
				}
			} catch (IOException e) {
				L.w("Can't read EXIF tags from file [%s]", imageUri);
			}
		}
		return new ExifInfo(rotation, flip);
	}

	protected Options prepareDecodingOptions(ImageSize imageSize, ImageDecodingInfo decodingInfo) {
		ImageScaleType scaleType = decodingInfo.getImageScaleType();
		ImageSize targetSize = decodingInfo.getTargetSize();
		int scale = 1;
		if (scaleType != ImageScaleType.NONE) {
			boolean powerOf2 = scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2;
			scale = ImageSizeUtils.computeImageSampleSize(imageSize, targetSize, decodingInfo.getViewScaleType(), powerOf2);

			if (loggingEnabled) L.i(LOG_SABSAMPLE_IMAGE, imageSize, imageSize.scaleDown(scale), scale, decodingInfo.getImageKey());
		}
		Options decodingOptions = decodingInfo.getDecodingOptions();
		decodingOptions.inSampleSize = scale;
		return decodingOptions;
	}

	protected Bitmap decodeStream(InputStream imageStream, Options decodingOptions) throws IOException {
		try {
			return BitmapFactory.decodeStream(imageStream, null, decodingOptions);
		} finally {
			IoUtils.closeSilently(imageStream);
		}
	}

	protected Bitmap considerExactScaleAndOrientaiton(Bitmap subsampledBitmap, ImageDecodingInfo decodingInfo, int rotation, boolean flipHorizontal) {
		Matrix m = new Matrix();
		// Scale to exact size if need
		ImageScaleType scaleType = decodingInfo.getImageScaleType();
		if (scaleType == ImageScaleType.EXACTLY || scaleType == ImageScaleType.EXACTLY_STRETCHED) {
			ImageSize srcSize = new ImageSize(subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), rotation);
			float scale = ImageSizeUtils.computeImageScale(srcSize, decodingInfo.getTargetSize(), decodingInfo.getViewScaleType(),
					scaleType == ImageScaleType.EXACTLY_STRETCHED);
			if (Float.compare(scale, 1f) != 0) {
				m.setScale(scale, scale);

				if (loggingEnabled) L.i(LOG_SCALE_IMAGE, srcSize, srcSize.scale(scale), scale, decodingInfo.getImageKey());
			}
		}
		// Flip bitmap if need
		if (flipHorizontal) {
			m.postScale(-1, 1);

			if (loggingEnabled) L.i(LOG_FLIP_IMAGE, decodingInfo.getImageKey());
		}
		// Rotate bitmap if need
		if (rotation != 0) {
			m.postRotate(rotation);

			if (loggingEnabled) L.i(LOG_ROTATE_IMAGE, rotation, decodingInfo.getImageKey());
		}

		Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), m, true);
		if (finalBitmap != subsampledBitmap) {
			subsampledBitmap.recycle();
		}
		return finalBitmap;
	}

	public void setLoggingEnabled(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

	protected static class ExifInfo {

		final int rotation;
		final boolean flipHorizontal;

		ExifInfo() {
			this.rotation = 0;
			this.flipHorizontal = false;
		}

		ExifInfo(int rotation, boolean flipHorizontal) {
			this.rotation = rotation;
			this.flipHorizontal = flipHorizontal;
		}
	}

	protected static class ImageFileInfo {

		final ImageSize imageSize;
		final ExifInfo exif;

		ImageFileInfo(ImageSize imageSize, ExifInfo exif) {
			this.imageSize = imageSize;
			this.exif = exif;
		}
	}
}