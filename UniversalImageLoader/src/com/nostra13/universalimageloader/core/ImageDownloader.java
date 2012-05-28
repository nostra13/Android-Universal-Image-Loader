package com.nostra13.universalimageloader.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Provides retrieving of {@link InputStream} of image by URL.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class ImageDownloader {

	protected static final String PROTOCOL_FILE = "file";

	/** Retrieves {@link InputStream} of image by URL. Image can be located as in the network and on local file system. */
	protected InputStream getStream(String imageUrl) throws IOException {
		return getStream(new URL(imageUrl));
	}

	/** Retrieves {@link InputStream} of image by URL. Image can be located as in the network and on local file system. */
	protected InputStream getStream(URL imageUrl) throws IOException {
		String protocol = imageUrl.getProtocol();
		if (PROTOCOL_FILE.equals(protocol)) {
			return getStreamFromFile(imageUrl);
		} else {
			return getStreamFromNetwork(imageUrl);
		}
	}

	/** Retrieves {@link InputStream} of image by URL (image is located in the network) */
	protected abstract InputStream getStreamFromNetwork(URL imageUrl) throws IOException;

	/** Retrieves {@link InputStream} of image by URL (image is located on the local file system or SD card) */
	protected InputStream getStreamFromFile(URL imageUrl) throws IOException {
		return new BufferedInputStream(imageUrl.openStream());
	}
}

/** Default implementation of ImageDownloader. Uses {@link URLConnection} for image stream retrieving. */
class DefaultImageDownloader extends ImageDownloader {

	private int connectTimeout;
	private int readTimeout;

	public DefaultImageDownloader(int connectTimeout, int readTimeout) {
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

	@Override
	public InputStream getStreamFromNetwork(URL imageUrl) throws IOException {
		URLConnection conn = imageUrl.openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		return new BufferedInputStream(conn.getInputStream());
	}
}
