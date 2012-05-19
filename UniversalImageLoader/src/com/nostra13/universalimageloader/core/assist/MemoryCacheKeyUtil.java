package com.nostra13.universalimageloader.core.assist;

import java.util.Comparator;

/**
 * Utility for generating of keys for memory cache and key comparing
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class MemoryCacheKeyUtil {

	private static final String URL_AND_SIZE_SEPARATOR = "_";
	private static final String MEMORY_CACHE_KEY_FORMAT = "%s" + URL_AND_SIZE_SEPARATOR + "%sx%s";

	public static String generateKey(String imageUrl, ImageSize targetSize) {
		return String.format(MEMORY_CACHE_KEY_FORMAT, imageUrl, targetSize.getWidth(), targetSize.getHeight());
	}

	public static Comparator<String> createFuzzyKeyComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String key1, String key2) {
				String imageUrl1 = key1.substring(0, key1.lastIndexOf(URL_AND_SIZE_SEPARATOR));
				String imageUrl2 = key2.substring(0, key2.lastIndexOf(URL_AND_SIZE_SEPARATOR));
				return imageUrl1.compareTo(imageUrl2);
			}
		};
	}
}
