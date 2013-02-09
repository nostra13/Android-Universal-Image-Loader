package com.nostra13.universalimageloader.core.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Decorator. Prevents downloads from network (throws {@link IllegalStateException exception}).<br />
 * In most cases this downloader shouldn't be used directly.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
public class NetworkDeniedImageDownloader implements ImageDownloader {

	private final ImageDownloader wrappedDownloader;

	public NetworkDeniedImageDownloader(ImageDownloader wrappedDownloader) {
		this.wrappedDownloader = wrappedDownloader;
	}

	@Override
	public InputStream getStream(URI imageUri, Object extra) throws IOException {
		String scheme = imageUri.getScheme();
		if (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme)) {
			throw new IllegalStateException();
		} else {
			return wrappedDownloader.getStream(imageUri, extra);
		}
	}
}
