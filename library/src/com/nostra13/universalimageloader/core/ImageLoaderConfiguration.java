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

import java.util.concurrent.Executor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.download.NetworkDeniedImageDownloader;
import com.nostra13.universalimageloader.core.download.SlowNetworkImageDownloader;
import com.nostra13.universalimageloader.utils.L;

/**
 * Presents configuration for {@link ImageLoader}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 * @see ImageLoader
 * @see MemoryCacheAware
 * @see DiscCacheAware
 * @see DisplayImageOptions
 * @see ImageDownloader
 * @see FileNameGenerator
 */
public final class ImageLoaderConfiguration {

	final Context context;

	final int maxImageWidthForMemoryCache;
	final int maxImageHeightForMemoryCache;
	final int maxImageWidthForDiscCache;
	final int maxImageHeightForDiscCache;
	final CompressFormat imageCompressFormatForDiscCache;
	final int imageQualityForDiscCache;

	final Executor taskExecutor;
	final Executor taskExecutorForCachedImages;
	final boolean customExecutor;
	final boolean customExecutorForCachedImages;

	final int threadPoolSize;
	final int threadPriority;
	final QueueProcessingType tasksProcessingType;

	final MemoryCacheAware<String, Bitmap> memoryCache;
	final DiscCacheAware discCache;
	final ImageDownloader downloader;
	final ImageDecoder decoder;
	final DisplayImageOptions defaultDisplayImageOptions;
	final boolean loggingEnabled;

	final DiscCacheAware reserveDiscCache;
	final ImageDownloader networkDeniedDownloader;
	final ImageDownloader slowNetworkDownloader;

	private ImageLoaderConfiguration(final Builder builder) {
		context = builder.context;
		maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
		maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
		maxImageWidthForDiscCache = builder.maxImageWidthForDiscCache;
		maxImageHeightForDiscCache = builder.maxImageHeightForDiscCache;
		imageCompressFormatForDiscCache = builder.imageCompressFormatForDiscCache;
		imageQualityForDiscCache = builder.imageQualityForDiscCache;
		taskExecutor = builder.taskExecutor;
		taskExecutorForCachedImages = builder.taskExecutorForCachedImages;
		threadPoolSize = builder.threadPoolSize;
		threadPriority = builder.threadPriority;
		tasksProcessingType = builder.tasksProcessingType;
		discCache = builder.discCache;
		memoryCache = builder.memoryCache;
		defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
		loggingEnabled = builder.loggingEnabled;
		downloader = builder.downloader;
		decoder = builder.decoder;

		customExecutor = builder.customExecutor;
		customExecutorForCachedImages = builder.customExecutorForCachedImages;

		networkDeniedDownloader = new NetworkDeniedImageDownloader(downloader);
		slowNetworkDownloader = new SlowNetworkImageDownloader(downloader);

		reserveDiscCache = DefaultConfigurationFactory.createReserveDiscCache(context);
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
	 * <li>memoryCache = {@link DefaultConfigurationFactory#createMemoryCache(int)}</li>
	 * <li>discCache = {@link UnlimitedDiscCache}</li>
	 * <li>imageDownloader = {@link DefaultConfigurationFactory#createImageDownloader(Context)}</li>
	 * <li>imageDecoder = {@link DefaultConfigurationFactory#createImageDecoder(boolean)}</li>
	 * <li>discCacheFileNameGenerator = {@link DefaultConfigurationFactory#createFileNameGenerator()}</li>
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

		private static final String WARNING_OVERLAP_DISC_CACHE_PARAMS = "discCache(), discCacheSize() and discCacheFileCount calls overlap each other";
		private static final String WARNING_OVERLAP_DISC_CACHE_NAME_GENERATOR = "discCache() and discCacheFileNameGenerator() calls overlap each other";
		private static final String WARNING_OVERLAP_MEMORY_CACHE = "memoryCache() and memoryCacheSize() calls overlap each other";
		private static final String WARNING_OVERLAP_EXECUTOR = "threadPoolSize(), threadPriority() and tasksProcessingOrder() calls "
				+ "can overlap taskExecutor() and taskExecutorForCachedImages() calls.";

		/** {@value} */
		public static final int DEFAULT_THREAD_POOL_SIZE = 3;
		/** {@value} */
		public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;
		/** {@value} */
		public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;

		private Context context;

		private int maxImageWidthForMemoryCache = 0;
		private int maxImageHeightForMemoryCache = 0;
		private int maxImageWidthForDiscCache = 0;
		private int maxImageHeightForDiscCache = 0;
		private CompressFormat imageCompressFormatForDiscCache = null;
		private int imageQualityForDiscCache = 0;

		private Executor taskExecutor = null;
		private Executor taskExecutorForCachedImages = null;
		private boolean customExecutor = false;
		private boolean customExecutorForCachedImages = false;

		private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
		private int threadPriority = DEFAULT_THREAD_PRIORITY;
		private boolean denyCacheImageMultipleSizesInMemory = false;
		private QueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;

		private int memoryCacheSize = 0;
		private int discCacheSize = 0;
		private int discCacheFileCount = 0;

		private MemoryCacheAware<String, Bitmap> memoryCache = null;
		private DiscCacheAware discCache = null;
		private FileNameGenerator discCacheFileNameGenerator = null;
		private ImageDownloader downloader = null;
		private ImageDecoder decoder;
		private DisplayImageOptions defaultDisplayImageOptions = null;

		private boolean loggingEnabled = false;

		public Builder(Context context) {
			this.context = context.getApplicationContext();
		}

		/**
		 * Sets options for memory cache
		 * 
		 * @param maxImageWidthForMemoryCache Maximum image width which will be used for memory saving during decoding
		 *            an image to {@link android.graphics.Bitmap Bitmap}. <b>Default value - device's screen width</b>
		 * @param maxImageHeightForMemoryCache Maximum image height which will be used for memory saving during decoding
		 *            an image to {@link android.graphics.Bitmap Bitmap}. <b>Default value</b> - device's screen height
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
		 * @param maxImageWidthForDiscCache Maximum width of downloaded images for saving at disc cache
		 * @param maxImageHeightForDiscCache Maximum height of downloaded images for saving at disc cache
		 * @param compressFormat {@link android.graphics.Bitmap.CompressFormat Compress format} downloaded images to
		 *            save them at disc cache
		 * @param compressQuality Hint to the compressor, 0-100. 0 meaning compress for small size, 100 meaning compress
		 *            for max quality. Some formats, like PNG which is lossless, will ignore the quality setting
		 */
		public Builder discCacheExtraOptions(int maxImageWidthForDiscCache, int maxImageHeightForDiscCache, CompressFormat compressFormat, int compressQuality) {
			this.maxImageWidthForDiscCache = maxImageWidthForDiscCache;
			this.maxImageHeightForDiscCache = maxImageHeightForDiscCache;
			this.imageCompressFormatForDiscCache = compressFormat;
			this.imageQualityForDiscCache = compressQuality;
			return this;
		}

		/**
		 * Sets custom {@linkplain Executor executor} for tasks of loading and displaying images.<br />
		 * <br />
		 * <b>NOTE:</b> If you set custom executor then following configuration options will not be considered for this
		 * executor:
		 * <ul>
		 * <li>{@link #threadPoolSize(int)}</li>
		 * <li>{@link #threadPriority(int)}</li>
		 * <li>{@link #tasksProcessingOrder(QueueProcessingType)}</li>
		 * </ul>
		 * 
		 * @see #taskExecutorForCachedImages(Executor)
		 */
		public Builder taskExecutor(Executor executor) {
			if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE || threadPriority != DEFAULT_THREAD_PRIORITY || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
				L.w(WARNING_OVERLAP_EXECUTOR);
			}

			this.taskExecutor = executor;
			return this;
		}

		/**
		 * Sets custom {@linkplain Executor executor} for tasks of displaying <b>cached on disc</b> images (these tasks
		 * are executed quickly so UIL prefer to use separate executor for them).<br />
		 * <br />
		 * If you set the same executor for {@linkplain #taskExecutor(Executor) general tasks} and
		 * {@linkplain #taskExecutorForCachedImages(Executor) tasks about cached images} then these tasks will be in the
		 * same thread pool. So short-lived tasks can wait a long time for their turn.<br />
		 * <br />
		 * <b>NOTE:</b> If you set custom executor then following configuration options will not be considered for this
		 * executor:
		 * <ul>
		 * <li>{@link #threadPoolSize(int)}</li>
		 * <li>{@link #threadPriority(int)}</li>
		 * <li>{@link #tasksProcessingOrder(QueueProcessingType)}</li>
		 * </ul>
		 * 
		 * @see #taskExecutor(Executor)
		 */
		public Builder taskExecutorForCachedImages(Executor executorForCachedImages) {
			if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE || threadPriority != DEFAULT_THREAD_PRIORITY || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
				L.w(WARNING_OVERLAP_EXECUTOR);
			}

			this.taskExecutorForCachedImages = executorForCachedImages;
			return this;
		}

		/**
		 * Sets thread pool size for image display tasks.<br />
		 * Default value - {@link #DEFAULT_THREAD_POOL_SIZE this}
		 * */
		public Builder threadPoolSize(int threadPoolSize) {
			if (taskExecutor != null || taskExecutorForCachedImages != null) {
				L.w(WARNING_OVERLAP_EXECUTOR);
			}

			this.threadPoolSize = threadPoolSize;
			return this;
		}

		/**
		 * Sets the priority for image loading threads. Should be <b>NOT</b> greater than {@link Thread#MAX_PRIORITY} or
		 * less than {@link Thread#MIN_PRIORITY}<br />
		 * Default value - {@link #DEFAULT_THREAD_PRIORITY this}
		 * */
		public Builder threadPriority(int threadPriority) {
			if (taskExecutor != null || taskExecutorForCachedImages != null) {
				L.w(WARNING_OVERLAP_EXECUTOR);
			}

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
		 * Sets type of queue processing for tasks for loading and displaying images.<br />
		 * Default value - {@link QueueProcessingType#FIFO}
		 */
		public Builder tasksProcessingOrder(QueueProcessingType tasksProcessingType) {
			if (taskExecutor != null || taskExecutorForCachedImages != null) {
				L.w(WARNING_OVERLAP_EXECUTOR);
			}

			this.tasksProcessingType = tasksProcessingType;
			return this;
		}

		/**
		 * Sets maximum memory cache size for {@link android.graphics.Bitmap bitmaps} (in bytes).<br />
		 * Default value - 1/8 of available app memory.<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache LruMemoryCache} will be used as
		 * memory cache. You can use {@link #memoryCache(MemoryCacheAware)} method to set your own implementation of
		 * {@link MemoryCacheAware}.
		 */
		public Builder memoryCacheSize(int memoryCacheSize) {
			if (memoryCacheSize <= 0) throw new IllegalArgumentException("memoryCacheSize must be a positive number");

			if (memoryCache != null) {
				L.w(WARNING_OVERLAP_MEMORY_CACHE);
			}

			this.memoryCacheSize = memoryCacheSize;
			return this;
		}

		/**
		 * Sets memory cache for {@link android.graphics.Bitmap bitmaps}.<br />
		 * Default value - {@link com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache LruMemoryCache}
		 * with limited memory cache size (size = 1/8 of available app memory)<br />
		 * <br />
		 * <b>NOTE:</b> If you set custom memory cache then following configuration option will not be considered:
		 * <ul>
		 * <li>{@link #memoryCacheSize(int)}</li>
		 * </ul>
		 */
		public Builder memoryCache(MemoryCacheAware<String, Bitmap> memoryCache) {
			if (memoryCacheSize != 0) {
				L.w(WARNING_OVERLAP_MEMORY_CACHE);
			}

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

			if (discCache != null || discCacheFileCount > 0) {
				L.w(WARNING_OVERLAP_DISC_CACHE_PARAMS);
			}

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

			if (discCache != null || discCacheSize > 0) {
				L.w(WARNING_OVERLAP_DISC_CACHE_PARAMS);
			}

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
			if (discCache != null) {
				L.w(WARNING_OVERLAP_DISC_CACHE_NAME_GENERATOR);
			}

			this.discCacheFileNameGenerator = fileNameGenerator;
			return this;
		}

		/**
		 * Sets utility which will be responsible for downloading of image.<br />
		 * Default value -
		 * {@link com.nostra13.universalimageloader.core.DefaultConfigurationFactory#createImageDownloader(Context)
		 * DefaultConfigurationFactory.createImageDownloader()}
		 * */
		public Builder imageDownloader(ImageDownloader imageDownloader) {
			this.downloader = imageDownloader;
			return this;
		}

		/**
		 * Sets utility which will be responsible for decoding of image stream.<br />
		 * Default value -
		 * {@link com.nostra13.universalimageloader.core.DefaultConfigurationFactory#createImageDecoder(boolean)
		 * DefaultConfigurationFactory.createImageDecoder()}
		 * */
		public Builder imageDecoder(ImageDecoder imageDecoder) {
			this.decoder = imageDecoder;
			return this;
		}

		/**
		 * Sets disc cache for images.<br />
		 * Default value - {@link com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache
		 * UnlimitedDiscCache}. Cache directory is defined by
		 * {@link com.nostra13.universalimageloader.utils.StorageUtils#getCacheDirectory(Context)
		 * StorageUtils.getCacheDirectory(Context)}.<br />
		 * <br />
		 * <b>NOTE:</b> If you set custom disc cache then following configuration option will not be considered:
		 * <ul>
		 * <li>{@link #discCacheSize(int)}</li>
		 * <li>{@link #discCacheFileCount(int)}</li>
		 * <li>{@link #discCacheFileNameGenerator(FileNameGenerator)}</li>
		 * </ul>
		 */
		public Builder discCache(DiscCacheAware discCache) {
			if (discCacheSize > 0 || discCacheFileCount > 0) {
				L.w(WARNING_OVERLAP_DISC_CACHE_PARAMS);
			}
			if (discCacheFileNameGenerator != null) {
				L.w(WARNING_OVERLAP_DISC_CACHE_NAME_GENERATOR);
			}

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
			if (taskExecutor == null) {
				taskExecutor = DefaultConfigurationFactory.createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
			} else {
				customExecutor = true;
			}
			if (taskExecutorForCachedImages == null) {
				taskExecutorForCachedImages = DefaultConfigurationFactory.createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
			} else {
				customExecutorForCachedImages = true;
			}
			if (discCache == null) {
				if (discCacheFileNameGenerator == null) {
					discCacheFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
				}
				discCache = DefaultConfigurationFactory.createDiscCache(context, discCacheFileNameGenerator, discCacheSize, discCacheFileCount);
			}
			if (memoryCache == null) {
				memoryCache = DefaultConfigurationFactory.createMemoryCache(memoryCacheSize);
			}
			if (denyCacheImageMultipleSizesInMemory) {
				memoryCache = new FuzzyKeyMemoryCache<String, Bitmap>(memoryCache, MemoryCacheUtil.createFuzzyKeyComparator());
			}
			if (downloader == null) {
				downloader = DefaultConfigurationFactory.createImageDownloader(context);
			}
			if (decoder == null) {
				decoder = DefaultConfigurationFactory.createImageDecoder(loggingEnabled);
			}
			if (defaultDisplayImageOptions == null) {
				defaultDisplayImageOptions = DisplayImageOptions.createSimple();
			}
		}
	}
}
