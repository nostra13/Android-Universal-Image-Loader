package com.nostra13.universalimageloader.core;

import java.lang.reflect.Field;

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
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
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
	private ImageLoaderEngine engine;

	private final ImageLoadingListener emptyListener = new SimpleImageLoadingListener();
	private final BitmapDisplayer fakeBitmapDisplayer = new FakeBitmapDisplayer();

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
		engine.prepareDisplayTaskFor(imageView, memoryCacheKey);

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

			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageView, targetSize, options, listener, engine.getLockForUri(uri));
			final LoadAndDisplayImageTask displayImageTask = new LoadAndDisplayImageTask(engine, imageLoadingInfo, new Handler());
			engine.submit(displayImageTask);
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
	 * @param minImageSize Minimal size for {@link Bitmap} which will be returned in
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
	public void loadImage(String uri, ImageSize minImageSize, DisplayImageOptions options, ImageLoadingListener listener) {
		checkConfiguration();
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
			optionsWithFakeDisplayer = new DisplayImageOptions.Builder().cloneFrom(options).displayer(fakeBitmapDisplayer).build();
		}

		ImageView fakeImage = new ImageView(configuration.context);
		fakeImage.setLayoutParams(new LayoutParams(minImageSize.getWidth(), minImageSize.getHeight()));
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
}