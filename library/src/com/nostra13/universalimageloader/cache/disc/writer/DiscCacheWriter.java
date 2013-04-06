package com.nostra13.universalimageloader.cache.disc.writer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
/**
 * Provides storing of {@link InputStream} or {@link Bitmap} of image .
 * Implementations have to be thread-safe.
 * 
 */
public interface DiscCacheWriter {
	/**
	 * Stores {@link InputStream} in the provided target file.
	 * @param inputstream {@link InputStream} of image
	 * @param targetFile File to write to
	 * @param buffersize size of the buffer to use
	 * @throws IOException if some I/O error occurs storing image stream
	 */
	public void writeImage(InputStream inputstream, File targetFile) throws IOException;
	/**
	 * Stores {@link InputStream} in the provide file after compressing the given {@link Bitmap}.
	 * @param bmp
	 * @param imageCompressFormatForDiscCache - Compress format to use
	 * @param imageQualityForDiscCache - quality to use for compression
	 * @param targetFile - File to write to
	 * @param buffersize - size of the buffer to use
	 * @return true if saved, false otherwise
	 * @throws IOException if some I/O error occurs storing image
	 */
	public boolean writeSizedImage(Bitmap bmp, CompressFormat imageCompressFormatForDiscCache,int imageQualityForDiscCache, File targetFile) throws IOException;
		
}
