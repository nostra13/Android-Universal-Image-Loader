package com.nostra13.universalimageloader.core.assist;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.test.UILTestRunner;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created with Intellij with Android, BIZZBY product.
 * See licencing for usage of this code.
 * <p/>
 * User: chris
 * Date: 02/03/2013
 * Time: 18:34
 */
@RunWith(UILTestRunner.class)
public class ImageSizeTest
{
    private Activity mActivity;
    private View mView;
    private ImageLoaderConfiguration mConfiguration;
    private DisplayMetrics mDisplayMetrics;

    @Before
    public void setUp() throws Exception
    {
        mActivity = new Activity();

        // Make and set view with some prelim values to test
        mView = new View(mActivity);
        mView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mView.measure(View.MeasureSpec.makeMeasureSpec(250, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(250, View.MeasureSpec.EXACTLY));

        mConfiguration = new ImageLoaderConfiguration.Builder(mActivity)
                .memoryCacheExtraOptions(500, 500)
                .build();
        mDisplayMetrics = mView.getContext().getResources().getDisplayMetrics();
    }

    @Test
    public void testGetImageSizeScaleTo_null() throws Exception
    {
        ImageSize result = ImageSize.getImageSizeScaleTo(null, mConfiguration, mDisplayMetrics);
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void testGetImageSizeScaleTo_useImageActualSize() throws Exception
    {
        // We layout the view to give it a width and height
        mView.layout(0, 0, 200, 200);

        ImageSize expected = new ImageSize(200, 200);
        ImageSize result = ImageSize.getImageSizeScaleTo(mView, mConfiguration, mDisplayMetrics);
        Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
    }

    /**
     * This will make sure the view falls back to the ViewParams/Max/Or Config if wrap content so that it is
     * never shrunk to the first image size.
     * In this case it falls back to the config size
     *
     * @throws Exception
     */
    @Test
    public void testGetImageSizeScaleTo_dontUseImageActualSizeWithWrapContent() throws Exception
    {
        //Set it to wrap content so that it will fall back to
        mView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mView.measure(View.MeasureSpec.makeMeasureSpec(250, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(250, View.MeasureSpec.EXACTLY));
        // We layout the view to give it a width and height
        mView.layout(0, 0, 200, 200);

        ImageSize expected = new ImageSize(500, 500);
        ImageSize result = ImageSize.getImageSizeScaleTo(mView, mConfiguration, mDisplayMetrics);
        Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
    }

    @Test
    public void testGetImageSizeScaleTo_useImageLayoutParams() throws Exception
    {
        // Set a defined width
        mView.setLayoutParams(new FrameLayout.LayoutParams(300, 300));

        ImageSize expected = new ImageSize(300, 300);
        ImageSize result = ImageSize.getImageSizeScaleTo(mView, mConfiguration, mDisplayMetrics);
        Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
    }

    @Test
    public void testGetImageSizeScaleTo_useImageCacheMaxSize() throws Exception
    {
        mConfiguration = new ImageLoaderConfiguration.Builder(mActivity)
                .memoryCacheExtraOptions(500, 500)
                .build();
        ImageSize expected = new ImageSize(500, 500);
        ImageSize result = ImageSize.getImageSizeScaleTo(mView, mConfiguration, mDisplayMetrics);
        Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
    }

    @Test
    public void testGetImageSizeScaleTo_useDisplayMetrics() throws Exception
    {
        mConfiguration = new ImageLoaderConfiguration.Builder(mActivity).build();

        //The default Robolectic disp metrics are 480x800 normal hdpi device basically
        ImageSize expected = new ImageSize(480, 800);
        ImageSize result = ImageSize.getImageSizeScaleTo(mView, mConfiguration, mDisplayMetrics);
        Assertions.assertThat(result).isNotNull().isEqualsToByComparingFields(expected);
    }
}
