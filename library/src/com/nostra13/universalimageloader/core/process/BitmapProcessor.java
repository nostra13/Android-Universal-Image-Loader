package com.nostra13.universalimageloader.core.process;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Makes some processing on {@link Bitmap}. Implementations can apply any changes to original {@link Bitmap}.<br />
 * Implementations have to be thread-safe.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface BitmapProcessor {

	/**
	 * Makes some processing of incoming bitmap.<br />
	 * This method is executing on additional thread (not on UI thread).<br />
	 * If this processor is used as {@linkplain DisplayImageOptions.Builder#preProcessor(BitmapProcessor) pre-processor}
	 * then don't forget {@linkplain Bitmap#recycle() to recycle} incoming bitmap if you return a new created one.
	 * 
	 * @param bitmap Original {@linkplain Bitmap bitmap}
	 * @param imageView {@linkplain ImageView} which result bitmap will be displayed in
	 * @return Processed {@linkplain Bitmap bitmap}
	 */
	Bitmap process(Bitmap bitmap, ImageView imageView);
}
