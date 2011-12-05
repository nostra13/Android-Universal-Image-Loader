package com.nostra13.universalimageloader.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Stack;

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

	private final PhotosQueue photosQueue = new PhotosQueue();
	private final PhotosLoader photoLoaderThread = new PhotosLoader();

	private static ImageLoader instance = null;

	/** Returns singletone class instance */
	public static ImageLoader getInstance(Context context) {
		if (instance == null) {
			instance = new ImageLoader(context);
		}
		return instance;
	}

	private ImageLoader(Context context) {
		// Make the background thread low priority. This way it will not affect the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		// Find the directory to save cached images
		cacheDir = StorageUtils.getCacheDirectory(context);
	}

	/**
	 * Adds display image task to queue. Image will be set to ImageView when it's turn. <br/>
	 * {@linkplain DisplayImageOptions Display image options} {@linkplain DisplayImageOptions#createForListView()
	 * appropriated for ListViews will be used}.
	 * 
	 * @param url
	 *            Image URI (i.e. "http://site.com/image.png", "file://mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 */
	public void displayImage(String url, ImageView imageView) {
		displayImage(url, imageView, DisplayImageOptions.createForListView(), null);
	}

	/**
	 * Add display image task to queue. Image will be set to ImageView when it's turn.
	 * 
	 * @param url
	 *            Image URI (i.e. "http://site.com/image.png", "file://mnt/sdcard/image.png")
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
	 *            Image URI (i.e. "http://site.com/image.png", "file://mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param options
	 *            {@link DisplayImageOptions Display options} for image displaying
	 * @param listener
	 *            {@link ImageLoadingListener Listener} for image loading process. Listener fire events only if there is
	 *            no image for loading in memory cache. If there is image for loading in memory cache then image is
	 *            displayed at ImageView but listener does not fire any event.
	 */
	public void displayImage(String url, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
		if (url == null || url.length() == 0) {
			return;
		}
		imageView.setTag(url);

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
				imageView.setImageResource(Constants.STUB_IMAGE);
			} else {
				imageView.setImageBitmap(null);
			}
		}
	}

	private void queuePhoto(PhotoToLoad photoToLoad) {
		if (photoToLoad.listener != null) {
			photoToLoad.listener.onLoadingStarted();
		}

		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.clean(photoToLoad.imageView);

		// Make a two queues for split loading of cached on file system images and loading from web
		// It will reduce the time of waiting to display cached images (they will be displayed first)
		if (isCachedImage(photoToLoad.url)) {
			synchronized (photosQueue.photosToLoadCached) {
				photosQueue.photosToLoadCached.push(photoToLoad);
			}
		} else {
			synchronized (photosQueue.photosToLoad) {
				photosQueue.photosToLoad.push(photoToLoad);
			}
		}
		synchronized (photosQueue.lock) {
			photosQueue.lock.notifyAll();
		}

		// Start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW) {
			photoLoaderThread.start();
		}
	}

	private boolean isCachedImage(String url) {
		boolean result = false;
		File f = getLocalImageFile(url);

		try {
			result = f.exists();
		} catch (Exception e) {
		}

		return result;
	}

	private File getLocalImageFile(String imageUrl) {
		String fileName = String.valueOf(imageUrl.hashCode());
		return new File(cacheDir, fileName);
	}

	private Bitmap getBitmap(String imageUrl, ImageSize targetImageSize, boolean cacheImageOnDisc) {
		File f = getLocalImageFile(imageUrl);

		// try to load from SD cache
		try {
			if (f.exists()) {
				Bitmap b = ImageDecoder.decodeFile(f.toURL(), targetImageSize);
				if (b != null) {
					return b;
				}
			}
		} catch (IOException e) {
			// no image in SD cache
			// Do nothing
		}

		// from web
		Bitmap bitmap = null;
		try {
			URL imageUrlForDecoding = null;
			if (cacheImageOnDisc) {
				InputStream is = new URL(imageUrl).openStream();
				OutputStream os = new FileOutputStream(f);
				FileUtils.copyStream(is, os);
				is.close();
				os.close();
				imageUrlForDecoding = f.toURL();
			} else {
				imageUrlForDecoding = new URL(imageUrl);
			}

			bitmap = ImageDecoder.decodeFile(imageUrlForDecoding, targetImageSize);
		} catch (Exception ex) {
			Log.e(TAG, String.format("Exception while loading bitmap from URL=%s : %s", imageUrl, ex.getMessage()), ex);
		}
		return bitmap;
	}

	public void stopThread() {
		photoLoaderThread.interrupt();
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

	public void clearMemoryCache() {
		synchronized (bitmapCache) {
			bitmapCache.clear();
		}
	}

	public void clearDiscCache() {
		File[] files = cacheDir.listFiles();
		for (File f : files)
			f.delete();
	}

	// Task for the queue
	private class PhotoToLoad {
		private String url;
		private ImageView imageView;
		private DisplayImageOptions options;
		private ImageLoadingListener listener;

		public PhotoToLoad(String url, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
			this.url = url;
			this.imageView = imageView;
			this.options = options;
			this.listener = listener;
		}
	}

	// stores list of photos to download
	class PhotosQueue {
		private Object lock = new Object();

		private final Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();
		private final Stack<PhotoToLoad> photosToLoadCached = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size();) {
				if (photosToLoad.get(j).imageView == image) {
					photosToLoad.remove(j);
				} else {
					++j;
				}
			}

			for (int j = 0; j < photosToLoadCached.size();) {
				if (photosToLoadCached.get(j).imageView == image) {
					photosToLoadCached.remove(j);
				} else {
					++j;
				}
			}
		}
	}

	class PhotosLoader extends Thread {
		@Override
		public void run() {
			while (true) {
				PhotoToLoad photoToLoad = null;
				Bitmap bmp = null;
				try {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.isEmpty() && photosQueue.photosToLoadCached.isEmpty())
						synchronized (photosQueue.lock) {
							photosQueue.lock.wait();
						}
					if (!photosQueue.photosToLoadCached.isEmpty()) {
						synchronized (photosQueue.photosToLoadCached) {
							photoToLoad = photosQueue.photosToLoadCached.pop();
						}
					} else if (!photosQueue.photosToLoad.isEmpty()) {
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
					}

					if (photoToLoad != null) {
						ImageSize targetImageSize = getImageSizeScaleTo(photoToLoad.imageView);
						bmp = getBitmap(photoToLoad.url, targetImageSize, photoToLoad.options.isCacheOnDisc());
						if (bmp == null) {
							continue;
						}
						if (photoToLoad.options.isCacheInMemory()) {
							synchronized (bitmapCache) {
								bitmapCache.put(photoToLoad.url, bmp);
							}
						}
					}

					if (Thread.interrupted()) {
						break;
					}
				} catch (InterruptedException e) {
					Log.e(TAG, "" + e.getMessage());
				} finally {
					if (photoToLoad != null) {
						BitmapDisplayer bd = new BitmapDisplayer(photoToLoad, bmp);
						Activity a = (Activity) photoToLoad.imageView.getContext();
						a.runOnUiThread(bd);
					}
				}
			}
		}
	}

	/** Used to display bitmap in the UI thread */
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(PhotoToLoad photoToLoad, Bitmap bitmap) {
			this.bitmap = bitmap;
			this.photoToLoad = photoToLoad;
		}

		public void run() {
			String tag = (String) photoToLoad.imageView.getTag();

			if (photoToLoad != null && tag != null && tag.equals(photoToLoad.url) && bitmap != null) {
				photoToLoad.imageView.setImageBitmap(bitmap);
				if (photoToLoad.listener != null) {
					photoToLoad.listener.onLoadingComplete();
				}
			}
		}
	}

	class ImageSize {
		int width;
		int height;

		public ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}
}