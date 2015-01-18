/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
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

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.L;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

/**
 * Presents configuration for {@link ImageLoader}
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoader
 * @see MemoryCache
 * @see DiskCache
 * @see DisplayImageOptions
 * @see ImageDownloader
 * @see FileNameGenerator
 * @since 1.0.0
 */
public final class ImageLoaderConfiguration {

	final Resources resources;

	final int maxImageWidthForMemoryCache;
	final int maxImageHeightForMemoryCache;
	final int maxImageWidthForDiskCache;
	final int maxImageHeightForDiskCache;
	final BitmapProcessor processorForDiskCache;

	final Executor taskExecutor;
	final Executor taskExecutorForCachedImages;
	final boolean customExecutor;
	final boolean customExecutorForCachedImages;

	final int threadPoolSize;
	final int threadPriority;
	final QueueProcessingType tasksProcessingType;

	final MemoryCache memoryCache;
	final DiskCache diskCache;
	final ImageDownloader downloader;
	final ImageDecoder decoder;
	final DisplayImageOptions defaultDisplayImageOptions;

	final ImageDownloader networkDeniedDownloader;
	final ImageDownloader slowNetworkDownloader;

	private ImageLoaderConfiguration(final Builder builder) {
		resources = builder.context.getResources();
		maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
		maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
		maxImageWidthForDiskCache = builder.maxImageWidthForDiskCache;
		maxImageHeightForDiskCache = builder.maxImageHeightForDiskCache;
		processorForDiskCache = builder.processorForDiskCache;
		taskExecutor = builder.taskExecutor;
		taskExecutorForCachedImages = builder.taskExecutorForCachedImages;
		threadPoolSize = builder.threadPoolSize;
		threadPriority = builder.threadPriority;
		tasksProcessingType = builder.tasksProcessingType;
		diskCache = builder.diskCache;
		memoryCache = builder.memoryCache;
		defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
		downloader = builder.downloader;
		decoder = builder.decoder;

		customExecutor = builder.customExecutor;
		customExecutorForCachedImages = builder.customExecutorForCachedImages;

		networkDeniedDownloader = new NetworkDeniedImageDownloader(downloader);
		slowNetworkDownloader = new SlowNetworkImageDownloader(downloader);

		L.writeDebugLogs(builder.writeLogs);
	}

	/**
	 * Creates default configuration for {@link ImageLoader} <br />
	 * <b>Default values:</b>
	 * <ul>
	 * <li>maxImageWidthForMemoryCache = device's screen width</li>
	 * <li>maxImageHeightForMemoryCache = device's screen height</li>
	 * <li>maxImageWidthForDikcCache = unlimited</li>
	 * <li>maxImageHeightForDiskCache = unlimited</li>
	 * <li>threadPoolSize = {@link Builder#DEFAULT_THREAD_POOL_SIZE this}</li>
	 * <li>threadPriority = {@link Builder#DEFAULT_THREAD_PRIORITY this}</li>
	 * <li>allow to cache different sizes of image in memory</li>
	 * <li>memoryCache = {@link DefaultConfigurationFactory#createMemoryCache(android.content.Context, int)}</li>
	 * <li>diskCache = {@link com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache}</li>
	 * <li>imageDownloader = {@link DefaultConfigurationFactory#createImageDownloader(Context)}</li>
	 * <li>imageDecoder = {@link DefaultConfigurationFactory#createImageDecoder(boolean)}</li>
	 * <li>diskCacheFileNameGenerator = {@link DefaultConfigurationFactory#createFileNameGenerator()}</li>
	 * <li>defaultDisplayImageOptions = {@link DisplayImageOptions#createSimple() Simple options}</li>
	 * <li>tasksProcessingOrder = {@link QueueProcessingType#FIFO}</li>
	 * <li>detailed logging disabled</li>
	 * </ul>
	 */
	public static ImageLoaderConfiguration createDefault(Context context) {
		return new Builder(context).build();
	}

	ImageSize getMaxImageSize() {
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();

		int width = maxImageWidthForMemoryCache;
		if (width <= 0) {
			width = displayMetrics.widthPixels;
		}
		int height = maxImageHeightForMemoryCache;
		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}
		return new ImageSize(width, height);
	}

	/**
	 * Builder for {@link ImageLoaderConfiguration}
	 *
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 */
	public static class Builder {

		private static final String WARNING_OVERLAP_DISK_CACHE_PARAMS = "diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other";
		private static final String WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR = "diskCache() and diskCacheFileNameGenerator() calls overlap each other";
		private static final String WARNING_OVERLAP_MEMORY_CACHE = "memoryCache() and memoryCacheSize() calls overlap each other";
		private static final String WARNING_OVERLAP_EXECUTOR = "threadPoolSize(), threadPriority() and tasksProcessingOrder() calls "
				+ "can overlap taskExecutor() and taskExecutorForCachedImages() calls.";

		/** {@value} */
		public static final int DEFAULT_THREAD_POOL_SIZE = 3;
		/** {@value} */
		public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;
		/** {@value} */
		public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;

		private Context context;

		private int maxImageWidthForMemoryCache = 0;
		private int maxImageHeightForMemoryCache = 0;
		private int maxImageWidthForDiskCache = 0;
		private int maxImageHeightForDiskCache = 0;
		private BitmapProcessor processorForDiskCache = null;

		private Executor taskExecutor = null;
		private Executor taskExecutorForCachedImages = null;
		private boolean customExecutor = false;
		private boolean customExecutorForCachedImages = false;

		private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
		private int threadPriority = DEFAULT_THREAD_PRIORITY;
		private boolean denyCacheImageMultipleSizesInMemory = false;
		private QueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;

		private int memoryCacheSize = 0;
		private long diskCacheSize = 0;
		private int diskCacheFileCount = 0;

		private MemoryCache memoryCache = null;
		private DiskCache diskCache = null;
		private FileNameGenerator diskCacheFileNameGenerator = null;
		private ImageDownloader downloader = null;
		private ImageDecoder decoder;
		private DisplayImageOptions defaultDisplayImageOptions = null;

		private boolean writeLogs = false;

		public Builder(Context context) {
			this.context = context.getApplicationContext();
		}

		/**
		 * Sets options for memory cache
		 *
		 * @param maxImageWidthForMemoryCache  Maximum image width which will be used for memory saving during decoding
		 *                                     an image to {@link android.graphics.Bitmap Bitmap}. <b>Default value - device's screen width</b>
		 * @param maxImageHeightForMemoryCache Maximum image height which will be used for memory saving during decoding
		 *                                     an image to {@link android.graphics.Bitmap Bitmap}. <b>Default value</b> - device's screen height
		 */
		public Builder memoryCacheExtraOptions(int maxImageWidthForMemoryCache, int maxImageHeightForMemoryCache) {
			this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
			this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
			return this;
		}

		/**
		 * @deprecated Use
		 * {@link #diskCacheExtraOptions(int, int, com.nostra13.universalimageloader.core.process.BitmapProcessor)}
		 * instead
		 */
		@Deprecated
		public Builder discCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache,
				BitmapProcessor processorForDiskCache) {
			return diskCacheExtraOptions(maxImageWidthForDiskCache, maxImageHeightForDiskCache, processorForDiskCache);
		}

		/**
		 * Sets options for resizing/compressing of downloaded images before saving to disk cache.<br />
		 * <b>NOTE: Use this option only when you have appropriate needs. It can make ImageLoader slower.</b>
		 *
		 * @param maxImageWidthForDiskCache  Maximum width of downloaded images for saving at disk cache
		 * @param maxImageHeightForDiskCache Maximum height of downloaded images for saving at disk cache
		 * @param processorForDiskCache      null-ok; {@linkplain BitmapProcessor Bitmap processor} which process images before saving them in disc cache
		 */
		public Builder diskCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache,
				BitmapProcessor processorForDiskCache) {
			this.maxImageWidthForDiskCache = maxImageWidthForDiskCache;
			this.maxImageHeightForDiskCache = maxImageHeightForDiskCache;
			this.processorForDiskCache = processorForDiskCache;
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
		 * Sets custom {@linkplain Executor executor} for tasks of displaying <b>cached on disk</b> images (these tasks
		 * are executed quickly so UIL prefer to use separate executor for them).<br />
		 * <br />
		 * If you set the same executor for {@linkplain #taskExecutor(Executor) general tasks} and
		 * tasks about cached images (this method) then these tasks will be in the
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
		 */
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
		 */
		public Builder threadPriority(int threadPriority) {
			if (taskExecutor != null || taskExecutorForCachedImages != null) {
				L.w(WARNING_OVERLAP_EXECUTOR);
			}

			if (threadPriority < Thread.MIN_PRIORITY) {
				this.threadPriority = Thread.MIN_PRIORITY;
			} else {
				if (threadPriority > Thread.MAX_PRIORITY) {
					this.threadPriority = Thread.MAX_PRIORITY;
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
		 */
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
		 * memory cache. You can use {@link #memoryCache(MemoryCache)} method to set your own implementation of
		 * {@link MemoryCache}.
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
		 * Sets maximum memory cache size (in percent of available app memory) for {@link android.graphics.Bitmap
		 * bitmaps}.<br />
		 * Default value - 1/8 of available app memory.<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache LruMemoryCache} will be used as
		 * memory cache. You can use {@link #memoryCache(MemoryCache)} method to set your own implementation of
		 * {@link MemoryCache}.
		 */
		public Builder memoryCacheSizePercentage(int availableMemoryPercent) {
			if (availableMemoryPercent <= 0 || availableMemoryPercent >= 100) {
				throw new IllegalArgumentException("availableMemoryPercent must be in range (0 < % < 100)");
			}

			if (memoryCache != null) {
				L.w(WARNING_OVERLAP_MEMORY_CACHE);
			}

			long availableMemory = Runtime.getRuntime().maxMemory();
			memoryCacheSize = (int) (availableMemory * (availableMemoryPercent / 100f));
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
		public Builder memoryCache(MemoryCache memoryCache) {
			if (memoryCacheSize != 0) {
				L.w(WARNING_OVERLAP_MEMORY_CACHE);
			}

			this.memoryCache = memoryCache;
			return this;
		}

		/** @deprecated Use {@link #diskCacheSize(int)} instead */
		@Deprecated
		public Builder discCacheSize(int maxCacheSize) {
			return diskCacheSize(maxCacheSize);
		}

		/**
		 * Sets maximum disk cache size for images (in bytes).<br />
		 * By default: disk cache is unlimited.<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache LruDiskCache}
		 * will be used as disk cache. You can use {@link #diskCache(DiskCache)} method for introduction your own
		 * implementation of {@link DiskCache}
		 */
		public Builder diskCacheSize(int maxCacheSize) {
			if (maxCacheSize <= 0) throw new IllegalArgumentException("maxCacheSize must be a positive number");

			if (diskCache != null) {
				L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
			}

			this.diskCacheSize = maxCacheSize;
			return this;
		}

		/** @deprecated Use {@link #diskCacheFileCount(int)} instead */
		@Deprecated
		public Builder discCacheFileCount(int maxFileCount) {
			return diskCacheFileCount(maxFileCount);
		}

		/**
		 * Sets maximum file count in disk cache directory.<br />
		 * By default: disk cache is unlimited.<br />
		 * <b>NOTE:</b> If you use this method then
		 * {@link com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache LruDiskCache}
		 * will be used as disk cache. You can use {@link #diskCache(DiskCache)} method for introduction your own
		 * implementation of {@link DiskCache}
		 */
		public Builder diskCacheFileCount(int maxFileCount) {
			if (maxFileCount <= 0) throw new IllegalArgumentException("maxFileCount must be a positive number");

			if (diskCache != null) {
				L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
			}

			this.diskCacheFileCount = maxFileCount;
			return this;
		}

		/** @deprecated Use {@link #diskCacheFileNameGenerator(com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator)} */
		@Deprecated
		public Builder discCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
			return diskCacheFileNameGenerator(fileNameGenerator);
		}

		/**
		 * Sets name generator for files cached in disk cache.<br />
		 * Default value -
		 * {@link com.nostra13.universalimageloader.core.DefaultConfigurationFactory#createFileNameGenerator()
		 * DefaultConfigurationFactory.createFileNameGenerator()}
		 */
		public Builder diskCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
			if (diskCache != null) {
				L.w(WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR);
			}

			this.diskCacheFileNameGenerator = fileNameGenerator;
			return this;
		}

		/** @deprecated Use {@link #diskCache(com.nostra13.universalimageloader.cache.disc.DiskCache)} */
		@Deprecated
		public Builder discCache(DiskCache diskCache) {
			return diskCache(diskCache);
		}

		/**
		 * Sets disk cache for images.<br />
		 * Default value - {@link com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache
		 * UnlimitedDiskCache}. Cache directory is defined by
		 * {@link com.nostra13.universalimageloader.utils.StorageUtils#getCacheDirectory(Context)
		 * StorageUtils.getCacheDirectory(Context)}.<br />
		 * <br />
		 * <b>NOTE:</b> If you set custom disk cache then following configuration option will not be considered:
		 * <ul>
		 * <li>{@link #diskCacheSize(int)}</li>
		 * <li>{@link #diskCacheFileCount(int)}</li>
		 * <li>{@link #diskCacheFileNameGenerator(FileNameGenerator)}</li>
		 * </ul>
		 */
		public Builder diskCache(DiskCache diskCache) {
			if (diskCacheSize > 0 || diskCacheFileCount > 0) {
				L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
			}
			if (diskCacheFileNameGenerator != null) {
				L.w(WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR);
			}

			this.diskCache = diskCache;
			return this;
		}

		/**
		 * Sets utility which will be responsible for downloading of image.<br />
		 * Default value -
		 * {@link com.nostra13.universalimageloader.core.DefaultConfigurationFactory#createImageDownloader(Context)
		 * DefaultConfigurationFactory.createImageDownloader()}
		 */
		public Builder imageDownloader(ImageDownloader imageDownloader) {
			this.downloader = imageDownloader;
			return this;
		}

		/**
		 * Sets utility which will be responsible for decoding of image stream.<br />
		 * Default value -
		 * {@link com.nostra13.universalimageloader.core.DefaultConfigurationFactory#createImageDecoder(boolean)
		 * DefaultConfigurationFactory.createImageDecoder()}
		 */
		public Builder imageDecoder(ImageDecoder imageDecoder) {
			this.decoder = imageDecoder;
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

		/**
		 * Enables detail logging of {@link ImageLoader} work. To prevent detail logs don't call this method.
		 * Consider {@link com.nostra13.universalimageloader.utils.L#disableLogging()} to disable
		 * ImageLoader logging completely (even error logs)
		 */
		public Builder writeDebugLogs() {
			this.writeLogs = true;
			return this;
		}

		/** Builds configured {@link ImageLoaderConfiguration} object */
		public ImageLoaderConfiguration build() {
			initEmptyFieldsWithDefaultValues();
			return new ImageLoaderConfiguration(this);
		}

		private void initEmptyFieldsWithDefaultValues() {
			if (taskExecutor == null) {
				taskExecutor = DefaultConfigurationFactory
						.createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
			} else {
				customExecutor = true;
			}
			if (taskExecutorForCachedImages == null) {
				taskExecutorForCachedImages = DefaultConfigurationFactory
						.createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
			} else {
				customExecutorForCachedImages = true;
			}
			if (diskCache == null) {
				if (diskCacheFileNameGenerator == null) {
					diskCacheFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
				}
				diskCache = DefaultConfigurationFactory
						.createDiskCache(context, diskCacheFileNameGenerator, diskCacheSize, diskCacheFileCount);
			}
			if (memoryCache == null) {
				memoryCache = DefaultConfigurationFactory.createMemoryCache(context, memoryCacheSize);
			}
			if (denyCacheImageMultipleSizesInMemory) {
				memoryCache = new FuzzyKeyMemoryCache(memoryCache, MemoryCacheUtils.createFuzzyKeyComparator());
			}
			if (downloader == null) {
				downloader = DefaultConfigurationFactory.createImageDownloader(context);
			}
			if (decoder == null) {
				decoder = DefaultConfigurationFactory.createImageDecoder(writeLogs);
			}
			if (defaultDisplayImageOptions == null) {
				defaultDisplayImageOptions = DisplayImageOptions.createSimple();
			}
		}
	}

	/**
	 * Decorator. Prevents downloads from network (throws {@link IllegalStateException exception}).<br />
	 * In most cases this downloader shouldn't be used directly.
	 *
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 * @since 1.8.0
	 */
	private static class NetworkDeniedImageDownloader implements ImageDownloader {

		private final ImageDownloader wrappedDownloader;

		public NetworkDeniedImageDownloader(ImageDownloader wrappedDownloader) {
			this.wrappedDownloader = wrappedDownloader;
		}

		@Override
		public InputStream getStream(String imageUri, Object extra) throws IOException {
			switch (Scheme.ofUri(imageUri)) {
				case HTTP:
				case HTTPS:
					throw new IllegalStateException();
				default:
					return wrappedDownloader.getStream(imageUri, extra);
			}
		}
	}

	/**
	 * Decorator. Handles <a href="http://code.google.com/p/android/issues/detail?id=6066">this problem</a> on slow networks
	 * using {@link com.nostra13.universalimageloader.core.assist.FlushedInputStream}.
	 *
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 * @since 1.8.1
	 */
	private static class SlowNetworkImageDownloader implements ImageDownloader {

		private final ImageDownloader wrappedDownloader;

		public SlowNetworkImageDownloader(ImageDownloader wrappedDownloader) {
			this.wrappedDownloader = wrappedDownloader;
		}

		@Override
		public InputStream getStream(String imageUri, Object extra) throws IOException {
			InputStream imageStream = wrappedDownloader.getStream(imageUri, extra);
			switch (Scheme.ofUri(imageUri)) {
				case HTTP:
				case HTTPS:
					return new FlushedInputStream(imageStream);
				default:
					return imageStream;
			}
		}
	}
}
