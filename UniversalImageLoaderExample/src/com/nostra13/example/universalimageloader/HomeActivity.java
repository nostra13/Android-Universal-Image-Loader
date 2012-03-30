package com.nostra13.example.universalimageloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HomeActivity extends BaseActivity {

	private String[] imageUrls;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);

		String[] heavyImages = getResources().getStringArray(R.array.heavy_images);
		String[] lightImages = getResources().getStringArray(R.array.light_images);

		imageUrls = new String[heavyImages.length + lightImages.length];
		List<String> urls = new ArrayList<String>();
		urls.addAll(Arrays.asList(heavyImages));
		urls.addAll(Arrays.asList(lightImages));
		imageUrls = (String[]) urls.toArray(new String[0]);
	}

	public void onImageListClick(View view) {
		Intent intent = new Intent(this, ImageListActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		startActivity(intent);
	}

	public void onImageGridClick(View view) {
		Intent intent = new Intent(this, ImageGridActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		startActivity(intent);
	}

	public void onImagePagerClick(View view) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		startActivity(intent);
	}

	public void onImageGalleryClick(View view) {
		Intent intent = new Intent(this, ImageGalleryActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		startActivity(intent);
	}
}