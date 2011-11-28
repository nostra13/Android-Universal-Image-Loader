package com.nostra13.test.imageloader;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides operations with files
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class FileUtils {

	public static final String TAG = FileUtils.class.getSimpleName();

	private FileUtils() {
	}

	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			while (true) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage(), ex);
		}
	}
}
