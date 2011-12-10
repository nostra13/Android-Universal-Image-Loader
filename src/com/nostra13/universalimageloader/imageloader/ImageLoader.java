package com.nostra13.universalimageloader.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.nostra13.universalimageloader.Constants;
import com.nostra13.universalimageloader.cache.Cache;
import com.nostra13.universalimageloader.cache.ImageCache;
import com.nostra13.universalimageloader.utils.FileUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * Singltone for image loading and displaying at {@link ImageView ImageViews}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private final Cache<String, Bitmap> bitmapCache = new ImageCache(Constants.MEMORY_CACHE_SIZE);
	private final File cacheDir;

	private ExecutorService photoLoaderExecutor;
	private final DisplayImageOptions defaultOptions = DisplayImageOptions.createSimple();

	private volatile static ImageLoader instance;

	/** Returns singletone class instance */
	public static ImageLoader getInstance(Context context) {
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader(context);
				}
			}
		}
		return instance;
	}

	private ImageLoader(Context context) {
		photoLoaderExecutor = Executors.newFixedThreadPool(Constants.THREAD_POOL_SIZE);
		cacheDir = StorageUtils.getCacheDirectory(context);
	}

	/**
	 * Adds display image task to queue. Image will be set to ImageView when it's turn. <br/>
	 * {@linkplain DisplayImageOptions Display image options} {@linkplain DisplayImageOptions#createForListView()
	 * appropriated for ListViews will be used}.
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 */
	public void displayImage(String url, ImageView imageView) {
		displayImage(url, imageView, defaultOptions, null);
	}

	/**
	 * Add display image task to queue. Image will be set to ImageView when it's turn.
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param options
	 *            {@link DisplayImageOptions Display options} for image displaying
	 */
	public void displayImage(String url, ImageView imageView, DisplayImageOptions options) {
		displayImage(url, imageView, options, null);
	}

	/**
	 * Add display image task to queue. Image will be set to ImageView when it's turn.
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param options
	 *            {@link DisplayImageOptions Display options} for image displaying
	 * @param listener
	 *            {@link ImageLoadingListener Listener} for image loading process. Listener fires events only if there
	 *            is no image for loading in memory cache. If there is image for loading in memory cache then image is
	 *            displayed at ImageView but listener does not fire any event. Listener fires events on UI thread.
	 */
	public void displayImage(String url, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
		if (url == null || url.length() == 0 || imageView == null) {
			return;
		}

		PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView, options, listener);

		Bitmap image = null;
		synchronized (bitmapCache) {
			image = bitmapCache.get(url);
		}

		if (image != null && !image.isRecycled()) {
			imageView.setImageBitmap(image);
		} else {
			queuePhoto(photoToLoad);
			if (options.isShowStubImage()) {
				imageView.setImageResource(options.getStubImage());
			} else {
				imageView.setImageBitmap(null);
			}
		}
	}

	/** Stops all running display image tasks, discards all other scheduled tasks */
	public void stop() {
		photoLoaderExecutor.shutdown();
	}

	public void clearMemoryCache() {
		synchronized (bitmapCache) {
			bitmapCache.clear();
		}
	}

	public void clearDiscCache() {
		File[] files = cacheDir.listFiles();
		for (File f : files) {
			f.delete();
		}
	}

	private void queuePhoto(PhotoToLoad photoToLoad) {
		if (photoToLoad.listener != null) {
			photoToLoad.listener.onLoadingStarted();
		}

		photoLoaderExecutor.submit(new PhotosLoader(photoToLoad));
	}

	private File getLocalImageFile(String imageUrl) {
		String fileName = String.valueOf(imageUrl.hashCode());
		return new File(cacheDir, fileName);
	}

	private Bitmap getBitmap(String imageUrl, ImageSize targetImageSize, boolean cacheImageOnDisc) {
		File f = getLocalImageFile(imageUrl);

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
				saveImageFromUrl(imageUrl, f);
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

	private void saveImageFromUrl(String imageUrl, File targetFile) throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(imageUrl).openConnection();
		conn.setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT);
		conn.setReadTimeout(Constants.HTTP_READ_TIMEOUT);
		InputStream is = conn.getInputStream();
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

	/**
	 * Compute image size for loading at memory (for memory economy).<br />
	 * Size computing algorithm:<br />
	 * 1) Gets maxWidth and maxHeight. If both of them are not set then go to step #2. (<i>this step is not working
	 * now</i>)</br > 2) Get layout_width and layout_height. If both of them are not set then go to step #3.</br > 3)
	 * Get device screen dimensions.
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
		return new ImageSize(width, height);
	}

	// Task for the queue
	private class PhotoToLoad {
		private String url;
		private ImageView imageView;
		private DisplayImageOptions options;
		private ImageLoadingListener listener;

		PhotoToLoad(String url, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
			imageView.setTag(Constants.TAG_KEY, url);
			this.url = url;
			this.imageView = imageView;
			this.options = options;
			this.listener = listener;
		}

		boolean isConsistent() {
			return url.equals(imageView.getTag(Constants.TAG_KEY));
		}
	}

	private class PhotosLoader implements Runnable {

		private PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (!photoToLoad.isConsistent()) {
				return;
			}
			// Load bitmap						
			ImageSize targetImageSize = getImageSizeScaleTo(photoToLoad.imageView);
			Bitmap bmp = getBitmap(photoToLoad.url, targetImageSize, photoToLoad.options.isCacheOnDisc());

			if (!photoToLoad.isConsistent() || bmp == null) {
				return;
			}
			// Cache bitmap in memory
			if (photoToLoad.options.isCacheInMemory()) {
				synchronized (bitmapCache) {
					bitmapCache.put(photoToLoad.url, bmp);
				}
			}

			// Display image in {@link ImageView} on UI thread
			BitmapDisplayer bd = new BitmapDisplayer(photoToLoad, bmp);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/** Used to display bitmap in the UI thread */
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(PhotoToLoad photoToLoad, Bitmap bitmap) {
			this.bitmap = bitmap;
			this.photoToLoad = photoToLoad;
		}

		public void run() {
			if (photoToLoad.isConsistent()) {
				photoToLoad.imageView.setImageBitmap(bitmap);
				// Notify listener
				if (photoToLoad.listener != null) {
					photoToLoad.listener.onLoadingComplete();
				}
			}
		}
	}
}