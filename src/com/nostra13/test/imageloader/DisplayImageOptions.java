package com.nostra13.test.imageloader;

import android.widget.ImageView;
import android.widget.ListView;

/**
 * Contains options for image display. Defines:
 * <ul>
 * <li>whether {@link android.widget.ImageView ImageView} will be reset before image loading</li>
 * <li>whether stub image will be displayed in {@link android.widget.ImageView ImageView} during image loading</li>
 * <li>whether loaded image will be cached in memory</li>
 * <li>whether loaded image will be cached on disc</li>
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

	private DisplayImageOptions(boolean resetViewBeforeLoading, boolean showStubImageDuringLoading, boolean cacheImageInMemory, boolean cacheImageOnDisc) {
		this.resetViewBeforeLoading = resetViewBeforeLoading;
		this.showStubImageDuringLoading = showStubImageDuringLoading;
		this.cacheImageInMemory = cacheImageInMemory;
		this.cacheImageOnDisc = cacheImageOnDisc;
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

	public static class Builder {
		private boolean resetViewBeforeLoading = false;
		private boolean showStubImageDuringLoading = false;
		private boolean cacheImageInMemory = false;
		private boolean cacheImageOnDisc = false;

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

		public DisplayImageOptions build() {
			return new DisplayImageOptions(resetViewBeforeLoading, showStubImageDuringLoading, cacheImageInMemory, cacheImageOnDisc);
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
		return new DisplayImageOptions(true, true, true, true);
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
		return new DisplayImageOptions(false, false, false, false);
	}
}
