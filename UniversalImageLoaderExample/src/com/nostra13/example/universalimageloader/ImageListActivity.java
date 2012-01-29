package com.nostra13.example.universalimageloader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DecodingType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.FailReason;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoadingListener;

/** Activity for {@link ImageLoader} testing */
public class ImageListActivity extends BaseActivity {

	private String[] imageUrls;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_list);

		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray(Extra.IMAGES);

		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(new ItemAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {
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

	class ItemAdapter extends BaseAdapter {

		private class ViewHolder {
			public TextView text;
			public ImageView image;
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, null);
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
			imageLoader.displayImage(imageUrls[position], holder.image, options, new ImageLoadingListener() {
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