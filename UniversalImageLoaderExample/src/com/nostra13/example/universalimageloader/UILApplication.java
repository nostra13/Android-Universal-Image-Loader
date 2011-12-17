package com.nostra13.example.universalimageloader;

import java.io.File;

import android.app.Application;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.cache.disc.DiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.DefaultDiscCache;
import com.nostra13.universalimageloader.imageloader.ImageLoader;
import com.nostra13.universalimageloader.imageloader.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class UILApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		File cacheDir = StorageUtils.getCacheDirectory(this, "UniversalImageLoaderApp/Cache");
		DiscCache discCache = new DefaultDiscCache(cacheDir);
		// This configuration tuning is custom. You can tune every option, you may tune some of them, 
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.maxImageWidthForMemoryCache(displayMetrics.widthPixels)
			.maxImageHeightForMemoryCache(displayMetrics.heightPixels)
			.threadPoolSize(3)
			.discCache(discCache)
			.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}