/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
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

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Memory cache with {@linkplain WeakReference weak references} to {@linkplain android.graphics.Bitmap bitmaps}<br />
 * <br />
 * <b>NOTE:</b> This cache uses only weak references for stored Bitmaps.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.3
 */
public class WeakMemoryCache implements MemoryCache {

	/** Stores not strong references to objects */
	protected final Map<String, Reference<Bitmap>> softMap = Collections
			.synchronizedMap(new HashMap<String, Reference<Bitmap>>());

	@Override
	public Bitmap get(String key) {
		Bitmap result = null;
		Reference<Bitmap> reference = softMap.get(key);
		if (reference != null) {
			result = reference.get();
		}
		return result;
	}

	@Override
	public void put(String key, Bitmap value) {
		softMap.put(key, createReference(value));
	}

	@Override
	public Bitmap remove(String key) {
		Reference<Bitmap> bmpRef = softMap.remove(key);
		return bmpRef == null ? null : bmpRef.get();
	}

	@Override
	public Set<String> keys() {
		synchronized (softMap) {
			return new HashSet<String>(softMap.keySet());
		}
	}

	@Override
	public void clear() {
		softMap.clear();
	}

	@Override
	public int size() {
		int size = 0;
		synchronized (softMap) {
			for (Map.Entry<String, Reference<Bitmap>> entry : softMap.entrySet()) {
				Bitmap bitmap = entry.getValue().get();
				if (bitmap != null) {
					size += bitmap.getRowBytes() * bitmap.getHeight();
				}
			}
		}
		return size;
	}

	/** Creates {@linkplain Reference not strong} reference of value */
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
