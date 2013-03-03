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
package com.nostra13.universalimageloader.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.BaseMemoryCache;

/**
 * Memory cache with {@linkplain WeakReference weak references} to {@linkplain android.graphics.Bitmap bitmaps}<br />
 * <br />
 * <b>NOTE:</b> This cache uses only weak references for stored Bitmaps.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.3
 */
public class WeakMemoryCache extends BaseMemoryCache<String, Bitmap> {
	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
