package com.nostra13.example.universalimageloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.Constants;
import com.nostra13.universalimageloader.imageloader.DisplayImageOptions;
import com.nostra13.universalimageloader.imageloader.ImageLoader;
import com.nostra13.universalimageloader.imageloader.ImageLoadingListener;

/** Activity for {@link ImageLoader} testing */
public class UILActivity extends ListActivity {

	public ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		imageLoader = ImageLoader.getInstance(UILActivity.this);

		ListView listView = getListView();
		listView.setAdapter(new ItemAdapter());
	}

	@Override
	protected void onDestroy() {
		imageLoader.stop();
		super.onDestroy();
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
				imageLoader.clearMemoryCache();
				return true;
			case R.id.item_clear_disc_cache:
				imageLoader.clearDiscCache();
				return true;
			default:
				return false;
		}
	}

	class ItemAdapter extends BaseAdapter {

		private List<String> imageUrls;

		private ItemAdapter() {
			String[] heavyImages = getResources().getStringArray(R.array.heavy_images);
			String[] lightImages = getResources().getStringArray(R.array.light_images);

			imageUrls = new ArrayList<String>(heavyImages.length + lightImages.length);
			imageUrls.addAll(Arrays.asList(heavyImages));
			imageUrls.addAll(Arrays.asList(lightImages));
		}

		public int getCount() {
			return imageUrls.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public class ViewHolder {
			public TextView text;
			public ImageView image;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else
				holder = (ViewHolder) view.getTag();

			holder.text.setText("Item " + position);
			DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(Constants.STUB_IMAGE).cacheInMemory().cacheOnDisc().build();
			imageLoader.displayImage(imageUrls.get(position), holder.image, options, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted() {
					holder.text.setText("...loading...");
				}

				@Override
				public void onLoadingComplete() {
					holder.text.setText("Item " + position);
				}
			});
			return view;
		}
	}
}