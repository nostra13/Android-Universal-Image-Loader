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

import java.lang.reflect.Field;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FakeBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

/**
 * Singletone for image loading and displaying at {@link ImageView ImageViews}<br />
 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before any other method.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();
	static final String LOG_WAITING_FOR_RESUME = "ImageLoader is paused. Waiting...  [%s]";
	static final String LOG_RESUME_AFTER_PAUSE = ".. Resume loading [%s]";
	static final String LOG_DELAY_BEFORE_LOADING = "Delay %d ms before loading...  [%s]";
	static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
	static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
	static final String LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING = "...Get cached bitmap from memory after waiting. [%s]";
	static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";
	static final String LOG_LOAD_IMAGE_FROM_INTERNET = "Load image from Internet [%s]";
	static final String LOG_LOAD_IMAGE_FROM_DISC_CACHE = "Load image from disc cache [%s]";
	static final String LOG_IMAGE_SUBSAMPLING = "Original image (%1$dx%2$d) is going to be subsampled to %3$dx%4$d view. Computed scale size - %5$d";
	static final String LOG_IMAGE_SCALED = "Subsampled image (%1$dx%2$d) was scaled to %3$dx%4$d";
	static final String LOG_PREPROCESS_IMAGE = "PreProcess image before caching in memory [%s]";
	static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
	static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
	static final String LOG_CACHE_IMAGE_ON_DISC = "Cache image on disc [%s]";
	static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView [%s]";
	static final String LOG_TASK_CANCELLED = "ImageView is reused for another image. Task is cancelled. [%s]";
	static final String LOG_TASK_INTERRUPTED = "Task was interrupted [%s]";
	static final String LOG_CANT_DECODE_IMAGE = "Image can't be decoded [%s]";
	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference are required)";
	private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";
	private volatile static ImageLoader instance;

	/** Returns singleton class instance */
	public static ImageLoader getInstance() {
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader();
				}
			}
		}
		return instance;
	}
	private final ImageLoadingListener emptyListener = new SimpleImageLoadingListener();
	private final BitmapDisplayer fakeBitmapDisplayer = new FakeBitmapDisplayer();
	private ImageLoaderConfiguration configuration;
	private ImageLoaderEngine engine;

	protected ImageLoader() {
	}

	/**
	 * Initializes ImageLoader's singleton instance with configuration. Method should be called <b>once</b> (each
	 * following call will have no effect)<br />
	 *
	 * @param configuration {@linkplain ImageLoaderConfiguration ImageLoader configuration}
	 * @throws IllegalArgumentException if <b>configuration</b> parameter is null
	 */
	public synchronized void init(ImageLoaderConfiguration configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
		}
		if (this.configuration == null) {
			engine = new ImageLoaderEngine(configuration);
			this.configuration = configuration;
		}
	}

	/**
	 * Returns <b>true</b> - if ImageLoader {@linkplain #init(ImageLoaderConfiguration) is initialized with
	 * configuration}; <b>false</b> - otherwise
	 */
	public boolean isInited() {
		return configuration != null;
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn. <br/>
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView) {
		displayImage(uri, imageView, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param options {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> -
	 *            default display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
		displayImage(uri, imageView, options, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
		displayImage(uri, imageView, null, listener);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param options {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> -
	 *            default display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
		checkConfiguration();
		if (imageView == null) {
			throw new IllegalArgumentException(ERROR_WRONG_ARGUMENTS);
		}
		if (listener == null) {
			listener = emptyListener;
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		if (uri == null || uri.length() == 0) {
			engine.cancelDisplayTaskFor(imageView);
			listener.onLoadingStarted(uri, imageView);
			if (options.shouldShowImageForEmptyUri()) {
				imageView.setImageResource(options.getImageForEmptyUri());
			} else {
				imageView.setImageBitmap(null);
			}
			listener.onLoadingComplete(uri, imageView, null);
			return;
		}

        final DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		final ImageSize targetSize = ImageSize.getImageSizeScaleTo(imageView, configuration, displayMetrics);
		String memoryCacheKey = MemoryCacheUtil.generateKey(uri, targetSize);
		engine.prepareDisplayTaskFor(imageView, memoryCacheKey);

		listener.onLoadingStarted(uri, imageView);
		Bitmap bmp = configuration.memoryCache.get(memoryCacheKey);
		if (bmp != null && !bmp.isRecycled()) {
			if (configuration.loggingEnabled) L.i(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, memoryCacheKey);

			if (options.shouldPostProcess()) {
				ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageView, targetSize, options, listener, engine.getLockForUri(uri));
				ProcessAndDisplayImageTask displayTask = new ProcessAndDisplayImageTask(engine, bmp, imageLoadingInfo, new Handler());
				engine.submit(displayTask);
			} else {
				options.getDisplayer().display(bmp, imageView);
				listener.onLoadingComplete(uri, imageView, bmp);
			}
		} else {
			if (options.shouldShowStubImage()) {
				imageView.setImageResource(options.getStubImage());
			} else {
				if (options.isResetViewBeforeLoading()) {
					imageView.setImageBitmap(null);
				}
			}

			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageView, targetSize, options, listener, engine.getLockForUri(uri));
			LoadAndDisplayImageTask displayTask = new LoadAndDisplayImageTask(engine, imageLoadingInfo, new Handler());
			engine.submit(displayTask);
		}
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageLoadingListener listener) {
		loadImage(uri, null, null, listener);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param minImageSize Minimal size for {@link Bitmap} which will be returned in
	 *            {@linkplain ImageLoadingListener#onLoadingComplete(Bitmap) callback}. Downloaded image will be decoded
	 *            and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit larger) than
	 *            incoming minImageSize .
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageSize minImageSize, ImageLoadingListener listener) {
		loadImage(uri, minImageSize, null, listener);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param options {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> -
	 *            default display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.<br />
	 *            Incoming options should contain {@link FakeBitmapDisplayer} as displayer.
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener) {
		loadImage(uri, null, options, listener);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param targetImageSize Minimal size for {@link Bitmap} which will be returned in
	 *            {@linkplain ImageLoadingListener#onLoadingComplete(Bitmap) callback}. Downloaded image will be decoded
	 *            and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit larger) than
	 *            incoming minImageSize .
	 * @param options {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> -
	 *            default display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.<br />
	 *            Incoming options should contain {@link FakeBitmapDisplayer} as displayer.
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener) {
		checkConfiguration();
		if (targetImageSize == null) {
			targetImageSize = new ImageSize(configuration.maxImageWidthForMemoryCache, configuration.maxImageHeightForMemoryCache);
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		DisplayImageOptions optionsWithFakeDisplayer;
		if (options.getDisplayer() instanceof FakeBitmapDisplayer) {
			optionsWithFakeDisplayer = options;
		} else {
			optionsWithFakeDisplayer = new DisplayImageOptions.Builder().cloneFrom(options).displayer(fakeBitmapDisplayer).build();
		}

		ImageView fakeImage = new ImageView(configuration.context);
		fakeImage.setLayoutParams(new LayoutParams(targetImageSize.getWidth(), targetImageSize.getHeight()));
		fakeImage.setScaleType(ScaleType.CENTER_CROP);

		displayImage(uri, fakeImage, optionsWithFakeDisplayer, listener);
	}

	/**
	 * Checks if ImageLoader's configuration was initialized
	 *
	 * @throws IllegalStateException if configuration wasn't initialized
	 */
	private void checkConfiguration() {
		if (configuration == null) {
			throw new IllegalStateException(ERROR_NOT_INIT);
		}
	}

	/**
	 * Returns memory cache
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public MemoryCacheAware<String, Bitmap> getMemoryCache() {
		checkConfiguration();
		return configuration.memoryCache;
	}

	/**
	 * Clears memory cache
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void clearMemoryCache() {
		checkConfiguration();
		configuration.memoryCache.clear();
	}

	/**
	 * Returns disc cache
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public DiscCacheAware getDiscCache() {
		checkConfiguration();
		return configuration.discCache;
	}

	/**
	 * Clears disc cache.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void clearDiscCache() {
		checkConfiguration();
		configuration.discCache.clear();
	}

	/** Returns URI of image which is loading at this moment into passed {@link ImageView} */
	public String getLoadingUriForView(ImageView imageView) {
		return engine.getLoadingUriForView(imageView);
	}

	/**
	 * Cancel the task of loading and displaying image for passed {@link ImageView}.
	 *
	 * @param imageView {@link ImageView} for which display task will be cancelled
	 */
	public void cancelDisplayTask(ImageView imageView) {
		engine.cancelDisplayTaskFor(imageView);
	}

	/**
	 * Denies ImageLoader to download images from network. If image isn't cached then
	 * {@link ImageLoadingListener#onLoadingFailed(String, View, FailReason)} callback was fired with
	 * {@link FailReason#NETWORK_DENIED}
	 */
	public void denyNetworkDownloads() {
		engine.denyNetworkDownloads();
	}

	/** Allows ImageLoader to download images from network. */
	public void allowNetworkDownloads() {
		engine.allowNetworkDownloads();
	}

	/**
	 * Pause ImageLoader. All new "load&display" tasks won't be executed until ImageLoader is {@link #resume() resumed}.<br />
	 * Already running tasks are not paused.
	 */
	public void pause() {
		engine.pause();
	}

	/** Resumes waiting "load&display" tasks */
	public void resume() {
		engine.resume();
	}

	/** Stops all running display image tasks, discards all other scheduled tasks */
	public void stop() {
		engine.stop();
	}

}