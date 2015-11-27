/*******************************************************************************
 * Copyright 2013-2014 Sergey Tarasevich
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
package com.nostra13.universalimageloader.utils;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.opengl.EGL14;
import android.opengl.GLES10;
import android.os.Build;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.EGL14;
import android.opengl.*;
import android.opengl.GLES20;


/**
 * Provides calculations with image sizes, scales
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.3
 */
public final class ImageSizeUtils {

	private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;

	private static ImageSize maxBitmapSize;

	static {
		maxBitmapSize = new ImageSize(DEFAULT_MAX_BITMAP_DIMENSION, DEFAULT_MAX_BITMAP_DIMENSION);;
		try {
			if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
				readBitMaxSizeWithEgl14();
			}else{
				readBitMaxSizeWithEgl10();
			}
		}catch (RuntimeException e){
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private static void readBitMaxSizeWithEgl10(){
		EGL10 egl = (EGL10) EGLContext.getEGL();

		EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		int[] vers = new int[2];
		egl.eglInitialize(dpy, vers);

		int[] configAttr = {
				EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
				EGL10.EGL_LEVEL, 0,
				EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
				EGL10.EGL_NONE
		};
		EGLConfig[] configs = new EGLConfig[1];
		int[] numConfig = new int[1];
		egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
		if (numConfig[0] == 0) {
			// TROUBLE! No config found.
			return;
		}
		EGLConfig config = configs[0];

		int[] surfAttr = {
				EGL10.EGL_WIDTH, 64,
				EGL10.EGL_HEIGHT, 64,
				EGL10.EGL_NONE
		};
		EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
		final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;  // missing in EGL10
		int[] ctxAttrib = {
				EGL_CONTEXT_CLIENT_VERSION, 1,
				EGL10.EGL_NONE
		};
		EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib);
		egl.eglMakeCurrent(dpy, surf, surf, ctx);
		int[] maxSize = new int[1];
		GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
		int maxBitmapDimension = Math.max(maxSize[0], DEFAULT_MAX_BITMAP_DIMENSION);
		maxBitmapSize = new ImageSize(maxBitmapDimension, maxBitmapDimension);
		egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
				EGL10.EGL_NO_CONTEXT);
		egl.eglDestroySurface(dpy, surf);
		egl.eglDestroyContext(dpy, ctx);
		egl.eglTerminate(dpy);
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private static void readBitMaxSizeWithEgl14(){
		android.opengl.EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
		int[] vers = new int[2];
		EGL14.eglInitialize(dpy, vers, 0, vers, 1);
		int[] configAttr = {
				EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
				EGL14.EGL_LEVEL, 0,
				EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
				EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
				EGL14.EGL_NONE
		};
		android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[1];
		int[] numConfig = new int[1];
		EGL14.eglChooseConfig(dpy, configAttr, 0,
				configs, 0, 1, numConfig, 0);
		if (numConfig[0] == 0) {
			// TROUBLE! No config found.
			return;
		}
		android.opengl.EGLConfig config = configs[0];
		int[] surfAttr = {
				EGL14.EGL_WIDTH, 64,
				EGL14.EGL_HEIGHT, 64,
				EGL14.EGL_NONE
		};
		android.opengl.EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);
		int[] ctxAttrib = {
				EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
				EGL14.EGL_NONE
		};
		android.opengl.EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);
		EGL14.eglMakeCurrent(dpy, surf, surf, ctx);
		int[] maxSize = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);
		int maxBitmapDimension = Math.max(maxSize[0], DEFAULT_MAX_BITMAP_DIMENSION);
		maxBitmapSize = new ImageSize(maxBitmapDimension, maxBitmapDimension);
		EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
				EGL14.EGL_NO_CONTEXT);
		EGL14.eglDestroySurface(dpy, surf);
		EGL14.eglDestroyContext(dpy, ctx);
		EGL14.eglTerminate(dpy);
	}

	private ImageSizeUtils() {
	}

	/**
	 * Defines target size for image aware view. Size is defined by target
	 * {@link com.nostra13.universalimageloader.core.imageaware.ImageAware view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 */
	public static ImageSize defineTargetSizeForView(ImageAware imageAware, ImageSize maxImageSize) {
		int width = imageAware.getWidth();
		if (width <= 0) width = maxImageSize.getWidth();

		int height = imageAware.getHeight();
		if (height <= 0) height = maxImageSize.getHeight();

		return new ImageSize(width, height);
	}

	/**
	 * Computes sample size for downscaling image size (<b>srcSize</b>) to view size (<b>targetSize</b>). This sample
	 * size is used during
	 * {@linkplain BitmapFactory#decodeStream(java.io.InputStream, android.graphics.Rect, android.graphics.BitmapFactory.Options)
	 * decoding image} to bitmap.<br />
	 * <br />
	 * <b>Examples:</b><br />
	 * <p/>
	 * <pre>
	 * srcSize(100x100), targetSize(10x10), powerOf2Scale = true -> sampleSize = 8
	 * srcSize(100x100), targetSize(10x10), powerOf2Scale = false -> sampleSize = 10
	 *
	 * srcSize(100x100), targetSize(20x40), viewScaleType = FIT_INSIDE -> sampleSize = 5
	 * srcSize(100x100), targetSize(20x40), viewScaleType = CROP       -> sampleSize = 2
	 * </pre>
	 * <p/>
	 * <br />
	 * The sample size is the number of pixels in either dimension that correspond to a single pixel in the decoded
	 * bitmap. For example, inSampleSize == 4 returns an image that is 1/4 the width/height of the original, and 1/16
	 * the number of pixels. Any value <= 1 is treated the same as 1.
	 *
	 * @param srcSize       Original (image) size
	 * @param targetSize    Target (view) size
	 * @param viewScaleType {@linkplain ViewScaleType Scale type} for placing image in view
	 * @param powerOf2Scale <i>true</i> - if sample size be a power of 2 (1, 2, 4, 8, ...)
	 * @return Computed sample size
	 */
	public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType,
			boolean powerOf2Scale) {
		final int srcWidth = srcSize.getWidth();
		final int srcHeight = srcSize.getHeight();
		final int targetWidth = targetSize.getWidth();
		final int targetHeight = targetSize.getHeight();

		int scale = 1;

		switch (viewScaleType) {
			case FIT_INSIDE:
				if (powerOf2Scale) {
					final int halfWidth = srcWidth / 2;
					final int halfHeight = srcHeight / 2;
					while ((halfWidth / scale) > targetWidth || (halfHeight / scale) > targetHeight) { // ||
						scale *= 2;
					}
				} else {
					scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
				}
				break;
			case CROP:
				if (powerOf2Scale) {
					final int halfWidth = srcWidth / 2;
					final int halfHeight = srcHeight / 2;
					while ((halfWidth / scale) > targetWidth && (halfHeight / scale) > targetHeight) { // &&
						scale *= 2;
					}
				} else {
					scale = Math.min(srcWidth / targetWidth, srcHeight / targetHeight); // min
				}
				break;
		}

		if (scale < 1) {
			scale = 1;
		}
		scale = considerMaxTextureSize(srcWidth, srcHeight, scale, powerOf2Scale);

		return scale;
	}

	private static int considerMaxTextureSize(int srcWidth, int srcHeight, int scale, boolean powerOf2) {
		final int maxWidth = maxBitmapSize.getWidth();
		final int maxHeight = maxBitmapSize.getHeight();
		while ((srcWidth / scale) > maxWidth || (srcHeight / scale) > maxHeight) {
			if (powerOf2) {
				scale *= 2;
			} else {
				scale++;
			}
		}
		return scale;
	}

	/**
	 * Computes minimal sample size for downscaling image so result image size won't exceed max acceptable OpenGL
	 * texture size.<br />
	 * We can't create Bitmap in memory with size exceed max texture size (usually this is 2048x2048) so this method
	 * calculate minimal sample size which should be applied to image to fit into these limits.
	 *
	 * @param srcSize Original image size
	 * @return Minimal sample size
	 */
	public static int computeMinImageSampleSize(ImageSize srcSize) {
		final int srcWidth = srcSize.getWidth();
		final int srcHeight = srcSize.getHeight();
		final int targetWidth = maxBitmapSize.getWidth();
		final int targetHeight = maxBitmapSize.getHeight();

		final int widthScale = (int) Math.ceil((float) srcWidth / targetWidth);
		final int heightScale = (int) Math.ceil((float) srcHeight / targetHeight);

		return Math.max(widthScale, heightScale); // max
	}

	/**
	 * Computes scale of target size (<b>targetSize</b>) to source size (<b>srcSize</b>).<br />
	 * <br />
	 * <b>Examples:</b><br />
	 * <p/>
	 * <pre>
	 * srcSize(40x40), targetSize(10x10) -> scale = 0.25
	 *
	 * srcSize(10x10), targetSize(20x20), stretch = false -> scale = 1
	 * srcSize(10x10), targetSize(20x20), stretch = true  -> scale = 2
	 *
	 * srcSize(100x100), targetSize(20x40), viewScaleType = FIT_INSIDE -> scale = 0.2
	 * srcSize(100x100), targetSize(20x40), viewScaleType = CROP       -> scale = 0.4
	 * </pre>
	 *
	 * @param srcSize       Source (image) size
	 * @param targetSize    Target (view) size
	 * @param viewScaleType {@linkplain ViewScaleType Scale type} for placing image in view
	 * @param stretch       Whether source size should be stretched if target size is larger than source size. If <b>false</b>
	 *                      then result scale value can't be greater than 1.
	 * @return Computed scale
	 */
	public static float computeImageScale(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType,
			boolean stretch) {
		final int srcWidth = srcSize.getWidth();
		final int srcHeight = srcSize.getHeight();
		final int targetWidth = targetSize.getWidth();
		final int targetHeight = targetSize.getHeight();

		final float widthScale = (float) srcWidth / targetWidth;
		final float heightScale = (float) srcHeight / targetHeight;

		final int destWidth;
		final int destHeight;
		if ((viewScaleType == ViewScaleType.FIT_INSIDE && widthScale >= heightScale) || (viewScaleType == ViewScaleType.CROP && widthScale < heightScale)) {
			destWidth = targetWidth;
			destHeight = (int) (srcHeight / widthScale);
		} else {
			destWidth = (int) (srcWidth / heightScale);
			destHeight = targetHeight;
		}

		float scale = 1;
		if ((!stretch && destWidth < srcWidth && destHeight < srcHeight) || (stretch && destWidth != srcWidth && destHeight != srcHeight)) {
			scale = (float) destWidth / srcWidth;
		}

		return scale;
	}
}
