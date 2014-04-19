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

import com.nostra13.universalimageloader.utils.L;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limited cache. Provides object storing. Size of all stored bitmaps will not to exceed size limit (
 * {@link #getSizeLimit()}).<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored Bitmaps. Strong references - for limited count of
 * Bitmaps (depends on cache size), weak references - for all other cached Bitmaps.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see BaseMemoryCache
 * @since 1.0.0
 */
public abstract class LimitedMemoryCache extends BaseMemoryCache {

	private static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16;
	private static final int MAX_NORMAL_CACHE_SIZE = MAX_NORMAL_CACHE_SIZE_IN_MB * 1024 * 1024;

	private final int sizeLimit;

	private final AtomicInteger cacheSize;

	/**
	 * Contains strong references to stored objects. Each next object is added last. If hard cache size will exceed
	 * limit then first object is deleted (but it continue exist at {@link #softMap} and can be collected by GC at any
	 * time)
	 */
	private final List<Bitmap> hardCache = Collections.synchronizedList(new LinkedList<Bitmap>());

	/** @param sizeLimit Maximum size for cache (in bytes) */
	public LimitedMemoryCache(int sizeLimit) {
		this.sizeLimit = sizeLimit;
		cacheSize = new AtomicInteger();
		if (sizeLimit > MAX_NORMAL_CACHE_SIZE) {
			L.w("You set too large memory cache size (more than %1$d Mb)", MAX_NORMAL_CACHE_SIZE_IN_MB);
		}
	}

	@Override
	public boolean put(String key, Bitmap value) {
		boolean putSuccessfully = false;
		// Try to add value to hard cache
		int valueSize = getSize(value);
		int sizeLimit = getSizeLimit();
		int curCacheSize = cacheSize.get();
		if (valueSize < sizeLimit) {
			while (curCacheSize + valueSize > sizeLimit) {
				Bitmap removedValue = removeNext();
				if (hardCache.remove(removedValue)) {
					curCacheSize = cacheSize.addAndGet(-getSize(removedValue));
				}
			}
			hardCache.add(value);
			cacheSize.addAndGet(valueSize);

			putSuccessfully = true;
		}
		// Add value to soft cache
		super.put(key, value);
		return putSuccessfully;
	}

	@Override
	public void remove(String key) {
		Bitmap value = super.get(key);
		if (value != null) {
			if (hardCache.remove(value)) {
				cacheSize.addAndGet(-getSize(value));
			}
		}
		super.remove(key);
	}

	@Override
	public void clear() {
		hardCache.clear();
		cacheSize.set(0);
		super.clear();
	}

	protected int getSizeLimit() {
		return sizeLimit;
	}

	protected abstract int getSize(Bitmap value);

	protected abstract Bitmap removeNext();
}
