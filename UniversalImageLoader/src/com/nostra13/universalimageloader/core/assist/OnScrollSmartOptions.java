package com.nostra13.universalimageloader.core.assist;

import java.util.concurrent.atomic.AtomicBoolean;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ListView;

/**
 * Listener-helper for {@linkplain AbsListView list views} ({@link ListView}, {@link GridView}) which makes some
 * {@link #LOADING_DELAY_ON_FLING delay} before starting image loading during fast list scrolling (fling). It prevents
 * redundant loadings.<br />
 * Set it to your list view's {@link AbsListView#setOnScrollListener(OnScrollListener) setOnScrollListener(...)} and
 * later get options (using {@linkplain #getOptions()}) for
 * {@link ImageLoader#displayImage(String, android.widget.ImageView, DisplayImageOptions) ImageLoader.displayImage(...)}
 * calls.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class OnScrollSmartOptions implements OnScrollListener {

	/** {@value} */
	public static final int LOADING_DELAY_ON_FLING = 100; // ms

	private final DisplayImageOptions options;
	private final DisplayImageOptions flingOptions;
	private final OnScrollListener externalListener;

	private final AtomicBoolean isFlingScrolling;

	/**
	 * @param displayOptions
	 *            {@linkplain DisplayImageOptions Display options} you want to use for displaying images in you
	 *            {@linkplain AbsListView list view}
	 */
	public OnScrollSmartOptions(DisplayImageOptions displayOptions) {
		this(displayOptions, null);
	}

	/**
	 * @param displayOptions
	 *            {@linkplain DisplayImageOptions Display options} you want to use for displaying images in you
	 *            {@linkplain AbsListView list view}
	 * @param customListener
	 *            Your custom {@link OnScrollListener} for {@linkplain AbsListView list view} which also will be get
	 *            scroll events
	 */
	public OnScrollSmartOptions(DisplayImageOptions displayOptions, OnScrollListener customListener) {
		options = displayOptions;
		externalListener = customListener;

		flingOptions = new DisplayImageOptions.Builder()
			.cloneFrom(options)
			.delayBeforeLoading(LOADING_DELAY_ON_FLING)
			.build();
		isFlingScrolling = new AtomicBoolean(false);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				isFlingScrolling.set(false);
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				isFlingScrolling.set(true);
				break;
		}
		if (externalListener != null) {
			externalListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (externalListener != null) {
			externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	/**
	 * Returns appropriate {@linkplain DisplayImageOptions display options} depending on {@linkplain AbsListView list
	 * view's} current scrolling state
	 */
	public DisplayImageOptions getOptions() {
		return isFlingScrolling.get() ? flingOptions : options;
	}
}
