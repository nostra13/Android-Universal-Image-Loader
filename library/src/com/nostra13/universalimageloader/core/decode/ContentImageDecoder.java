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
package com.nostra13.universalimageloader.core.decode;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;
import android.util.LruCache;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

import java.io.IOException;
import java.io.InputStream;

/**
 * Initialization:
 *
 * ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
 *             ...
 *             .imageDecoder(new ContentImageDecoder(getApplicationContext()))
 *             .build();
 *
 * Credit:
 *   [Daniel Gabriel] (http://stackoverflow.com/questions/20931585/is-it-possible-to-display-video-thumbnails-using-universal-image-loader-and-how)
 */
public class ContentImageDecoder extends BaseImageDecoder {
    public static final String TAG = "ContentImageDecoder";

    private final Context mContext;
    private ContentResolver mContentResolver;

    public static final int K = 1024;
    private static LruCache<String, Integer> sRotationCache;

    private static final boolean DEBUG = false;
    private static class Log8 {
        public static int d(Object... arr) {
            if (!DEBUG) return 0;
            StackTraceElement call = Thread.currentThread().getStackTrace()[3];
            String className = call.getClassName();
            className = className.substring(className.lastIndexOf('.') + 1);
            return android.util.Log.d(TAG,
                className + "."
                + call.getMethodName() + ":"
                + call.getLineNumber() + ": "
                + java.util.Arrays.deepToString(arr));
        }
    }

    public ContentImageDecoder(Context context) {
        this(false, context);
    }

    public ContentImageDecoder(boolean loggingEnabled, Context context) {
        super(false);
        mContext = context;
    }

    private ContentResolver getContentResolver() {
        if (mContentResolver == null) {
            mContentResolver = mContext.getContentResolver();
        }
        Log8.d(mContentResolver);
        return mContentResolver;
    }

    @Override
    public Bitmap decode(ImageDecodingInfo info) throws IOException {
        if (TextUtils.isEmpty(info.getImageKey())) {
            return null;
        }

        String cleanedUriString = cleanUriString(info.getImageKey());
        Uri uri = Uri.parse(cleanedUriString);
        if (isVideoUri(uri)) {
            int width = info.getTargetSize().getWidth();
            int height = info.getTargetSize().getHeight();
            Bitmap thumbnail = makeVideoThumbnailFromMediaMetadataRetriever(
                    width, height, getMediaMetadataRetriever(mContext, uri));
            /*
            thumbnail = makeVideoThumbnailFromMediaMetadataRetriever(
                    width, height, getMediaMetadataRetriever(getVideoFilePath(uri)));
            */
            Log8.d(thumbnail);
            if (thumbnail == null) {
                Log8.d(getVideoFilePath(uri));
                thumbnail = makeVideoThumbnail(width, height, getVideoFilePath(uri));
                Log8.d(thumbnail);
            }
            return thumbnail;
        }
        else {
            return super.decode(info);
        }
    }

    private Bitmap makeVideoThumbnail(int width, int height, String filePath) {
        if (TextUtils.isEmpty(filePath)) return null;
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
        if (thumbnail == null) return null;
        Bitmap scaledThumb = scaleBitmap(thumbnail, width, height);
        thumbnail.recycle();
        return scaledThumb;
    }

    private Bitmap makeVideoThumbnailFromMediaMetadataRetriever(int width, int height, MediaMetadataRetriever retriever) {
        if (retriever == null) return null;

        Bitmap thumbnail = null;
        byte[] picture = retriever.getEmbeddedPicture();

        if (picture != null) {
            Log8.d();
            thumbnail = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        } else {
            Log8.d();
        }

        if (thumbnail == null) {
            Log8.d();
            thumbnail = retriever.getFrameAtTime();
        }

        if (thumbnail == null) {
            Log8.d();
            return null;
        }

        Bitmap scaledThumb = scaleBitmap(thumbnail, width, height);
        thumbnail.recycle();
        return scaledThumb;
    }

    private static MediaMetadataRetriever getMediaMetadataRetriever(String filePath) {
        if (TextUtils.isEmpty(filePath)) return null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        return retriever;
    }

    private static MediaMetadataRetriever getMediaMetadataRetriever(Context context, Uri uri) {
        if (context == null || uri == null) return null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        return retriever;
    }

    private boolean isVideoUri(Uri uri) {
        String type = getContentResolver().getType(uri);
        return !TextUtils.isEmpty(type) && type.startsWith("video/");
    }

    private String getVideoFilePath(Uri uri) {
        String columnName = MediaStore.Video.VideoColumns.DATA;
        Cursor cursor = getContentResolver().query(uri, new String[] { columnName }, null, null, null);
        try {
            int dataIndex = cursor.getColumnIndex(columnName);
            if (dataIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(dataIndex);
            }
        }
        finally {
            cursor.close();
        }
        return null;
    }

    private Bitmap scaleBitmap(Bitmap origBitmap, int width, int height) {
        float scale = Math.min(
                ((float)width) / ((float)origBitmap.getWidth()),
                ((float)height) / ((float)origBitmap.getHeight())
        );
        return Bitmap.createScaledBitmap(origBitmap,
                (int)(((float)origBitmap.getWidth()) * scale),
                (int)(((float)origBitmap.getHeight()) * scale),
                false
        );
    }

    private String cleanUriString(String contentUriWithAppendedSize) {
        // replace the size at the end of the URI with an empty string.
        // the URI will be in the form "content://....._256x256
        return contentUriWithAppendedSize.replaceFirst("_\\d+x\\d+$", "");
    }

    private static LruCache<String, Integer> getRotationCache() {
        if (sRotationCache == null) {
            sRotationCache = new LruCache<String, Integer>(K);
        }
        return sRotationCache;
    }

    @Override
    protected ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, ImageDecodingInfo decodingInfo) throws IOException {
        String imageUri = decodingInfo.getImageUri();

        if (Scheme.ofUri(imageUri) == Scheme.FILE) {
            return super.defineImageSizeAndRotation(imageStream, decodingInfo);
        }
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);

        ExifInfo exif;

        Integer rotation = getRotationCache().get(imageUri);
        if (rotation == null) {
            if (decodingInfo.shouldConsiderExifParams()) {
                exif = getExifInfo(imageUri, null);
                getRotationCache().put(imageUri, exif.rotation);
            } else {
                exif = new ExifInfo();
            }
        } else {
            exif = new ExifInfo(rotation, false);
        }
        return new ImageFileInfo(new ImageSize(options.outWidth, options.outHeight, exif.rotation), exif);
    }

    protected ExifInfo getExifInfo(String imageUri, Object extra) {
        switch (Scheme.ofUri(imageUri)) {
            case FILE:
                return getExifInfoFromFile(imageUri, extra);
            case CONTENT:
                return getExifInfoFromContent(imageUri, extra);
            default:
                return new ExifInfo();
        }
    }

    protected ExifInfo getExifInfoFromContent(String imageUri, Object extra) {
        int rotation = 0;
        boolean flip = false;
        final String[] PROJECTION = {
            ImageColumns.ORIENTATION
        };
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(Uri.parse(imageUri), PROJECTION, null, null, null);
        } catch (Exception e) {
        }
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int orientation = cursor.getInt(cursor.getColumnIndexOrThrow((ImageColumns.ORIENTATION)));
                    rotation = (orientation + 360) % 360;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return new ExifInfo(rotation, flip);
    }

    protected ExifInfo getExifInfoFromFile(String imageUri, Object extra) {
        return defineExifOrientation(imageUri);
    }
}
