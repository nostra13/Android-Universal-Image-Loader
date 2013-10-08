/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * Copyright 2013 Daniel Martí
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
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;

/**
 * Displays image with "fade in" animation
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.6.4
 */
public class FadeInBitmapDisplayer implements BitmapDisplayer {

	private final int durationMillis;
	private final boolean fromNetwork;
	private final boolean fromDisc;
	private final boolean fromMemory;

	public FadeInBitmapDisplayer(int durationMillis) {
		this.durationMillis = durationMillis;
		this.fromNetwork = true;
		this.fromDisc = true;
		this.fromMemory = true;
	}

	public FadeInBitmapDisplayer(int durationMillis,
			boolean fromNetwork, boolean fromDisc, boolean fromMemory) {
		this.durationMillis = durationMillis;
		this.fromNetwork = fromNetwork;
		this.fromDisc = fromDisc;
		this.fromMemory = fromMemory;
	}

	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
		imageView.setImageBitmap(bitmap);

		if ((fromNetwork && loadedFrom == LoadedFrom.NETWORK) ||
				(fromDisc && loadedFrom == LoadedFrom.DISC_CACHE) ||
				(fromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE))
			animate(imageView, durationMillis);

		return bitmap;
	}

	/**
	 * Animates {@link ImageView} with "fade-in" effect
	 *
	 * @param imageView      {@link ImageView} which display image in
	 * @param durationMillis The length of the animation in milliseconds
	 */
	public static void animate(ImageView imageView, int durationMillis) {
		AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
		fadeImage.setDuration(durationMillis);
		fadeImage.setInterpolator(new DecelerateInterpolator());
		imageView.startAnimation(fadeImage);
	}
}
