/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
 * Can display bitmap cropped by a circle. This implementation works only with ImageViews wrapped
 * in ImageViewAware.
 * <br />
 * This implementation is inspired by
 * <a href="http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/">
 * Romain Guy's article</a>. It rounds images using custom drawable drawing. Original bitmap isn't changed.
 * <br />
 * <br />
 * If this implementation doesn't meet your needs then consider
 * <a href="https://github.com/vinc3m1/RoundedImageView">RoundedImageView</a> or
 * <a href="https://github.com/Pkmmte/CircularImageView">CircularImageView</a> projects for usage.
 */
public class CircleBitmapDisplayer implements BitmapDisplayer {

	protected final Integer strokeColor;

	public CircleBitmapDisplayer() {
		this(null);
	}

	public CircleBitmapDisplayer(Integer strokeColor) {
		this.strokeColor = strokeColor;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}

		imageAware.setImageDrawable(new RoundedDrawable(bitmap, strokeColor));
	}

	public static class RoundedDrawable extends Drawable {

		protected float radius;

		protected final RectF mRect = new RectF(),
				mBitmapRect;
		protected final BitmapShader bitmapShader;
		protected final Paint paint;
		protected final Paint strokePaint;

		public RoundedDrawable(Bitmap bitmap, Integer strokeColor) {
			radius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2;

			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mBitmapRect = new RectF (0, 0, bitmap.getWidth(), bitmap.getHeight());
			
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
			paint.setFilterBitmap(true);
			paint.setDither(true);
			if (strokeColor == null) strokePaint = null;
			else {
				strokePaint = new Paint();
				strokePaint.setStyle(Paint.Style.STROKE);
				strokePaint.setColor(strokeColor);
				strokePaint.setStrokeWidth(0.0F);
				strokePaint.setAntiAlias(true);
			}
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mRect.set(0, 0, bounds.width(), bounds.height());
			radius = Math.min(bounds.width(), bounds.height()) / 2;
			
			// Resize the original bitmap to fit the new bound
			Matrix shaderMatrix = new Matrix();
			shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
			bitmapShader.setLocalMatrix(shaderMatrix);
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawCircle(radius, radius, radius, paint);
			if (strokePaint != null) canvas.drawCircle(radius, radius, radius, strokePaint);
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
