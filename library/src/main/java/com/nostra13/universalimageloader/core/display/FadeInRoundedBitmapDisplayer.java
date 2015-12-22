package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;


/**
 * Can display bitmap with rounded corners and vignette effect and with fade in visibility effect when loaded.
 * This implementation works only with ImageViews wrapped in ImageViewAware.
 * <br />
 * this implementation is a combination of both {@link RoundedBitmapDisplayer} and {@link FadeInBitmapDisplayer}
 * <br />
 *
 * @author Sepehr Behroozi (3pehrbehroozi[at]gmail[dot]com)
 * @since 1.9.5
 */
public class FadeInRoundedBitmapDisplayer implements BitmapDisplayer {

    protected final int cornerRadius;
    protected final int margin;
    private final int durationMillis;
    private final boolean animateFromNetwork;
    private final boolean animateFromDisk;
    private final boolean animateFromMemory;


    /**
     * Default constructor with 500 milliseconds for fade in duration
     * and 20px for corner radius. Also it animates ImageView when loaded from
     * everywhere
     * <br/>
     * Consider {@link com.nostra13.universalimageloader.core.display.FadeInRoundedBitmapDisplayer.Builder} to use
     */
    public FadeInRoundedBitmapDisplayer() {
        this(500, 20);
    }


    /**
     * @param fadeInDurationMillis Duration in millis for fade in animate
     * @param cornerRadius Bitmap corner radius pixels
     *
     * it fires the fade in animation when loaded from everywhere
     */
    public FadeInRoundedBitmapDisplayer(int fadeInDurationMillis, int cornerRadius) {
        this(fadeInDurationMillis, true, true, true, cornerRadius, 0);
    }


    /**
     * @param fadeInDurationMillis Duration in millis for fade in animate
     * @param animateFromNetwork Whether should fire the fade in animation when loaded image from network
     * @param animateFromDisk Whether should fire the fade in animation when loaded image from disk cache
     * @param animateFromMemory Whether should fire the fade in animation when loaded image from memory cache
     * @param cornerRadius Bitmap corner radius pixels
     * @param margin Margins of loaded image
     */
    public FadeInRoundedBitmapDisplayer(int fadeInDurationMillis, boolean animateFromNetwork, boolean animateFromDisk, boolean animateFromMemory, int cornerRadius, int margin) {
        this.durationMillis = fadeInDurationMillis;
        this.animateFromNetwork = animateFromNetwork;
        this.animateFromDisk = animateFromDisk;
        this.animateFromMemory = animateFromMemory;
        this.cornerRadius = cornerRadius;
        this.margin = margin;
    }

    /**
     * Animates {@link ImageView} with "fade-in" effect
     *
     * @param imageView      {@link ImageView} which display image in
     * @param durationMillis The length of the animation in milliseconds
     */
    public static void animate(View imageView, int durationMillis) {
        if (imageView != null) {
            AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
            fadeImage.setDuration(durationMillis);
            fadeImage.setInterpolator(new DecelerateInterpolator());
            imageView.startAnimation(fadeImage);
        }
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        imageAware.setImageDrawable(new RoundedBitmapDisplayer.RoundedDrawable(bitmap, cornerRadius, margin));
        if ((animateFromNetwork && loadedFrom == LoadedFrom.NETWORK) ||
                (animateFromDisk && loadedFrom == LoadedFrom.DISC_CACHE) ||
                (animateFromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE)) {
            animate(imageAware.getWrappedView(), durationMillis);
        }

    }

    /**
     * Builder of {@link FadeInRoundedBitmapDisplayer} class.
     * the default values is : <br/>
     * cornerRadius = 20px
     * margin = 0px;
     * fadeInDurationMillis = 500ms
     * animateFromNetwork = true
     * animateFromMemory = true
     * animateFromDisk = true
     *
     * @author Sepehr Behroozi (3pehrbehroozi[at]gmail[dot]com)
     * @since 1.9.5
     * @see FadeInRoundedBitmapDisplayer
     */
    public class Builder {
        private int cornerRadius = 20;
        private int margin = 0;
        private int fadeInDurationMillis = 500;

        private boolean animateFromNetwork = true;
        private boolean animateFromMemory = true;
        private boolean animateFromDisk = true;


        /**
         * @param cornerRadius Corner radius pixels of image
         *                     default = 20px
         * @return builder
         */
        public Builder cornerRadiusPixels(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        /**
         * @param marginPixels Loaded image margin pixels
         *                     default = 0px
         * @return builder
         */
        public Builder margin(int marginPixels) {
            this.margin = marginPixels;
            return this;
        }

        /**
         * @param durationMillis Fade in animation duration millis
         *                       default = 500ms
         * @return builder
         */
        public Builder fadeInDurationMillis(int durationMillis) {
            this.fadeInDurationMillis = durationMillis;
            return this;
        }

        /**
         * @param animateFromNetwork Whether should fire the fade in animation when loaded image from network
         *                           default = true
         * @return builder
         */
        public Builder animateFromNetwork(boolean animateFromNetwork) {
            this.animateFromNetwork = animateFromNetwork;
            return this;
        }

        /**
         * @param animateFromMemory Whether should fire the fade in animation when loaded image from memory cache
         *                          default = true
         * @return builder
         */
        public Builder animateFromMemory(boolean animateFromMemory) {
            this.animateFromNetwork = animateFromMemory;
            return this;
        }

        /**
         * @param animateFromDisk Whether should fire the fade in animation when loaded image from disk cache
         *                        default = true
         * @return builder
         */
        public Builder animateFromDisk(boolean animateFromDisk) {
            this.animateFromNetwork = animateFromDisk;
            return this;
        }

        /**
         * @return FadeInRoundedBitmapDisplayer instance from given parameters
         */
        public FadeInRoundedBitmapDisplayer build() {
            return new FadeInRoundedBitmapDisplayer(fadeInDurationMillis, animateFromNetwork, animateFromDisk, animateFromMemory, cornerRadius, margin);
        }
    }
}
