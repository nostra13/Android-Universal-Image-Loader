package com.nostra13.universalimageloader.core.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;

public class ThumbImageDownloader extends BaseImageDownloader {

	public ThumbImageDownloader(Context context) {
		super(context);
	}

	@Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
		URL src = new URL(imageUri); 

		return  src.openStream();
	}
}
