/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.assist;

/**
 * Presents the reason why image loading and displaying was failed
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class FailReason {

	private final FailType type;

	private final Throwable cause;

	public FailReason(FailType type, Throwable cause) {
		this.type = type;
		this.cause = cause;
	}

	/** @return {@linkplain FailType Fail type} */
	public FailType getType() {
		return type;
	}

	/** @return Thrown exception/error, can be <b>null</b> */
	public Throwable getCause() {
		return cause;
	}

	/** Presents type of fail while image loading */
	public static enum FailType {
		/** Input/output error. Can be caused by network communication fail or error while caching image on file system. */
		IO_ERROR,
		/**
		 * Error while
		 * {@linkplain android.graphics.BitmapFactory#decodeStream(java.io.InputStream, android.graphics.Rect, android.graphics.BitmapFactory.Options)
		 * decode image to Bitmap}
		 */
		DECODING_ERROR,
		/**
		 * {@linkplain com.nostra13.universalimageloader.core.ImageLoader#denyNetworkDownloads(boolean) Network
		 * downloads are denied} and requested image wasn't cached in disk cache before.
		 */
		NETWORK_DENIED,
		/** Not enough memory to create needed Bitmap for image */
		OUT_OF_MEMORY,
		/** Unknown error was occurred while loading image */
		UNKNOWN
	}
}