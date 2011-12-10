package com.nostra13.universalimageloader.imageloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

/**
 * Decodes image from URL (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png") into {@link Bitmap}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class ImageDecoder {

	public static final String TAG = ImageDecoder.class.getSimpleName();

	private ImageDecoder() {
	}

	/**
	 * Decodes image to {@link Bitmap}. Image is scaled close to incoming {@link ImageSize image size} during decoding.
	 * Initial image size is reduced by the power of 2 (according Android recommendations)
	 * 
	 * @param imageUrl
	 * @param targetImageSize
	 * @return
	 * @throws IOException
	 */
	public static Bitmap decodeFile(URL imageUrl, ImageSize targetImageSize) throws IOException {
		InputStream is = imageUrl.openStream();
		Options decodeOptions = getBitmapOptionsForImageDecoding(is, targetImageSize);
		is.close();

		is = imageUrl.openStream();
		Bitmap result = decodeImageStream(is, decodeOptions);
		is.close();

		return result;
	}

	private static Options getBitmapOptionsForImageDecoding(InputStream imageStream, ImageSize targetImageSize) {
		Options options = new Options();
		options.inSampleSize = computeImageScale(imageStream, targetImageSize);
		return options;
	}

	private static int computeImageScale(InputStream imageStream, ImageSize targetImageSize) {
		int width = targetImageSize.width;
		int height = targetImageSize.height;

		// decode image size
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(imageStream, null, options);

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = options.outWidth;
		int height_tmp = options.outHeight;

		int scale = 1;
		while (true) {
			if (width_tmp / 2 < width || height_tmp / 2 < height) break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		return scale;
	}

	private static Bitmap decodeImageStream(InputStream imageStream, Options decodeOptions) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(imageStream, null, decodeOptions);
		} catch (Throwable th) {
			Log.e(TAG, "OUT OF MEMMORY: " + th.getMessage(), th);
		}
		return bitmap;
	}
}
