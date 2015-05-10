package com.nostra13.universalimageloader.cache.memory.impl;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Tarasevich
 * @since 1.9.4
 */
public abstract class BaseMemoryCache implements MemoryCache {

	protected final int maxSize;

	protected final Collection<MemoryCacheListener> listeners = new CopyOnWriteArrayList<MemoryCacheListener>();

	protected BaseMemoryCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize <= 0");
		}
		this.maxSize = maxSize;
	}

	@Override
	public int maxSize() {
		return maxSize;
	}

	@Override
	public void addMemoryCacheListener(MemoryCacheListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMemoryCacheListener(MemoryCacheListener listener) {
		listeners.remove(listener);
	}

	protected void fireRemovedEvent(String key, Bitmap removedBitmap) {
		for (MemoryCacheListener listener : listeners) {
			listener.onEntryRemoved(key, removedBitmap);
		}
	}
}
