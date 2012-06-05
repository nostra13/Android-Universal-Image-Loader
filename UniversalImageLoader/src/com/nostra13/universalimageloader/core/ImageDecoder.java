package com.nostra13.universalimageloader.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.nostra13.universalimageloader.core.assist.DecodingType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * Decodes images to {@link Bitmap}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see DecodingType
 */
class ImageDecoder {

	private final URL imageUrl;
	private final ImageDownloader imageDownloader;
	private final ImageSize targetSize;
	private final DecodingType decodingType;

	/**
	 * @param imageUrl
	 *            Image URL (<b>i.e.:</b> "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageDownloader
	 *            Image downloader
	 * @param targetImageSize
	 *            Image size to scale to during decoding
	 * @param decodingType
	 *            {@link DecodingType Decoding type}
	 */
	ImageDecoder(URL imageUrl, ImageDownloader imageDownloader, ImageSize targetImageSize, DecodingType decodingType) {
		this.imageUrl = imageUrl;
		this.imageDownloader = imageDownloader;
		this.targetSize = targetImageSize;
		this.decodingType = decodingType;
	}

	/**
	 * Decodes image from URL into {@link Bitmap}. Image is scaled close to incoming {@link ImageSize image size} during
	 * decoding. Initial image size is reduced by the power of 2 (according Android recommendations)
	 * 
	 * @return Decoded bitmap
	 * @throws IOException
	 */
	public Bitmap decode() throws IOException {
		Options decodeOptions = getBitmapOptionsForImageDecoding();
		InputStream imageStream = imageDownloader.getStream(imageUrl);
		try {
			return BitmapFactory.decodeStream(imageStream, null, decodeOptions);
		} finally {
			imageStream.close();
		}
	}

	private Options getBitmapOptionsForImageDecoding() throws IOException {
		Options options = new Options();
		options.inSampleSize = computeImageScale();
		return options;
	}

	private int computeImageScale() throws IOException {
		int width = targetSize.getWidth();
		int height = targetSize.getHeight();

		// decode image size
		Options options = new Options();
		options.inJustDecodeBounds = true;
		InputStream imageStream = imageDownloader.getStream(imageUrl);
		try {
			BitmapFactory.decodeStream(imageStream, null, options);
		} finally {
			imageStream.close();
		}

		int scale = 1;
		switch (decodingType) {
			default:
			case FAST:
				// Find the correct scale value. It should be the power of 2.
				int width_tmp = options.outWidth;
				int height_tmp = options.outHeight;

				while (true) {
					if (width_tmp / 2 < width || height_tmp / 2 < height) break;
					width_tmp /= 2;
					height_tmp /= 2;
					scale *= 2;
				}
				break;
			case MEMORY_SAVING:
				int widthScale = (int) (Math.floor(((double) options.outWidth) / width));
				int heightScale = (int) (Math.floor(((double) options.outHeight) / height));
				int minScale = Math.min(widthScale, heightScale);
				if (minScale > 1) {
					scale = minScale;
				}
				break;
		}

		return scale;
	}
}