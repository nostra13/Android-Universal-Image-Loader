package com.nostra13.universalimageloader.core.assist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.AdapterView;
import android.widget.ImageView.ScaleType;

/**
 * Type of image scaling during decoding.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public enum ImageScaleType {
	/**
	 * The same as {@link #IN_SAMPLE_POWER_OF_2}
	 * 
	 * @deprecated Will be deleted in the future. Use {@link #IN_SAMPLE_POWER_OF_2}.
	 */
	@Deprecated
	POWER_OF_2,
	/**
	 * Image will be reduces 2-fold until next reduce step make image smaller target size.<br />
	 * It's <b>fast</b> type and it's preferable for usage in lists/grids/galleries (and other {@linkplain AdapterView
	 * adapter-views}) .<br />
	 * Relates to {@link BitmapFactory.Options#inSampleSize}<br />
	 * Note: If original image size is smaller than target size then original image <b>won't</b> be scaled.
	 */
	IN_SAMPLE_POWER_OF_2,
	/**
	 * The same as {@link #IN_SAMPLE_INT}
	 * 
	 * @deprecated Will be deleted in the future. Use {@link #IN_SAMPLE_INT}.
	 */
	@Deprecated
	EXACT,
	/**
	 * Image will be subsampled in an integer number of times. Use it if memory economy is quite important.<br />
	 * Relates to {@link BitmapFactory.Options#inSampleSize}<br />
	 * Note: If original image size is smaller than target size then original image <b>won't</b> be scaled.
	 */
	IN_SAMPLE_INT,
	/**
	 * Image will scaled-down exactly to target size (scaled width or height or both will be equal to target size;
	 * depends on {@linkplain ScaleType ImageView's scale type}). Use it if memory economy is critically important.<br />
	 * Note: If original image size is smaller than target size then original image <b>won't</b> be scaled.<br />
	 * <br />
	 * <b>Important note:</b> For creating result Bitmap (of exact size) additional Bitmap will be created with
	 * {@link Bitmap#createScaledBitmap(Bitmap, int, int, boolean) Bitmap.createScaledBitmap(...)}. So this scale type
	 * requires more memory for creation of result Bitmap, but then save memory by keeping in memory smaller Bitmap
	 * (comparing with IN_SAMPLE... scale types).
	 */
	EXACTLY,

	/**
	 * Image will scaled exactly to target size (scaled width or height or both will be equal to target size; depends on
	 * {@linkplain ScaleType ImageView's scale type}). Use it if memory economy is critically important.<br />
	 * Note: If original image size is smaller than target size then original image <b>will be stretched</b> to target
	 * size.<br />
	 * <br />
	 * <b>Important note:</b> For creating result Bitmap (of exact size) additional Bitmap will be created with
	 * {@link Bitmap#createScaledBitmap(Bitmap, int, int, boolean) Bitmap.createScaledBitmap(...)}. So this scale type
	 * requires more memory for creation of result Bitmap, but then save memory by keeping in memory smaller Bitmap
	 * (comparing with IN_SAMPLE... scale types).
	 */
	EXACTLY_STRETCHED
}
