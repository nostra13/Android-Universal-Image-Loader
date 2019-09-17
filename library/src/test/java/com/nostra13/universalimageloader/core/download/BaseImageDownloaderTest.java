package com.nostra13.universalimageloader.core.download;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

@RunWith(RobolectricTestRunner.class)
public class BaseImageDownloaderTest {

	@Test
	public void testSchemeHttp() throws Exception {
		String uri = "http://image.com/1.png";
		Scheme result = Scheme.ofUri(uri);
		Scheme expected = Scheme.HTTP;
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testSchemeHttps() throws Exception {
		String uri = "https://image.com/1.png";
		Scheme result = Scheme.ofUri(uri);
		Scheme expected = Scheme.HTTPS;
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testSchemeContent() throws Exception {
		String uri = "content://path/to/content";
		Scheme result = Scheme.ofUri(uri);
		Scheme expected = Scheme.CONTENT;
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testSchemeAssets() throws Exception {
		String uri = "assets://folder/1.png";
		Scheme result = Scheme.ofUri(uri);
		Scheme expected = Scheme.ASSETS;
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testSchemeDrawables() throws Exception {
		String uri = "drawable://123456890";
		Scheme result = Scheme.ofUri(uri);
		Scheme expected = Scheme.DRAWABLE;
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testSchemeFile() throws Exception {
		String uri = "file://path/on/the/device/1.png";
		Scheme result = Scheme.ofUri(uri);
		Scheme expected = Scheme.FILE;
		Assertions.assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testSchemeUnknown() throws Exception {
		String uri = "other://image.com/1.png";
		Scheme result = Scheme.ofUri(uri);
		Scheme expected = Scheme.UNKNOWN;
		Assertions.assertThat(result).isEqualTo(expected);
	}
}
