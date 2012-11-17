package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

/**
 * Contains options for image display. Defines:
 * <ul>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} during image loading</li>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} if empty URI is passed</li>
 * <li>whether {@link android.widget.ImageView ImageView} should be reset before image loading start</li>
 * <li>whether loaded image will be cached in memory</li>
 * <li>whether loaded image will be cached on disc</li>
 * <li>image scale type</li>
 * <li>bitmap decoding configuration</li>
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
 */
public final class DisplayImageOptions {

	private final Integer stubImage;
	private final Integer imageForEmptyUri;
	private final boolean resetViewBeforeLoading;
	private final boolean cacheInMemory;
	private final boolean cacheOnDisc;
	private final ImageScaleType imageScaleType;
	private final Bitmap.Config bitmapConfig;
	private final BitmapDisplayer displayer;

	private DisplayImageOptions(Builder builder) {
		stubImage = builder.stubImage;
		imageForEmptyUri = builder.imageForEmptyUri;
		resetViewBeforeLoading = builder.resetViewBeforeLoading;
		cacheInMemory = builder.cacheInMemory;
		cacheOnDisc = builder.cacheOnDisc;
		imageScaleType = builder.imageScaleType;
		bitmapConfig = builder.bitmapConfig;
		displayer = builder.displayer;
	}

	boolean isShowStubImage() {
		return stubImage != null;
	}

	boolean isShowImageForEmptyUri() {
		return imageForEmptyUri != null;
	}

	Integer getStubImage() {
		return stubImage;
	}

	Integer getImageForEmptyUri() {
		return imageForEmptyUri;
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

	BitmapDisplayer getDisplayer() {
		return displayer;
	}

	/**
	 * Builder for {@link DisplayImageOptions}
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 */
	public static class Builder {
		private Integer stubImage = null;
		private Integer imageForEmptyUri = null;
		private boolean resetViewBeforeLoading = false;
		private boolean cacheInMemory = false;
		private boolean cacheOnDisc = false;
		private ImageScaleType imageScaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2;
		private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
		private BitmapDisplayer displayer = DefaultConfigurationFactory.createBitmapDisplayer();

		/**
		 * Stub image will be displayed in {@link android.widget.ImageView ImageView} during image loading
		 * 
		 * @param stubImageRes
		 *            Stub image resource
		 */
		public Builder showStubImage(int stubImageRes) {
			stubImage = stubImageRes;
			return this;
		}

		/**
		 * Image will be displayed in {@link android.widget.ImageView ImageView} if empty URI (null or empty string)
		 * will be passed to <b>ImageLoader.displayImage(...)</b> method.
		 * 
		 * @param imageRes
		 *            Image resource
		 */
		public Builder showImageForEmptyUri(int imageRes) {
			imageForEmptyUri = imageRes;
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

		/**
		 * Sets custom {@link BitmapDisplayer displayer} for image loading task. Default value -
		 * {@link DefaultConfigurationFactory#createBitmapDisplayer()}
		 */
		public Builder displayer(BitmapDisplayer displayer) {
			this.displayer = displayer;
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
	 * <li>Stub image will <b>not</b> be displayed in {@link android.widget.ImageView ImageView} during image loading</li>
	 * <li>Loaded image will <b>not</b> be cached in memory</li>
	 * <li>Loaded image will <b>not</b> be cached on disc (application cache directory or on SD card)</li>
	 * <li>{@link ImageScaleType#POWER_OF_2 FAST} decoding type will be used</li>
	 * </ul>
	 * 
	 * These option are appropriate for simple single-use image (from drawables or from internet) displaying.
	 */
	public static DisplayImageOptions createSimple() {
		return new Builder().build();
	}
}
