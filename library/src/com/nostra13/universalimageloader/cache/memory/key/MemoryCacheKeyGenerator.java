/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.cache.memory.key;

import com.nostra13.universalimageloader.core.assist.ImageSize;

/**
 * @author Sergey Tarasevich
 *         12.09.2014.
 */
public interface MemoryCacheKeyGenerator {

	String generate(String imageUri, ImageSize originalSize, ImageSize targetSize);

	boolean isKeyOfUri(String key, String imageUri);
}
