package com.nostra13.universalimageloader.core;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FakeBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

/**
 * Singletone for image loading and displaying at {@link ImageView ImageViews}<br />
 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before any other method.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference are required)";
	private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";
	private static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";

	private ImageLoaderConfiguration configuration;
	private ExecutorService imageLoadingExecutor;
	private ExecutorService cachedImageLoadingExecutor;

	private final ImageLoadingListener emptyListener = new SimpleImageLoadingListener();
	private final BitmapDisplayer fakeBitmapDisplayer = new FakeBitmapDisplayer();

	private final Map<Integer, String> cacheKeysForImageViews = Collections.synchronizedMap(new HashMap<Integer, String>());
	private final Map<String, ReentrantLock> uriLocks = new WeakHashMap<String, ReentrantLock>();
	private final AtomicBoolean paused = new AtomicBoolean(false);

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

	protected ImageLoader() {
	}

	/**
	 * Initializes ImageLoader's singleton instance with configuration. Method should be called <b>once</b> (each
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
		}
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn. <br/>
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void displayImage(String uri, ImageView imageView) {
		displayImage(uri, imageView, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
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
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
		displayImage(uri, imageView, options, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
		displayImage(uri, imageView, null, listener);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param options
	 *            {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> - default
	 *            display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
		if (configuration == null) {
			throw new RuntimeException(ERROR_NOT_INIT);
		}
		if (imageView == null) {
			L.w(TAG, ERROR_WRONG_ARGUMENTS);
			return;
		}
		if (listener == null) {
			listener = emptyListener;
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		if (uri == null || uri.length() == 0) {
			cacheKeysForImageViews.remove(imageView.hashCode());
			listener.onLoadingStarted();
			if (options.isShowImageForEmptyUri()) {
				imageView.setImageResource(options.getImageForEmptyUri());
			} else {
				imageView.setImageBitmap(null);
			}
			listener.onLoadingComplete(null);
			return;
		}

		ImageSize targetSize = getImageSizeScaleTo(imageView);
		String memoryCacheKey = MemoryCacheUtil.generateKey(uri, targetSize);
		cacheKeysForImageViews.put(imageView.hashCode(), memoryCacheKey);

		Bitmap bmp = configuration.memoryCache.get(memoryCacheKey);
		if (bmp != null && !bmp.isRecycled()) {
			if (configuration.loggingEnabled) L.i(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, memoryCacheKey);
			listener.onLoadingStarted();
			options.getDisplayer().display(bmp, imageView);
			listener.onLoadingComplete(bmp);
		} else {
			listener.onLoadingStarted();

			if (options.isShowStubImage()) {
				imageView.setImageResource(options.getStubImage());
			} else {
				if (options.isResetViewBeforeLoading()) {
					imageView.setImageBitmap(null);
				}
			}

			initExecutorsIfNeed();
			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageView, targetSize, options, listener, getLockForUri(uri));
			LoadAndDisplayImageTask displayImageTask = new LoadAndDisplayImageTask(configuration, imageLoadingInfo, new Handler());
			boolean isImageCachedOnDisc = configuration.discCache.get(uri).exists();
			if (isImageCachedOnDisc) {
				cachedImageLoadingExecutor.submit(displayImageTask);
			} else {
				imageLoadingExecutor.submit(displayImageTask);
			}
		}
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param context
	 *            Application context (used for creation of fake {@link ImageView})
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 */
	public void loadImage(Context context, String uri, ImageLoadingListener listener) {
		loadImage(context, uri, null, null, listener);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param context
	 *            Application context (used for creation of fake {@link ImageView})
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param minImageSize
	 *            Minimal size for {@link Bitmap} which will be returned in
	 *            {@linkplain ImageLoadingListener#onLoadingComplete(Bitmap) callback}. Downloaded image will be decoded
	 *            and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit larger) than
	 *            incoming minImageSize .
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 */
	public void loadImage(Context context, String uri, ImageSize minImageSize, ImageLoadingListener listener) {
		loadImage(context, uri, minImageSize, null, listener);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param context
	 *            Application context (used for creation of fake {@link ImageView})
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param options
	 *            {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> - default
	 *            display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.<br />
	 *            Incoming options should contain {@link FakeBitmapDisplayer} as displayer.
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 */
	public void loadImage(Context context, String uri, DisplayImageOptions options, ImageLoadingListener listener) {
		loadImage(context, uri, null, options, listener);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(Bitmap) callback}.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 * 
	 * @param context
	 *            Application context (used for creation of fake {@link ImageView})
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param minImageSize
	 *            Minimal size for {@link Bitmap} which will be returned in
	 *            {@linkplain ImageLoadingListener#onLoadingComplete(Bitmap) callback}. Downloaded image will be decoded
	 *            and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit larger) than
	 *            incoming minImageSize .
	 * @param options
	 *            {@linkplain DisplayImageOptions Display image options} for image displaying. If <b>null</b> - default
	 *            display image options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *            configuration} will be used.<br />
	 *            Incoming options should contain {@link FakeBitmapDisplayer} as displayer.
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *            thread.
	 */
	public void loadImage(Context context, String uri, ImageSize minImageSize, DisplayImageOptions options, ImageLoadingListener listener) {
		if (minImageSize == null) {
			minImageSize = new ImageSize(configuration.maxImageWidthForMemoryCache, configuration.maxImageHeightForMemoryCache);
		}
		if (options == null) {
			options = configuration.defaultDisplayImageOptions;
		}

		DisplayImageOptions optionsWithFakeDisplayer;
		if (options.getDisplayer() instanceof FakeBitmapDisplayer) {
			optionsWithFakeDisplayer = options;
		} else {
			optionsWithFakeDisplayer = new DisplayImageOptions.Builder()
				.cloneFrom(options)
				.displayer(fakeBitmapDisplayer)
				.build();
		}

		ImageView fakeImage = new ImageView(context);
		fakeImage.setLayoutParams(new LayoutParams(minImageSize.getWidth(), minImageSize.getHeight()));
		fakeImage.setScaleType(ScaleType.CENTER_CROP);

		displayImage(uri, fakeImage, optionsWithFakeDisplayer, listener);
	}

	private void initExecutorsIfNeed() {
		if (imageLoadingExecutor == null || imageLoadingExecutor.isShutdown()) {
			imageLoadingExecutor = createExecutor();
		}
		if (cachedImageLoadingExecutor == null || cachedImageLoadingExecutor.isShutdown()) {
			cachedImageLoadingExecutor = createExecutor();
		}
	}

	private ExecutorService createExecutor() {
		boolean lifo = configuration.tasksProcessingType == QueueProcessingType.LIFO;
		BlockingQueue<Runnable> taskQueue = lifo ? new LIFOLinkedBlockingDeque<Runnable>() : new LinkedBlockingQueue<Runnable>();
		return new ThreadPoolExecutor(configuration.threadPoolSize, configuration.threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue,
				configuration.displayImageThreadFactory);
	}

	/** Returns memory cache */
	public MemoryCacheAware<String, Bitmap> getMemoryCache() {
		return configuration.memoryCache;
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

	/** Returns disc cache */
	public DiscCacheAware getDiscCache() {
		return configuration.discCache;
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

	/** Returns URI of image which is loading at this moment into passed {@link ImageView} */
	public String getLoadingUriForView(ImageView imageView) {
		return cacheKeysForImageViews.get(imageView.hashCode());
	}

	/**
	 * Cancel the task of loading and displaying image for passed {@link ImageView}.
	 * 
	 * @param imageView
	 *            {@link ImageView} for which display task will be cancelled
	 */
	public void cancelDisplayTask(ImageView imageView) {
		cacheKeysForImageViews.remove(imageView.hashCode());
	}

	/**
	 * Pause ImageLoader. All new "load&display" tasks won't be executed until ImageLoader is {@link #resume() resumed}.<br />
	 * Already running tasks are not paused.
	 */
	public void pause() {
		paused.set(true);
	}

	/** Resumes waiting "load&display" tasks */
	public void resume() {
		synchronized (paused) {
			paused.set(false);
			paused.notifyAll();
		}
	}

	/** Stops all running display image tasks, discards all other scheduled tasks */
	public void stop() {
		if (imageLoadingExecutor != null) {
			imageLoadingExecutor.shutdownNow();
		}
		if (cachedImageLoadingExecutor != null) {
			cachedImageLoadingExecutor.shutdownNow();
		}
	}

	/**
	 * Defines image size for loading at memory (for memory economy) by {@link ImageView} parameters.<br />
	 * Size computing algorithm:<br />
	 * 1) Get <b>layout_width</b> and <b>layout_height</b>. If both of them haven't exact value then go to step #2.</br>
	 * 2) Get <b>maxWidth</b> and <b>maxHeight</b>. If both of them are not set then go to step #3.<br />
	 * 3) Get <b>maxImageWidthForMemoryCache</b> and <b>maxImageHeightForMemoryCache</b> from configuration. If both of
	 * them are not set then go to step #3.<br />
	 * 4) Get device screen dimensions.
	 */
	private ImageSize getImageSizeScaleTo(ImageView imageView) {
		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

		LayoutParams params = imageView.getLayoutParams();
		int width = params.width; // Get layout width parameter
		if (width <= 0) width = getFieldValue(imageView, "mMaxWidth"); // Check maxWidth parameter
		if (width <= 0) width = configuration.maxImageWidthForMemoryCache;
		if (width <= 0) width = displayMetrics.widthPixels;

		int height = params.height; // Get layout height parameter
		if (height <= 0) height = getFieldValue(imageView, "mMaxHeight"); // Check maxHeight parameter
		if (height <= 0) height = configuration.maxImageHeightForMemoryCache;
		if (height <= 0) height = displayMetrics.heightPixels;

		return new ImageSize(width, height);
	}

	private int getFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			L.e(e);
		}
		return value;
	}

	private ReentrantLock getLockForUri(String uri) {
		ReentrantLock lock = uriLocks.get(uri);
		if (lock == null) {
			lock = new ReentrantLock();
			uriLocks.put(uri, lock);
		}
		return lock;
	}

	AtomicBoolean getPause() {
		return paused;
	}
}
