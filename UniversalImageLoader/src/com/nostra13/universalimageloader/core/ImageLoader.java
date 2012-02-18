package com.nostra13.universalimageloader.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.nostra13.universalimageloader.utils.FileUtils;

/**
 * Singletone for image loading and displaying at {@link ImageView ImageViews}<br />
 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before any other method.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (image URL and ImageView reference are required)";
	private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";
	private static final String ERROR_IMAGEVIEW_CONTEXT = "ImageView context must be of Activity type"
			+ "If you create ImageView in code you must pass your current activity in ImageView constructor (e.g. new ImageView(MyActivity.this); or new ImageView(getActivity())).";

	private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_INTERNET = "Load image from Internet [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_DISC_CACHE = "Load image from disc cache [%s]";
	private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
	private static final String LOG_CACHE_IMAGE_ON_DISC = "Cache image on disc [%s]";
	private static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView [%s]";

	private ImageLoaderConfiguration configuration;
	private ExecutorService imageLoadingExecutor;
	private ExecutorService cachedImageLoadingExecutor;
	private ImageLoadingListener emptyListener;

	private Map<ImageView, String> cacheKeyForImageView = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

	private boolean loggingEnabled = false;

	private volatile static ImageLoader instance;

	/** Returns singletone class instance */
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

	private ImageLoader() {
	}

	/**
	 * Initializes ImageLoader's singletone instance with configuration. Method shoiuld be called <b>once</b> (each
	 * following call will have no effect)<br />
	 * 
	 * @param configuration
	 *            {@linkplain ImageLoaderConfiguration ImageLoader configuration}
	 * @throws IllegalArgumentException
	 *             if <b>configuration</b> parameter is null
	 */
	public synchronized void init(ImageLoaderConfiguration configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
		}
		if (this.configuration == null) {
			this.configuration = configuration;
			emptyListener = new EmptyListener();
		}
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn. <br/>
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void displayImage(String url, ImageView imageView) {
		displayImage(url, imageView, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param options
	 *            {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> - default
	 *            display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void displayImage(String url, ImageView imageView, DisplayImageOptions options) {
		displayImage(url, imageView, options, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events only if
	 *            there is no image for loading in memory cache. If there is image for loading in memory cache then
	 *            image is displayed at ImageView but listener does not fire any event. Listener fires events on UI
	 *            thread.
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void displayImage(String url, ImageView imageView, ImageLoadingListener listener) {
		displayImage(url, imageView, null, listener);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param options
	 *            {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> - default
	 *            display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events only if
	 *            there is no image for loading in memory cache. If there is image for loading in memory cache then
	 *            image is displayed at ImageView but listener does not fire any event. Listener fires events on UI
	 *            thread.
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void displayImage(String url, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
		if (configuration == null) {
			throw new RuntimeException(ERROR_NOT_INIT);
		}
		if (url == null || url.length() == 0 || imageView == null) {
			Log.w(TAG, ERROR_WRONG_ARGUMENTS);
			return;
		}
		if (listener == null) {
			listener = emptyListener;
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		ImageSize targetSize = getImageSizeScaleTo(imageView);
		String memoryCacheKey = MemoryCacheKeyUtil.generateKey(url, targetSize);
		cacheKeyForImageView.put(imageView, memoryCacheKey);

		Bitmap bmp = configuration.memoryCache.get(memoryCacheKey);
		if (bmp != null && !bmp.isRecycled()) {
			if (loggingEnabled) Log.i(TAG, String.format(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, memoryCacheKey));
			listener.onLoadingStarted();
			imageView.setImageBitmap(bmp);
			listener.onLoadingComplete();
		} else {
			listener.onLoadingStarted();
			checkExecutors();

			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(url, imageView, targetSize, options, listener);
			DisplayImageTask displayImageTask = new DisplayImageTask(imageLoadingInfo);
			if (displayImageTask.isImageCachedOnDisc()) {
				cachedImageLoadingExecutor.submit(displayImageTask);
			} else {
				imageLoadingExecutor.submit(displayImageTask);
			}

			if (options.isShowStubImage()) {
				imageView.setImageResource(options.getStubImage());
			} else {
				imageView.setImageBitmap(null);
			}
		}
	}

	private void checkExecutors() {
		if (imageLoadingExecutor == null || imageLoadingExecutor.isShutdown()) {
			imageLoadingExecutor = Executors.newFixedThreadPool(configuration.threadPoolSize, configuration.displayImageThreadFactory);
		}
		if (cachedImageLoadingExecutor == null || cachedImageLoadingExecutor.isShutdown()) {
			cachedImageLoadingExecutor = Executors.newSingleThreadExecutor(configuration.displayImageThreadFactory);
		}
	}

	/** Stops all running display image tasks, discards all other scheduled tasks */
	public void stop() {
		if (imageLoadingExecutor != null) {
			imageLoadingExecutor.shutdown();
		}
		if (cachedImageLoadingExecutor != null) {
			cachedImageLoadingExecutor.shutdown();
		}
	}

	/**
	 * Clear memory cache.<br />
	 * Do nothing if {@link #init(ImageLoaderConfiguration)} method wasn't called before.
	 */
	public void clearMemoryCache() {
		if (configuration != null) {
			configuration.memoryCache.clear();
		}
	}

	/**
	 * Clear disc cache.<br />
	 * Do nothing if {@link #init(ImageLoaderConfiguration)} method wasn't called before.
	 */
	public void clearDiscCache() {
		if (configuration != null) {
			configuration.discCache.clear();
		}
	}

	/** Enables logging of loading image process (in LogCat) */
	public void enableLogging() {
		loggingEnabled = true;
	}

	/**
	 * Defines image size for loading at memory (for memory economy) by {@link ImageView} parameters.<br />
	 * Size computing algorithm:<br />
	 * 1) Get <b>maxWidth</b> and <b>maxHeight</b>. If both of them are not set then go to step #2.<br />
	 * 2) Get <b>layout_width</b> and <b>layout_height</b>. If both of them haven't exact value then go to step #3.</br>
	 * 3) Get device screen dimensions.
	 */
	private ImageSize getImageSizeScaleTo(ImageView imageView) {
		int width = -1;
		int height = -1;

		// Check maxWidth and maxHeight parameters
		try {
			Field maxWidthField = ImageView.class.getDeclaredField("mMaxWidth");
			Field maxHeightField = ImageView.class.getDeclaredField("mMaxHeight");
			maxWidthField.setAccessible(true);
			maxHeightField.setAccessible(true);
			int maxWidth = (Integer) maxWidthField.get(imageView);
			int maxHeight = (Integer) maxHeightField.get(imageView);

			if (maxWidth >= 0 && maxWidth < Integer.MAX_VALUE) {
				width = maxWidth;
			}
			if (maxHeight >= 0 && maxHeight < Integer.MAX_VALUE) {
				height = maxHeight;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		if (width < 0 && height < 0) {
			// Get layout width and height parameters
			LayoutParams params = imageView.getLayoutParams();
			width = params.width;
			height = params.height;
		}

		if (width < 0 && height < 0) {
			// Get device screen dimensions
			width = configuration.maxImageWidthForMemoryCache;
			height = configuration.maxImageHeightForMemoryCache;

			// Consider device screen orientation
			int screenOrientation = imageView.getContext().getResources().getConfiguration().orientation;
			if ((screenOrientation == Configuration.ORIENTATION_PORTRAIT && width > height)
					|| (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && width < height)) {
				int tmp = width;
				width = height;
				height = tmp;
			}
		}
		return new ImageSize(width, height);
	}

	/** Information about display image task */
	private final class ImageLoadingInfo {
		private final String url;
		private final ImageView imageView;
		private final ImageSize targetSize;
		private final DisplayImageOptions options;
		private final ImageLoadingListener listener;
		private final String memoryCacheKey;

		public ImageLoadingInfo(String url, ImageView imageView, ImageSize targetSize, DisplayImageOptions options, ImageLoadingListener listener) {
			this.url = url;
			this.imageView = imageView;
			this.targetSize = targetSize;
			this.options = options;
			this.listener = listener;
			memoryCacheKey = MemoryCacheKeyUtil.generateKey(url, targetSize);
		}

		/** Whether image URL of this task matches to URL which corresponds to current ImageView */
		boolean isConsistent() {
			String currentCacheKey = cacheKeyForImageView.get(imageView);
			// Entry with imageView could be collected by GC. In that case we also consider that current memory cache key (image URL) is right.
			return currentCacheKey == null || memoryCacheKey.equals(currentCacheKey);
		}
	}

	/**
	 * Presents display image task. Used to load image from Internet or file system, decode it to {@link Bitmap}, and
	 * display it in {@link ImageView} through {@link DisplayBitmapTask}.
	 */
	private class DisplayImageTask implements Runnable {

		private final ImageLoadingInfo imageLoadingInfo;

		public DisplayImageTask(ImageLoadingInfo imageLoadingInfo) {
			this.imageLoadingInfo = imageLoadingInfo;
		}

		@Override
		public void run() {
			if (loggingEnabled) Log.i(TAG, String.format(LOG_START_DISPLAY_IMAGE_TASK, imageLoadingInfo.memoryCacheKey));
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
				if (loggingEnabled) Log.i(TAG, String.format(LOG_CACHE_IMAGE_IN_MEMORY, imageLoadingInfo.memoryCacheKey));
				configuration.memoryCache.put(imageLoadingInfo.memoryCacheKey, bmp);
			}

			DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(imageLoadingInfo, bmp);
			tryRunOnUiThread(displayBitmapTask);
		}

		private Bitmap loadBitmap() {
			File f = configuration.discCache.get(imageLoadingInfo.url);

			Bitmap bitmap = null;
			try {
				// Try to load image from disc cache
				if (f.exists()) {
					if (loggingEnabled) Log.i(TAG, String.format(LOG_LOAD_IMAGE_FROM_DISC_CACHE, imageLoadingInfo.memoryCacheKey));
					Bitmap b = decodeImage(f.toURL());
					if (b != null) {
						return b;
					}
				}

				// Load image from Web
				if (loggingEnabled) Log.i(TAG, String.format(LOG_LOAD_IMAGE_FROM_INTERNET, imageLoadingInfo.memoryCacheKey));
				URL imageUrlForDecoding = null;
				if (imageLoadingInfo.options.isCacheOnDisc()) {
					if (loggingEnabled) Log.i(TAG, String.format(LOG_CACHE_IMAGE_ON_DISC, imageLoadingInfo.memoryCacheKey));
					saveImageOnDisc(f);
					imageUrlForDecoding = f.toURL();
				} else {
					imageUrlForDecoding = new URL(imageLoadingInfo.url);
				}

				bitmap = decodeImage(imageUrlForDecoding);

				if (imageLoadingInfo.options.isCacheOnDisc()) {
					configuration.discCache.put(imageLoadingInfo.url, f);
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
				fireImageLoadingFailedEvent(FailReason.IO_ERROR);
				if (f.exists()) {
					f.delete();
				}
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				fireImageLoadingFailedEvent(FailReason.UNKNOWN);
			}
			return bitmap;
		}

		private boolean isImageCachedOnDisc() {
			File f = configuration.discCache.get(imageLoadingInfo.url);
			return f.exists();
		}

		private Bitmap decodeImage(URL imageUrl) throws IOException {
			Bitmap bmp = null;
			try {
				ImageDecoder decoder = new ImageDecoder(imageUrl, imageLoadingInfo.targetSize, imageLoadingInfo.options.getDecodingType());
				bmp = decoder.decodeFile();
				decoder = null;
			} catch (OutOfMemoryError e) {
				Log.e(TAG, e.getMessage(), e);
				fireImageLoadingFailedEvent(FailReason.MEMORY_OVERFLOW);
			}
			return bmp;
		}

		private void saveImageOnDisc(File targetFile) throws MalformedURLException, IOException {
			HttpURLConnection conn = (HttpURLConnection) new URL(imageLoadingInfo.url).openConnection();
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

		private void fireImageLoadingFailedEvent(final FailReason reason) {
			tryRunOnUiThread(new Runnable() {
				@Override
				public void run() {
					imageLoadingInfo.listener.onLoadingFailed(reason);
				}
			});
		}

		private void tryRunOnUiThread(Runnable runnable) {
			Context context = imageLoadingInfo.imageView.getContext();
			if (context instanceof Activity) {
				((Activity) context).runOnUiThread(runnable);
			} else {
				Log.e(TAG, ERROR_IMAGEVIEW_CONTEXT);
				imageLoadingInfo.listener.onLoadingFailed(FailReason.WRONG_CONTEXT);
			}
		}
	}

	/** Used to display bitmap in {@link ImageView}. Must be called on UI thread. */
	private class DisplayBitmapTask implements Runnable {
		private final Bitmap bitmap;
		private final ImageLoadingInfo imageLoadingInfo;

		public DisplayBitmapTask(ImageLoadingInfo imageLoadingInfo, Bitmap bitmap) {
			this.bitmap = bitmap;
			this.imageLoadingInfo = imageLoadingInfo;
		}

		public void run() {
			if (imageLoadingInfo.isConsistent()) {
				if (loggingEnabled) Log.i(TAG, String.format(LOG_DISPLAY_IMAGE_IN_IMAGEVIEW, imageLoadingInfo.memoryCacheKey));
				imageLoadingInfo.imageView.setImageBitmap(bitmap);
				imageLoadingInfo.listener.onLoadingComplete();
			}
		}
	}

	private class EmptyListener implements ImageLoadingListener {
		@Override
		public void onLoadingStarted() {
		}

		@Override
		public void onLoadingFailed(FailReason failReason) {
		}

		@Override
		public void onLoadingComplete() {
		}
	}
}
