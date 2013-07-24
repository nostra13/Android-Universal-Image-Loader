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
package com.nostra13.universalimageloader.cache.disc;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.*;

/**
 * Base disc cache. Implements common functionality for disc cache.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see DiscCacheAware
 * @see FileNameGenerator
 * @since 1.0.0
 */
public abstract class BaseDiscCache implements DiscCacheAware {

	private static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
	private static final String ERROR_ARG_NULL = "\"%s\" argument must be not null";

	protected File cacheDir;

	private FileNameGenerator fileNameGenerator;

	public BaseDiscCache(File cacheDir) {
		this(cacheDir, DefaultConfigurationFactory.createFileNameGenerator());
	}

	public BaseDiscCache(File cacheDir, FileNameGenerator fileNameGenerator) {
		if (cacheDir == null) {
			throw new IllegalArgumentException("cacheDir" + ERROR_ARG_NULL);
		}
		if (fileNameGenerator == null) {
			throw new IllegalArgumentException("fileNameGenerator" + ERROR_ARG_NULL);
		}

		this.cacheDir = cacheDir;
		this.fileNameGenerator = fileNameGenerator;
	}

	@Override
	public File getDirectory() {
		return cacheDir;
	}

	@Override
	public File get(String imageUri) {
		File file = getFile(imageUri); // TODO : Maybe check root dir if it not available. Think about reserve cache dir
		return file.exists() ? file : null;
	}

	@Override
	public boolean save(String imageUri, InputStream imageStream) throws IOException {
		File imageFile = getFile(imageUri);
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(imageFile), BUFFER_SIZE);
			try {
				IoUtils.copyStream(imageStream, os, BUFFER_SIZE);
			} finally {
				IoUtils.closeSilently(os);
			}
		} finally {
			IoUtils.closeSilently(imageStream);
		}
		return true;
	}

	@Override
	public boolean save(String imageUri, Bitmap bitmap, Bitmap.CompressFormat format, int quality) throws IOException {
		File imageFile = getFile(imageUri);
		OutputStream os = new BufferedOutputStream(new FileOutputStream(imageFile), BUFFER_SIZE);
		boolean savedSuccessfully;
		try {
			savedSuccessfully = bitmap.compress(format, quality, os);
		} finally {
			IoUtils.closeSilently(os);
		}
		bitmap.recycle();
		return savedSuccessfully;
	}

	@Override
	public boolean remove(String imageUri) {
		return getFile(imageUri).delete();
	}

	@Override
	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}
	}

	protected File getFile(String imageUri) {
		String fileName = fileNameGenerator.generate(imageUri);
		return new File(cacheDir, fileName);
	}
}