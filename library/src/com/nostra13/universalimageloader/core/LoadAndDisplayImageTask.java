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

import android.graphics.Bitmap;
import android.os.Handler;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.core.assist.*;
import com.nostra13.universalimageloader.core.assist.FailReason.FailType;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Presents load'n'display image task. Used to load image from Internet or file system, decode it to {@link Bitmap}, and
 * display it in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware} using {@link DisplayBitmapTask}.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoaderConfiguration
 * @see ImageLoadingInfo
 * @since 1.3.1
 */
final class LoadAndDisplayImageTask implements Runnable {

	private static final String LOG_WAITING_FOR_RESUME = "ImageLoader is paused. Waiting...  [%s]";
	private static final String LOG_RESUME_AFTER_PAUSE = ".. Resume loading [%s]";
	private static final String LOG_DELAY_BEFORE_LOADING = "Delay %d ms before loading...  [%s]";
	private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
	private static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
	private static final String LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING = "...Get cached bitmap from memory after waiting. [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_NETWORK = "Load image from network [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_DISC_CACHE = "Load image from disc cache [%s]";
	private static final String LOG_PREPROCESS_IMAGE = "PreProcess image before caching in memory [%s]";
	private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
	private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
	private static final String LOG_CACHE_IMAGE_ON_DISC = "Cache image on disc [%s]";
	private static final String LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISC = "Process image before cache on disc [%s]";
	private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
	private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";
	private static final String LOG_TASK_INTERRUPTED = "Task was interrupted [%s]";

	private static final String ERROR_PRE_PROCESSOR_NULL = "Pre-processor returned null [%s]";
	private static final String ERROR_POST_PROCESSOR_NULL = "Pre-processor returned null [%s]";
	private static final String ERROR_PROCESSOR_FOR_DISC_CACHE_NULL = "Bitmap processor for disc cache returned null [%s]";

	private static final int BUFFER_SIZE = 32 * 1024; // 32 Kb

	private final ImageLoaderEngine engine;
	private final ImageLoadingInfo imageLoadingInfo;
	private final Handler handler;

	// Helper references
	private final ImageLoaderConfiguration configuration;
	private final ImageDownloader downloader;
	private final ImageDownloader networkDeniedDownloader;
	private final ImageDownloader slowNetworkDownloader;
	private final ImageDecoder decoder;
	private final boolean writeLogs;
	final String uri;
	private final String memoryCacheKey;
	final ImageAware imageAware;
	private final ImageSize targetSize;
	final DisplayImageOptions options;
	final ImageLoadingListener listener;

	// State vars
	private LoadedFrom loadedFrom = LoadedFrom.NETWORK;
	private boolean imageAwareCollected = false;

	public LoadAndDisplayImageTask(ImageLoaderEngine engine, ImageLoadingInfo imageLoadingInfo, Handler handler) {
		this.engine = engine;
		this.imageLoadingInfo = imageLoadingInfo;
		this.handler = handler;

		configuration = engine.configuration;
		downloader = configuration.downloader;
		networkDeniedDownloader = configuration.networkDeniedDownloader;
		slowNetworkDownloader = configuration.slowNetworkDownloader;
		decoder = configuration.decoder;
		writeLogs = configuration.writeLogs;
		uri = imageLoadingInfo.uri;
		memoryCacheKey = imageLoadingInfo.memoryCacheKey;
		imageAware = imageLoadingInfo.imageAware;
		targetSize = imageLoadingInfo.targetSize;
		options = imageLoadingInfo.options;
		listener = imageLoadingInfo.listener;
	}

	@Override
	public void run() {
		if (waitIfPaused()) return;
		if (delayIfNeed()) return;

		ReentrantLock loadFromUriLock = imageLoadingInfo.loadFromUriLock;
		log(LOG_START_DISPLAY_IMAGE_TASK);
		if (loadFromUriLock.isLocked()) {
			log(LOG_WAITING_FOR_IMAGE_LOADED);
		}

		loadFromUriLock.lock();
		Bitmap bmp;
		try {
			if (checkTaskIsNotActual()) return;

			bmp = configuration.memoryCache.get(memoryCacheKey);
			if (bmp == null) {
				bmp = tryLoadBitmap();
				if (imageAwareCollected) return; // listener callback already was fired
				if (bmp == null) return; // listener callback already was fired

				if (checkTaskIsNotActual() || checkTaskIsInterrupted()) return;

				if (options.shouldPreProcess()) {
					log(LOG_PREPROCESS_IMAGE);
					bmp = options.getPreProcessor().process(bmp);
					if (bmp == null) {
						L.e(ERROR_PRE_PROCESSOR_NULL);
					}
				}

				if (bmp != null && options.isCacheInMemory()) {
					log(LOG_CACHE_IMAGE_IN_MEMORY);
					configuration.memoryCache.put(memoryCacheKey, bmp);
				}
			} else {
				loadedFrom = LoadedFrom.MEMORY_CACHE;
				log(LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING);
			}

			if (bmp != null && options.shouldPostProcess()) {
				log(LOG_POSTPROCESS_IMAGE);
				bmp = options.getPostProcessor().process(bmp);
				if (bmp == null) {
					L.e(ERROR_POST_PROCESSOR_NULL, memoryCacheKey);
				}
			}
		} finally {
			loadFromUriLock.unlock();
		}

		if (checkTaskIsNotActual() || checkTaskIsInterrupted()) return;

		DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(bmp, imageLoadingInfo, engine, loadedFrom);
		displayBitmapTask.setLoggingEnabled(writeLogs);
		if (options.isSyncLoading()) {
			displayBitmapTask.run();
		} else {
			handler.post(displayBitmapTask);
		}
	}

	/** @return true - if task should be interrupted; false - otherwise */
	private boolean waitIfPaused() {
		AtomicBoolean pause = engine.getPause();
		synchronized (pause) {
			if (pause.get()) {
				log(LOG_WAITING_FOR_RESUME);
				try {
					pause.wait();
				} catch (InterruptedException e) {
					L.e(LOG_TASK_INTERRUPTED, memoryCacheKey);
					return true;
				}
				log(LOG_RESUME_AFTER_PAUSE);
			}
		}
		return checkTaskIsNotActual();
	}

	/** @return true - if task should be interrupted; false - otherwise */
	private boolean delayIfNeed() {
		if (options.shouldDelayBeforeLoading()) {
			log(LOG_DELAY_BEFORE_LOADING, options.getDelayBeforeLoading(), memoryCacheKey);
			try {
				Thread.sleep(options.getDelayBeforeLoading());
			} catch (InterruptedException e) {
				L.e(LOG_TASK_INTERRUPTED, memoryCacheKey);
				return true;
			}
			return checkTaskIsNotActual();
		}
		return false;
	}

	/**
	 * Check whether target ImageAware wasn't collected by GC and the image URI of this task matches to image URI which is actual
	 * for current ImageAware at this moment and fire {@link ImageLoadingListener#onLoadingCancelled(String, android.view.View)}}
	 * event if it doesn't.
	 */
	private boolean checkTaskIsNotActual() {
		return checkViewCollected() || checkViewReused();
	}

	private boolean checkViewCollected() {
		if (imageAware.isCollected()) {
			imageAwareCollected = true;
			log(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED);
			fireCancelEvent();
			return true;
		}
		return false;
	}

	private boolean checkViewReused() {
		String currentCacheKey = engine.getLoadingUriForView(imageAware);
		// Check whether memory cache key (image URI) for current ImageAware is actual.
		// If ImageAware is reused for another task then current task should be cancelled.
		boolean imageAwareWasReused = !memoryCacheKey.equals(currentCacheKey);
		if (imageAwareWasReused) {
			log(LOG_TASK_CANCELLED_IMAGEAWARE_REUSED);
			fireCancelEvent();
		}
		return imageAwareWasReused;
	}

	/** Check whether the current task was interrupted */
	private boolean checkTaskIsInterrupted() {
		boolean interrupted = Thread.interrupted();
		if (interrupted) log(LOG_TASK_INTERRUPTED);
		return interrupted;
	}

	private Bitmap tryLoadBitmap() {
		File imageFile = getImageFileInDiscCache();

		Bitmap bitmap = null;
		try {
			if (imageFile.exists()) {
				log(LOG_LOAD_IMAGE_FROM_DISC_CACHE);

				loadedFrom = LoadedFrom.DISC_CACHE;
				bitmap = decodeImage(Scheme.FILE.wrap(imageFile.getAbsolutePath()));
				if (imageAwareCollected) return null;
			}
			if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
				log(LOG_LOAD_IMAGE_FROM_NETWORK);

				loadedFrom = LoadedFrom.NETWORK;
				String imageUriForDecoding = options.isCacheOnDisc() ? tryCacheImageOnDisc(imageFile) : uri;
				if (!checkTaskIsNotActual()) {
					bitmap = decodeImage(imageUriForDecoding);
					if (imageAwareCollected) return null;
					if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
						fireFailEvent(FailType.DECODING_ERROR, null);
					}
				}
			}
		} catch (IllegalStateException e) {
			fireFailEvent(FailType.NETWORK_DENIED, null);
		} catch (IOException e) {
			L.e(e);
			fireFailEvent(FailType.IO_ERROR, e);
			if (imageFile.exists()) {
				imageFile.delete();
			}
		} catch (OutOfMemoryError e) {
			L.e(e);
			fireFailEvent(FailType.OUT_OF_MEMORY, e);
		} catch (Throwable e) {
			L.e(e);
			fireFailEvent(FailType.UNKNOWN, e);
		}
		return bitmap;
	}

	private File getImageFileInDiscCache() {
		DiscCacheAware discCache = configuration.discCache;
		File imageFile = discCache.get(uri);
		File cacheDir = imageFile.getParentFile();
		if (cacheDir == null || (!cacheDir.exists() && !cacheDir.mkdirs())) {
			imageFile = configuration.reserveDiscCache.get(uri);
			cacheDir = imageFile.getParentFile();
			if (cacheDir != null && !cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		}
		return imageFile;
	}

	private Bitmap decodeImage(String imageUri) throws IOException {
		if (checkViewCollected()) return null;

		ViewScaleType viewScaleType = imageAware.getScaleType();
		if (viewScaleType == null) return null;
		ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey, imageUri, targetSize, viewScaleType, getDownloader(), options);
		return decoder.decode(decodingInfo);
	}

	/** @return Cached image URI; or original image URI if caching failed */
	private String tryCacheImageOnDisc(File targetFile) {
		log(LOG_CACHE_IMAGE_ON_DISC);

		try {
			int width = configuration.maxImageWidthForDiscCache;
			int height = configuration.maxImageHeightForDiscCache;
			boolean saved = false;
			if (width > 0 || height > 0) {
				saved = downloadSizedImage(targetFile, width, height);
			}
			if (!saved) {
				downloadImage(targetFile);
			}

			configuration.discCache.put(uri, targetFile);
			return Scheme.FILE.wrap(targetFile.getAbsolutePath());
		} catch (IOException e) {
			L.e(e);
			if (targetFile.exists()) {
				targetFile.delete();
			}
			return uri;
		}
	}

	private boolean downloadSizedImage(File targetFile, int maxWidth, int maxHeight) throws IOException {
		// Download, decode, compress and save image
		ImageSize targetImageSize = new ImageSize(maxWidth, maxHeight);
		DisplayImageOptions specialOptions = new DisplayImageOptions.Builder().cloneFrom(options)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
		ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey, uri, targetImageSize, ViewScaleType.FIT_INSIDE, getDownloader(), specialOptions);
		Bitmap bmp = decoder.decode(decodingInfo);
		if (bmp == null) return false;

		if (configuration.processorForDiscCache != null) {
			log(LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISC);
			bmp = configuration.processorForDiscCache.process(bmp);
			if (bmp == null) {
				L.e(ERROR_PROCESSOR_FOR_DISC_CACHE_NULL, memoryCacheKey);
				return false;
			}
		}

		OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile), BUFFER_SIZE);
		boolean savedSuccessfully;
		try {
			savedSuccessfully = bmp
					.compress(configuration.imageCompressFormatForDiscCache, configuration.imageQualityForDiscCache, os);
		} finally {
			IoUtils.closeSilently(os);
		}
		bmp.recycle();
		return savedSuccessfully;
	}

	private void downloadImage(File targetFile) throws IOException {
		InputStream is = getDownloader().getStream(uri, options.getExtraForDownloader());
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile), BUFFER_SIZE);
			try {
				IoUtils.copyStream(is, os);
			} finally {
				IoUtils.closeSilently(os);
			}
		} finally {
			IoUtils.closeSilently(is);
		}
	}

	private void fireFailEvent(final FailType failType, final Throwable failCause) {
		if (!Thread.interrupted()) {
			if (options.isSyncLoading()) {
				listener.onLoadingFailed(uri, imageAware.getWrappedView(), new FailReason(failType, failCause));
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (options.shouldShowImageOnFail()) {
							imageAware.setImageDrawable(options.getImageOnFail(configuration.resources));
						}
						listener.onLoadingFailed(uri, imageAware.getWrappedView(), new FailReason(failType, failCause));
					}
				});
			}
		}
	}

	private void fireCancelEvent() {
		if (!Thread.interrupted()) {
			if (options.isSyncLoading()) {
				listener.onLoadingCancelled(uri, imageAware.getWrappedView());
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						listener.onLoadingCancelled(uri, imageAware.getWrappedView());
					}
				});
			}
		}
	}

	private ImageDownloader getDownloader() {
		ImageDownloader d;
		if (engine.isNetworkDenied()) {
			d = networkDeniedDownloader;
		} else if (engine.isSlowNetwork()) {
			d = slowNetworkDownloader;
		} else {
			d = downloader;
		}
		return d;
	}

	String getLoadingUri() {
		return uri;
	}

	private void log(String message) {
		if (writeLogs) L.d(message, memoryCacheKey);
	}

	private void log(String message, Object... args) {
		if (writeLogs) L.d(message, args);
	}
}
