package com.nostra13.universalimageloader.core.download;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;

/**
 * Provides retrieving of {@link InputStream} of image by URI from network or file system or app resources.<br />
 * {@link URLConnection} is used to retrieve image stream from network.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * 
 * @see HttpClientImageDownloader
 */
public class BaseImageDownloader implements ImageDownloader {

	/** {@value} */
	public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
	/** {@value} */
	public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

	protected static final int BUFFER_SIZE = 8 * 1024; // 8 Kb

	protected static final String SCHEME_HTTP = "http";
	protected static final String SCHEME_HTTPS = "https";
	protected static final String SCHEME_FILE = "file";
	protected static final String SCHEME_CONTENT = "content";
	protected static final String SCHEME_ASSETS = "assets";
	protected static final String SCHEME_DRAWABLE = "drawable";

	protected static final String SCHEME_ASSETS_PREFIX = SCHEME_ASSETS + "://";
	protected static final String SCHEME_DRAWABLE_PREFIX = SCHEME_DRAWABLE + "://";

	protected final Context context;
	protected final int connectTimeout;
	protected final int readTimeout;

	public BaseImageDownloader(Context context) {
		this.context = context.getApplicationContext();
		this.connectTimeout = DEFAULT_HTTP_CONNECT_TIMEOUT;
		this.readTimeout = DEFAULT_HTTP_READ_TIMEOUT;
	}

	public BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
		this.context = context.getApplicationContext();
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

	@Override
	public InputStream getStream(URI imageUri, Object extra) throws IOException {
		String scheme = imageUri.getScheme();
		if (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme)) {
			return getStreamFromNetwork(imageUri, extra);
		} else if (SCHEME_FILE.equals(scheme)) {
			return getStreamFromFile(imageUri, extra);
		} else if (SCHEME_CONTENT.equals(scheme)) {
			return getStreamFromContent(imageUri, extra);
		} else if (SCHEME_ASSETS.equals(scheme)) {
			return getStreamFromAssets(imageUri, extra);
		} else if (SCHEME_DRAWABLE.equals(scheme)) {
			return getStreamFromDrawable(imageUri, extra);
		} else {
			return getStreamFromOtherSource(imageUri, extra);
		}
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is located in the network).
	 * 
	 * @param imageUri Image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
	 *             URI.
	 */
	protected InputStream getStreamFromNetwork(URI imageUri, Object extra) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) imageUri.toURL().openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		return new FlushedInputStream(conn.getInputStream(), BUFFER_SIZE);
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is located on the local file system or SD card).
	 * 
	 * @param imageUri Image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException if some I/O error occurs reading from file system
	 */
	protected InputStream getStreamFromFile(URI imageUri, Object extra) throws IOException {
		return new BufferedInputStream(imageUri.toURL().openStream(), BUFFER_SIZE);
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is accessed using {@link ContentResolver}).
	 * 
	 * @param imageUri Image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws FileNotFoundException if the provided URI could not be opened
	 */
	protected InputStream getStreamFromContent(URI imageUri, Object extra) throws FileNotFoundException {
		ContentResolver res = context.getContentResolver();
		Uri uri = Uri.parse(imageUri.toString());
		return res.openInputStream(uri);
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is located in assets of application).
	 * 
	 * @param imageUri Image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException if some I/O error occurs file reading
	 */
	protected InputStream getStreamFromAssets(URI imageUri, Object extra) throws IOException {
		String filePath = imageUri.toString().substring(SCHEME_ASSETS_PREFIX.length()); // Remove "assets://" prefix from image URI
		return context.getAssets().open(filePath);
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is located in drawable resources of application).
	 * 
	 * @param imageUri Image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 */
	protected InputStream getStreamFromDrawable(URI imageUri, Object extra) {
		String drawableIdString = imageUri.toString().substring(SCHEME_DRAWABLE_PREFIX.length()); // Remove "drawable://" prefix from image URI
		int drawableId = Integer.parseInt(drawableIdString);
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(drawableId);
		Bitmap bitmap = drawable.getBitmap();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, os);
		return new ByteArrayInputStream(os.toByteArray());
	}

	/**
	 * Retrieves {@link InputStream} of image by URI from other source. Should be overriden by successors to implement
	 * image downloading from special sources.
	 * 
	 * @param imageUri Image URI
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException if some I/O error occurs
	 */
	protected InputStream getStreamFromOtherSource(URI imageUri, Object extra) throws IOException {
		return null;
	}
}