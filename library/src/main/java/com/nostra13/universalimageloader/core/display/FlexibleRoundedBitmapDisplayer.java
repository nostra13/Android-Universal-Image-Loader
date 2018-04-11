package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Flexible rounded bitmap displayer, support define all four borders of a imageView
 *
 * */
public class FlexibleRoundedBitmapDisplayer implements BitmapDisplayer {

    private final int leftTopCornerRadius;
    private final int rightTopCornerRadius;
    private final int leftBottomCornerRadius;
    private final int rightBottomCornerRadius;
    private final int margin;

    public FlexibleRoundedBitmapDisplayer(int cornerRadiusPixels) {
        this(cornerRadiusPixels, cornerRadiusPixels, 0, 0, 0);
    }

    public FlexibleRoundedBitmapDisplayer(int leftTopCornerRadiusPixels,
                                          int rightTopCornerRadiusPixels,
                                          int leftBottomCornerRadiusPixels,
                                          int rightBottomCornerRadiusPixels,
                                          int marginPixels) {
        this.leftTopCornerRadius = leftTopCornerRadiusPixels;
        this.rightTopCornerRadius = rightTopCornerRadiusPixels;
        this.leftBottomCornerRadius = leftBottomCornerRadiusPixels;
        this.rightBottomCornerRadius = rightBottomCornerRadiusPixels;
        this.margin = marginPixels;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        imageAware.setImageDrawable(new FlexibleRoundedDrawable(bitmap,
                leftTopCornerRadius,
                rightTopCornerRadius,
                leftBottomCornerRadius,
                rightBottomCornerRadius,
                margin));
    }

    public static class FlexibleRoundedDrawable extends Drawable {

        final float leftTopCornerRadius;
        final float rightTopCornerRadius;
        final float leftBottomCornerRadius;
        final float rightBottomCornerRadius;
        final int margin;

        final RectF bottomRect = new RectF(), topRect = new RectF(),
                bitmapRect;
        final BitmapShader bitmapShader;
        final Paint paint;

        FlexibleRoundedDrawable(Bitmap bitmap,
                                int leftTopCornerRadiusPixels,
                                int rightTopCornerRadiusPixels,
                                int leftBottomCornerRadiusPixels,
                                int rightBottomCornerRadiusPixels,
                                int margin) {
            this.leftTopCornerRadius = leftTopCornerRadiusPixels;
            this.rightTopCornerRadius = rightTopCornerRadiusPixels;
            this.leftBottomCornerRadius = leftBottomCornerRadiusPixels;
            this.rightBottomCornerRadius = rightBottomCornerRadiusPixels;
            this.margin = margin;

            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            bitmapRect = new RectF (margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
            paint.setFilterBitmap(true);
            paint.setDither(true);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            float maxTopRadius = Math.max(leftTopCornerRadius, rightTopCornerRadius);
            topRect.set(margin, margin, bounds.width() - margin, margin + maxTopRadius * 2);
            bottomRect.set(margin, margin + maxTopRadius, bounds.width() - margin, bounds.height() - margin - maxTopRadius);

            // Resize the original bitmap to fit the new bound
            Matrix shaderMatrix = new Matrix();
            shaderMatrix.setRectToRect(bitmapRect, bottomRect, Matrix.ScaleToFit.FILL);
            bitmapShader.setLocalMatrix(shaderMatrix);

        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(topRect, leftTopCornerRadius, rightTopCornerRadius, paint);
            canvas.drawRoundRect(bottomRect, leftBottomCornerRadius, rightBottomCornerRadius, paint);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }
    }
}
