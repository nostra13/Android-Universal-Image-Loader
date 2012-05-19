package com.nostra13.universalimageloader.cache.disc.naming;

/**
 * Names image file as image URL {@linkplain String#hashCode() hashcode}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HashCodeFileNameGenerator implements FileNameGenerator {
	@Override
	public String generate(String imageUrl) {
		return String.valueOf(imageUrl.hashCode());
	}
}
