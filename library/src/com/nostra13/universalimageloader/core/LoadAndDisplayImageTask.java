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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.FailReason.FailType;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
final class LoadAndDisplayImageTask implements Runnable, IoUtils.CopyListener {

	private static final String LOG_WAITING_FOR_RESUME = "ImageLoader is paused. Waiting...  [%s]";
	private static final String LOG_RESUME_AFTER_PAUSE = ".. Resume loading [%s]";
	private static final String LOG_DELAY_BEFORE_LOADING = "Delay %d ms before loading...  [%s]";
	private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
	private static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
	private static final String LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING = "...Get cached bitmap from memory after waiting. [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_NETWORK = "Load image from network [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_DISC_CACHE = "Load image from disc cache [%s]";
	private static final String LOG_RESIZE_CACHED_IMAGE_FILE = "Resize image in disc cache [%s]";
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
	final ImageLoadingProgressListener progressListener;

	// State vars
	private LoadedFrom loadedFrom = LoadedFrom.NETWORK;

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
		progressListener = imageLoadingInfo.progressListener;
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
			checkTaskNotActual();

			bmp = configuration.memoryCache.get(memoryCacheKey);
			if (bmp == null) {
				bmp = tryLoadBitmap();
				if (bmp == null) return; // listener callback already was fired

				checkTaskNotActual();
				checkTaskInterrupted();

				if (options.shouldPreProcess()) {
					log(LOG_PREPROCESS_IMAGE);
					bmp = options.getPreProcessor().process(bmp);
					if (bmp == null) {
						L.e(ERROR_PRE_PROCESSOR_NULL, memoryCacheKey);
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
			checkTaskNotActual();
			checkTaskInterrupted();
		} catch (TaskCancelledException e) {
			fireCancelEvent();
			return;
		} finally {
			loadFromUriLock.unlock();
		}

		DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(bmp, imageLoadingInfo, engine, loadedFrom);
		displayBitmapTask.setLoggingEnabled(writeLogs);
		if (options.isSyncLoading()) {
			displayBitmapTask.run();
		} else {
			handler.post(displayBitmapTask);
		}
	}

	/** @return <b>true</b> - if task should be interrupted; <b>false</b> - otherwise */
	private boolean waitIfPaused() {
		AtomicBoolean pause = engine.getPause();
		if (pause.get()) {
			synchronized (engine.getPauseLock()) {
				if (pause.get()) {
					log(LOG_WAITING_FOR_RESUME);
					try {
						engine.getPauseLock().wait();
					} catch (InterruptedException e) {
						L.e(LOG_TASK_INTERRUPTED, memoryCacheKey);
						return true;
					}
					log(LOG_RESUME_AFTER_PAUSE);
				}
			}
		}
		return isTaskNotActual();
	}

	/** @return <b>true</b> - if task should be interrupted; <b>false</b> - otherwise */
	private boolean delayIfNeed() {
		if (options.shouldDelayBeforeLoading()) {
			log(LOG_DELAY_BEFORE_LOADING, options.getDelayBeforeLoading(), memoryCacheKey);
			try {
				Thread.sleep(options.getDelayBeforeLoading());
			} catch (InterruptedException e) {
				L.e(LOG_TASK_INTERRUPTED, memoryCacheKey);
				return true;
			}
			return isTaskNotActual();
		}
		return false;
	}

	private Bitmap tryLoadBitmap() throws TaskCancelledException {
		Bitmap bitmap = null;
		try {
			File imageFile = configuration.discCache.get(uri);
			if (imageFile != null && imageFile.exists()) {
				log(LOG_LOAD_IMAGE_FROM_DISC_CACHE);
				loadedFrom = LoadedFrom.DISC_CACHE;

				checkTaskNotActual();
				bitmap = decodeImage(Scheme.FILE.wrap(imageFile.getAbsolutePath()));
			}
			if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
				log(LOG_LOAD_IMAGE_FROM_NETWORK);
				loadedFrom = LoadedFrom.NETWORK;

				String imageUriForDecoding = uri;
				if (options.isCacheOnDisc() && tryCacheImageOnDisc()) {
					imageFile = configuration.discCache.get(uri);
					if (imageFile != null) {
						imageUriForDecoding = Scheme.FILE.wrap(imageFile.getAbsolutePath());
					}
				}

				checkTaskNotActual();
				bitmap = decodeImage(imageUriForDecoding);

				if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
					fireFailEvent(FailType.DECODING_ERROR, null);
				}
			}
		} catch (IllegalStateException e) {
			fireFailEvent(FailType.NETWORK_DENIED, null);
		} catch (TaskCancelledException e) {
			throw e;
		} catch (IOException e) {
			L.e(e);
			fireFailEvent(FailType.IO_ERROR, e);
		} catch (OutOfMemoryError e) {
			L.e(e);
			fireFailEvent(FailType.OUT_OF_MEMORY, e);
		} catch (Throwable e) {
			L.e(e);
			fireFailEvent(FailType.UNKNOWN, e);
		}
		return bitmap;
	}

	private Bitmap decodeImage(String imageUri) throws IOException {
		ViewScaleType viewScaleType = imageAware.getScaleType();
		ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey, imageUri, targetSize, viewScaleType,
				getDownloader(), options);
		return decoder.decode(decodingInfo);
	}

	/** @return <b>true</b> - if image was downloaded successfully; <b>false</b> - otherwise */
	private boolean tryCacheImageOnDisc() throws TaskCancelledException {
		log(LOG_CACHE_IMAGE_ON_DISC);

		boolean loaded;
		try {
			loaded = downloadImage();
			if (loaded) {
				int width = configuration.maxImageWidthForDiscCache;
				int height = configuration.maxImageHeightForDiscCache;
				if (width > 0 || height > 0) {
					log(LOG_RESIZE_CACHED_IMAGE_FILE);
					resizeAndSaveImage(width, height); // TODO : process boolean result
				}
			}
		} catch (IOException e) {
			L.e(e);
			loaded = false;
		}
		return loaded;
	}

	private boolean downloadImage() throws IOException {
		InputStream is = getDownloader().getStream(uri, options.getExtraForDownloader());
		return configuration.discCache.save(uri, is, this);
	}

	/** Decodes image file into Bitmap, resize it and save it back */
	private boolean resizeAndSaveImage(int maxWidth, int maxHeight) throws IOException {
		// Decode image file, compress and re-save it
		boolean saved = false;
		File targetFile = configuration.discCache.get(uri);
		if (targetFile != null && targetFile.exists()) {
			ImageSize targetImageSize = new ImageSize(maxWidth, maxHeight);
			DisplayImageOptions specialOptions = new DisplayImageOptions.Builder().cloneFrom(options)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
			ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey,
					Scheme.FILE.wrap(targetFile.getAbsolutePath()), targetImageSize, ViewScaleType.FIT_INSIDE,
					getDownloader(), specialOptions);
			Bitmap bmp = decoder.decode(decodingInfo);
			if (bmp != null && configuration.processorForDiscCache != null) {
				log(LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISC);
				bmp = configuration.processorForDiscCache.process(bmp);
				if (bmp == null) {
					L.e(ERROR_PROCESSOR_FOR_DISC_CACHE_NULL, memoryCacheKey);
				}
			}
			if (bmp != null) {
				saved = configuration.discCache.save(uri, bmp, configuration.imageCompressFormatForDiscCache,
						configuration.imageQualityForDiscCache);
				bmp.recycle();
			}
		}
		return saved;
	}

	@Override
	public boolean onBytesCopied(int current, int total) {
		return progressListener == null || fireProgressEvent(current, total);
	}

	/** @return <b>true</b> - if loading should be continued; <b>false</b> - if loading should be interrupted */
	private boolean fireProgressEvent(final int current, final int total) {
		if (options.isSyncLoading() || isTaskInterrupted() || isTaskNotActual()) return false;
		handler.post(new Runnable() {
			@Override
			public void run() {
				progressListener.onProgressUpdate(uri, imageAware.getWrappedView(), current, total);
			}
		});
		return true;
	}

	private void fireFailEvent(final FailType failType, final Throwable failCause) {
		if (options.isSyncLoading() || isTaskInterrupted() || isTaskNotActual()) return;
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

	private void fireCancelEvent() {
		if (options.isSyncLoading() || isTaskInterrupted()) return;
		handler.post(new Runnable() {
			@Override
			public void run() {
				listener.onLoadingCancelled(uri, imageAware.getWrappedView());
			}
		});
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

	/**
	 * @throws TaskCancelledException if task is not actual (target ImageAware is collected by GC or the image URI of
	 *                                this task doesn't match to image URI which is actual for current ImageAware at
	 *                                this moment)
	 */
	private void checkTaskNotActual() throws TaskCancelledException {
		checkViewCollected();
		checkViewReused();
	}

	/**
	 * @return <b>true</b> - if task is not actual (target ImageAware is collected by GC or the image URI of this task
	 * doesn't match to image URI which is actual for current ImageAware at this moment)); <b>false</b> - otherwise
	 */
	private boolean isTaskNotActual() {
		return isViewCollected() || isViewReused();
	}

	/** @throws TaskCancelledException if target ImageAware is collected */
	private void checkViewCollected() throws TaskCancelledException {
		if (isViewCollected()) {
			throw new TaskCancelledException();
		}
	}

	/** @return <b>true</b> - if target ImageAware is collected by GC; <b>false</b> - otherwise */
	private boolean isViewCollected() {
		if (imageAware.isCollected()) {
			log(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED);
			return true;
		}
		return false;
	}

	/** @throws TaskCancelledException if target ImageAware is collected by GC */
	private void checkViewReused() throws TaskCancelledException {
		if (isViewReused()) {
			throw new TaskCancelledException();
		}
	}

	/** @return <b>true</b> - if current ImageAware is reused for displaying another image; <b>false</b> - otherwise */
	private boolean isViewReused() {
		String currentCacheKey = engine.getLoadingUriForView(imageAware);
		// Check whether memory cache key (image URI) for current ImageAware is actual.
		// If ImageAware is reused for another task then current task should be cancelled.
		boolean imageAwareWasReused = !memoryCacheKey.equals(currentCacheKey);
		if (imageAwareWasReused) {
			log(LOG_TASK_CANCELLED_IMAGEAWARE_REUSED);
			return true;
		}
		return false;
	}

	/** @throws TaskCancelledException if current task was interrupted */
	private void checkTaskInterrupted() throws TaskCancelledException {
		if (isTaskInterrupted()) {
			throw new TaskCancelledException();
		}
	}

	/** @return <b>true</b> - if current task was interrupted; <b>false</b> - otherwise */
	private boolean isTaskInterrupted() {
		if (Thread.interrupted()) {
			log(LOG_TASK_INTERRUPTED);
			return true;
		}
		return false;
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

	/**
	 * Exceptions for case when task is cancelled (thread is interrupted, image view is reused for another task, view is
	 * collected by GC).
	 *
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 * @since 1.9.1
	 */
	class TaskCancelledException extends Exception {
	}
}
