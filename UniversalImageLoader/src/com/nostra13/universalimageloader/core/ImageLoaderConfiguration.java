package com.nostra13.universalimageloader.core;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.cache.disc.DiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheable;
import com.nostra13.universalimageloader.cache.memory.FuzzyKeyCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedCache;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * Presents configuration for {@link ImageLoader}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoader
 * @see MemoryCache
 * @see DiscCache
 * @see DisplayImageOptions
 */
public final class ImageLoaderConfiguration {

	final int maxImageWidthForMemoryCache;
	final int maxImageHeightForMemoryCache;
	final int httpConnectTimeout;
	final int httpReadTimeout;
	final int threadPoolSize;
	final int threadPriority;
	final MemoryCacheable<String, Bitmap> memoryCache;
	final DiscCache discCache;
	final DisplayImageOptions defaultDisplayImageOptions;

	private ImageLoaderConfiguration(Builder builder) {
		maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
		maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
		httpConnectTimeout = builder.httpConnectTimeout;
		httpReadTimeout = builder.httpReadTimeout;
		threadPoolSize = builder.threadPoolSize;
		threadPriority = builder.threadPriority;
		discCache = builder.discCache;
		memoryCache = builder.memoryCache;
		defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
	}

	/**
	 * Creates default configuration for {@link ImageLoader} <br />
	 * <b>Default values:</b>
	 * <ul>
	 * <li>maxImageWidthForMemoryCache = {@link Builder#DEFAULT_MAX_IMAGE_WIDTH this}</li>
	 * <li>maxImageHeightForMemoryCache = {@link Builder#DEFAULT_MAX_IMAGE_HEIGHT this}</li>
	 * <li>httpConnectTimeout = {@link Builder#DEFAULT_HTTP_CONNECTION_TIMEOUT this}</li>
	 * <li>httpReadTimeout = {@link Builder#DEFAULT_HTTP_READ_TIMEOUT this}</li>
	 * <li>threadPoolSize = {@link Builder#DEFAULT_THREAD_POOL_SIZE this}</li>
	 * <li>threadPriority = {@link Builder#DEFAULT_THREAD_PRIORITY this}</li>
	 * <li>allow to cache different sizes of image in memory</li>
	 * <li>memoryCache = {@link com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedCache
	 * UsingFreqLimitedCache} with limited memory cache size ( {@link Builder#DEFAULT_MEMORY_CACHE_SIZE this} bytes)</li>
	 * <li>discCache = {@link com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache DefaultDiscCache}</li>
	 * <li>defaultDisplayImageOptions = {@link DisplayImageOptions#createSimple() Simple options}</li>
	 * </ul>
	 * */
	public static ImageLoaderConfiguration createDefault(Context context) {
		return new Builder(context).build();
	}

	/**
	 * Builder for {@link ImageLoaderConfiguration}
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 */
	public static class Builder {

		/** {@value} milliseconds */
		public static final int DEFAULT_HTTP_CONNECTION_TIMEOUT = 5000;
		/** {@value} milliseconds */
		public static final int DEFAULT_HTTP_READ_TIMEOUT = 20000;
		/** {@value} */
		public static final int DEFAULT_THREAD_POOL_SIZE = 5;
		/** {@value} */
		public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;
		/** {@value} bytes */
		public static final int DEFAULT_MEMORY_CACHE_SIZE = 2000000;
		/** {@value} */
		public static final String DEFAULT_CACHE_DIRECTORY = "UniversalImageLoader/Cache";

		private Context context;

		private int maxImageWidthForMemoryCache = 0;
		private int maxImageHeightForMemoryCache = 0;
		private int httpConnectTimeout = DEFAULT_HTTP_CONNECTION_TIMEOUT;
		private int httpReadTimeout = DEFAULT_HTTP_READ_TIMEOUT;
		private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
		private int threadPriority = DEFAULT_THREAD_PRIORITY;
		private boolean allowCacheImageMultipleSizesInMemory = true;
		private MemoryCacheable<String, Bitmap> memoryCache = null;
		private DiscCache discCache = null;
		private DisplayImageOptions defaultDisplayImageOptions = null;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Sets maximum image width which will be used for memory saving during decoding an image to
		 * {@link android.graphics.Bitmap Bitmap}.<br />
		 * Default value - device's screen width
		 * */
		public Builder maxImageWidthForMemoryCache(int maxImageWidthForMemoryCache) {
			this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
			return this;
		}

		/**
		 * Sets maximum image height which will be used for memory saving during decoding an image to
		 * {@link android.graphics.Bitmap Bitmap}.<br />
		 * Default value - device's screen height
		 * */
		public Builder maxImageHeightForMemoryCache(int maxImageHeightForMemoryCache) {
			this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
			return this;
		}

		/**
		 * Sets timeout for HTTP connection establishment (during image loading).<br />
		 * Default value - {@link #DEFAULT_HTTP_CONNECTION_TIMEOUT this}
		 * */
		public Builder httpConnectTimeout(int timeout) {
			httpConnectTimeout = timeout;
			return this;
		}

		/**
		 * Sets timeout for HTTP reading (during image loading).<br />
		 * Default value - {@link #DEFAULT_HTTP_READ_TIMEOUT this}
		 * */
		public Builder httpReadTimeout(int timeout) {
			httpReadTimeout = timeout;
			return this;
		}

		/**
		 * Sets thread pool size for image display tasks.<br />
		 * Default value - {@link #DEFAULT_THREAD_POOL_SIZE this}
		 * */
		public Builder threadPoolSize(int threadPoolSize) {
			this.threadPoolSize = threadPoolSize;
			return this;
		}

		/**
		 * Sets the priority for image loading threads. Must be <b>NOT</b> greater than {@link Thread#MAX_PRIORITY} or
		 * less than {@link Thread#MIN_PRIORITY}<br />
		 * Default value - {@link #DEFAULT_THREAD_PRIORITY this}
		 * */
		public Builder threadPriority(int threadPriority) {
			if (threadPriority < Thread.MIN_PRIORITY) {
				this.threadPriority = Thread.MIN_PRIORITY;
			} else {
				if (threadPriority > Thread.MAX_PRIORITY) {
					threadPriority = Thread.MAX_PRIORITY;
				} else {
					this.threadPriority = threadPriority;
				}
			}
			return this;
		}

		/**
		 * When you display an image in a small {@link android.widget.ImageView ImageView} and later you try to display
		 * this image (from identical URL) in a larger {@link android.widget.ImageView ImageView} so decoded image of
		 * bigger size will be cached in memory as a previous decoded image of smaller size.<br />
		 * So <b>the default behavior is to allow to cache multiple sizes of one image in memory</b>. You can
		 * <b>deny</b> it by calling <b>this</b> method: so when some image will be cached in memory then previous
		 * cached size of this image (if it exists) will be removed from memory cache before.
		 * */
		public Builder denyCacheImageMultipleSizesInMemory() {
			this.allowCacheImageMultipleSizesInMemory = false;
			return this;
		}

		/**
		 * Sets memory cache size for {@link android.graphics.Bitmap bitmaps} (in bytes).<br />
		 * Default value - {@link #DEFAULT_MEMORY_CACHE_SIZE this}<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedCache UsingFreqLimitedCache} will
		 * be used as memory cache. You can use {@link #memoryCache(MemoryCache)} method for introduction your own
		 * implementation of {@link MemoryCache}.
		 */
		public Builder memoryCacheSize(int memoryCacheSize) {
			this.memoryCache = new UsingFreqLimitedCache(memoryCacheSize);
			return this;
		}

		/**
		 * Sets memory cache for {@link android.graphics.Bitmap bitmaps}.<br />
		 * Default value - {@link com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedCache
		 * UsingFreqLimitedCache} with limited memory cache size (size = {@link #DEFAULT_MEMORY_CACHE_SIZE this})<br />
		 * <b>NOTE:</b> You can use {@link #memoryCacheSize(int)} method instead of this method to simplify memory cache
		 * tuning.
		 */
		public Builder memoryCache(MemoryCache<String, Bitmap> memoryCache) {
			this.memoryCache = memoryCache;
			return this;
		}

		/**
		 * Sets cache directory path for images on SD card.
		 * {@link com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache DefaultDiscCache} will be used in
		 * this case.<br />
		 * Default value - {@link #DEFAULT_CACHE_DIRECTORY this}</b>.<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache DefaultDiscCache} will be used as
		 * disc cache. You can use {@link #discCache(DiscCache)} method for introduction your own implementation of
		 * {@link DiscCache}.
		 */
		public Builder discCacheDir(String discCacheDirPath) {
			File cacheDir = StorageUtils.getCacheDirectory(context, discCacheDirPath);
			this.discCache = new DefaultDiscCache(cacheDir);
			return this;
		}

		/**
		 * Sets disc cache for {@link android.graphics.Bitmap bitmaps}.<br />
		 * Default value - {@link com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache DefaultDiscCache}.
		 * Cache directory is defined by <b>
		 * {@link com.nostra13.universalimageloader.utils.StorageUtils#getCacheDirectory(Context, String)
		 * StorageUtils.getCacheDirectory(context, cacheDirPath)}</b>, where <b>cacheDirPath</b> =
		 * {@link #DEFAULT_CACHE_DIRECTORY this}</b>.<br />
		 * <b>NOTE:</b> You can use {@link #discCacheDir(String)} method instead of this method to simplify disc cache
		 * tuning.
		 */
		public Builder discCache(DiscCache discCache) {
			this.discCache = discCache;
			return this;
		}

		/**
		 * Sets default {@linkplain DisplayImageOptions display image options} for image displaying. These options will
		 * be used for every {@linkplain ImageLoader#displayImage(String, android.widget.ImageView) image display call}
		 * without passing custom {@linkplain DisplayImageOptions options}<br />
		 * Default value - {@link DisplayImageOptions#createSimple() Simple options}
		 */
		public Builder defaultDisplayImageOptions(DisplayImageOptions defaultDisplayImageOptions) {
			this.defaultDisplayImageOptions = defaultDisplayImageOptions;
			return this;
		}

		/** Builds configured {@link ImageLoaderConfiguration} object */
		public ImageLoaderConfiguration build() {
			initEmptyFiledsWithDefaultValues();
			return new ImageLoaderConfiguration(this);
		}

		private void initEmptyFiledsWithDefaultValues() {
			if (discCache == null) {
				File cacheDir = StorageUtils.getCacheDirectory(context, DEFAULT_CACHE_DIRECTORY);
				discCache = new DefaultDiscCache(cacheDir);
			}
			if (memoryCache == null) {
				memoryCache = new UsingFreqLimitedCache(DEFAULT_MEMORY_CACHE_SIZE);
			}
			if (!allowCacheImageMultipleSizesInMemory) {
				memoryCache = new FuzzyKeyCache<String, Bitmap>(memoryCache, MemoryCacheKeyUtil.createFuzzyKeyComparator());
			}
			if (defaultDisplayImageOptions == null) {
				defaultDisplayImageOptions = DisplayImageOptions.createSimple();
			}
			DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
			if (maxImageWidthForMemoryCache == 0) {
				maxImageWidthForMemoryCache = displayMetrics.widthPixels;
			}
			if (maxImageHeightForMemoryCache == 0) {
				maxImageHeightForMemoryCache = displayMetrics.heightPixels;
			}
		}
	}
}
