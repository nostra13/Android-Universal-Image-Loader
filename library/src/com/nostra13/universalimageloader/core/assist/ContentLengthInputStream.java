/*******************************************************************************
 * Copyright 2013 Sergey Tarasevich
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
 *
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com), Mariotaku
 * @since 1.9.1
 */
public class ContentLengthInputStream extends InputStream {

	private final InputStream stream;
	private final long length;

	private long pos;

	public ContentLengthInputStream(InputStream stream, long length) {
		this.stream = stream;
		this.length = length;
	}

	@Override
	public synchronized int available() {
		return (int) (length - pos);
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	@Override
	public void mark(final int readlimit) {
		pos = readlimit;
		stream.mark(readlimit);
	}

	@Override
	public int read() throws IOException {
		pos++;
		return stream.read();
	}

	@Override
	public int read(final byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	@Override
	public int read(final byte[] buffer, final int byteOffset, final int byteCount) throws IOException {
		pos += byteCount;
		return stream.read(buffer, byteOffset, byteCount);
	}

	@Override
	public synchronized void reset() throws IOException {
		pos = 0;
		stream.reset();
	}

	@Override
	public long skip(final long byteCount) throws IOException {
		pos += byteCount;
		return stream.skip(byteCount);
	}
}