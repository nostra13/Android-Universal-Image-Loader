package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Displays {@link Bitmap} in {@link ImageView}. Successors can apply some changes to Bitmap before displaying or apply
 * animation.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface BitmapDisplayer {

	/**
	 * Display bitmap in {@link ImageView}. Incoming bitmap can be changed any way before displaying. Displayed bitmap
	 * should be returned.
	 * 
	 * @param bitmap
	 *            Source bitmap
	 * @param imageView
	 *            {@link ImageView Image view}
	 * @return Bitmap which was displayed in {@link ImageView}
	 */
	Bitmap display(Bitmap bitmap, ImageView imageView);
}
