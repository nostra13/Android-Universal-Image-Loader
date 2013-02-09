package com.nostra13.universalimageloader.core.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Provides retrieving of {@link InputStream} of image by URI.<br />
 * Implementations have to be thread-safe.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.4.0
 */
public interface ImageDownloader {

	/// Supported URI schemes(protocols)
	String SCHEME_HTTP = "http";
	String SCHEME_HTTPS = "https";
	String SCHEME_FILE = "file";
	String SCHEME_CONTENT = "content";
	String SCHEME_ASSETS = "assets";
	String SCHEME_DRAWABLE = "drawable";

	/**
	 * Retrieves {@link InputStream} of image by URI.
	 * 
	 * @param imageUri Image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException if some I/O error occurs during getting image stream
	 * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
	 */
	InputStream getStream(URI imageUri, Object extra) throws IOException;
}
