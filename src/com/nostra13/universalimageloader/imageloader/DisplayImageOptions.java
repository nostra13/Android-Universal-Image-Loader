package com.nostra13.universalimageloader.imageloader;

import android.widget.ImageView;
import android.widget.ListView;

/**
 * Contains options for image display. Defines:
 * <ul>
 * <li>whether stub image will be displayed in {@link ImageView} during image loading</li>
 * <li>whether loaded image will be cached in memory</li>
 * <li>whether loaded image will be cached on disc</li>
 * </ul>
 * 
 * You can create instance:
 * <ul>
 * <li>with {@link Builder}:<br />
 * <b>i.e.</b> :
 * <code>new {@link DisplayImageOptions}.{@link Builder#Builder() Builder()}.{@link Builder#cacheInMemory() cacheImageInMemory()}.
 * {@link Builder#showStubImage() showStubImageWhileLoading()}.{@link Builder#build() build()}</sode><br /></li>
 * <li>or by static methods: {@link #createForListView()}, {@link #createForSingleLoad()}</li> <br />
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class DisplayImageOptions {

	private final boolean showStubImage;
	private final boolean cacheInMemory;
	private final boolean cacheOnDisc;

	private DisplayImageOptions(Builder builder) {
		showStubImage = builder.showStubImage;
		cacheInMemory = builder.cacheInMemory;
		cacheOnDisc = builder.cacheOnDisc;
	}

	boolean isShowStubImage() {
		return showStubImage;
	}

	boolean isCacheInMemory() {
		return cacheInMemory;
	}

	boolean isCacheOnDisc() {
		return cacheOnDisc;
	}

	/**
	 * Builder for {@link DisplayImageOptions}
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 */
	public static class Builder {
		private boolean showStubImage = false;
		private boolean cacheInMemory = false;
		private boolean cacheOnDisc = false;

		/** Stub image will be displayed in {@link ImageView} during image loading */
		public Builder showStubImage() {
			showStubImage = true;
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

		/** Builds configured {@link DisplayImageOptions} object */
		public DisplayImageOptions build() {
			return new DisplayImageOptions(this);
		}
	}

	/**
	 * Creates options appropriate for image displaying at {@link ListView}:
	 * <ul>
	 * <li>Stub image will be displayed in {@link ImageView} during image loading</li>
	 * <li>Loaded image will be cached in memory</li>
	 * <li>Loaded image will be cached on disc (application cache directory or on SD card)</li>
	 * </ul>
	 */
	public static DisplayImageOptions createForListView() {
		Builder builder = new Builder().showStubImage().cacheInMemory().cacheOnDisc();
		return builder.build();
	}

	/**
	 * Creates options appropriate for single displaying:
	 * <ul>
	 * <li>Stub image will <b>not</b> be displayed in {@link ImageView} during image loading</li>
	 * <li>Loaded image will <b>not</b> be cached in memory</li>
	 * <li>Loaded image will <b>not</b> be cached on disc (application cache directory or on SD card)</li>
	 * </ul>
	 * 
	 * These option are appropriate for simple single-use image (from drawables or from internet) displaying.
	 */
	public static DisplayImageOptions createForSingleLoad() {
		return new Builder().build();
	}
}
