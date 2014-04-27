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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Contains options for image display. Defines:
 * <ul>
 * <li>whether stub image will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
 * image aware view} during image loading</li>
 * <li>whether stub image will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
 * image aware view} if empty URI is passed</li>
 * <li>whether stub image will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
 * image aware view} if image loading fails</li>
 * <li>whether {@link com.nostra13.universalimageloader.core.imageaware.ImageAware image aware view} should be reset
 * before image loading start</li>
 * <li>whether loaded image will be cached in memory</li>
 * <li>whether loaded image will be cached on disk</li>
 * <li>image scale type</li>
 * <li>decoding options (including bitmap decoding configuration)</li>
 * <li>delay before loading of image</li>
 * <li>whether consider EXIF parameters of image</li>
 * <li>auxiliary object which will be passed to {@link ImageDownloader#getStream(String, Object) ImageDownloader}</li>
 * <li>pre-processor for image Bitmap (before caching in memory)</li>
 * <li>post-processor for image Bitmap (after caching in memory, before displaying)</li>
 * <li>how decoded {@link Bitmap} will be displayed</li>
 * </ul>
 * <p/>
 * You can create instance:
 * <ul>
 * <li>with {@link Builder}:<br />
 * <b>i.e.</b> :
 * <code>new {@link DisplayImageOptions}.{@link Builder#Builder() Builder()}.{@link Builder#cacheInMemory() cacheInMemory()}.
 * {@link Builder#showImageOnLoading(int) showImageOnLoading()}.{@link Builder#build() build()}</code><br />
 * </li>
 * <li>or by static method: {@link #createSimple()}</li> <br />
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class DisplayImageOptions {

	private final int imageResOnLoading;
	private final int imageResForEmptyUri;
	private final int imageResOnFail;
	private final Drawable imageOnLoading;
	private final Drawable imageForEmptyUri;
	private final Drawable imageOnFail;
	private final boolean resetViewBeforeLoading;
	private final boolean cacheInMemory;
	private final boolean cacheOnDisk;
	private final ImageScaleType imageScaleType;
	private final Options decodingOptions;
	private final int delayBeforeLoading;
	private final boolean considerExifParams;
	private final Object extraForDownloader;
	private final BitmapProcessor preProcessor;
	private final BitmapProcessor postProcessor;
	private final BitmapDisplayer displayer;
	private final Handler handler;
	private final boolean isSyncLoading;

	private DisplayImageOptions(Builder builder) {
		imageResOnLoading = builder.imageResOnLoading;
		imageResForEmptyUri = builder.imageResForEmptyUri;
		imageResOnFail = builder.imageResOnFail;
		imageOnLoading = builder.imageOnLoading;
		imageForEmptyUri = builder.imageForEmptyUri;
		imageOnFail = builder.imageOnFail;
		resetViewBeforeLoading = builder.resetViewBeforeLoading;
		cacheInMemory = builder.cacheInMemory;
		cacheOnDisk = builder.cacheOnDisk;
		imageScaleType = builder.imageScaleType;
		decodingOptions = builder.decodingOptions;
		delayBeforeLoading = builder.delayBeforeLoading;
		considerExifParams = builder.considerExifParams;
		extraForDownloader = builder.extraForDownloader;
		preProcessor = builder.preProcessor;
		postProcessor = builder.postProcessor;
		displayer = builder.displayer;
		handler = builder.handler;
		isSyncLoading = builder.isSyncLoading;
	}

	public boolean shouldShowImageOnLoading() {
		return imageOnLoading != null || imageResOnLoading != 0;
	}

	public boolean shouldShowImageForEmptyUri() {
		return imageForEmptyUri != null || imageResForEmptyUri != 0;
	}

	public boolean shouldShowImageOnFail() {
		return imageOnFail != null || imageResOnFail != 0;
	}

	public boolean shouldPreProcess() {
		return preProcessor != null;
	}

	public boolean shouldPostProcess() {
		return postProcessor != null;
	}

	public boolean shouldDelayBeforeLoading() {
		return delayBeforeLoading > 0;
	}

	public Drawable getImageOnLoading(Resources res) {
		return imageResOnLoading != 0 ? res.getDrawable(imageResOnLoading) : imageOnLoading;
	}

	public Drawable getImageForEmptyUri(Resources res) {
		return imageResForEmptyUri != 0 ? res.getDrawable(imageResForEmptyUri) : imageForEmptyUri;
	}

	public Drawable getImageOnFail(Resources res) {
		return imageResOnFail != 0 ? res.getDrawable(imageResOnFail) : imageOnFail;
	}

	public boolean isResetViewBeforeLoading() {
		return resetViewBeforeLoading;
	}

	public boolean isCacheInMemory() {
		return cacheInMemory;
	}

	public boolean isCacheOnDisk() {
		return cacheOnDisk;
	}

	public ImageScaleType getImageScaleType() {
		return imageScaleType;
	}

	public Options getDecodingOptions() {
		return decodingOptions;
	}

	public int getDelayBeforeLoading() {
		return delayBeforeLoading;
	}

	public boolean isConsiderExifParams() {
		return considerExifParams;
	}

	public Object getExtraForDownloader() {
		return extraForDownloader;
	}

	public BitmapProcessor getPreProcessor() {
		return preProcessor;
	}

	public BitmapProcessor getPostProcessor() {
		return postProcessor;
	}

	public BitmapDisplayer getDisplayer() {
		return displayer;
	}

	public Handler getHandler() {
		return handler;
	}

	boolean isSyncLoading() {
		return isSyncLoading;
	}

	/**
	 * Builder for {@link DisplayImageOptions}
	 *
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 */
	public static class Builder {
		private int imageResOnLoading = 0;
		private int imageResForEmptyUri = 0;
		private int imageResOnFail = 0;
		private Drawable imageOnLoading = null;
		private Drawable imageForEmptyUri = null;
		private Drawable imageOnFail = null;
		private boolean resetViewBeforeLoading = false;
		private boolean cacheInMemory = false;
		private boolean cacheOnDisk = false;
		private ImageScaleType imageScaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2;
		private Options decodingOptions = new Options();
		private int delayBeforeLoading = 0;
		private boolean considerExifParams = false;
		private Object extraForDownloader = null;
		private BitmapProcessor preProcessor = null;
		private BitmapProcessor postProcessor = null;
		private BitmapDisplayer displayer = DefaultConfigurationFactory.createBitmapDisplayer();
		private Handler handler = null;
		private boolean isSyncLoading = false;

		public Builder() {
			decodingOptions.inPurgeable = true;
			decodingOptions.inInputShareable = true;
		}

		/**
		 * Stub image will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} during image loading
		 *
		 * @param imageRes Stub image resource
		 * @deprecated Use {@link #showImageOnLoading(int)} instead
		 */
		@Deprecated
		public Builder showStubImage(int imageRes) {
			imageResOnLoading = imageRes;
			return this;
		}

		/**
		 * Incoming image will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} during image loading
		 *
		 * @param imageRes Image resource
		 */
		public Builder showImageOnLoading(int imageRes) {
			imageResOnLoading = imageRes;
			return this;
		}

		/**
		 * Incoming drawable will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} during image loading.
		 * This option will be ignored if {@link DisplayImageOptions.Builder#showImageOnLoading(int)} is set.
		 */
		public Builder showImageOnLoading(Drawable drawable) {
			imageOnLoading = drawable;
			return this;
		}

		/**
		 * Incoming image will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} if empty URI (null or empty
		 * string) will be passed to <b>ImageLoader.displayImage(...)</b> method.
		 *
		 * @param imageRes Image resource
		 */
		public Builder showImageForEmptyUri(int imageRes) {
			imageResForEmptyUri = imageRes;
			return this;
		}

		/**
		 * Incoming drawable will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} if empty URI (null or empty
		 * string) will be passed to <b>ImageLoader.displayImage(...)</b> method.
		 * This option will be ignored if {@link DisplayImageOptions.Builder#showImageForEmptyUri(int)} is set.
		 */
		public Builder showImageForEmptyUri(Drawable drawable) {
			imageForEmptyUri = drawable;
			return this;
		}

		/**
		 * Incoming image will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} if some error occurs during
		 * requested image loading/decoding.
		 *
		 * @param imageRes Image resource
		 */
		public Builder showImageOnFail(int imageRes) {
			imageResOnFail = imageRes;
			return this;
		}

		/**
		 * Incoming drawable will be displayed in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} if some error occurs during
		 * requested image loading/decoding.
		 * This option will be ignored if {@link DisplayImageOptions.Builder#showImageOnFail(int)} is set.
		 */
		public Builder showImageOnFail(Drawable drawable) {
			imageOnFail = drawable;
			return this;
		}

		/**
		 * {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} will be reset (set <b>null</b>) before image loading start
		 *
		 * @deprecated Use {@link #resetViewBeforeLoading(boolean) resetViewBeforeLoading(true)} instead
		 */
		public Builder resetViewBeforeLoading() {
			resetViewBeforeLoading = true;
			return this;
		}

		/**
		 * Sets whether {@link com.nostra13.universalimageloader.core.imageaware.ImageAware
		 * image aware view} will be reset (set <b>null</b>) before image loading start
		 */
		public Builder resetViewBeforeLoading(boolean resetViewBeforeLoading) {
			this.resetViewBeforeLoading = resetViewBeforeLoading;
			return this;
		}

		/**
		 * Loaded image will be cached in memory
		 *
		 * @deprecated Use {@link #cacheInMemory(boolean) cacheInMemory(true)} instead
		 */
		@Deprecated
		public Builder cacheInMemory() {
			cacheInMemory = true;
			return this;
		}

		/** Sets whether loaded image will be cached in memory */
		public Builder cacheInMemory(boolean cacheInMemory) {
			this.cacheInMemory = cacheInMemory;
			return this;
		}

		/**
		 * Loaded image will be cached on disk
		 *
		 * @deprecated Use {@link #cacheOnDisk(boolean) cacheOnDisk(true)} instead
		 */
		@Deprecated
		public Builder cacheOnDisc() {
			return cacheOnDisk(true);
		}

		/**
		 * Sets whether loaded image will be cached on disk
		 *
		 * @deprecated Use {@link #cacheOnDisk(boolean)} instead
		 */
		@Deprecated
		public Builder cacheOnDisc(boolean cacheOnDisk) {
			return cacheOnDisk(cacheOnDisk);
		}

		/** Sets whether loaded image will be cached on disk */
		public Builder cacheOnDisk(boolean cacheOnDisk) {
			this.cacheOnDisk = cacheOnDisk;
			return this;
		}

		/**
		 * Sets {@linkplain ImageScaleType scale type} for decoding image. This parameter is used while define scale
		 * size for decoding image to Bitmap. Default value - {@link ImageScaleType#IN_SAMPLE_POWER_OF_2}
		 */
		public Builder imageScaleType(ImageScaleType imageScaleType) {
			this.imageScaleType = imageScaleType;
			return this;
		}

		/** Sets {@link Bitmap.Config bitmap config} for image decoding. Default value - {@link Bitmap.Config#ARGB_8888} */
		public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
			if (bitmapConfig == null) throw new IllegalArgumentException("bitmapConfig can't be null");
			decodingOptions.inPreferredConfig = bitmapConfig;
			return this;
		}

		/**
		 * Sets options for image decoding.<br />
		 * <b>NOTE:</b> {@link Options#inSampleSize} of incoming options will <b>NOT</b> be considered. Library
		 * calculate the most appropriate sample size itself according yo {@link #imageScaleType(ImageScaleType)}
		 * options.<br />
		 * <b>NOTE:</b> This option overlaps {@link #bitmapConfig(android.graphics.Bitmap.Config) bitmapConfig()}
		 * option.
		 */
		public Builder decodingOptions(Options decodingOptions) {
			if (decodingOptions == null) throw new IllegalArgumentException("decodingOptions can't be null");
			this.decodingOptions = decodingOptions;
			return this;
		}

		/** Sets delay time before starting loading task. Default - no delay. */
		public Builder delayBeforeLoading(int delayInMillis) {
			this.delayBeforeLoading = delayInMillis;
			return this;
		}

		/** Sets auxiliary object which will be passed to {@link ImageDownloader#getStream(String, Object)} */
		public Builder extraForDownloader(Object extra) {
			this.extraForDownloader = extra;
			return this;
		}

		/** Sets whether ImageLoader will consider EXIF parameters of JPEG image (rotate, flip) */
		public Builder considerExifParams(boolean considerExifParams) {
			this.considerExifParams = considerExifParams;
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
		 * Sets bitmap processor which will be process bitmaps before they will be displayed in
		 * {@link com.nostra13.universalimageloader.core.imageaware.ImageAware image aware view} but
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
			if (displayer == null) throw new IllegalArgumentException("displayer can't be null");
			this.displayer = displayer;
			return this;
		}

		Builder syncLoading(boolean isSyncLoading) {
			this.isSyncLoading = isSyncLoading;
			return this;
		}

		/**
		 * Sets custom {@linkplain Handler handler} for displaying images and firing {@linkplain ImageLoadingListener
		 * listener} events.
		 */
		public Builder handler(Handler handler) {
			this.handler = handler;
			return this;
		}

		/** Sets all options equal to incoming options */
		public Builder cloneFrom(DisplayImageOptions options) {
			imageResOnLoading = options.imageResOnLoading;
			imageResForEmptyUri = options.imageResForEmptyUri;
			imageResOnFail = options.imageResOnFail;
			imageOnLoading = options.imageOnLoading;
			imageForEmptyUri = options.imageForEmptyUri;
			imageOnFail = options.imageOnFail;
			resetViewBeforeLoading = options.resetViewBeforeLoading;
			cacheInMemory = options.cacheInMemory;
			cacheOnDisk = options.cacheOnDisk;
			imageScaleType = options.imageScaleType;
			decodingOptions = options.decodingOptions;
			delayBeforeLoading = options.delayBeforeLoading;
			considerExifParams = options.considerExifParams;
			extraForDownloader = options.extraForDownloader;
			preProcessor = options.preProcessor;
			postProcessor = options.postProcessor;
			displayer = options.displayer;
			handler = options.handler;
			isSyncLoading = options.isSyncLoading;
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
	 * <li>Loaded image will <b>not</b> be cached on disk</li>
	 * <li>{@link ImageScaleType#IN_SAMPLE_POWER_OF_2} decoding type will be used</li>
	 * <li>{@link Bitmap.Config#ARGB_8888} bitmap config will be used for image decoding</li>
	 * <li>{@link SimpleBitmapDisplayer} will be used for image displaying</li>
	 * </ul>
	 * <p/>
	 * These option are appropriate for simple single-use image (from drawables or from Internet) displaying.
	 */
	public static DisplayImageOptions createSimple() {
		return new Builder().build();
	}
}
