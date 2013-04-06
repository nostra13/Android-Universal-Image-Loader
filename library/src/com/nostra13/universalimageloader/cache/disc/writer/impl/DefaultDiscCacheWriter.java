package com.nostra13.universalimageloader.cache.disc.writer.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.cache.disc.writer.DiscCacheWriter;
import com.nostra13.universalimageloader.utils.IoUtils;
/**
 * Provides storing of the image without doing any modifications. 
 *
 */
public class DefaultDiscCacheWriter implements DiscCacheWriter {
	private static final int BUFFER_SIZE = 8 * 1024; // 8 Kb

	@Override
	public boolean writeSizedImage(Bitmap bmp, CompressFormat imageCompressFormatForDiscCache,int imageQualityForDiscCache, File targetFile) throws IOException {
		boolean savedSuccessfully = false;
		if (bmp != null) {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile), BUFFER_SIZE);
			try {
				savedSuccessfully = bmp.compress(imageCompressFormatForDiscCache, imageQualityForDiscCache, os);
			} finally {
				IoUtils.closeSilently(os);
			}
		}
		return savedSuccessfully;
	}

	@Override
	public void writeImage(InputStream inputstream, File targetFile) throws IOException {
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile), BUFFER_SIZE);
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
