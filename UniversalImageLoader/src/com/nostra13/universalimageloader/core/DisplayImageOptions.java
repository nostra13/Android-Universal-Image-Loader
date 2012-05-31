package com.nostra13.universalimageloader.core;

import com.nostra13.universalimageloader.core.assist.DecodingType;

/**
 * Contains options for image display. Defines:
 * <ul>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} during image loading</li>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} if empty URL is passed</li>
 * <li>whether loaded image will be cached in memory</li>
 * <li>whether loaded image will be cached on disc</li>
 * </ul>
 * 
 * You can create instance:
 * <ul>
 * <li>with {@link Builder}:<br />
 * <b>i.e.</b> :
 * <code>new {@link DisplayImageOptions}.{@link Builder#Builder() Builder()}.{@link Builder#cacheInMemory() cacheInMemory()}.
 * {@link Builder#showStubImage(int) showStubImage()}.{@link Builder#build() build()}</sode><br /></li>
 * <li>or by static method: {@link #createSimple()}</li> <br />
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class DisplayImageOptions {

	private final Integer stubImage;
	private final Integer imageForEmptyUrl;
	private final boolean cacheInMemory;
	private final boolean cacheOnDisc;
	private final DecodingType decodingType;

	private DisplayImageOptions(Builder builder) {
		stubImage = builder.stubImage;
		imageForEmptyUrl = builder.imageForEmptyUrl;
		cacheInMemory = builder.cacheInMemory;
		cacheOnDisc = builder.cacheOnDisc;
		decodingType = builder.decodingType;
	}

	boolean isShowStubImage() {
		return stubImage != null;
	}

	boolean isShowImageForEmptyUrl() {
		return imageForEmptyUrl != null;
	}

	Integer getStubImage() {
		return stubImage;
	}

	Integer getImageForEmptyUrl() {
		return imageForEmptyUrl;
	}

	boolean isCacheInMemory() {
		return cacheInMemory;
	}

	boolean isCacheOnDisc() {
		return cacheOnDisc;
	}

	DecodingType getDecodingType() {
		return decodingType;
	}

	/**
	 * Builder for {@link DisplayImageOptions}
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 */
	public static class Builder {
		private Integer stubImage = null;
		private Integer imageForEmptyUrl = null;
		private boolean cacheInMemory = false;
		private boolean cacheOnDisc = false;
		private DecodingType decodingType = DecodingType.FAST;

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
		 * Image will be displayed in {@link android.widget.ImageView ImageView} if empty Url (null or empty string)
		 * will be passed to <b>ImageLoader.displayImage(...)</b> method.
		 * 
		 * @param imageRes
		 *            Image resource
		 */
		public Builder showImageForEmptyUrl(int imageRes) {
			imageForEmptyUrl = imageRes;
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

		/** Sets {@link DecodingType decoding type} for image loading task. Default value - {@link DecodingType#FAST} */
		public Builder decodingType(DecodingType decodingType) {
			this.decodingType = decodingType;
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
	 * <li>{@link DecodingType#FAST FAST} decoding type will be used</li>
	 * </ul>
	 * 
	 * These option are appropriate for simple single-use image (from drawables or from internet) displaying.
	 */
	public static DisplayImageOptions createSimple() {
		return new Builder().build();
	}
}
