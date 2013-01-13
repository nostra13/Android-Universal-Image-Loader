package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import com.nostra13.universalimageloader.utils.L;

/**
 * Displays bitmap with rounded corners. <br />
 * <b>NOTE:</b> New {@link Bitmap} object is created for displaying. So this class needs more memory and can cause
 * {@link OutOfMemoryError}.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class RoundedBitmapDisplayer implements BitmapDisplayer {

	private int roundPixels;

	public RoundedBitmapDisplayer(int roundPixels) {
		this.roundPixels = roundPixels;
	}

	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView) {
		Bitmap roundBitmap;
		try {
			roundBitmap = getRoundedCornerBitmap(bitmap);
		} catch (OutOfMemoryError e) {
			L.e(e, "Can't create bitmap with rounded corners. Not enough memory.");
			roundBitmap = bitmap;
		}
		imageView.setImageBitmap(roundBitmap);
		return roundBitmap;
	}

	private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFFFFFFFF);
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
}
