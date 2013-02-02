package com.nostra13.universalimageloader.core.download;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

/**
 * Image downloader which supports retrieving images from {@linkplain ContentProvider content providers}
 * (<b>"content://..."</b>), assets (<b>"assets://..."</b>) and drawables (<b>"drawable://..."</b>).
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ExtendedImageDownloader extends URLConnectionImageDownloader {

	protected static final String SCHEME_CONTENT = ContentResolver.SCHEME_CONTENT;
	protected static final String SCHEME_ASSETS = "assets";
	protected static final String SCHEME_DRAWABLE = "drawable";

	private static final String SCHEME_ASSETS_PREFIX = SCHEME_ASSETS + "://";
	private static final String SCHEME_DRAWABLE_PREFIX = SCHEME_DRAWABLE + "://";

	protected Context context;

	public ExtendedImageDownloader(Context context) {
		this.context = context;
	}

	@Override
	protected InputStream getStreamFromOtherSource(URI imageUri) throws IOException {
		String protocol = imageUri.getScheme();
		if (SCHEME_CONTENT.equals(protocol)) {
			return getStreamFromContent(imageUri);
		} else if (SCHEME_ASSETS.equals(protocol)) {
			return getStreamFromAssets(imageUri);
		} else if (SCHEME_DRAWABLE.equals(protocol)) {
			return getStreamFromDrawable(imageUri);
		} else {
			return super.getStreamFromOtherSource(imageUri);
		}
	}

	/** Retrieves {@link InputStream} of image by URI (image is accessed using {@link ContentResolver}) */
	protected InputStream getStreamFromContent(URI imageUri) throws IOException {
		ContentResolver res = context.getContentResolver();
		Uri uri = Uri.parse(imageUri.toString());
		return res.openInputStream(uri);
	}

	/** Retrieves {@link InputStream} of image by URI (image is located in assets of application) */
	protected InputStream getStreamFromAssets(URI imageUri) throws IOException {
		String filePath = imageUri.toString().substring(SCHEME_ASSETS_PREFIX.length()); // Remove "assets://" prefix from image URI
		return context.getAssets().open(filePath);
	}

	/** Retrieves {@link InputStream} of image by URI (image is located in drawable resources of application) */
	protected InputStream getStreamFromDrawable(URI imageUri) {
		String drawableIdString = imageUri.toString().substring(SCHEME_DRAWABLE_PREFIX.length()); // Remove "drawable://" prefix from image URI
		int drawableId = Integer.parseInt(drawableIdString);
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(drawableId);
		Bitmap bitmap = drawable.getBitmap();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, os);
		return new ByteArrayInputStream(os.toByteArray());
	}
}
