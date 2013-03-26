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
package com.nostra13.universalimageloader.core.assist;

import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * Simplify {@linkplain ScaleType ImageView's scale type} to 2 types: {@link #FIT_INSIDE} and {@link #CROP}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.6.1
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

	/**
	 * Defines scale type of ImageView.
	 * 
	 * @param imageView {@link ImageView}
	 * @return {@link #FIT_INSIDE} for
	 *         <ul>
	 *         <li>{@link ScaleType#FIT_CENTER}</li>
	 *         <li>{@link ScaleType#FIT_XY}</li>
	 *         <li>{@link ScaleType#FIT_START}</li>
	 *         <li>{@link ScaleType#FIT_END}</li>
	 *         <li>{@link ScaleType#CENTER_INSIDE}</li>
	 *         </ul>
	 *         {@link #CROP} for
	 *         <ul>
	 *         <li>{@link ScaleType#CENTER}</li>
	 *         <li>{@link ScaleType#CENTER_CROP}</li>
	 *         <li>{@link ScaleType#MATRIX}</li>
	 *         </ul>
	 *         ,
	 * 
	 */
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
