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

		int vw = imageView.getWidth();
		int vh = imageView.getHeight();
		int bw = bitmap.getWidth();
		int bh = bitmap.getHeight();

		int destWidth, destHeight;
		Rect bitmapSrcRect;
		switch (imageView.getScaleType()) {
			case CENTER_INSIDE:
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			default:
				destWidth = bw;
				destHeight = bh;
				bitmapSrcRect = new Rect(0, 0, destWidth, destHeight);
				break;
			case CENTER_CROP:
				float vRation = (float) vw / vh;
				float bRation = (float) bw / bh;
				int x, y;
				if (vRation > bRation) {
					destWidth = bw;
					destHeight = (int) (vh * ((float) bw / vw));
					x = 0;
					y = (bh - destHeight) / 2;
				} else {
					destWidth = (int) (vw * ((float) bh / vh));
					destHeight = bh;
					x = (bw - destWidth) / 2;
					y = 0;
				}
				bitmapSrcRect = new Rect(x, y, destWidth, destHeight);
				break;
			case FIT_XY:
				destWidth = vw;
				destHeight = vh;
				bitmapSrcRect = new Rect(0, 0, bw, bh);
				break;
			case CENTER:
			case MATRIX:
				destWidth = vw;
				destHeight = vh;
				bitmapSrcRect = new Rect((bw - vw) / 2, (bh - vh) / 2, destWidth, destHeight);
				break;
		}

		try {
			roundBitmap = getRoundedCornerBitmap(bitmap, bitmapSrcRect, destWidth, destHeight);
		} catch (OutOfMemoryError e) {
			L.e(e, "Can't create bitmap with rounded corners. Not enough memory.");
			roundBitmap = bitmap;
		}
		imageView.setImageBitmap(roundBitmap);
		return roundBitmap;
	}

	private Bitmap getRoundedCornerBitmap(Bitmap bitmap, Rect srcRect, int width, int height) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect destRect = new Rect(0, 0, width, height);
		final RectF destRectF = new RectF(destRect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFF000000);
		canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

		return output;
	}
}
