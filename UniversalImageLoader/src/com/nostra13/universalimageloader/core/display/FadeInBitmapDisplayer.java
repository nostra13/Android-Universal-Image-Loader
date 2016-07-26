package com.nostra13.universalimageloader.core.display;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

/**
* Just displays {@link Bitmap} in {@link ImageView} with fade effect
*
* @author Petr Nohejl
*/
public class FadeInBitmapDisplayer implements BitmapDisplayer
{
	private int mDurationMillis;


	public FadeInBitmapDisplayer(int durationMillis)
	{
		mDurationMillis = durationMillis;
	}


	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView)
	{
		setImageBitmapWithFade(bitmap, imageView);
		return bitmap;
	}


	private void setImageBitmapWithFade(final Bitmap bitmap, final ImageView imageView)
	{
		Resources resources = imageView.getResources();
		BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
		setImageDrawableWithFade(bitmapDrawable, imageView);
	}


	private void setImageDrawableWithFade(final Drawable drawable, final ImageView imageView)
	{
		Drawable currentDrawable = imageView.getDrawable();
		if(currentDrawable != null)
		{
			Drawable[] drawableArray = new Drawable[2];
			drawableArray[0] = currentDrawable;
			drawableArray[1] = drawable;
			TransitionDrawable transitionDrawable = new TransitionDrawable(drawableArray);
			transitionDrawable.setCrossFadeEnabled(true);
			imageView.setImageDrawable(transitionDrawable);
			transitionDrawable.startTransition(mDurationMillis);
		}
		else
		{
			imageView.setImageDrawable(drawable);
		}
	}
}
