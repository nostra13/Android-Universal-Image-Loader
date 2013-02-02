package com.nostra13.universalimageloader.core.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;

/**
 * Implementation of ImageDownloader which uses {@link HttpClient} for image stream retrieving.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HttpClientImageDownloader extends ImageDownloader {

	private HttpClient httpClient;

	public HttpClientImageDownloader(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	protected InputStream getStreamFromNetwork(URI imageUri) throws IOException {
		HttpGet httpRequest = new HttpGet(imageUri.toString());
		HttpResponse response = httpClient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
	}
}
