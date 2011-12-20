package com.nostra13.universalimageloader.imageloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.nostra13.universalimageloader.R;
import com.nostra13.universalimageloader.utils.FileUtils;

/**
 * Singletone for image loading and displaying at {@link ImageView ImageViews}<br />
 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before any other method.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageLoader {

	private static final String TAG = ImageLoader.class.getSimpleName();
	private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";
	private static final int IMAGE_TAG_KEY = R.id.tag_image_loader;

	private ImageLoaderConfiguration configuration;
	private ExecutorService imageLoadingExecutor;
	private ImageLoadingListener emptyListener;

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
			imageLoadingExecutor = Executors.newFixedThreadPool(configuration.threadPoolSize);
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
			return;
		}
		if (listener == null) {
			listener = emptyListener;
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}
		// Set specific tag to ImageView. This tag will be used to prevent load image from other URL into this ImageView.
		imageView.setTag(IMAGE_TAG_KEY, url);

		Bitmap bmp = configuration.memoryCache.get(url);
		if (bmp != null && !bmp.isRecycled()) {
			imageView.setImageBitmap(bmp);
		} else {
			listener.onLoadingStarted();
			if (imageLoadingExecutor.isShutdown()) {
				imageLoadingExecutor = Executors.newFixedThreadPool(configuration.threadPoolSize);
			}

			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(url, imageView, options, listener);
			imageLoadingExecutor.submit(new DisplayImageTask(imageLoadingInfo));

			if (options.isShowStubImage()) {
				imageView.setImageResource(options.getStubImage());
			} else {
				imageView.setImageBitmap(null);
			}
		}
	}

	/** Stops all running display image tasks, discards all other scheduled tasks */
	public void stop() {
		if (imageLoadingExecutor != null) {
			imageLoadingExecutor.shutdown();
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

	/** Information about display image task */
	private final class ImageLoadingInfo {
		private final String url;
		private final ImageView imageView;
		private final DisplayImageOptions options;
		private final ImageLoadingListener listener;

		public ImageLoadingInfo(String url, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
			this.url = url;
			this.imageView = imageView;
			this.options = options;
			this.listener = listener;
		}

		/** Whether current URL matches to URL from ImageView tag */
		boolean isConsistent() {
			return url.equals(imageView.getTag(IMAGE_TAG_KEY));
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
			if (!imageLoadingInfo.isConsistent()) {
				return;
			}
			// Load bitmap						
			ImageSize targetImageSize = getImageSizeScaleTo(imageLoadingInfo.imageView);
			Bitmap bmp = getBitmap(imageLoadingInfo.url, targetImageSize, imageLoadingInfo.options.isCacheOnDisc());
			if (bmp == null) {
				imageLoadingInfo.listener.onLoadingFailed();
				return;
			}

			if (!imageLoadingInfo.isConsistent()) {
				return;
			}
			// Cache bitmap in memory
			if (imageLoadingInfo.options.isCacheInMemory()) {
				configuration.memoryCache.put(imageLoadingInfo.url, bmp);
			}

			// Display image in {@link ImageView} on UI thread
			DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(imageLoadingInfo, bmp);
			Activity activity = (Activity) imageLoadingInfo.imageView.getContext();
			activity.runOnUiThread(displayBitmapTask);
		}

		/**
		 * Defines image size for loading at memory (for memory economy) by {@link ImageView} parameters.<br />
		 * Size computing algorithm:<br />
		 * 1) Get <b>maxWidth</b> and <b>maxHeight</b>. If both of them are not set then go to step #2.<br />
		 * 2) Get <b>layout_width</b> and <b>layout_height</b>. If both of them haven't exact value then go to step
		 * #3.</br> 3) Get device screen dimensions.
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

			// Get device screen dimensions
			if (width < 0 && height < 0) {
				width = configuration.maxImageWidthForMemoryCache;
				height = configuration.maxImageHeightForMemoryCache;
			}
			return new ImageSize(width, height);
		}

		private Bitmap getBitmap(String imageUrl, ImageSize targetImageSize, boolean cacheImageOnDisc) {
			File f = configuration.discCache.getFile(imageUrl);

			// Try to load image from disc cache
			try {
				if (f.exists()) {
					Bitmap b = ImageDecoder.decodeFile(f.toURL(), targetImageSize);
					if (b != null) {
						return b;
					}
				}
			} catch (IOException e) {
				// There is no image in disc cache. Do nothing
			}

			// Load image from Web
			Bitmap bitmap = null;
			try {
				URL imageUrlForDecoding = null;
				if (cacheImageOnDisc) {
					saveImageOnDisc(imageUrl, f);
					imageUrlForDecoding = f.toURL();
				} else {
					imageUrlForDecoding = new URL(imageUrl);
				}

				bitmap = ImageDecoder.decodeFile(imageUrlForDecoding, targetImageSize);
			} catch (Exception ex) {
				Log.e(TAG, String.format("Exception while loading bitmap from URL=%s : %s", imageUrl, ex.getMessage()), ex);
				if (f.exists()) {
					f.delete();
				}
			}
			return bitmap;
		}

		private void saveImageOnDisc(String imageUrl, File targetFile) throws MalformedURLException, IOException {
			HttpURLConnection conn = (HttpURLConnection) new URL(imageUrl).openConnection();
			conn.setConnectTimeout(configuration.httpConnectTimeout);
			conn.setReadTimeout(configuration.httpReadTimeout);
			BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
			try {
				OutputStream os = new FileOutputStream(targetFile);
				try {
					long start = System.currentTimeMillis();
					FileUtils.copyStream(is, os);
					long finish = System.currentTimeMillis();
					Log.i("NOSTRA", finish - start + " ms");
				} finally {
					os.close();
				}
			} finally {
				is.close();
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
		public void onLoadingFailed() {
		}

		@Override
		public void onLoadingComplete() {
		}
	}
}
