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

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Contains options for image display. Defines:
 * <ul>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} during image loading</li>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} if empty URI is passed</li>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} if image loading fails</li>
 * <li>whether {@link android.widget.ImageView ImageView} should be reset before image loading start</li>
 * <li>whether loaded image will be cached in memory</li>
 * <li>whether loaded image will be cached on disc</li>
 * <li>image scale type</li>
 * <li>bitmap decoding configuration</li>
 * <li>delay before loading of image</li>
 * <li>auxiliary object which will be passed to {@link ImageDownloader#getStream(java.net.URI, Object) ImageDownloader}</li>
 * <li>pre-processor for image Bitmap (before caching in memory)</li>
 * <li>post-processor for image Bitmap (after caching in memory, before displaying)</li>
 * <li>how decoded {@link Bitmap} will be displayed</li>
 * </ul>
 * 
 * You can create instance:
 * <ul>
 * <li>with {@link Builder}:<br />
 * <b>i.e.</b> :
 * <code>new {@link DisplayImageOptions}.{@link Builder#Builder() Builder()}.{@link Builder#cacheInMemory() cacheInMemory()}.
 * {@link Builder#showStubImage(int) showStubImage()}.{@link Builder#build() build()}</code><br />
 * </li>
 * <li>or by static method: {@link #createSimple()}</li> <br />
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class DisplayImageOptions {

	private final int stubImage;
	private final int imageForEmptyUri;
	private final int imageOnFail;
	private final boolean resetViewBeforeLoading;
	private final boolean cacheInMemory;
	private final boolean cacheOnDisc;
	private final ImageScaleType imageScaleType;
	private final Bitmap.Config bitmapConfig;
	private final int delayBeforeLoading;
	private final Object extraForDownloader;
	private final BitmapProcessor preProcessor;
	private final BitmapProcessor postProcessor;
	private final BitmapDisplayer displayer;

	private DisplayImageOptions(Builder builder) {
		stubImage = builder.stubImage;
		imageForEmptyUri = builder.imageForEmptyUri;
		imageOnFail = builder.imageOnFail;
		resetViewBeforeLoading = builder.resetViewBeforeLoading;
		cacheInMemory = builder.cacheInMemory;
		cacheOnDisc = builder.cacheOnDisc;
		imageScaleType = builder.imageScaleType;
		bitmapConfig = builder.bitmapConfig;
		delayBeforeLoading = builder.delayBeforeLoading;
		extraForDownloader = builder.extraForDownloader;
		preProcessor = builder.preProcessor;
		postProcessor = builder.postProcessor;
		displayer = builder.displayer;
	}

	boolean shouldShowStubImage() {
		return stubImage != 0;
	}

	boolean shouldShowImageForEmptyUri() {
		return imageForEmptyUri != 0;
	}

	boolean shouldShowImageOnFail() {
		return imageOnFail != 0;
	}

	boolean shouldPreProcess() {
		return preProcessor != null;
	}

	boolean shouldPostProcess() {
		return postProcessor != null;
	}

	boolean shouldDelayBeforeLoading() {
		return delayBeforeLoading > 0;
	}

	int getStubImage() {
		return stubImage;
	}

	int getImageForEmptyUri() {
		return imageForEmptyUri;
	}

	int getImageOnFail() {
		return imageOnFail;
	}

	boolean isResetViewBeforeLoading() {
		return resetViewBeforeLoading;
	}

	boolean isCacheInMemory() {
		return cacheInMemory;
	}

	boolean isCacheOnDisc() {
		return cacheOnDisc;
	}

	ImageScaleType getImageScaleType() {
		return imageScaleType;
	}

	Bitmap.Config getBitmapConfig() {
		return bitmapConfig;
	}

	int getDelayBeforeLoading() {
		return delayBeforeLoading;
	}

	Object getExtraForDownloader() {
		return extraForDownloader;
	}

	BitmapProcessor getPreProcessor() {
		return preProcessor;
	}

	BitmapProcessor getPostProcessor() {
		return postProcessor;
	}

	BitmapDisplayer getDisplayer() {
		return displayer;
	}

	/**
	 * Builder for {@link DisplayImageOptions}
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 */
	public static class Builder {
		private int stubImage = 0;
		private int imageForEmptyUri = 0;
		private int imageOnFail = 0;
		private boolean resetViewBeforeLoading = false;
		private boolean cacheInMemory = false;
		private boolean cacheOnDisc = false;
		private ImageScaleType imageScaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2;
		private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
		private int delayBeforeLoading = 0;
		private Object extraForDownloader = null;
		private BitmapProcessor preProcessor = null;
		private BitmapProcessor postProcessor = null;
		private BitmapDisplayer displayer = DefaultConfigurationFactory.createBitmapDisplayer();

		/**
		 * Stub image will be displayed in {@link android.widget.ImageView ImageView} during image loading
		 * 
		 * @param stubImageRes Stub image resource
		 */
		public Builder showStubImage(int stubImageRes) {
			stubImage = stubImageRes;
			return this;
		}

		/**
		 * Incoming image will be displayed in {@link android.widget.ImageView ImageView} if empty URI (null or empty
		 * string) will be passed to <b>ImageLoader.displayImage(...)</b> method.
		 * 
		 * @param imageRes Image resource
		 */
		public Builder showImageForEmptyUri(int imageRes) {
			imageForEmptyUri = imageRes;
			return this;
		}

		/**
		 * Incoming image will be displayed in {@link android.widget.ImageView ImageView} if some error occurs during
		 * requested image loading/decoding.
		 * 
		 * @param imageRes Image resource
		 */
		public Builder showImageOnFail(int imageRes) {
			imageOnFail = imageRes;
			return this;
		}

		/** {@link android.widget.ImageView ImageView} will be reset (set <b>null</b>) before image loading start */
		public Builder resetViewBeforeLoading() {
			resetViewBeforeLoading = true;
			return this;
		}

		/** Loaded image will be cached in memory */
		public Builder cacheInMemory() {
			cacheInMemory = true;
			return this;
		}

		/** Loaded image will be cached on disc */
		public Builder cacheOnDisc() {
			cacheOnDisc = true;
			return this;
		}

		/**
		 * Sets {@link ImageScaleType decoding type} for image loading task. Default value -
		 * {@link ImageScaleType#POWER_OF_2}
		 */
		public Builder imageScaleType(ImageScaleType imageScaleType) {
			this.imageScaleType = imageScaleType;
			return this;
		}

		/** Sets {@link Bitmap.Config bitmap config} for image decoding. Default value - {@link Bitmap.Config#ARGB_8888} */
		public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
			this.bitmapConfig = bitmapConfig;
			return this;
		}

		/** Sets delay time before starting loading task. Default - no delay. */
		public Builder delayBeforeLoading(int delayInMillis) {
			this.delayBeforeLoading = delayInMillis;
			return this;
		}

		/** Sets auxiliary object which will be passed to {@link ImageDownloader#getStream(java.net.URI, Object)} */
		public Builder extraForDownloader(Object extra) {
			this.extraForDownloader = extra;
			return this;
		}

		/**
		 * Sets bitmap processor which will be process bitmaps before they will be cached in memory. So memory cache
		 * will contain bitmap processed by incoming preProcessor.<br />
		 * Image will be pre-processed even if caching in memory is disabled.
		 */
		public Builder preProcessor(BitmapProcessor preProcessor) {
			this.preProcessor = preProcessor;
			return this;
		}

		/**
		 * Sets bitmap processor which will be process bitmaps before they will be displayed in {@link ImageView} but
		 * after they'll have been saved in memory cache.
		 */
		public Builder postProcessor(BitmapProcessor postProcessor) {
			this.postProcessor = postProcessor;
			return this;
		}

		/**
		 * Sets custom {@link BitmapDisplayer displayer} for image loading task. Default value -
		 * {@link DefaultConfigurationFactory#createBitmapDisplayer()}
		 */
		public Builder displayer(BitmapDisplayer displayer) {
			this.displayer = displayer;
			return this;
		}

		/** Sets all options equal to incoming options */
		public Builder cloneFrom(DisplayImageOptions options) {
			stubImage = options.stubImage;
			imageForEmptyUri = options.imageForEmptyUri;
			imageOnFail = options.imageOnFail;
			resetViewBeforeLoading = options.resetViewBeforeLoading;
			cacheInMemory = options.cacheInMemory;
			cacheOnDisc = options.cacheOnDisc;
			imageScaleType = options.imageScaleType;
			bitmapConfig = options.bitmapConfig;
			delayBeforeLoading = options.delayBeforeLoading;
			extraForDownloader = options.extraForDownloader;
			preProcessor = options.preProcessor;
			postProcessor = options.postProcessor;
			displayer = options.displayer;
			return this;
		}

		/** Builds configured {@link DisplayImageOptions} object */
		public DisplayImageOptions build() {
			return new DisplayImageOptions(this);
		}
	}

	/**
	 * Creates options appropriate for single displaying:
	 * <ul>
	 * <li>View will <b>not</b> be reset before loading</li>
	 * <li>Loaded image will <b>not</b> be cached in memory</li>
	 * <li>Loaded image will <b>not</b> be cached on disc</li>
	 * <li>{@link ImageScaleType#IN_SAMPLE_POWER_OF_2} decoding type will be used</li>
	 * <li>{@link Bitmap.Config#ARGB_8888} bitmap config will be used for image decoding</li>
	 * <li>{@link SimpleBitmapDisplayer} will be used for image displaying</li>
	 * </ul>
	 * 
	 * These option are appropriate for simple single-use image (from drawables or from Internet) displaying.
	 */
	public static DisplayImageOptions createSimple() {
		return new Builder().build();
	}
}
