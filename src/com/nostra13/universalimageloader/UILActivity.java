package com.nostra13.universalimageloader;

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

		private String[] mUrls = {
				// Heavy images
//				"http://www.android.com/media/wallpaper/gif/android_logo.gif",
//				"http://www.android.com/media/wallpaper/gif/cupcake_2009.gif",
//				"http://cdn4.digitaltrends.com/wp-content/uploads/2011/04/Google-Android.png",
//				"http://2.bp.blogspot.com/_JSR8IC77Ub4/TATYokamR0I/AAAAAAAAAhc/O1KKFe5Fay0/s1600/richd-android1920.jpg",
//				"http://1.bp.blogspot.com/_0nd55rShouI/TPMx2PZMPEI/AAAAAAAAAC8/GSCW4W4fkcA/s1600/android.jpg",
//				"http://atlantistilemarble.com/wp-content/uploads/2011/02/android-wallpaper1_1024x768.png",
//				"http://www.android.com/media/wallpaper/gif/androids.gif",
//				"http://3.bp.blogspot.com/_Sd45ASngYHA/TVC78RORKoI/AAAAAAAAARk/y0GcNkTmb40/s1600/android+logo1.jpg",
//				"http://1.bp.blogspot.com/-mhrnwr-R_9k/TcwraA1SsWI/AAAAAAAAAjs/LIcWBaOOM88/s1600/Android%2540Home.jpg",
//				"http://www.android.com/goodies/wallpaper/android-wallpaper4_1024x768.png",
//				"http://4.bp.blogspot.com/__n_rRWNSWVs/TOyBihqZlVI/AAAAAAAAAms/z23vVZnAtUk/s1600/android_vulnerability.jpg",
//				"http://3.bp.blogspot.com/__n_rRWNSWVs/THRRtuq-1gI/AAAAAAAAAkc/REqEJwOky2U/s1600/good_looking_android_launcher_pro.png",
//				"http://4android-phone.com/wp-content/uploads/2011/08/How-to-Browse-Files-on-an-Android-Phone21.png",
//				"http://img2.etsystatic.com/il_fullxfull.253478178.jpg",
//				"http://3.bp.blogspot.com/-myXkCrTnCDs/TWmIJiLdWBI/AAAAAAAAmn4/-XRCxm1kCKc/s1600/pXUsV.jpg",
//				"http://all-free-download.com/downloadfiles/wallpapers/1280_1024/android_wallpaper_google_computers_wallpaper_1280_1024_2675.jpg",
//				"http://www.designerterminal.com/wp-content/uploads/2011/04/18_thumb.jpg",
//				"http://www.designerterminal.com/wp-content/uploads/2011/04/13_thumb.jpg",
//				"http://cdn.androidcentral.com/sites/androidcentral.com/files/postimages/9274/tz.com_3_0.jpg",
//				"http://2.bp.blogspot.com/-Q78WS_f1YP8/TiQBauodVpI/AAAAAAAAAm8/hmZnQw1g6m8/s1600/IMG_0223.jpg",
//				"http://2.bp.blogspot.com/_y9zuMy5_0aY/S974L2fvXXI/AAAAAAAACJ8/98KQl0gMAhc/s1600/IMG_0466.jpg",
//				"http://cache.gawkerassets.com/assets/images/17/2011/05/icecreamsandwich.jpg",
//				"http://disfi.com/wp-content/uploads/2011/11/panasonicToughpadA1andB11.jpg",
//				"http://fastcache.gawkerassets.com/assets/images/4/2010/08/decayedandroid2.jpg",
//				"http://i00.i.aliimg.com/img/pb/767/772/267/1284794302575_hz-myalibaba-temp13_2190.jpg",
//				"http://img.docstoccdn.com/thumb/orig/17435666.png"

				// Light images
				"http://a3.twimg.com/profile_images/670625317/aam-logo-v3-twitter.png",
				"http://a3.twimg.com/profile_images/740897825/AndroidCast-350_normal.png", "http://a3.twimg.com/profile_images/121630227/Droid_normal.jpg",
				"http://a1.twimg.com/profile_images/957149154/twitterhalf_normal.jpg", "http://a1.twimg.com/profile_images/97470808/icon_normal.png",
				"http://a3.twimg.com/profile_images/511790713/AG.png", "http://a3.twimg.com/profile_images/956404323/androinica-avatar_normal.png",
				"http://a1.twimg.com/profile_images/909231146/Android_Biz_Man_normal.png",
				"http://a3.twimg.com/profile_images/72774055/AndroidHomme-LOGO_normal.jpg",
				"http://a1.twimg.com/profile_images/349012784/android_logo_small_normal.jpg",
				"http://a1.twimg.com/profile_images/841338368/ea-twitter-icon.png",
				"http://a3.twimg.com/profile_images/64827025/android-wallpaper6_2560x160_normal.png",
				"http://a3.twimg.com/profile_images/77641093/AndroidPlanet_normal.png",
				"http://a1.twimg.com/profile_images/850960042/elandroidelibre-logo_300x300_normal.jpg",
				"http://a1.twimg.com/profile_images/655119538/andbook.png", "http://a3.twimg.com/profile_images/768060227/ap4u_normal.jpg",
				"http://a1.twimg.com/profile_images/74724754/android_logo_normal.png",
				"http://a3.twimg.com/profile_images/681537837/SmallAvatarx150_normal.png",
				"http://a1.twimg.com/profile_images/63737974/2008-11-06_1637_normal.png", "http://a3.twimg.com/profile_images/548410609/icon_8_73.png",
				"http://a1.twimg.com/profile_images/612232882/nexusoneavatar_normal.jpg",
				"http://a1.twimg.com/profile_images/213722080/Bugdroid-phone_normal.png",
				"http://a1.twimg.com/profile_images/645523828/OT_icon_090918_android_normal.png",
				"http://a3.twimg.com/profile_images/77641093/AndroidPlanet.png", "http://a1.twimg.com/profile_images/655119538/andbook_normal.png",
				"http://a3.twimg.com/profile_images/511790713/AG_normal.png", "http://a3.twimg.com/profile_images/956404323/androinica-avatar.png",
				"http://a1.twimg.com/profile_images/841338368/ea-twitter-icon_normal.png",
				"http://a1.twimg.com/profile_images/850960042/elandroidelibre-logo_300x300.jpg", "http://a3.twimg.com/profile_images/121630227/Droid.jpg",
				"http://a1.twimg.com/profile_images/909231146/Android_Biz_Man.png",
				"http://a3.twimg.com/profile_images/670625317/aam-logo-v3-twitter_normal.png", "http://a1.twimg.com/profile_images/97470808/icon.png",
				"http://a1.twimg.com/profile_images/74724754/android_logo.png", "http://a3.twimg.com/profile_images/548410609/icon_8_73_normal.png",
				"http://a1.twimg.com/profile_images/645523828/OT_icon_090918_android.png", "http://a1.twimg.com/profile_images/957149154/twitterhalf.jpg",
				"http://a1.twimg.com/profile_images/349012784/android_logo_small.jpg"};

		public int getCount() {
			return mUrls.length;
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
			DisplayImageOptions options = DisplayImageOptions.createForListView();
			imageLoader.displayImage(mUrls[position], holder.image, options, new ImageLoadingListener() {
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