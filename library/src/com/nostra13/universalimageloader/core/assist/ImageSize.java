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
package com.nostra13.universalimageloader.core.assist;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

import java.lang.reflect.Field;

/**
 * Present width and height values
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class ImageSize
{

    private static final String TO_STRING_PATTERN = "%sx%s";

    /**
     * Defines image size for loading at memory (for memory economy) by {@link android.widget.ImageView} parameters.<br />
     * Size computing algorithm:<br />
     * <p/>
     * 1) Get the actual width and height of the view<br />
     * 2) Get <b>layout_width</b> and <b>layout_height</b>. If both of them haven't exact value then go to step #2.</br>
     * 3) Get <b>maxWidth</b> and <b>maxHeight</b>. If both of them are not set then go to step #3.<br />
     * 4) Get <b>maxImageWidthForMemoryCache</b> and <b>maxImageHeightForMemoryCache</b> from configuration. If both of
     * them are not set then go to step #3.<br />
     * 6) Get device screen dimensions.
     *
     * @param imageView      Pass in a view to base your measurements off
     * @param configuration  the current ImageLoaderConfiguration you want to fall back too
     * @param displayMetrics the display metrics to use, this should be passed in, as pulling it from the resources everytime
     *                       is wasted time.
     */
    public static ImageSize getImageSizeScaleTo(final View imageView, final ImageLoaderConfiguration configuration, final DisplayMetrics displayMetrics)
    {
        if (imageView == null) return null;

        final ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width = imageView.getWidth(); // Get the actual image width
        if (width <= 0) width = params.width; // Get layout width parameter
        if (width <= 0) width = getFieldValue(imageView, "mMaxWidth"); // Check maxWidth parameter
        if (width <= 0) width = configuration.getMaxImageWidthForMemoryCache();
        if (width <= 0) width = displayMetrics.widthPixels;

        int height = imageView.getHeight(); //get actual image height first
        if (height <= 0) height = params.height; // Get layout height parameter
        if (height <= 0) height = getFieldValue(imageView, "mMaxHeight"); // Check maxHeight parameter
        if (height <= 0) height = configuration.getMaxImageHeightForMemoryCache();
        if (height <= 0) height = displayMetrics.heightPixels;

        return new ImageSize(width, height);
    }

    private static int getFieldValue(Object object, String fieldName)
    {
        int value = 0;
        try
        {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
            {
                value = fieldValue;
            }
        }
        catch (Exception e)
        {
            L.e(e);
        }
        return value;
    }

    private final int width;
    private final int height;

    public ImageSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    @Override
    public String toString()
    {
        return String.format(TO_STRING_PATTERN, width, height);
    }
}
