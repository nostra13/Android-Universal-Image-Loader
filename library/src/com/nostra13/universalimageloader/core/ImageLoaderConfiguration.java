package com.nostra13.universalimageloader.core;

import java.util.concurrent.ThreadFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.L;

/**
 * Presents configuration for {@link ImageLoader}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoader
 * @see MemoryCacheAware
 * @see DiscCacheAware
 * @see DisplayImageOptions
 * @see ImageDownloader
 * @see FileNameGenerator
 */
public final class ImageLoaderConfiguration {

	final int maxImageWidthForMemoryCache;
	final int maxImageHeightForMemoryCache;
	final int maxImageWidthForDiscCache;
	final int maxImageHeightForDiscCache;
	final CompressFormat imageCompressFormatForDiscCache;
	final int imageQualityForDiscCache;

	final int threadPoolSize;
	final boolean handleOutOfMemory;
	final QueueProcessingType tasksProcessingType;

	final MemoryCacheAware<String, Bitmap> memoryCache;
	final DiscCacheAware discCache;
	final ImageDownloader downloader;
	final DisplayImageOptions defaultDisplayImageOptions;
	final ThreadFactory displayImageThreadFactory;
	final boolean loggingEnabled;

	private ImageLoaderConfiguration(final Builder builder) {
		maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
		maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
		maxImageWidthForDiscCache = builder.maxImageWidthForDiscCache;
		maxImageHeightForDiscCache = builder.maxImageHeightForDiscCache;
		imageCompressFormatForDiscCache = builder.imageCompressFormatForDiscCache;
		imageQualityForDiscCache = builder.imageQualityForDiscCache;
		threadPoolSize = builder.threadPoolSize;
		handleOutOfMemory = builder.handleOutOfMemory;
		discCache = builder.discCache;
		memoryCache = builder.memoryCache;
		defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
		loggingEnabled = builder.loggingEnabled;
		downloader = builder.downloader;
		tasksProcessingType = builder.tasksProcessingType;
		displayImageThreadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setPriority(builder.threadPriority);
				return t;
			}
		};
	}

	/**
	 * Creates default configuration for {@link ImageLoader} <br />
	 * <b>Default values:</b>
	 * <ul>
	 * <li>maxImageWidthForMemoryCache = device's screen width</li>
	 * <li>maxImageHeightForMemoryCache = device's screen height</li>
	 * <li>maxImageWidthForDiscCache = unlimited</li>
	 * <li>maxImageHeightForDiscCache = unlimited</li>
	 * <li>threadPoolSize = {@link Builder#DEFAULT_THREAD_POOL_SIZE this}</li>
	 * <li>threadPriority = {@link Builder#DEFAULT_THREAD_PRIORITY this}</li>
	 * <li>allow to cache different sizes of image in memory</li>
	 * <li>memoryCache = {@link UsingFreqLimitedCache} with limited memory cache size (
	 * {@link Builder#DEFAULT_MEMORY_CACHE_SIZE this} bytes)</li>
	 * <li>discCache = {@link UnlimitedDiscCache}</li>
	 * <li>imageDownloader = {@link ImageDownloader#createDefault()}</li>
	 * <li>discCacheFileNameGenerator = {@link FileNameGenerator#createDefault()}</li>
	 * <li>defaultDisplayImageOptions = {@link DisplayImageOptions#createSimple() Simple options}</li>
	 * <li>tasksProcessingOrder = {@link QueueProcessingType#FIFO}</li>
	 * <li>detailed logging disabled</li>
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

		private static final String WARNING_OVERLAP_MEMORY_CACHE_SIZE = "This method's call overlaps memoryCacheSize() method call";
		private static final String WARNING_MEMORY_CACHE_ALREADY_SET = "You already have set memory cache. This method call will make no effect.";
		private static final String WARNING_OVERLAP_DISC_CACHE_SIZE = "This method's call overlaps discCacheSize() method call";
		private static final String WARNING_OVERLAP_DISC_CACHE_FILE_COUNT = "This method's call overlaps discCacheFileCount() method call";
		private static final String WARNING_OVERLAP_DISC_CACHE_FILE_NAME_GENERATOR = "This method's call overlaps discCacheFileNameGenerator() method call";
		private static final String WARNING_DISC_CACHE_ALREADY_SET = "You already have set disc cache. This method call will make no effect.";

		/** {@value} */
		public static final int DEFAULT_THREAD_POOL_SIZE = 3;
		/** {@value} */
		public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;
		/** {@value} */
		public static final int DEFAULT_MEMORY_CACHE_SIZE = 2 * 1024 * 1024; // bytes

		private Context context;

		private int maxImageWidthForMemoryCache = 0;
		private int maxImageHeightForMemoryCache = 0;
		private int maxImageWidthForDiscCache = 0;
		private int maxImageHeightForDiscCache = 0;
		private CompressFormat imageCompressFormatForDiscCache = null;
		private int imageQualityForDiscCache = 0;

		private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
		private int threadPriority = DEFAULT_THREAD_PRIORITY;
		private boolean denyCacheImageMultipleSizesInMemory = false;
		private boolean handleOutOfMemory = true;
		private QueueProcessingType tasksProcessingType = QueueProcessingType.FIFO;

		private int memoryCacheSize = DEFAULT_MEMORY_CACHE_SIZE;
		private int discCacheSize = 0;
		private int discCacheFileCount = 0;

		private MemoryCacheAware<String, Bitmap> memoryCache = null;
		private DiscCacheAware discCache = null;
		private FileNameGenerator discCacheFileNameGenerator = null;
		private ImageDownloader downloader = null;
		private DisplayImageOptions defaultDisplayImageOptions = null;

		private boolean loggingEnabled = false;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Sets options for memory cache
		 * 
		 * @param maxImageWidthForMemoryCache
		 *            Maximum image width which will be used for memory saving during decoding an image to
		 *            {@link android.graphics.Bitmap Bitmap}. <b>Default value - device's screen width</b>
		 * @param maxImageHeightForMemoryCache
		 *            Maximum image height which will be used for memory saving during decoding an image to
		 *            {@link android.graphics.Bitmap Bitmap}. <b>Default value</b> - device's screen height
		 */
		public Builder memoryCacheExtraOptions(int maxImageWidthForMemoryCache, int maxImageHeightForMemoryCache) {
			this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
			this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
			return this;
		}

		/**
		 * Sets options for resizing/compressing of downloaded images before saving to disc cache.<br />
		 * <b>NOTE: Use this option only when you have appropriate needs. It can make ImageLoader slower.</b>
		 * 
		 * @param maxImageWidthForDiscCache
		 *            Maximum width of downloaded images for saving at disc cache
		 * @param maxImageHeightForDiscCache
		 *            Maximum height of downloaded images for saving at disc cache
		 * @param compressFormat
		 *            {@link android.graphics.Bitmap.CompressFormat Compress format} downloaded images to save them at
		 *            disc cache
		 * @param compressQuality
		 *            Hint to the compressor, 0-100. 0 meaning compress for small size, 100 meaning compress for max
		 *            quality. Some formats, like PNG which is lossless, will ignore the quality setting
		 */
		public Builder discCacheExtraOptions(int maxImageWidthForDiscCache, int maxImageHeightForDiscCache, CompressFormat compressFormat, int compressQuality) {
			this.maxImageWidthForDiscCache = maxImageWidthForDiscCache;
			this.maxImageHeightForDiscCache = maxImageHeightForDiscCache;
			this.imageCompressFormatForDiscCache = compressFormat;
			this.imageQualityForDiscCache = compressQuality;
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
		 * this image (from identical URI) in a larger {@link android.widget.ImageView ImageView} so decoded image of
		 * bigger size will be cached in memory as a previous decoded image of smaller size.<br />
		 * So <b>the default behavior is to allow to cache multiple sizes of one image in memory</b>. You can
		 * <b>deny</b> it by calling <b>this</b> method: so when some image will be cached in memory then previous
		 * cached size of this image (if it exists) will be removed from memory cache before.
		 * */
		public Builder denyCacheImageMultipleSizesInMemory() {
			this.denyCacheImageMultipleSizesInMemory = true;
			return this;
		}

		/**
		 * ImageLoader try clean memory and re-display image it self when {@link OutOfMemoryError} occurs. You can
		 * switch off this feature by this method and process error by your way (you can know that
		 * {@link OutOfMemoryError} occurred if you got {@link FailReason#OUT_OF_MEMORY} in
		 * {@link ImageLoadingListener#onLoadingFailed(FailReason)}).
		 */
		public Builder offOutOfMemoryHandling() {
			this.handleOutOfMemory = false;
			return this;
		}

		/**
		 * Sets type of queue processing for tasks for loading and displaying images.<br />
		 * Default value - {@link QueueProcessingType#FIFO}
		 */
		public Builder tasksProcessingOrder(QueueProcessingType tasksProcessingType) {
			this.tasksProcessingType = tasksProcessingType;
			return this;
		}

		/**
		 * Sets maximum memory cache size for {@link android.graphics.Bitmap bitmaps} (in bytes).<br />
		 * Default value - {@link #DEFAULT_MEMORY_CACHE_SIZE this}<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache UsingFreqLimitedCache}
		 * will be used as memory cache. You can use {@link #memoryCache(MemoryCacheAware)} method for introduction your
		 * own implementation of {@link MemoryCacheAware}.
		 */
		public Builder memoryCacheSize(int memoryCacheSize) {
			if (memoryCacheSize <= 0) throw new IllegalArgumentException("memoryCacheSize must be a positive number");
			if (memoryCache != null) L.w(WARNING_MEMORY_CACHE_ALREADY_SET);

			this.memoryCacheSize = memoryCacheSize;
			return this;
		}

		/**
		 * Sets memory cache for {@link android.graphics.Bitmap bitmaps}.<br />
		 * Default value - {@link com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache
		 * UsingFreqLimitedCache} with limited memory cache size (size = {@link #DEFAULT_MEMORY_CACHE_SIZE this})<br />
		 * <b>NOTE:</b> You can use {@link #memoryCacheSize(int)} method instead of this method to simplify memory cache
		 * tuning.
		 */
		public Builder memoryCache(MemoryCacheAware<String, Bitmap> memoryCache) {
			if (memoryCacheSize != DEFAULT_MEMORY_CACHE_SIZE) L.w(WARNING_OVERLAP_MEMORY_CACHE_SIZE);

			this.memoryCache = memoryCache;
			return this;
		}

		/**
		 * Sets maximum disc cache size for images (in bytes).<br />
		 * By default: disc cache is unlimited.<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache TotalSizeLimitedDiscCache}
		 * will be used as disc cache. You can use {@link #discCache(DiscCacheAware)} method for introduction your own
		 * implementation of {@link DiscCacheAware}
		 */
		public Builder discCacheSize(int maxCacheSize) {
			if (maxCacheSize <= 0) throw new IllegalArgumentException("maxCacheSize must be a positive number");
			if (discCache != null) L.w(WARNING_DISC_CACHE_ALREADY_SET);
			if (discCacheFileCount > 0) L.w(WARNING_OVERLAP_DISC_CACHE_FILE_COUNT);

			this.discCacheSize = maxCacheSize;
			return this;
		}

		/**
		 * Sets maximum file count in disc cache directory.<br />
		 * By default: disc cache is unlimited.<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache FileCountLimitedDiscCache}
		 * will be used as disc cache. You can use {@link #discCache(DiscCacheAware)} method for introduction your own
		 * implementation of {@link DiscCacheAware}
		 */
		public Builder discCacheFileCount(int maxFileCount) {
			if (maxFileCount <= 0) throw new IllegalArgumentException("maxFileCount must be a positive number");
			if (discCache != null) L.w(WARNING_DISC_CACHE_ALREADY_SET);
			if (discCacheSize > 0) L.w(WARNING_OVERLAP_DISC_CACHE_SIZE);

			this.discCacheSize = 0;
			this.discCacheFileCount = maxFileCount;
			return this;
		}

		/**
		 * Sets name generator for files cached in disc cache.<br />
		 * Default value -
		 * {@link com.nostra13.universalimageloader.core.DefaultConfigurationFactory#createFileNameGenerator()
		 * DefaultConfigurationFactory.createFileNameGenerator()}
		 */
		public Builder discCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
			if (discCache != null) L.w(WARNING_DISC_CACHE_ALREADY_SET);

			this.discCacheFileNameGenerator = fileNameGenerator;
			return this;
		}

		/**
		 * Sets utility which will be responsible for downloading of image.<br />
		 * Default value -
		 * {@link com.nostra13.universalimageloader.core.DefaultConfigurationFactory#createImageDownloader()
		 * DefaultConfigurationFactory.createImageDownloader()}
		 * */
		public Builder imageDownloader(ImageDownloader imageDownloader) {
			this.downloader = imageDownloader;
			return this;
		}

		/**
		 * Sets disc cache for images.<br />
		 * Default value - {@link com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache
		 * UnlimitedDiscCache}. Cache directory is defined by <b>
		 * {@link com.nostra13.universalimageloader.utils.StorageUtils#getCacheDirectory(Context)
		 * StorageUtils.getCacheDirectory(Context)}.<br />
		 */
		public Builder discCache(DiscCacheAware discCache) {
			if (discCacheSize > 0) L.w(WARNING_OVERLAP_DISC_CACHE_SIZE);
			if (discCacheFileCount > 0) L.w(WARNING_OVERLAP_DISC_CACHE_FILE_COUNT);
			if (discCacheFileNameGenerator != null) L.w(WARNING_OVERLAP_DISC_CACHE_FILE_NAME_GENERATOR);

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

		/** Enabled detail logging of {@link ImageLoader} work */
		public Builder enableLogging() {
			this.loggingEnabled = true;
			return this;
		}

		/** Builds configured {@link ImageLoaderConfiguration} object */
		public ImageLoaderConfiguration build() {
			initEmptyFiledsWithDefaultValues();
			return new ImageLoaderConfiguration(this);
		}

		private void initEmptyFiledsWithDefaultValues() {
			if (discCache == null) {
				if (discCacheFileNameGenerator == null) {
					discCacheFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
				}
				discCache = DefaultConfigurationFactory.createDiscCache(context, discCacheFileNameGenerator, discCacheSize, discCacheFileCount);
			}
			if (memoryCache == null) {
				memoryCache = DefaultConfigurationFactory.createMemoryCache(memoryCacheSize, denyCacheImageMultipleSizesInMemory);
			}
			if (downloader == null) {
				downloader = DefaultConfigurationFactory.createImageDownloader();
			}
			if (defaultDisplayImageOptions == null) {
				defaultDisplayImageOptions = DisplayImageOptions.createSimple();
			}
		}
	}
}
