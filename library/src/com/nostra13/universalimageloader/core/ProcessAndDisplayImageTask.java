package com.nostra13.universalimageloader.core;

import static com.nostra13.universalimageloader.core.ImageLoader.LOG_POSTPROCESS_IMAGE;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.L;

/**
 * Presents process'n'display image task. Processes image {@linkplain Bitmap} and display it in {@link ImageView} using
 * {@link DisplayBitmapTask}.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
class ProcessAndDisplayImageTask implements Runnable {

	private final ImageLoaderEngine engine;
	private final Bitmap bitmap;
	private final ImageLoadingInfo imageLoadingInfo;
	private final Handler handler;

	public ProcessAndDisplayImageTask(ImageLoaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, Handler handler) {
		this.engine = engine;
		this.bitmap = bitmap;
		this.imageLoadingInfo = imageLoadingInfo;
		this.handler = handler;
	}

	@Override
	public void run() {
		if (engine.configuration.loggingEnabled) L.i(LOG_POSTPROCESS_IMAGE, imageLoadingInfo.memoryCacheKey);
		BitmapProcessor processor = imageLoadingInfo.options.getPostProcessor();
		final Bitmap processedBitmap = processor.process(bitmap);
		if (processedBitmap != bitmap) {
			bitmap.recycle();
		}
		handler.post(new DisplayBitmapTask(processedBitmap, imageLoadingInfo, engine));
	}
}
