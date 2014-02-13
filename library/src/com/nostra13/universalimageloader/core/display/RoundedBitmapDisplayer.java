/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
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

import android.graphics.*;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Can display bitmap with rounded corners. This implementation works only with ImageViews wrapped
 * in ImageViewAware.
 * <br />
 * This implementation is inspired by
 * <a href="http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/">
 * Romain Guy's article</a>. It rounds images using custom drawable drawing. Original bitmap isn't changed.
 * <br />
 * <br />
 * If this implementation doesn't meet your needs then consider
 * <a href="https://github.com/vinc3m1/RoundedImageView">this project</a> for usage.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public class RoundedBitmapDisplayer implements BitmapDisplayer {

	protected final int cornerRadius;
	protected final int margin;

	public RoundedBitmapDisplayer(int cornerRadiusPixels) {
		this(cornerRadiusPixels, 0);
	}

	public RoundedBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
		this.cornerRadius = cornerRadiusPixels;
		this.margin = marginPixels;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}

		imageAware.setImageDrawable(new RoundedDrawable(bitmap, cornerRadius, margin));
	}

	public static class RoundedDrawable extends Drawable {

		protected final float cornerRadius;
		protected final int margin;

		protected final RectF mRect = new RectF(),
				mBitmapRect;
		protected final BitmapShader bitmapShader;
		protected final Paint paint;

		public RoundedDrawable(Bitmap bitmap, int cornerRadius, int margin) {
			this.cornerRadius = cornerRadius;
			this.margin = margin;

			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mBitmapRect = new RectF (margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
			
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mRect.set(margin, margin, bounds.width() - margin, bounds.height() - margin);
			
			// Resize the original bitmap to fit the new bound
			Matrix shaderMatrix = new Matrix();
			shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
			bitmapShader.setLocalMatrix(shaderMatrix);
			
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
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
