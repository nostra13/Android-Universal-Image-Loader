/*******************************************************************************
 * Copyright 2015 Sergey Tarasevich
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
package com.nostra13.universalimageloader.sample.ext;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Downloader supporting "base64://..." URIs.
 * E.g.: "base64://data:image/jpeg;base64,/9j/4AAQSkZ..."
 *
 * @author mrleolink, Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class Base64ImageDownloader extends BaseImageDownloader {

	public static final String BASE64_SCHEME = "base64";
	public static final String BASE64_URI_PREFIX = BASE64_SCHEME + "://";

	public static final String BASE64_DATA_PREFIX = "base64,";

	public Base64ImageDownloader(Context context) {
		super(context);
	}

	public Base64ImageDownloader(Context context, int connectTimeout, int readTimeout) {
		super(context, connectTimeout, readTimeout);
	}

	@Override
	public InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
		if (imageUri.startsWith(BASE64_URI_PREFIX)) {
			return getStreamFormBase64(imageUri, extra);
		}
		return super.getStreamFromOtherSource(imageUri, extra);
	}

	protected InputStream getStreamFormBase64(String imageUri, Object extra) {
		int dataStartIndex = imageUri.indexOf(BASE64_DATA_PREFIX) + BASE64_DATA_PREFIX.length();
		String base64 = imageUri.substring(dataStartIndex);
		return new ByteArrayInputStream(Base64.decode(base64, Base64.DEFAULT));
	}
}
