package com.nostra13.universalimageloader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides operations with files
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class FileUtils {

	private FileUtils() {
	}

	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		final int buffer_size = 1024;
		byte[] bytes = new byte[buffer_size];
		while (true) {
			int count = is.read(bytes, 0, buffer_size);
			if (count == -1) {
				break;
			}
			os.write(bytes, 0, count);
		}
	}
}
