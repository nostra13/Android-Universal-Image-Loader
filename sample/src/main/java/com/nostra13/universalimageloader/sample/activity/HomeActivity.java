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
package com.nostra13.universalimageloader.sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.sample.Constants;
import com.nostra13.universalimageloader.sample.R;
import com.nostra13.universalimageloader.sample.fragment.ImageGalleryFragment;
import com.nostra13.universalimageloader.sample.fragment.ImageGridFragment;
import com.nostra13.universalimageloader.sample.fragment.ImageListFragment;
import com.nostra13.universalimageloader.sample.fragment.ImagePagerFragment;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HomeActivity extends Activity {

	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);

		File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);
		}
	}

	public void onImageListClick(View view) {
		Intent intent = new Intent(this, SimpleImageActivity.class);
		intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageListFragment.INDEX);
		startActivity(intent);
	}

	public void onImageGridClick(View view) {
		Intent intent = new Intent(this, SimpleImageActivity.class);
		intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
		startActivity(intent);
	}

	public void onImagePagerClick(View view) {
		Intent intent = new Intent(this, SimpleImageActivity.class);
		intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
		startActivity(intent);
	}

	public void onImageGalleryClick(View view) {
		Intent intent = new Intent(this, SimpleImageActivity.class);
		intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGalleryFragment.INDEX);
		startActivity(intent);
	}

	public void onFragmentsClick(View view) {
		Intent intent = new Intent(this, ComplexImageActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		ImageLoader.getInstance().stop();
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_clear_memory_cache:
				ImageLoader.getInstance().clearMemoryCache();
				return true;
			case R.id.item_clear_disc_cache:
				ImageLoader.getInstance().clearDiskCache();
				return true;
			default:
				return false;
		}
	}

	private void copyTestImageToSdCard(final File testImageOnSdCard) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = getAssets().open(TEST_FILE_NAME);
					FileOutputStream fos = new FileOutputStream(testImageOnSdCard);
					byte[] buffer = new byte[8192];
					int read;
					try {
						while ((read = is.read(buffer)) != -1) {
							fos.write(buffer, 0, read);
						}
					} finally {
						fos.flush();
						fos.close();
						is.close();
					}
				} catch (IOException e) {
					L.w("Can't copy test image onto SD card");
				}
			}
		}).start();
	}
}