/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nostra13.universalimageloader.core.assist;

import java.io.IOException;
import java.io.InputStream;
import com.nostra13.universalimageloader.utils.IoUtils;

/**
 * An input stream wrapper that supports unlimited independent cursors for
 * marking and resetting. Each cursor is a token, and it's the caller's
 * responsibility to keep track of these.
 */
public abstract class ReloadableInputStream extends InputStream {

	private InputStream in;

	public ReloadableInputStream(InputStream in) {
		this.in = in;
	}

	abstract public InputStream reload() throws IOException;

	@Override
	public void mark(int readLimit) {
		in.mark(readLimit);
	}

	@Override
	public void reset() throws IOException {
		try {
			in.reset();
		} catch (IOException e) {
			IoUtils.closeSilently(in);
			in = reload();
		}
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return in.read(buffer);
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		return in.read(buffer, offset, length);
	}

	@Override
	public long skip(long byteCount) throws IOException {
		return in.skip(byteCount);
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}
}
