package com.nostra13.universalimageloader.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.utils.FileUtils;

/**
 * Presents load'n'display image task. Used to load image from Internet or file system, decode it to {@link Bitmap}, and
 * display it in {@link ImageView} through {@link DisplayBitmapTask}.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoaderConfiguration
 * @see ImageLoadingInfo
 */
final class LoadAndDisplayImageTask implements Runnable {

	private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_INTERNET = "Load image from Internet [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_DISC_CACHE = "Load image from disc cache [%s]";
	private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
	private static final String LOG_CACHE_IMAGE_ON_DISC = "Cache image on disc [%s]";
	private static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView [%s]";

	private static final int ATTEMPT_COUNT_TO_DECODE_BITMAP = 3;

	private final ImageLoaderConfiguration configuration;
	private final ImageLoadingInfo imageLoadingInfo;
	private final Handler handler;

	public LoadAndDisplayImageTask(ImageLoaderConfiguration configuration, ImageLoadingInfo imageLoadingInfo, Handler handler) {
		this.configuration = configuration;
		this.imageLoadingInfo = imageLoadingInfo;
		this.handler = handler;
	}

	@Override
	public void run() {
		if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_START_DISPLAY_IMAGE_TASK, imageLoadingInfo.memoryCacheKey));

		if (!isTaskActual()) return;
		Bitmap bmp = loadBitmap();
		if (bmp == null) return;

		if (!isTaskActual()) return;
		if (imageLoadingInfo.options.isCacheInMemory()) {
			if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_CACHE_IMAGE_IN_MEMORY, imageLoadingInfo.memoryCacheKey));

			configuration.memoryCache.put(imageLoadingInfo.memoryCacheKey, bmp);
		}

		if (isTaskActual()) {
			if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_DISPLAY_IMAGE_IN_IMAGEVIEW, imageLoadingInfo.memoryCacheKey));

			DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(bmp, imageLoadingInfo.imageView, imageLoadingInfo.listener);
			handler.post(displayBitmapTask);
		}
	}

	/** Whether the image URL of this task matches to image URL which is actual for current ImageView at this moment */
	boolean isTaskActual() {
		String currentCacheKey = ImageLoader.getInstance().getLoadingUrlForView(imageLoadingInfo.imageView);
		// Check whether memory cache key (image URL) for current ImageView is actual.
		return imageLoadingInfo.memoryCacheKey.equals(currentCacheKey);
	}

	private Bitmap loadBitmap() {
		File imageFile = configuration.discCache.get(imageLoadingInfo.url);

		Bitmap bitmap = null;
		try {
			// Try to load image from disc cache
			if (imageFile.exists()) {
				if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_LOAD_IMAGE_FROM_DISC_CACHE, imageLoadingInfo.memoryCacheKey));

				Bitmap b = decodeImage(imageFile.toURL());
				if (b != null) {
					return b;
				}
			}

			// Load image from Web
			if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_LOAD_IMAGE_FROM_INTERNET, imageLoadingInfo.memoryCacheKey));

			URL imageUrlForDecoding;
			if (imageLoadingInfo.options.isCacheOnDisc()) {
				if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_CACHE_IMAGE_ON_DISC, imageLoadingInfo.memoryCacheKey));

				saveImageOnDisc(imageFile);
				configuration.discCache.put(imageLoadingInfo.url, imageFile);
				imageUrlForDecoding = imageFile.toURL();
			} else {
				imageUrlForDecoding = new URL(imageLoadingInfo.url);
			}

			bitmap = decodeImage(imageUrlForDecoding);
		} catch (IOException e) {
			Log.e(ImageLoader.TAG, e.getMessage(), e);
			fireImageLoadingFailedEvent(FailReason.IO_ERROR);
			if (imageFile.exists()) {
				imageFile.delete();
			}
		} catch (OutOfMemoryError e) {
			Log.e(ImageLoader.TAG, e.getMessage(), e);
			fireImageLoadingFailedEvent(FailReason.OUT_OF_MEMORY);
		} catch (Throwable e) {
			Log.e(ImageLoader.TAG, e.getMessage(), e);
			fireImageLoadingFailedEvent(FailReason.UNKNOWN);
		}
		return bitmap;
	}

	private Bitmap decodeImage(URL imageUrl) throws IOException {
		Bitmap bmp = null;
		ImageDecoder decoder = new ImageDecoder(imageUrl, configuration.downloader, imageLoadingInfo.targetSize, imageLoadingInfo.options.getDecodingType());

		if (configuration.handleOutOfMemory) {
			bmp = decodeWithOOMHandling(decoder);
		} else {
			bmp = decoder.decode();
		}

		decoder = null;
		return bmp;
	}

	private Bitmap decodeWithOOMHandling(ImageDecoder decoder) throws IOException {
		Bitmap result = null;
		for (int attempt = 1; attempt <= ATTEMPT_COUNT_TO_DECODE_BITMAP; attempt++) {
			try {
				result = decoder.decode();
			} catch (OutOfMemoryError e) {
				Log.e(ImageLoader.TAG, e.getMessage(), e);

				switch (attempt) {
					case 1:
						System.gc();
						break;
					case 2:
						configuration.memoryCache.clear();
						System.gc();
						break;
					case 3:
						throw e;
				}
				// Wait some time while GC is working
				try {
					Thread.sleep(attempt * 1000);
				} catch (InterruptedException ie) {
					Log.e(ImageLoader.TAG, ie.getMessage(), ie);
				}
			}
		}
		return result;
	}

	private void saveImageOnDisc(File targetFile) throws MalformedURLException, IOException {
		InputStream is = configuration.downloader.getStream(imageLoadingInfo.url);
		try {
			OutputStream os = new FileOutputStream(targetFile);
			try {
				FileUtils.copyStream(is, os);
			} finally {
				os.close();
			}
		} finally {
			is.close();
		}
	}

	private void fireImageLoadingFailedEvent(final FailReason failReason) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				imageLoadingInfo.listener.onLoadingFailed(failReason);
			}
		});
	}
}
