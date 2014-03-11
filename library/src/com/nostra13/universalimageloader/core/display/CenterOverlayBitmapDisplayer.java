/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
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
package com.nostra13.universalimageloader.core.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * Initialization:
 *
 *   .displayer(new com.nostra13.universalimageloader.core.display.CenterOverlayBitmapDisplayer(context, R.drawable.ic_control_play) {
 *       @Override
 *       public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom, String uri) {
 *           if (isVideoUri(uri)) {
 *               super.display(bitmap, imageAware, loadedFrom, uri);
 *           } else {
 *               imageAware.setImageBitmap(bitmap);
 *           }
 *       }
 *
 *       private boolean isVideoUri(String uri) {
 *           return getContext().getContentResolver().getType(Uri.parse(uri)).startsWith("video/");
 *       }
 *   })
 */
public class CenterOverlayBitmapDisplayer extends SimpleBitmapDisplayer {
	private Context context;
	private int resourceId;

	public CenterOverlayBitmapDisplayer(Context context, int resourceId) {
		this.context = context;
		this.resourceId = resourceId;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom, String uri) {
		Bitmap overlay = BitmapFactory.decodeResource(context.getResources(),
				resourceId);

		final int x = bitmap.getWidth() / 2;
		final int y = bitmap.getHeight() / 2;

		final int w;
		final int h;
		if (bitmap.getWidth() >= bitmap.getHeight()) {
			h = bitmap.getHeight() / 5;
			w = overlay.getWidth() * h / overlay.getHeight();
		} else {
			w = bitmap.getWidth() / 5;
			h = overlay.getHeight() * w / overlay.getWidth();
		}

		final Rect rect = new Rect(x - w / 2, y - h / 2, x + w / 2, y + h / 2);

		final Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(overlay, null, rect, null);

		overlay.recycle();
		overlay = null;

		super.display(bitmap, imageAware, loadedFrom, uri);
	}

	private static final boolean DEBUG = true;
	private static class Log8 {
		public static int d(Object... arr) {
			if (!DEBUG) return 0;
			StackTraceElement call = Thread.currentThread().getStackTrace()[3];
			String className = call.getClassName();
			className = className.substring(className.lastIndexOf('.') + 1);
			return android.util.Log.d("",
					className + "."
					+ call.getMethodName() + ":"
					+ call.getLineNumber() + ": "
					+ java.util.Arrays.deepToString(arr));
		}
	}

}
