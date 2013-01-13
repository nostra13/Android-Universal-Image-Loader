package com.nostra13.example.universalimageloader.downloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;

import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ExtendedImageDownloader extends URLConnectionImageDownloader {

	public static final String PROTOCOL_ASSETS = "assets";
	public static final String PROTOCOL_DRAWABLE = "drawable";

	private static final String PROTOCOL_ASSETS_PREFIX = PROTOCOL_ASSETS + "://";
	private static final String PROTOCOL_DRAWABLE_PREFIX = PROTOCOL_DRAWABLE + "://";

	private Context context;

	public ExtendedImageDownloader(Context context) {
		this.context = context;
	}

	@Override
	protected InputStream getStreamFromOtherSource(URI imageUri) throws IOException {
		String protocol = imageUri.getScheme();
		if (PROTOCOL_ASSETS.equals(protocol)) {
			return getStreamFromAssets(imageUri);
		} else if (PROTOCOL_DRAWABLE.equals(protocol)) {
			return getStreamFromDrawable(imageUri);
		} else {
			return super.getStreamFromOtherSource(imageUri);
		}
	}

	private InputStream getStreamFromAssets(URI imageUri) throws IOException {
		String filePath = imageUri.toString().substring(PROTOCOL_ASSETS_PREFIX.length()); // Remove "assets://" prefix from image URI
		return context.getAssets().open(filePath);
	}

	private InputStream getStreamFromDrawable(URI imageUri) {
		String drawableIdString = imageUri.toString().substring(PROTOCOL_DRAWABLE_PREFIX.length()); // Remove "drawable://" prefix from image URI
		int drawableId = Integer.parseInt(drawableIdString);
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(drawableId);
		Bitmap bitmap = drawable.getBitmap();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, os);
		return new ByteArrayInputStream(os.toByteArray());
	}
}
