package com.nostra13.universalimageloader.core.download;

import com.nostra13.universalimageloader.test.UILTestRunner;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;

/**
 * Created with Intellij with Android, BIZZBY product.
 * See licencing for usage of this code.
 * <p/>
 * User: chris
 * Date: 02/03/2013
 * Time: 14:08
 */
@RunWith(UILTestRunner.class)
public class BaseImageDownloaderTest
{

    @Test
    public void testSchemeHttp() throws Exception
    {
        URI uri = URI.create("http://image.com/1.png");
        ImageDownloader.SCHEME result = ImageDownloader.SCHEME.getScheme(uri);
        ImageDownloader.SCHEME expected = ImageDownloader.SCHEME.SCHEME_HTTP;
        Assertions.assertThat(result).isEqualTo(expected);
    }
    @Test
    public void testSchemeHttps() throws Exception
    {
        URI uri = URI.create("https://image.com/1.png");
        ImageDownloader.SCHEME result = ImageDownloader.SCHEME.getScheme(uri);
        ImageDownloader.SCHEME expected = ImageDownloader.SCHEME.SCHEME_HTTPS;
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testSchemeContent() throws Exception
    {
        URI uri = URI.create("content://path/to/content");
        ImageDownloader.SCHEME result = ImageDownloader.SCHEME.getScheme(uri);
        ImageDownloader.SCHEME expected = ImageDownloader.SCHEME.SCHEME_CONTENT;
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testSchemeAssets() throws Exception
    {
        URI uri = URI.create("assets://folder/1.png");
        ImageDownloader.SCHEME result = ImageDownloader.SCHEME.getScheme(uri);
        ImageDownloader.SCHEME expected = ImageDownloader.SCHEME.SCHEME_ASSETS;
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testSchemeDrawables() throws Exception
    {
        URI uri = URI.create("drawable://123456890");
        ImageDownloader.SCHEME result = ImageDownloader.SCHEME.getScheme(uri);
        ImageDownloader.SCHEME expected = ImageDownloader.SCHEME.SCHEME_DRAWABLE;
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testSchemeFile() throws Exception
    {
        URI uri = URI.create("file://path/on/the/device/1.png");
        ImageDownloader.SCHEME result = ImageDownloader.SCHEME.getScheme(uri);
        ImageDownloader.SCHEME expected = ImageDownloader.SCHEME.SCHEME_FILE;
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testSchemeUnknown() throws Exception
    {
        URI uri = URI.create("other://image.com/1.png");
        ImageDownloader.SCHEME result = ImageDownloader.SCHEME.getScheme(uri);
        ImageDownloader.SCHEME expected = ImageDownloader.SCHEME.SCHEME_UNKNOWN;
        Assertions.assertThat(result).isEqualTo(expected);
    }










}
