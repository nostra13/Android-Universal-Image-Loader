package com.nostra13.universalimageloader;

import android.widget.ImageView;
import android.widget.ListView;

/**
 * Contains options for image display. Defines:
 * <ul>
 * <li>whether {@link ImageView} will be reset before image loading</li>
 * <li>whether stub image will be displayed in {@link ImageView} during image loading</li>
 * <li>whether loaded image will be cached in memory</li>
 * <li>whether loaded image will be cached on disc</li>
 * <li>width and/or height of {@link ImageView} which will be used for improvement of image caching in memory (by image
 * size reducing)</li>
 * </ul>
 * 
 * You can create instance:
 * <ul>
 * <li>with {@link Builder}:<br />
 * <b>i.e.</b> :
 * <code>new {@link DisplayImageOptions}.{@link Builder#Builder() Builder()}.{@link Builder#cacheImageInMemory() cacheImageInMemory()}.
 * {@link Builder#showStubImageWhileLoading() showStubImageWhileLoading()}.{@link Builder#build() build()}</sode><br /></li>
 * <li>or by static methods: {@link #createForListView()}, {@link #createForSingleLoad()}</li> <br />
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class DisplayImageOptions {
	private final boolean resetViewBeforeLoading;
	private final boolean showStubImageDuringLoading;
	private final boolean cacheImageInMemory;
	private final boolean cacheImageOnDisc;

	private final int viewWidth;
	private final int viewHeight;

	private DisplayImageOptions(Builder builder) {
		resetViewBeforeLoading = builder.resetViewBeforeLoading;
		showStubImageDuringLoading = builder.showStubImageDuringLoading;
		cacheImageInMemory = builder.cacheImageInMemory;
		cacheImageOnDisc = builder.cacheImageOnDisc;
		viewWidth = builder.viewWidth;
		viewHeight = builder.viewHeight;
	}

	public boolean isResetViewBeforeLoading() {
		return resetViewBeforeLoading;
	}

	public boolean isShowStubImageDuringLoading() {
		return showStubImageDuringLoading;
	}

	public boolean isCacheImageInMemory() {
		return cacheImageInMemory;
	}

	public boolean isCacheImageOnDisc() {
		return cacheImageOnDisc;
	}

	public int getViewWidth() {
		return viewWidth;
	}

	public int getViewHeight() {
		return viewHeight;
	}

	public static class Builder {
		private boolean resetViewBeforeLoading = false;
		private boolean showStubImageDuringLoading = false;
		private boolean cacheImageInMemory = false;
		private boolean cacheImageOnDisc = false;
		private int viewWidth = 0;
		private int viewHeight = 0;

		public Builder resetViewBeforeLoading() {
			resetViewBeforeLoading = true;
			return this;
		}

		public Builder showStubImageWhileLoading() {
			showStubImageDuringLoading = true;
			return this;
		}

		public Builder cacheImageInMemory() {
			cacheImageInMemory = true;
			return this;
		}

		public Builder cacheImageOnDisc() {
			cacheImageOnDisc = true;
			return this;
		}

		public Builder viewWidth(int viewWidth) {
			this.viewWidth = viewWidth;
			return this;
		}

		public Builder viewHeight(int viewHeight) {
			this.viewHeight = viewHeight;
			return this;
		}

		public DisplayImageOptions build() {
			return new DisplayImageOptions(this);
		}
	}

	/**
	 * Creates options appropriate for image displaying at {@link ListView}:
	 * <ul>
	 * <li>{@link ImageView} will be reset before image loading</li>
	 * <li>Stub image will be displayed in {@link ImageView} during image loading</li>
	 * <li>Loaded image will be cached in memory</li>
	 * <li>Loaded image will be cached on disc (application cache directory or on SD card)</li>
	 * </ul>
	 */
	public static DisplayImageOptions createForListView() {
		Builder builder = new Builder().resetViewBeforeLoading().showStubImageWhileLoading().cacheImageInMemory().cacheImageOnDisc();
		return builder.build();
	}

	/**
	 * Creates options appropriate for single displaying:
	 * <ul>
	 * <li>{@link ImageView} will <b>not</b> be reset before image loading</li>
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
