package com.nostra13.test.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.io.*;
import java.net.URL;
import java.util.Stack;

/**
 * 
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private final int MAX_IMAGE_DIMENSION_WIDTH;
	private final int MAX_IMAGE_DIMENSION_HEIGHT;

	private final ImageCache bitmapCache = new ImageCache(2000000);
	private final File cacheDir;

	private final PhotosQueue photosQueue = new PhotosQueue();
	private final PhotosLoader photoLoaderThread = new PhotosLoader();

	private static ImageLoader INSTANCE = null;

	public static ImageLoader getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new ImageLoader(context);
		}
		return INSTANCE;
	}

	private ImageLoader(Context context) {
		MAX_IMAGE_DIMENSION_WIDTH = (int) (context.getResources().getDisplayMetrics().widthPixels);
		MAX_IMAGE_DIMENSION_HEIGHT = (int) (context.getResources().getDisplayMetrics().heightPixels);

		// Make the background thread low priority. This way it will not affect
		// the UI performance
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
		imageView.setTag(url);
		if (url == null || url.length() == 0) {
			return;
		}

		PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView, options, listener);

		synchronized (bitmapCache) {
			if (bitmapCache.containsKey(url)) {

				Object image = bitmapCache.get(url);
				if (image != null && !((Bitmap) image).isRecycled()) {
					imageView.setImageBitmap((Bitmap) image);
				} else {
					queuePhoto(photoToLoad);
					if (options.isShowStubImageDuringLoading()) {
						imageView.setImageResource(Constants.STUB_IMAGE);
					} else {
						if (options.isResetViewBeforeLoading())
							imageView.setImageBitmap(null);
					}
				}
			} else {
				queuePhoto(photoToLoad);
				if (options.isShowStubImageDuringLoading()) {
					imageView.setImageResource(Constants.STUB_IMAGE);
				} else {
					if (options.isResetViewBeforeLoading())
						imageView.setImageBitmap(null);
				}
			}
		}
	}

	private void queuePhoto(PhotoToLoad photoToLoad) {
		if (photoToLoad.listener != null) {
			photoToLoad.listener.onLoadStarted();
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
				Bitmap b = decodeFile(f, targetImageSize);
				if (b != null)
					return b;
			}
		} catch (IOException e) {
			// no image in SD cache
			// Do nothing
		}

		// from web
		try {
			Bitmap bitmap = null;
			if (cacheImageOnDisc) {
				InputStream is = new URL(imageUrl).openStream();
				OutputStream os = new FileOutputStream(f);
				FileUtils.copyStream(is, os);
				is.close();
				os.close();
				bitmap = decodeFile(f, targetImageSize);
			} else {
				bitmap = decodeUrlFile(new URL(imageUrl), targetImageSize);
			}
			return bitmap;
		} catch (Exception ex) {
			Log.e(TAG, "Exception while loading bitmap from URL=" + imageUrl + " : " + ex.getMessage(), ex);
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File imageFile, ImageSize targetImageSize) throws IOException {
		FileInputStream is = new FileInputStream(imageFile);
		Options decodeOptions = getBitmapOptionsForImageDecoding(is, targetImageSize);
		is.close();

		is = new FileInputStream(imageFile);
		Bitmap result = decodeImageStream(is, decodeOptions);
		is.close();

		return result;
	}

	private Bitmap decodeUrlFile(URL imageUrl, ImageSize targetImageSize) throws IOException {
		InputStream is = imageUrl.openStream();
		Options decodeOptions = getBitmapOptionsForImageDecoding(is, targetImageSize);
		is.close();

		is = imageUrl.openStream();
		Bitmap result = decodeImageStream(is, decodeOptions);
		is.close();

		return result;
	}

	private Options getBitmapOptionsForImageDecoding(InputStream imageStream, ImageSize targetImageSize) {
		Options options = new Options();
		options.inSampleSize = computeImageScale(imageStream, targetImageSize);
		return options;
	}

	private int computeImageScale(InputStream imageStream, ImageSize targetImageSize) {
		int width = targetImageSize.width;
		int height = targetImageSize.height;

		if (width < 0 && height < 0) {
			width = MAX_IMAGE_DIMENSION_WIDTH;
			height = MAX_IMAGE_DIMENSION_HEIGHT;
		}

		// decode image size
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(imageStream, null, options);

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = options.outWidth;
		int height_tmp = options.outHeight;

		int scale = 1;
		while (true) {
			if (width_tmp / 2 < width || height_tmp / 2 < height)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		return scale;
	}

	private Bitmap decodeImageStream(InputStream imageStream, Options decodeOptions) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(imageStream, null, decodeOptions);
		} catch (Throwable th) {
			Log.e(TAG, "OUT OF MEMMORY: " + th.getMessage());
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
		// TypedArray a = imageView.getContext().obtainStyledAttributes(R.styleable.ImageView);
		// int width = a.getDimensionPixelSize(R.styleable.ImageView_android_maxWidth, -1);
		// int height = a.getDimensionPixelSize(R.styleable.ImageView_android_maxHeight, -1);
		// a.recycle();

		int width = -1;
		int height = -1;

		if (width < 0 && height < 0) {
			LayoutParams params = imageView.getLayoutParams();
			width = params.width;
			height = params.height;
			if (width < 0 && height < 0) {
//				width = height = MAX_IMAGE_DIMENSION;
				width = MAX_IMAGE_DIMENSION_WIDTH;
				height = MAX_IMAGE_DIMENSION_HEIGHT;
			}
		}
		return new ImageSize(width, height);
	}

	public void clearCache() {
		// clear memory cache
		synchronized (bitmapCache) {
			bitmapCache.clear();
		}

		// clear SD cache
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
						bmp = getBitmap(photoToLoad.url, targetImageSize, photoToLoad.options.isCacheImageOnDisc());
						if (bmp == null) {
							continue;
						}
						if (photoToLoad.options.isCacheImageInMemory()) {
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
					photoToLoad.listener.onLoadComplete();
				}
			}
		}
	}

	private class ImageSize {
		int width;
		int height;

		public ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}
}