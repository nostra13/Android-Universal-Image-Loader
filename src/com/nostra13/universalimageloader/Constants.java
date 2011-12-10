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
	public static final int TAG_KEY = R.id.tag_image_loader;
	public static final int MEMORY_CACHE_SIZE = 2000000; // bytes
	public static final int THREAD_POOL_SIZE = 3; 

	public static final int HTTP_CONNECT_TIMEOUT = 5000; // milliseconds
	public static final int HTTP_READ_TIMEOUT = 20000; // milliseconds
}
