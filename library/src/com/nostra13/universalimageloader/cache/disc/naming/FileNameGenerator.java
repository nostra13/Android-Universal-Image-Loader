package com.nostra13.universalimageloader.cache.disc.naming;

/**
 * Generates names for files at disc cache
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface FileNameGenerator {
	/** Generates unique file name for image defined by URI */
	public abstract String generate(String imageUri);
}
