package com.nostra13.universalimageloader.core;

/**
 * Present width and height values
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
class ImageSize {

	private static final String TO_STRING_PATTERN = "%sx%s";

	int width;
	int height;

	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return String.format(TO_STRING_PATTERN, width, height);
	}
}
