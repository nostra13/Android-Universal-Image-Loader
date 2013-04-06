package com.nostra13.universalimageloader.cache.disc.writer.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.cache.disc.writer.DiscCacheWriter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;
import com.nostra13.universalimageloader.utils.IoUtils;
/**
 * Provides storing of the image without doing any modifications. 
 *
 */
public class DefaultDiscCacheWriter implements DiscCacheWriter {
	@Override
	public boolean writeSizedImage(Bitmap bmp, CompressFormat imageCompressFormatForDiscCache,int imageQualityForDiscCache, File targetFile,int buffersize) throws IOException {
		boolean savedSuccessfully = false;
		if (bmp != null) {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile), buffersize);
			try {
				savedSuccessfully = bmp.compress(imageCompressFormatForDiscCache, imageQualityForDiscCache, os);
			} finally {
				IoUtils.closeSilently(os);
			}
		}
		return savedSuccessfully;
	}

	@Override
	public void writeImage(InputStream inputstream, File targetFile, int buffersize) throws IOException {
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile), buffersize);
			try {
				IoUtils.copyStream(inputstream, os);
			} finally {
				IoUtils.closeSilently(os);
			}
		} finally {
			IoUtils.closeSilently(inputstream);
		}
	}

}
