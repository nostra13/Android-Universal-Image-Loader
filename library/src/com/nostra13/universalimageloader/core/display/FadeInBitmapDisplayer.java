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

	private final int durationMillis;

	public FadeInBitmapDisplayer(int durationMillis) {
		this.durationMillis = durationMillis;
	}

	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView) {
		imageView.setImageBitmap(bitmap);

		animate(imageView, durationMillis);

		return bitmap;
	}

	/**
	 * Animates {@link ImageView} with "fade-in" effect
	 * 
	 * @param imageView {@link ImageView} which display image in
	 * @param durationMillis The length of the animation in milliseconds
	 */
	public static void animate(ImageView imageView, int durationMillis) {
		AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
		fadeImage.setDuration(durationMillis);
		fadeImage.setInterpolator(new DecelerateInterpolator());
		imageView.startAnimation(fadeImage);
	}

}
