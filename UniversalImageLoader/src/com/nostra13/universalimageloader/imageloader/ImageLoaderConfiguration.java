package com.nostra13.universalimageloader.imageloader;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.Constants;
import com.nostra13.universalimageloader.cache.disc.DiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedCache;
import com.nostra13.universalimageloader.utils.StorageUtils;

public final class ImageLoaderConfiguration {

	final int maxImageWidthForMemoryCache;
	final int maxImageHeightForMemoryCache;
	final int httpConnectTimeout;
	final int httpReadTimeout;
	final int threadPoolSize;
	final MemoryCache<String, Bitmap> memoryCache;
	final DiscCache discCache;
	final DisplayImageOptions defaultDisplayImageOptions;

	private ImageLoaderConfiguration(Builder builder) {
		maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
		maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
		httpConnectTimeout = builder.httpConnectTimeout;
		httpReadTimeout = builder.httpReadTimeout;
		threadPoolSize = builder.threadPoolSize;
		discCache = builder.discCache;
		memoryCache = builder.memoryCache;
		defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
	}

	public static ImageLoaderConfiguration createDefault(Context context) {
		return new Builder(context).build();
	}

	public static class Builder {

		private Context context;

		private int maxImageWidthForMemoryCache = Constants.DEFAULT_MAX_IMAGE_WIDTH;
		private int maxImageHeightForMemoryCache = Constants.DEFAULT_MAX_IMAGE_HEIGHT;
		private int httpConnectTimeout = Constants.DEFAULT_HTTP_CONNECTION_TIMEOUT;
		private int httpReadTimeout = Constants.DEFAULT_HTTP_READ_TIMEOUT;
		private int threadPoolSize = Constants.DEFAULT_THREAD_POOL_SIZE;
		private MemoryCache<String, Bitmap> memoryCache = null;
		private DiscCache discCache = null;
		private DisplayImageOptions defaultDisplayImageOptions = null;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Sets maximum image width which will be used for memory saving during decoding an image to {@link Bitmap}.<br />
		 * Default value - {@link Constants#DEFAULT_MAX_IMAGE_WIDTH}
		 * */
		public Builder maxImageWidthForMemoryCache(int maxImageWidthForMemoryCache) {
			this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
			return this;
		}

		/**
		 * Sets maximum image height which will be used for memory saving during decoding an image to {@link Bitmap}.<br />
		 * Default value - {@link Constants#DEFAULT_MAX_IMAGE_HEIGHT}
		 * */
		public Builder maxImageHeightForMemoryCache(int maxImageHeightForMemoryCache) {
			this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
			return this;
		}

		/**
		 * Sets timeout for HTTP connection establishment (during image loading).<br />
		 * Default value - {@link Constants#DEFAULT_HTTP_CONNECTION_TIMEOUT}
		 * */
		public Builder httpConnectTimeout(int timeout) {
			httpConnectTimeout = timeout;
			return this;
		}

		/**
		 * Sets timeout for HTTP reading (during image loading).<br />
		 * Default value - {@link Constants#DEFAULT_HTTP_READ_TIMEOUT}
		 * */
		public Builder httpReadTimeout(int timeout) {
			httpReadTimeout = timeout;
			return this;
		}

		/**
		 * Sets thread pool size for image display tasks.<br />
		 * Default value - {@link Constants#DEFAULT_THREAD_POOL_SIZE}
		 * */
		public Builder threadPoolSize(int threadPoolSize) {
			this.threadPoolSize = threadPoolSize;
			return this;
		}

		/**
		 * Sets memory cache for {@link Bitmap bitmaps}.<br />
		 * Default value - {@link UsingFreqLimitedCache} with limited memory cache size (
		 * {@link Constants#DEFAULT_MEMORY_CACHE_SIZE} bytes)
		 */
		public Builder memoryCache(MemoryCache<String, Bitmap> memoryCache) {
			this.memoryCache = memoryCache;
			return this;
		}

		/**
		 * Sets memory cache for {@link Bitmap bitmaps}.<br />
		 * Default value - {@link DefaultDiscCache}
		 */
		public Builder discCache(DiscCache discCache) {
			this.discCache = discCache;
			return this;
		}

		/**
		 * Sets default {@linkplain DisplayImageOptions display image options} for image displaying. It will be used for
		 * every {@linkplain ImageLoader#displayImage(String, android.widget.ImageView) image display call} without
		 * defined custom {@linkplain DisplayImageOptions options}<br />
		 * Default value - {@link DisplayImageOptions#createSimple() Simple options}
		 */
		public Builder defaultDisplayImageOptions(DisplayImageOptions defaultDisplayImageOptions) {
			this.defaultDisplayImageOptions = defaultDisplayImageOptions;
			return this;
		}

		/** Builds configured {@link ImageLoaderConfiguration} object */
		public ImageLoaderConfiguration build() {
			initEmptyFiledsWithDefaultValues();
			context = null;
			return new ImageLoaderConfiguration(this);
		}

		private void initEmptyFiledsWithDefaultValues() {
			if (discCache == null) {
				File cacheDir = StorageUtils.getCacheDirectory(context, Constants.DEFAULT_CACHE_DIRECTORY);
				discCache = new DefaultDiscCache(cacheDir);
			}
			if (memoryCache == null) {
				memoryCache = new UsingFreqLimitedCache(Constants.DEFAULT_MEMORY_CACHE_SIZE);
			}
			if (defaultDisplayImageOptions == null) {
				defaultDisplayImageOptions = DisplayImageOptions.createSimple();
			}
		}
	}
}
