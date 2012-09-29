package com.nostra13.universalimageloader.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * Decodes images to {@link Bitmap}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * 
 * @see ImageScaleType
 * @see ImageDownloader
 */
class ImageDecoder {

	private final URI imageUri;
	private final ImageDownloader imageDownloader;

	/**
	 * @param imageUri
	 *            Image URI (<b>i.e.:</b> "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageDownloader
	 *            Image downloader
	 * 
	 */
	ImageDecoder(URI imageUri, ImageDownloader imageDownloader) {
		this.imageUri = imageUri;
		this.imageDownloader = imageDownloader;
	}

	/**
	 * Decodes image from URI into {@link Bitmap}. Image is scaled close to incoming {@link ImageSize image size} during
	 * decoding (depend on incoming image scale type).
	 * 
	 * @param targetSize
	 *            Image size to scale to during decoding
	 * @param scaleType
	 *            {@link ImageScaleType Image scale type}
	 * 
	 * @return Decoded bitmap
	 * @throws IOException
	 */
	public Bitmap decode(ImageSize targetSize, ImageScaleType scaleType) throws IOException {
		return decode(targetSize, scaleType, ScaleType.CENTER_INSIDE);
	}

	/**
	 * Decodes image from URI into {@link Bitmap}. Image is scaled close to incoming {@link ImageSize image size} during
	 * decoding (depend on incoming image scale type).
	 * 
	 * @param targetSize
	 *            Image size to scale to during decoding
	 * @param scaleType
	 *            {@link ImageScaleType Image scale type}
	 * @param viewScaleType
	 *            {@link ScaleType ImageView scale type}
	 * 
	 * @return Decoded bitmap
	 * @throws IOException
	 */
	public Bitmap decode(ImageSize targetSize, ImageScaleType scaleType, ScaleType viewScaleType) throws IOException {
		Options decodeOptions = getBitmapOptionsForImageDecoding(targetSize, scaleType, viewScaleType);
		InputStream imageStream = imageDownloader.getStream(imageUri);
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, decodeOptions);

            // if we are in stretch to full width of target mode, check if the image is narrower than the target
            if (bitmap != null && scaleType == ImageScaleType.STRETCH_TO_FULL_WIDTH_OF_TARGET) {
                if (targetSize.getWidth() > bitmap.getWidth()) {
                    // calculate the proper height based on the width
                    int newImageHeight = targetSize.getWidth() * bitmap.getHeight() / bitmap.getWidth();
                    // stretch the image to the full target width and appropriate height
                    bitmap = Bitmap.createScaledBitmap(bitmap, targetSize.getWidth(), newImageHeight, true);
                }
            }
            return bitmap;
		} finally {
			imageStream.close();
		}
	}

	private Options getBitmapOptionsForImageDecoding(ImageSize targetSize, ImageScaleType scaleType, ScaleType viewScaleType) throws IOException {
		Options options = new Options();
		options.inSampleSize = computeImageScale(targetSize, scaleType, viewScaleType);
		return options;
	}

	private int computeImageScale(ImageSize targetSize, ImageScaleType scaleType, ScaleType viewScaleType) throws IOException {
		int targetWidth = targetSize.getWidth();
		int targetHeight = targetSize.getHeight();

		// decode image size
		Options options = new Options();
		options.inJustDecodeBounds = true;
		InputStream imageStream = imageDownloader.getStream(imageUri);
		try {
			BitmapFactory.decodeStream(imageStream, null, options);
		} finally {
			imageStream.close();
		}

		int scale = 1;
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		int widthScale = imageWidth / targetWidth;
		int heightScale = imageHeight / targetHeight;
		switch (viewScaleType) {
			case FIT_CENTER:
			case FIT_XY:
			case FIT_START:
			case FIT_END:
			case CENTER_INSIDE:
				switch (scaleType) {
					default:
					case POWER_OF_2:
						while (imageWidth / 2 >= targetWidth || imageHeight / 2 >= targetHeight) { // ||
							imageWidth /= 2;
							imageHeight /= 2;
							scale *= 2;
						}
						break;
					case EXACT:
						scale = Math.max(widthScale, heightScale); // max
						break;
				}
				break;
			case MATRIX:
			case CENTER:
			case CENTER_CROP:
			default:
				switch (scaleType) {
					default:
					case POWER_OF_2:
						while (imageWidth / 2 >= targetWidth && imageHeight / 2 >= targetHeight) { // &&
							imageWidth /= 2;
							imageHeight /= 2;
							scale *= 2;
						}
						break;
					case EXACT:
						scale = Math.min(widthScale, heightScale); // min
						break;
				}
		}

		if (scale < 1) {
			scale = 1;
		}

		return scale;
	}
}