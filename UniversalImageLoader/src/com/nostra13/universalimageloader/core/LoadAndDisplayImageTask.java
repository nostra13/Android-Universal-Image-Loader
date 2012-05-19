package com.nostra13.universalimageloader.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
		if (!imageLoadingInfo.isConsistent()) {
			return;
		}

		Bitmap bmp = loadBitmap();
		if (bmp == null) {
			return;
		}
		if (!imageLoadingInfo.isConsistent()) {
			return;
		}

		if (imageLoadingInfo.options.isCacheInMemory()) {
			if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_CACHE_IMAGE_IN_MEMORY, imageLoadingInfo.memoryCacheKey));
			configuration.memoryCache.put(imageLoadingInfo.memoryCacheKey, bmp);
		}

		DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(configuration, imageLoadingInfo, bmp);
		handler.post(displayBitmapTask);
	}

	private Bitmap loadBitmap() {
		File f = configuration.discCache.get(imageLoadingInfo.url);

		Bitmap bitmap = null;
		try {
			// Try to load image from disc cache
			if (f.exists()) {
				if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_LOAD_IMAGE_FROM_DISC_CACHE, imageLoadingInfo.memoryCacheKey));
				Bitmap b = decodeImage(f.toURL());
				if (b != null) {
					return b;
				}
			}

			// Load image from Web
			if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_LOAD_IMAGE_FROM_INTERNET, imageLoadingInfo.memoryCacheKey));
			URL imageUrlForDecoding;
			if (imageLoadingInfo.options.isCacheOnDisc()) {
				if (configuration.loggingEnabled) Log.i(ImageLoader.TAG, String.format(LOG_CACHE_IMAGE_ON_DISC, imageLoadingInfo.memoryCacheKey));
				saveImageOnDisc(f);
				configuration.discCache.put(imageLoadingInfo.url, f);
				imageUrlForDecoding = f.toURL();
			} else {
				imageUrlForDecoding = new URL(imageLoadingInfo.url);
			}

			bitmap = decodeImage(imageUrlForDecoding);
		} catch (IOException e) {
			Log.e(ImageLoader.TAG, e.getMessage(), e);
			fireImageLoadingFailedEvent(FailReason.IO_ERROR);
			if (f.exists()) {
				f.delete();
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

	boolean isImageCachedOnDisc() {
		File f = configuration.discCache.get(imageLoadingInfo.url);
		return f.exists();
	}

	private Bitmap decodeImage(URL imageUrl) throws IOException {
		Bitmap bmp = null;
		ImageDecoder decoder = new ImageDecoder(imageUrl, imageLoadingInfo.targetSize, imageLoadingInfo.options.getDecodingType());

		if (configuration.handleOutOfMemory) {
			for (int attempt = 1; attempt <= ATTEMPT_COUNT_TO_DECODE_BITMAP; attempt++) {
				try {
					bmp = decoder.decodeFile();
					break;
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
		} else {
			bmp = decoder.decodeFile();
		}

		decoder = null;
		return bmp;
	}

	private void saveImageOnDisc(File targetFile) throws MalformedURLException, IOException {
		URLConnection conn = new URL(imageLoadingInfo.url).openConnection();
		conn.setConnectTimeout(configuration.httpConnectTimeout);
		conn.setReadTimeout(configuration.httpReadTimeout);
		BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
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
