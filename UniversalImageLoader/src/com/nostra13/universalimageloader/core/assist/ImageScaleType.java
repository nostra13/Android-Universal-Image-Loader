package com.nostra13.universalimageloader.core.assist;

/**
 * Type of image scaling during decoding. Can be {@link #POWER_OF_2} or {@link #EXACT}(a little slower but more
 * efficiently use memory)
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public enum ImageScaleType {
	/**
	 * Image will be reduces 2-fold until next reduce step make image smaller target size.<br />
	 * It's <b>fast</b> type and it's preferable for usage.
	 */
	POWER_OF_2,
	/**
	 * Image will scaled exactly to target size.<br />
	 * Use it if memory economy is critically important.
	 */
	EXACT
}
