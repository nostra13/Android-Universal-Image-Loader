package com.nostra13.example.universalimageloader;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.nostra13.example.universalimageloader.Constants.Config;
import com.nostra13.example.universalimageloader.downloader.ExtendedImageDownloader;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILApplication extends Application {
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();

		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
		}

		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them, 
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.memoryCacheSize(2 * 1024 * 1024) // 2 Mb
			.denyCacheImageMultipleSizesInMemory()
			.discCacheFileNameGenerator(new Md5FileNameGenerator())
			.imageDownloader(new ExtendedImageDownloader(context))
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.enableLogging() // Not necessary in common
			.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}