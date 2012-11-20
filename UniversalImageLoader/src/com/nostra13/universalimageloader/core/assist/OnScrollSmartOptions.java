package com.nostra13.universalimageloader.core.assist;

import java.util.concurrent.atomic.AtomicInteger;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Listener-helper for {@linkplain AbsListView list views} ({@link ListView}, {@link GridView}) which makes some
 * {@link #LOADING_DELAY_ON_FLING delay} before starting image loading during touch scrolling and/or fast list scrolling
 * (fling). It prevents redundant loadings.<br />
 * Set it to your list view's {@link AbsListView#setOnScrollListener(OnScrollListener) setOnScrollListener(...)} and
 * later get options (using {@linkplain #getOptions()}) for
 * {@link ImageLoader#displayImage(String, android.widget.ImageView, DisplayImageOptions) ImageLoader.displayImage(...)}
 * calls.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class OnScrollSmartOptions implements OnScrollListener {

	/** {@value} */
	public static final int LOADING_DELAY_ON_TOUCH_SCROLL = 0; // ms
	/** {@value} */
	public static final int LOADING_DELAY_ON_FLING = 300; // ms

	private final DisplayImageOptions options;
	private final DisplayImageOptions scrollOptions;
	private final DisplayImageOptions flingOptions;
	private final OnScrollListener externalListener;

	private final AtomicInteger scrollState;

	/**
	 * Constructor<br />
	 * Delay before loading for touch scrolling - {@value #LOADING_DELAY_ON_TOUCH_SCROLL} ms<br />
	 * Delay before loading for fling scrolling - {@value #LOADING_DELAY_ON_FLING} ms
	 * 
	 * @param displayOptions
	 *            {@linkplain DisplayImageOptions Display options} you want to use for displaying images in you
	 *            {@linkplain AbsListView list view}
	 */
	public OnScrollSmartOptions(DisplayImageOptions displayOptions) {
		this(displayOptions, LOADING_DELAY_ON_TOUCH_SCROLL, LOADING_DELAY_ON_FLING);
	}

	/**
	 * Constructor
	 * 
	 * @param displayOptions
	 *            {@linkplain DisplayImageOptions Display options} you want to use for displaying images in you
	 *            {@linkplain AbsListView list view}
	 * @param loadingDelayOnScroll
	 *            Delay (in milliseconds) before starting loading during scrolling
	 * @param loadingDelayOnFling
	 *            Delay (in milliseconds) before starting loading during fast scrolling (fling)
	 */
	public OnScrollSmartOptions(DisplayImageOptions displayOptions, int loadingDelayOnScroll, int loadingDelayOnFling) {
		this(displayOptions, loadingDelayOnScroll, loadingDelayOnFling, null);
	}

	/**
	 * Constructor
	 * 
	 * @param displayOptions
	 *            {@linkplain DisplayImageOptions Display options} you want to use for displaying images in you
	 *            {@linkplain AbsListView list view}
	 * @param loadingDelayOnScroll
	 *            Delay (in milliseconds) before starting loading during touch scrolling
	 * @param loadingDelayOnFling
	 *            Delay (in milliseconds) before starting loading during fast scrolling (fling)
	 * @param customListener
	 *            Your custom {@link OnScrollListener} for {@linkplain AbsListView list view} which also will be get
	 *            scroll events
	 */
	public OnScrollSmartOptions(DisplayImageOptions displayOptions, int loadingDelayOnScroll, int loadingDelayOnFling, OnScrollListener customListener) {
		options = displayOptions;
		scrollOptions = new DisplayImageOptions.Builder()
			.cloneFrom(options)
			.delayBeforeLoading(loadingDelayOnScroll)
			.build();
		flingOptions = new DisplayImageOptions.Builder()
			.cloneFrom(options)
			.delayBeforeLoading(loadingDelayOnFling)
			.build();
		externalListener = customListener;

		scrollState = new AtomicInteger(OnScrollListener.SCROLL_STATE_IDLE);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState.set(scrollState);
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
		switch (scrollState.get()) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				return options;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				return scrollOptions;
			case OnScrollListener.SCROLL_STATE_FLING:
				return flingOptions;
			default:
				return options;
		}
	}
}
