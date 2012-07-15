package com.nostra13.example.universalimageloader.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.nostra13.example.universalimageloader.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Example widget provider
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Initialize ImageLoader with configuration.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(1500000) // 1.5 Mb
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.enableLogging() // Not necessary in common
				.build();
		ImageLoader.getInstance().init(config);

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

		String[] imageUrls = context.getResources().getStringArray(R.array.heavy_images);
		ImageView tempImageView1 = new ImageView(context);
		ImageView tempImageView2 = new ImageView(context);
		tempImageView1.setLayoutParams(new LayoutParams(70, 70)); // 70 - approximate size of ImageView in widget
		tempImageView2.setLayoutParams(new LayoutParams(70, 70)); // 70 - approximate size of ImageView in widget

		ImageLoader.getInstance().displayImage(imageUrls[0], tempImageView1, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				views.setImageViewBitmap(R.id.image_left, loadedImage);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		});
		ImageLoader.getInstance().displayImage(imageUrls[1], tempImageView2, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				views.setImageViewBitmap(R.id.image_right, loadedImage);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		});
	}
}
