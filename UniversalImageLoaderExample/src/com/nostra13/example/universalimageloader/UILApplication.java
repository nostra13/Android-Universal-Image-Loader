package com.nostra13.example.universalimageloader;

import com.nostra13.universalimageloader.Constants;

import android.app.Application;
import android.util.DisplayMetrics;

public class UILApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		Constants.SCREEN_WIDTH = displayMetrics.widthPixels;
		Constants.SCREEN_HEIGHT = displayMetrics.heightPixels;
	}
}
