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

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Base memory cache. Implements common functionality for memory cache. Provides object references (
 * {@linkplain Reference not strong}) storing.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public abstract class BaseMemoryCache<K, V> implements MemoryCacheAware<K, V> {

	/** Stores not strong references to objects */
	private final Map<K, Reference<V>> softMap = Collections.synchronizedMap(new HashMap<K, Reference<V>>());

	@Override
	public V get(K key) {
		V result = null;
		Reference<V> reference = softMap.get(key);
		if (reference != null) {
			result = reference.get();
		}
		return result;
	}

	@Override
	public boolean put(K key, V value) {
		softMap.put(key, createReference(value));
		return true;
	}

	@Override
	public void remove(K key) {
		softMap.remove(key);
	}

	@Override
	public Collection<K> keys() {
		synchronized (softMap) {
			return new HashSet<K>(softMap.keySet());
		}
	}

	@Override
	public void clear() {
		softMap.clear();
	}

	/** Creates {@linkplain Reference not strong} reference of value */
	protected abstract Reference<V> createReference(V value);
}
