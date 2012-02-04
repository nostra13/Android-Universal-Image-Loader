package com.nostra13.universalimageloader.utils;

import java.io.File;
import java.io.IOException;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Provides application storage paths
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class StorageUtils {

	private StorageUtils() {
	}

	/**
	 * Returns application cache directory. Cache directory will be created on SD card
	 * <i>("/Android/[app_package_name]/cache")</i> if card is mounted. Else - Android defines cache directory on
	 * device's file system.
	 * 
	 * @param context
	 *            Application context
	 * @return Cache {@link File directory}
	 */
	public static File getCacheDirectory(Context context) {
		File appCacheDir = null;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			try {
				new File(dataDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				Log.w(ImageLoader.TAG, "Can't create \".nomedia\" file in application external cache directory", e);
			}
			if (!appCacheDir.mkdirs()) {
				Log.w(ImageLoader.TAG, "Unable to create external cache directory");
				return null;
			}
		}
		return appCacheDir;
	}
}
