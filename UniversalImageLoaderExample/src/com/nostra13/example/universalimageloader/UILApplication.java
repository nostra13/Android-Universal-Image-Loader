package com.nostra13.example.universalimageloader;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class UILApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// This configuration tuning is custom. You can tune every option, you may tune some of them, 
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.threadPoolSize(3)
			.threadPriority(Thread.MIN_PRIORITY + 2)
			.discCacheDir("UniversalImageLoaderApp/Cache")
			.memoryCacheSize(1500000)
			.denyCacheImageMultipleSizesInMemory()
			.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}