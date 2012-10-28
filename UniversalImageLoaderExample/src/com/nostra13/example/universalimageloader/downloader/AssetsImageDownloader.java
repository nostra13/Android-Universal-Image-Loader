package com.nostra13.example.universalimageloader.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class AssetsImageDownloader extends URLConnectionImageDownloader {

	public static final String PROTOCOL_ASSETS = "assets";
	public static final String PROTOCOL_ASSETS_PREFIX = PROTOCOL_ASSETS + "://";

	private Context context;

	public AssetsImageDownloader(Context context) {
		this.context = context;
	}

	@Override
	protected InputStream getStreamFromOtherSource(URI imageUri) throws IOException {
		String protocol = imageUri.getScheme();
		if (PROTOCOL_ASSETS.equals(protocol)) {
			String filePath = imageUri.toString().substring(PROTOCOL_ASSETS_PREFIX.length()); // Remove "assets://" prefix from image URI
			return context.getAssets().open(filePath);
		} else {
			return super.getStreamFromOtherSource(imageUri);
		}
	}
}
