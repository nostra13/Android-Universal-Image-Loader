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

import android.view.View;

/**
 * Listener for image loading progress.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.1
 */
public interface ImageLoadingProgressListener {

	/**
	 * Is called when image loading progress changed.
	 *
	 * @param imageUri Image URI
	 * @param view     View for image. Can be <b>null</b>.
	 * @param current  Downloaded size in bytes
	 * @param total    Total size in bytes
	 */
	void onProgressUpdate(String imageUri, View view, int current, int total);
}
