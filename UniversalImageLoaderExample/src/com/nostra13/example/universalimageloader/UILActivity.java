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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DecodingType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.FailReason;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoadingListener;

/** Activity for {@link ImageLoader} testing */
public class UILActivity extends ListActivity {

	public ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getListView().setAdapter(new ItemAdapter());
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

			// Full "displayImage" method using.
			// You can use simple call:
			//  imageLoader.displayImage(imageUrls.get(position), holder.image);
			// instead of.
			DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.stub_image)
				.cacheInMemory()
				.cacheOnDisc()
				.decodingType(DecodingType.MEMORY_SAVING)
				.build();
			imageLoader.displayImage(imageUrls.get(position), holder.image, options, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted() {
					holder.text.setText("...loading...");
				}

				@Override
				public void onLoadingFailed(FailReason failReason) {
					String error;
					switch (failReason) {
						case IO_ERROR:
							error = "IO error!";
							break;
						case MEMORY_OVERFLOW:
							error = "Out Of Memory error!";
							break;
						default:
							error = "Error!";
							break;
					}
					holder.text.setText(error);
					holder.image.setImageResource(android.R.drawable.ic_delete);
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