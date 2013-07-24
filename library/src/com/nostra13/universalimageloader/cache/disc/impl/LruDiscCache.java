package com.nostra13.universalimageloader.cache.disc.impl;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.ext.DiskLruCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.*;

/** Created by Sergey.Tarasevich on 13.07.13. */
public class LruDiscCache implements DiscCacheAware {

	private static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
	private static final String ERROR_ARG_NULL = "\"%s\" argument must be not null";

	private DiskLruCache cache;

	private final FileNameGenerator fileNameGenerator;

	public LruDiscCache(File cacheDir, int cacheMaxSize) {
		this(cacheDir, cacheMaxSize, DefaultConfigurationFactory.createFileNameGenerator());
	}

	public LruDiscCache(File cacheDir, int cacheMaxSize, FileNameGenerator fileNameGenerator) {
		if (cacheDir == null) {
			throw new IllegalArgumentException("cacheDir" + ERROR_ARG_NULL);
		}
		if (fileNameGenerator == null) {
			throw new IllegalArgumentException("fileNameGenerator" + ERROR_ARG_NULL);
		}

		this.fileNameGenerator = fileNameGenerator;
		try {
			this.cache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize);
		} catch (IOException e) {
			L.e(e);
		}
	}

	@Override
	public File getDirectory() {
		return cache.getDirectory();
	}

	@Override
	public File get(String imageUri) {
		try {
			return cache.get(getKey(imageUri)).getFile(0);
		} catch (IOException e) {
			L.e(e);
			return null;
		}
	}

	@Override
	public boolean save(String imageUri, InputStream imageStream) throws IOException {
		DiskLruCache.Editor editor = cache.edit(getKey(imageUri));
		if (editor == null) {
			return false;
		}

		OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), BUFFER_SIZE);
		IoUtils.copyStream(imageStream, os, BUFFER_SIZE);
		editor.commit();
		return true;
	}

	@Override
	public boolean save(String imageUri, Bitmap bitmap, Bitmap.CompressFormat format, int quality) throws IOException {
		DiskLruCache.Editor editor = cache.edit(getKey(imageUri));
		if (editor == null) {
			return false;
		}

		OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), BUFFER_SIZE);
		boolean savedSuccessfully = false;
		try {
			savedSuccessfully = bitmap.compress(format, quality, os);
		} finally {
			IoUtils.closeSilently(os);
		}
		if (savedSuccessfully) {
			editor.commit();
		} else {
			editor.abort();
		}
		return savedSuccessfully;
	}

	@Override
	public boolean remove(String imageUri) {
		try {
			return cache.remove(getKey(imageUri));
		} catch (IOException e) {
			L.e(e);
			return false;
		}
	}

	@Override
	public void clear() {
		try {
			cache.delete();
		} catch (IOException e) {
			L.e(e);
		}
	}

	private String getKey(String imageUri) {
		return fileNameGenerator.generate(imageUri);
	}
}
