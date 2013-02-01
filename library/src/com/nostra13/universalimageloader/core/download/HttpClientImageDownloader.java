package com.nostra13.universalimageloader.core.download;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

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
