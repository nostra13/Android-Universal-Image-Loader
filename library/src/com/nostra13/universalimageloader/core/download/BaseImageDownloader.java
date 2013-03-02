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
package com.nostra13.universalimageloader.core.download;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;

/**
 * Provides retrieving of {@link InputStream} of image by URI from network or file system or app resources.<br />
 * {@link URLConnection} is used to retrieve image stream from network.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see HttpClientImageDownloader
 * @since 1.8.0
 */
public class BaseImageDownloader implements ImageDownloader
{
    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds
    /**
     * {@value}
     */
    protected static final int BUFFER_SIZE = 8 * 1024; // 8 Kb
    /**
     * {@value}
     */
    protected static final String SCHEME_ASSETS_PREFIX = SCHEME.SCHEME_ASSETS.scheme + "://";
    /**
     * {@value}
     */
    protected static final String SCHEME_DRAWABLE_PREFIX = SCHEME.SCHEME_DRAWABLE.scheme + "://";
    private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme [%s] by default. You should implement this support byself";
    protected final Context context;
    protected final int connectTimeout;
    protected final int readTimeout;

    public BaseImageDownloader(Context context)
    {
        this.context = context.getApplicationContext();
        this.connectTimeout = DEFAULT_HTTP_CONNECT_TIMEOUT;
        this.readTimeout = DEFAULT_HTTP_READ_TIMEOUT;
    }

    public BaseImageDownloader(Context context, int connectTimeout, int readTimeout)
    {
        this.context = context.getApplicationContext();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(URI imageUri, Object extra) throws IOException
    {
        switch (SCHEME.getScheme(imageUri))
        {
            case SCHEME_HTTP:
            case SCHEME_HTTPS:
                return getStreamFromNetwork(imageUri, extra);
            case SCHEME_FILE:
                return getStreamFromFile(imageUri, extra);
            case SCHEME_CONTENT:
                return getStreamFromContent(imageUri, extra);
            case SCHEME_ASSETS:
                return getStreamFromAssets(imageUri, extra);
            case SCHEME_DRAWABLE:
                return getStreamFromDrawable(imageUri, extra);
            case SCHEME_UNKNOWN:
            default:
                return getStreamFromOtherSource(imageUri, extra);
        }
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in the network).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
     *                     URI.
     */
    protected InputStream getStreamFromNetwork(URI imageUri, Object extra) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection) imageUri.toURL().openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        return new FlushedInputStream(conn.getInputStream(), BUFFER_SIZE);
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located on the local file system or SD card).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs reading from file system
     */
    protected InputStream getStreamFromFile(URI imageUri, Object extra) throws IOException
    {
        return new BufferedInputStream(imageUri.toURL().openStream(), BUFFER_SIZE);
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is accessed using {@link ContentResolver}).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws FileNotFoundException if the provided URI could not be opened
     */
    protected InputStream getStreamFromContent(URI imageUri, Object extra) throws FileNotFoundException
    {
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse(imageUri.toString());
        return res.openInputStream(uri);
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in assets of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs file reading
     */
    protected InputStream getStreamFromAssets(URI imageUri, Object extra) throws IOException
    {
        String filePath = imageUri.toString().substring(SCHEME_ASSETS_PREFIX.length()); // Remove "assets://" prefix from image URI
        return context.getAssets().open(filePath);
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in drawable resources of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     */
    protected InputStream getStreamFromDrawable(URI imageUri, Object extra)
    {
        String drawableIdString = imageUri.toString().substring(SCHEME_DRAWABLE_PREFIX.length()); // Remove "drawable://" prefix from image URI
        int drawableId = Integer.parseInt(drawableIdString);
        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(drawableId);
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    /**
     * Retrieves {@link InputStream} of image by URI from other source with unsupported scheme. Should be overriden by
     * successors to implement image downloading from special sources.<br />
     * This method is called only if image URI has unsupported scheme. Throws {@link UnsupportedOperationException} by
     * default.
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException                   if some I/O error occurs
     * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
     */
    protected InputStream getStreamFromOtherSource(URI imageUri, Object extra) throws IOException
    {
        throw new UnsupportedOperationException(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri.getScheme()));
    }
}