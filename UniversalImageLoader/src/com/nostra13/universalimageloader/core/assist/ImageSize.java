package com.nostra13.universalimageloader.core.assist;

/**
 * Present width and height values
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageSize {

	private static final String TO_STRING_PATTERN = "%sx%s";

	private final int width;
	private final int height;

	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return String.format(TO_STRING_PATTERN, width, height);
	}
}
