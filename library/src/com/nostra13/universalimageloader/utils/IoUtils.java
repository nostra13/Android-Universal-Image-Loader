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

	private static final int BUFFER_SIZE = 32 * 1024; // 32 KB
	
	private static final int BUFFER_SIZE_WITH_LISTENER = 1024; // 1 KB

	private IoUtils() {
	}

	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		while (true) {
			int count = is.read(bytes, 0, BUFFER_SIZE);
			if (count == -1) {
				break;
			}
			os.write(bytes, 0, count);
		}
	}

	public static void copyStream(final InputStream is, final OutputStream os, final StreamCopyListener listener)
			throws IOException {
		final byte[] bytes = new byte[BUFFER_SIZE_WITH_LISTENER];
		final int total = is.available();
		int current = 0;
		int count = 0;
		if (listener != null) {
			listener.onUpdate(current, total);
		}
		while ((count = is.read(bytes, 0, BUFFER_SIZE_WITH_LISTENER)) != -1) {
			os.write(bytes, 0, count);
			current += count;
			if (listener != null) {
				listener.onUpdate(current, total);
			}
		}
	}

	public static void closeSilently(Closeable closeable) {
		try {
			closeable.close();
		} catch (Exception e) {
			// Do nothing
		}
	}

	public static interface StreamCopyListener {

		void onUpdate(int current, int total);

	}
}
