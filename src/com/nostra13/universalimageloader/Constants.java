package com.nostra13.universalimageloader;

/**
 * Constants
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class Constants {

	private Constants() {
	}

	public static int SCREEN_WIDTH = 800; // You can redefined it on Application start
	public static int SCREEN_HEIGHT = 640; // You can redefined it on Application start

	public static final String APP_DIRECTORY = "UniversalImageLoader";
	public static final String APP_CACHE_DIRECTORY = "Cache";

	public static final int STUB_IMAGE = R.drawable.stub_image;
	public static final int MEMORY_CACHE_SIZE = 2000000; // 2 Mb
	
	public static final int HTTP_CONNECT_TIMEOUT = 20000; // 20 s
	public static final int HTTP_READ_TIMEOUT = 30000; // 30 s
}
