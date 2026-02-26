package com.nostra13.universalimageloader.cache.memory.key;

import com.nostra13.universalimageloader.core.assist.ImageSize;

/**
 * @author Sergey Tarasevich
 * @since 1.9.4
 */
public class ImageInfo {

	public final String imageUri;
	public final ImageSize originalSize;
	public final ImageSize targetSize;

	public ImageInfo(String imageUri, ImageSize originalSize, ImageSize targetSize) {
		this.imageUri = imageUri;
		this.originalSize = originalSize;
		this.targetSize = targetSize;
	}
}
