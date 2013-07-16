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

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Provides application storage paths
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class StorageUtils {

	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
	private static final String INDIVIDUAL_DIR_NAME = "uil-images";

	private StorageUtils() {
	}

	/**
	 * Returns application cache directory. Cache directory will be created on SD card
	 * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
	 * Android defines cache directory on device's file system.
	 *
	 * @param context Application context
	 * @return Cache {@link File directory}
	 */
	public static File getCacheDirectory(Context context) {
		File appCacheDir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		if (appCacheDir == null) {
			L.w("Can't define system cache directory! The app should be re-installed.");
		}
		return appCacheDir;
	}

	/**
	 * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
	 * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
	 * appropriate permission. Else - Android defines cache directory on device's file system.
	 *
	 * @param context Application context
	 * @return Cache {@link File directory}
	 */
	public static File getIndividualCacheDirectory(Context context) {
		File cacheDir = getCacheDirectory(context);
		File individualCacheDir = new File(cacheDir, INDIVIDUAL_DIR_NAME);
		if (!individualCacheDir.exists()) {
			if (!individualCacheDir.mkdir()) {
				individualCacheDir = cacheDir;
			}
		}
		return individualCacheDir;
	}

	/**
	 * Returns specified application cache directory. Cache directory will be created on SD card by defined path if card
	 * is mounted and app has appropriate permission. Else - Android defines cache directory on device's file system.
	 *
	 * @param context  Application context
	 * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
	 * @return Cache {@link File directory}
	 */
	public static File getOwnCacheDirectory(Context context, String cacheDir) {
		File appCacheDir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
			appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
		}
		if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w("Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				L.i("Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}
}
