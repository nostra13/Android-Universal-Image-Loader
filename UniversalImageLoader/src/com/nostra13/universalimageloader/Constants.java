package com.nostra13.universalimageloader;

/**
 * Constants
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class Constants {

	private Constants() {
	}

	// Default values of ImageLoaderConfiguration
	/** {@value} pixels*/
	public static final int DEFAULT_MAX_IMAGE_WIDTH = 800;
	/** {@value} pixels */
	public static final int DEFAULT_MAX_IMAGE_HEIGHT = 480;
	/** {@value} milliseconds*/
	public static final int DEFAULT_HTTP_CONNECTION_TIMEOUT = 5000;
	/** {@value} milliseconds*/
	public static final int DEFAULT_HTTP_READ_TIMEOUT = 20000;
	/** {@value} */
	public static final int DEFAULT_THREAD_POOL_SIZE = 5;
	/** {@value} bytes */
	public static final int DEFAULT_MEMORY_CACHE_SIZE = 2000000;
	/** {@value} */
	public static final String DEFAULT_CACHE_DIRECTORY = "UniversalImageLoader/Cache";
}
