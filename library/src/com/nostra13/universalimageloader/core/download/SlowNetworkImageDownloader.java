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
package com.nostra13.universalimageloader.core.download;

import java.io.IOException;
import java.io.InputStream;

import com.nostra13.universalimageloader.core.assist.FlushedInputStream;

/**
 * Decorator. Handles <a href="http://code.google.com/p/android/issues/detail?id=6066">this problem</a> on slow networks
 * using {@link FlushedInputStream}.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.1
 */
public class SlowNetworkImageDownloader implements ImageDownloader {

	private final ImageDownloader wrappedDownloader;

	public SlowNetworkImageDownloader(ImageDownloader wrappedDownloader) {
		this.wrappedDownloader = wrappedDownloader;
	}

	@Override
	public InputStream getStream(String imageUri, Object extra) throws IOException {
		InputStream imageStream = wrappedDownloader.getStream(imageUri, extra);
		switch (Scheme.ofUri(imageUri)) {
			case HTTP:
			case HTTPS:
				return new FlushedInputStream(imageStream);
			default:
				return imageStream;
		}
	}
}
