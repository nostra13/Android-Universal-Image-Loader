/*******************************************************************************
 * Copyright 2013 Sergey Tarasevich
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
package com.nostra13.universalimageloader.core.listener;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Listener which is designed for synchronous image loading.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.0
 */
public class SyncImageLoadingListener extends SimpleImageLoadingListener {

	private Bitmap loadedImage;

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		this.loadedImage = loadedImage;
	}

	public Bitmap getLoadedBitmap() {
		return loadedImage;
	}
}
