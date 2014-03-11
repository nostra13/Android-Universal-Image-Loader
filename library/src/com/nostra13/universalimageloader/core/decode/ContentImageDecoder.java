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

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.IOException;
import java.io.InputStream;

public class ContentImageDecoder extends BaseImageDecoder {
    private final ContentResolver mContentResolver;

    public ContentImageDecoder(Context context) {
        this(false, context);
    }

    public ContentImageDecoder(boolean loggingEnabled, Context context) {
        super(false);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public Bitmap decode(ImageDecodingInfo info) throws IOException {
        if (TextUtils.isEmpty(info.getImageKey())) {
            return null;
        }

        String cleanedUriString = cleanUriString(info.getImageKey());
        Uri uri = Uri.parse(cleanedUriString);
        if (isVideoUri(uri)) {
            return makeVideoThumbnail(info.getTargetSize().getWidth(), info.getTargetSize().getHeight(), getVideoFilePath(uri));
        }
        else {
            return super.decode(info);
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private Bitmap makeVideoThumbnail(int width, int height, String filePath) {
        if (filePath == null) {
            return null;
        }
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
        Bitmap scaledThumb = scaleBitmap(thumbnail, width, height);
        thumbnail.recycle();
        return scaledThumb;
    }

    private boolean isVideoUri(Uri uri) {
        String mimeType = mContentResolver.getType(uri);
        return mimeType.startsWith("video/");
    }

    private String getVideoFilePath(Uri uri) {
        String columnName = MediaStore.Video.VideoColumns.DATA;
        Cursor cursor = mContentResolver.query(uri, new String[] { columnName }, null, null, null);
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

        if (decodingInfo.shouldConsiderExifParams()) {
            exif = getExifInfo(imageUri, null);
        } else {
            exif = new ExifInfo();
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
            cursor = mContentResolver.query(Uri.parse(imageUri), PROJECTION, null, null, null);
        } catch (Exception e) {
        }
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int orientation = cursor.getInt(cursor.getColumnIndexOrThrow((ImageColumns.ORIENTATION)));
                    rotation = (orientation + 360) % 360;
                }
            } catch (Exception e) {
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
