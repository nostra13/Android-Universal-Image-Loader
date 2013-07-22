package com.nostra13.universalimageloader.core.assist;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.utils.ImageSizeUtils;

@RunWith(RobolectricTestRunner.class)
public class ImageSizeTest {
	private Activity mActivity;
	private ImageView mView;

	@Before
	public void setUp() throws Exception {
		mActivity = new Activity();

		// Make and set view with some prelim values to test
		mView = new TestImageView(mActivity);
		mView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mView.measure(View.MeasureSpec.makeMeasureSpec(250, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(250, View.MeasureSpec.EXACTLY));
	}

	@Test
	public void testGetImageSizeScaleTo_useImageActualSize() throws Exception {
		// We layout the view to give it a width and height
		mView.measure(View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY));
		mView.layout(0, 0, 200, 200);

		ImageSize expected = new ImageSize(200, 200);
		ImageSize result = ImageSizeUtils.defineTargetSizeForView(mView, 590, 590);
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getWidth()).isEqualTo(expected.getWidth());
		Assertions.assertThat(result.getHeight()).isEqualTo(expected.getHeight());
	}

	/**
	 * This will make sure the view falls back to the ViewParams/Max/Or Config if wrap content so that it is never
	 * shrunk to the first image size. In this case it falls back to the config size
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetImageSizeScaleTo_dontUseImageActualSizeWithWrapContent() throws Exception {
		//Set it to wrap content so that it will fall back to
		mView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mView.measure(View.MeasureSpec.makeMeasureSpec(250, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(250, View.MeasureSpec.EXACTLY));
		// We layout the view to give it a width and height
		mView.layout(0, 0, 200, 200);

		ImageSize expected = new ImageSize(500, 500);
		ImageSize result = ImageSizeUtils.defineTargetSizeForView(mView, 500, 500);
		Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
	}

	@Test
	public void testGetImageSizeScaleTo_useImageLayoutParams() throws Exception {
		// Set a defined width
		mView.setLayoutParams(new FrameLayout.LayoutParams(300, 300));

		ImageSize expected = new ImageSize(300, 300);
		ImageSize result = ImageSizeUtils.defineTargetSizeForView(mView, 500, 500);
		Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
	}

	@Test
	public void testGetImageSizeScaleTo_useImageCacheMaxSize() throws Exception {
		ImageSize expected = new ImageSize(500, 500);
		ImageSize result = ImageSizeUtils.defineTargetSizeForView(mView, 500, 500);
		Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
	}

	@Test
	public void testGetImageSizeScaleTo_useDisplayMetrics() throws Exception {
		//The default Robolectic disp metrics are 480x800 normal hdpi device basically
		ImageSize expected = new ImageSize(480, 800);
		ImageSize result = ImageSizeUtils.defineTargetSizeForView(mView, 0, 0);
		Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
	}

	/** Fixes {@link NoSuchMethodError} for <code>ImageView#onLayout(...)</code> */
	private class TestImageView extends ImageView {
		TestImageView(Context activity) {
			super(activity);
		}

		@Override
		public void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);
		}
	}
}
