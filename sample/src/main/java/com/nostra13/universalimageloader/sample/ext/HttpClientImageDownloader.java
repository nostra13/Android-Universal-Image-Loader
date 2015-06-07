/*******************************************************************************
 * Copyright 2011-2015 Sergey Tarasevich
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

import android.content.Context;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of ImageDownloader which uses {@link HttpClient} for image stream retrieving.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HttpClientImageDownloader extends BaseImageDownloader {

	private HttpClient httpClient;

	public HttpClientImageDownloader(Context context, HttpClient httpClient) {
		super(context);
		this.httpClient = httpClient;
	}

	@Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
		HttpGet httpRequest = new HttpGet(imageUri);
		HttpResponse response = httpClient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
	}
}