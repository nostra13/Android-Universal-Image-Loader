package com.nostra13.example.universalimageloader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.FailReason;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoadingListener;

/** Activity for {@link ImageLoader} testing */
public class ImageGridActivity extends BaseActivity {

	private String[] imageUrls;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);

		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray(Extra.IMAGES);
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter());
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImageGalleryActivity(position);
			}
		});
	}

	@Override
	protected void onDestroy() {
		imageLoader.stop();
		super.onDestroy();
	}

	private void startImageGalleryActivity(int position) {
		Intent intent = new Intent(this, ImageGalleryActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
			} else {
				imageView = (ImageView) convertView;
			}

			DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.stub_image)
				.showImageForEmptyUrl(R.drawable.image_for_empty_url)
				.cacheInMemory()
				.cacheOnDisc()
				.build();
			imageLoader.displayImage(imageUrls[position], imageView, options, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted() {
					// do nothing
				}

				@Override
				public void onLoadingFailed(FailReason failReason) {
					imageView.setImageResource(android.R.drawable.ic_delete);

					switch (failReason) {
						case MEMORY_OVERFLOW:
							imageLoader.clearMemoryCache();
							break;
					}
				}

				@Override
				public void onLoadingComplete() {
					// do nothing
				}
			});

			return imageView;
		}
	}
}