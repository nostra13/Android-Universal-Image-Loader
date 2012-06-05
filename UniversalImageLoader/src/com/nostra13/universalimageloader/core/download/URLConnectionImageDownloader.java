package com.nostra13.universalimageloader.core.download;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.nostra13.universalimageloader.core.assist.FlushedInputStream;

/**
 * Default implementation of ImageDownloader. Uses {@link URLConnection} for image stream retrieving.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class URLConnectionImageDownloader extends ImageDownloader {

	private int connectTimeout;
	private int readTimeout;

	public URLConnectionImageDownloader(int connectTimeout, int readTimeout) {
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

	@Override
	public InputStream getStreamFromNetwork(URL imageUrl) throws IOException {
		URLConnection conn = imageUrl.openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		return new FlushedInputStream(new BufferedInputStream(conn.getInputStream()));
	}
}