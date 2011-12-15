package com.nostra13.universalimageloader.imageloader;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.DiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedCache;
import com.nostra13.universalimageloader.utils.StorageUtils;

public final class ImageLoaderConfiguration {

	// Default values
	private static final String APP_CACHE_DIRECTORY = "UniversalImageLoader/Cache";
	private static final int MEMORY_CACHE_SIZE = 2000000; // bytes

	// Configuration fields
	final int maxImageWidthForMemoryCache;
	final int maxImageHeightForMemoryCache;
	final int httpConnectTimeout;
	final int httpReadTimeout;
	final int threadPoolSize;
	final DiscCache discCache;
	final MemoryCache<String, Bitmap> memoryCache;
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

		private int maxImageWidthForMemoryCache = 800;
		private int maxImageHeightForMemoryCache = 480;
		private int httpConnectTimeout = 5000; // milliseconds
		private int httpReadTimeout = 20000; // milliseconds
		private int threadPoolSize = 5;
		private DiscCache discCache = null;
		private MemoryCache<String, Bitmap> memoryCache = null;
		private DisplayImageOptions defaultDisplayImageOptions = null;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder maxImageWidthForMemoryCache(int maxImageWidthForMemoryCache) {
			this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
			return this;
		}

		public Builder maxImageHeightForMemoryCache(int maxImageHeightForMemoryCache) {
			this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
			return this;
		}

		public Builder httpConnectTimeout(int timeout) {
			httpConnectTimeout = timeout;
			return this;
		}

		public Builder httpReadTimeout(int timeout) {
			httpReadTimeout = timeout;
			return this;
		}

		public Builder threadPoolSize(int threadPoolSize) {
			this.threadPoolSize = threadPoolSize;
			return this;
		}

		public Builder discCache(DiscCache discCache) {
			this.discCache = discCache;
			return this;
		}

		public Builder memoryCache(MemoryCache<String, Bitmap> memoryCache) {
			this.memoryCache = memoryCache;
			return this;
		}

		public Builder defaultDisplayImageOptions(DisplayImageOptions defaultDisplayImageOptions) {
			this.defaultDisplayImageOptions = defaultDisplayImageOptions;
			return this;
		}

		public ImageLoaderConfiguration build() {
			initEmptyFiledsWithDefaultValues();
			context = null;
			return new ImageLoaderConfiguration(this);
		}

		private void initEmptyFiledsWithDefaultValues() {
			if (discCache == null) {
				File cacheDir = StorageUtils.getCacheDirectory(context, APP_CACHE_DIRECTORY);
				discCache = new DefaultDiscCache(cacheDir);
			}
			if (memoryCache == null) {
				memoryCache = new UsingFreqLimitedCache(MEMORY_CACHE_SIZE);
			}
			if (defaultDisplayImageOptions == null) {
				defaultDisplayImageOptions = DisplayImageOptions.createSimple();
			}
		}
	}
}
