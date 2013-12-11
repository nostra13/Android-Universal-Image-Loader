/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides I/O operations
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class IoUtils {

	private static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 KB

	private IoUtils() {
	}

	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		copyStream(is, os, null);
	}

	public static boolean copyStream(InputStream is, OutputStream os, CopyingListener listener)
			throws IOException {
		return copyStream(is, os, listener, DEFAULT_BUFFER_SIZE);
	}

	public static boolean copyStream(InputStream is, OutputStream os, CopyingListener listener, int bufferSize)
			throws IOException {
		int current = 0;
		final int total = is.available();

		final byte[] bytes = new byte[bufferSize];
		int count;
		if (shouldStopLoading(listener, current, total)) return false;
		while ((count = is.read(bytes, 0, bufferSize)) != -1) {
			os.write(bytes, 0, count);
			current += count;
			if (shouldStopLoading(listener, current, total)) return false;
		}
		return true;
	}

	private static boolean shouldStopLoading(CopyingListener listener, int current, int total) {
		if (listener != null) {
			boolean shouldContinue = listener.onBytesCopied(current, total);
			if (!shouldContinue) {
				if ((float) current / total < 0.75) {
					return true; // if loaded more than 75% then continue loading anyway
				}
			}
		}
		return false;
	}

	public static void closeSilently(Closeable closeable) {
		try {
			closeable.close();
		} catch (Exception e) {
			// Do nothing
		}
	}

	public static interface CopyingListener {
		/**
		 * @param current Loaded bytes
		 * @param total   Total bytes for loading
		 *
		 * @return <b>true</b> - if loading should be continued; <b>false</b> - if loading should be interrupted
		 */
		boolean onBytesCopied(int current, int total);
	}
}
