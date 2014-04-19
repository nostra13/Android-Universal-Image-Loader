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
package com.nostra13.universalimageloader.cache.memory;

import android.graphics.Bitmap;

import java.lang.ref.Reference;
import java.util.*;

/**
 * Base memory cache. Implements common functionality for memory cache. Provides object references (
 * {@linkplain Reference not strong}) storing.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public abstract class BaseMemoryCache implements MemoryCache {

	/** Stores not strong references to objects */
	private final Map<String, Reference<Bitmap>> softMap = Collections.synchronizedMap(new HashMap<String, Reference<Bitmap>>());

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
	public boolean put(String key, Bitmap value) {
		softMap.put(key, createReference(value));
		return true;
	}

	@Override
	public void remove(String key) {
		softMap.remove(key);
	}

	@Override
	public Collection<String> keys() {
		synchronized (softMap) {
			return new HashSet<String>(softMap.keySet());
		}
	}

	@Override
	public void clear() {
		softMap.clear();
	}

	/** Creates {@linkplain Reference not strong} reference of value */
	protected abstract Reference<Bitmap> createReference(Bitmap value);
}
