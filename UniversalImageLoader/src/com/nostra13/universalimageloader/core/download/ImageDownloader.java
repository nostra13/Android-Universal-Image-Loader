package com.nostra13.universalimageloader.core.download;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Provides retrieving of {@link InputStream} of image by URL.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class ImageDownloader {

	protected static final String PROTOCOL_FILE = "file";

	/** Retrieves {@link InputStream} of image by URL. Image can be located as in the network and on local file system. */
	public InputStream getStream(URL imageUrl) throws IOException {
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