/*******************************************************************************
 * Copyright 2013-2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.core.assist;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decorator for {@link java.io.InputStream InputStream}. Provides possibility to return defined stream length by
 * {@link #available()} method.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com), Mariotaku
 * @since 1.9.1
 */
public class ContentLengthInputStream extends InputStream {

	private final InputStream stream;
	private final int length;

	public ContentLengthInputStream(InputStream stream, int length) {
		this.stream = stream;
		this.length = length;
	}

	@Override
	public int available() {
		return length;
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	@Override
	public void mark(int readLimit) {
		stream.mark(readLimit);
	}

	@Override
	public int read() throws IOException {
		return stream.read();
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return stream.read(buffer);
	}

	@Override
	public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
		return stream.read(buffer, byteOffset, byteCount);
	}

	@Override
	public void reset() throws IOException {
		stream.reset();
	}

	@Override
	public long skip(long byteCount) throws IOException {
		return stream.skip(byteCount);
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}
}