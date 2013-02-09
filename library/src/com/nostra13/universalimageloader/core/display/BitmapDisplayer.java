package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Displays {@link Bitmap} in {@link ImageView}. Implementations can apply some changes to Bitmap or any animation for
 * displaying Bitmap.<br />
 * Implementations have to be thread-safe.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface BitmapDisplayer {
	/**
	 * Display bitmap in {@link ImageView}. Displayed bitmap should be returned.<br />
	 * <b>NOTE:</b> This method is called on UI thread so it's strongly recommended not to do any heavy work in it.
	 * 
	 * @param bitmap Source bitmap
	 * @param imageView {@linkplain ImageView Image view} to display Bitmap
	 * @return Bitmap which was displayed in {@link ImageView}
	 */
	Bitmap display(Bitmap bitmap, ImageView imageView);
}
