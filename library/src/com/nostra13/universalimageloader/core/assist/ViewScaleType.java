package com.nostra13.universalimageloader.core.assist;

import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * Simplify {@linkplain ScaleType ImageView's scale type} to 2 types: {@link #FIT_INSIDE} and {@link #CROP}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public enum ViewScaleType {
	/**
	 * Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions (width and height) of the
	 * image will be equal to or less the corresponding dimension of the view.
	 */
	FIT_INSIDE,
	/**
	 * Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions (width and height) of the
	 * image will be equal to or larger than the corresponding dimension of the view.
	 */
	CROP;

	public static ViewScaleType fromImageView(ImageView imageView) {
		switch (imageView.getScaleType()) {
			case FIT_CENTER:
			case FIT_XY:
			case FIT_START:
			case FIT_END:
			case CENTER_INSIDE:
				return FIT_INSIDE;
			case MATRIX:
			case CENTER:
			case CENTER_CROP:
			default:
				return CROP;
		}
	}
}
