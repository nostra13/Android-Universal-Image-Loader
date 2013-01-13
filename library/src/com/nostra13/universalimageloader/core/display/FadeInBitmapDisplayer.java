package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Displays image with "fade in" animation
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class FadeInBitmapDisplayer implements BitmapDisplayer {

	private int duration;

	public FadeInBitmapDisplayer(int duration) {
		this.duration = duration;
	}

	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView) {
		imageView.setImageBitmap(bitmap);

		AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
		fadeImage.setDuration(duration);
		fadeImage.setInterpolator(new DecelerateInterpolator());
		imageView.startAnimation(fadeImage);

		return bitmap;
	}
}
