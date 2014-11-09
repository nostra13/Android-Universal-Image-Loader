# ![Logo](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/sample/res/drawable-mdpi/ic_launcher.png) Universal Image Loader

UIL aims to provide a powerful, flexible and highly customizable instrument for image loading, caching and displaying. It provides a lot of configuration options and good control over the image loading and caching process.

![Screenshot](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/UniversalImageLoader.png)

## Project News
 **Upcoming changes in new UIL version (1.9.4+)**
 * Memory Cache redesign
 * **New API:** `DisplayImageOptions.targetSize(ImageSize)`
 * HTTP cache support
 * Consider `BitmapFactory.Options.inBitmap`
 * Time-to-live option for files in LruDiskCache

## Features
 * Multithread image loading (async or sync)
 * Wide customization of ImageLoader's configuration (thread executors, downloader, decoder, memory and disk cache, display image options, etc.)
 * Many customization options for every display image call (stub images, caching switch, decoding options, Bitmap processing and displaying, etc.)
 * Image caching in memory and/or on disk (device's file system or SD card)
 * Listening loading process (including downloading progress)

Android 2.0+ support

## Downloads
 * **[universal-image-loader-1.9.3.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.3.jar)**
 * **[universal-image-loader-1.9.3-sources.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.3-sources.jar)**
 * **[universal-image-loader-1.9.3-javadoc.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.3-javadoc.jar)**
 * **[universal-image-loader-1.9.3-with-sources.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.3-with-sources.jar)**
 * [![Demo app](https://camo.githubusercontent.com/dc1ffe0e4d25c2c28a69423c3c78000ef7ee96bf/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f696d616765732f6272616e642f656e5f6170705f7267625f776f5f34352e706e67)](https://play.google.com/store/apps/details?id=com.nostra13.universalimageloader.sample) [![QR Code](https://lh3.ggpht.com/csXEddxiLgQ6FxckefjQnP1PVugbaAYOdcuTa3vVtGV1PlWbFu2dYggoH8rI1w2RdEz1=w50)](http://chart.apis.google.com/chart?chs=300x300&cht=qr&chld=|1&chl=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.nostra13.universalimageloader.sample) [<img src="http://mobway.in/image/apk.png" height="45px" />](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-sample-1.9.3.apk)

### [Documentation](https://github.com/nostra13/Android-Universal-Image-Loader/wiki) | [Useful Info](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Useful-Info) | [User Support](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/User-Support) | [Changelog](https://github.com/nostra13/Android-Universal-Image-Loader/blob/master/CHANGELOG.md)

## Quick Setup

#### 1. Include library

**Manual:**
 * [Download JAR](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.3.jar)
 * Put the JAR in the **libs** subfolder of your Android project

or

**Maven dependency:**
``` xml
<dependency>
	<groupId>com.nostra13.universalimageloader</groupId>
	<artifactId>universal-image-loader</artifactId>
	<version>1.9.3</version>
</dependency>
```

or

**Gradle dependency:**
``` groovy
compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.3'
```

#### 2. Android Manifest
``` xml
<manifest>
	<!-- Include following permission if you load images from Internet -->
	<uses-permission android:name="android.permission.INTERNET" />
	<!-- Include following permission if you want to cache images on SD card -->
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	...
</manifest>
```

#### 3. Application or Activity class (before the first usage of ImageLoader)
``` java
public class MyActivity extends Activity {
	@Override
	public void onCreate() {
		super.onCreate();

		// Create global configuration and initialize ImageLoader with this config
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			...
			.build();
		ImageLoader.getInstance().init(config);
		...
	}
}
```

## Configuration and Display Options

 * ImageLoader **Configuration (`ImageLoaderConfiguration`) is global** for application.
 * **Display Options (`DisplayImageOptions`) are local** for every display task (`ImageLoader.displayImage(...)`).

### Configuration
All options in Configuration builder are optional. Use only those you really want to customize.<br />*See default values for config options in Java docs for every option.*
``` java
// DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of ALL options using.
// See the sample project how to use ImageLoader correctly.
File cacheDir = StorageUtils.getCacheDirectory(context);
ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.memoryCacheExtraOptions(480, 800) // default = device screen dimensions
		.diskCacheExtraOptions(480, 800, null)
		.taskExecutor(...)
		.taskExecutorForCachedImages(...)
		.threadPoolSize(3) // default
		.threadPriority(Thread.NORM_PRIORITY - 2) // default
		.tasksProcessingOrder(QueueProcessingType.FIFO) // default
		.denyCacheImageMultipleSizesInMemory()
		.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		.memoryCacheSize(2 * 1024 * 1024)
		.memoryCacheSizePercentage(13) // default
		.diskCache(new UnlimitedDiscCache(cacheDir)) // default
		.diskCacheSize(50 * 1024 * 1024)
		.diskCacheFileCount(100)
		.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
		.imageDownloader(new BaseImageDownloader(context)) // default
		.imageDecoder(new BaseImageDecoder()) // default
		.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
		.writeDebugLogs()
		.build();
```

### Display Options
Display Options can be applied to every display task (`ImageLoader.displayImage(...)` call).

**Note:** If Display Options wasn't passed to `ImageLoader.displayImage(...)`method then default Display Options from configuration (`ImageLoaderConfiguration.defaultDisplayImageOptions(...)`) will be used.
``` java
// DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of ALL options using.
// See the sample project how to use ImageLoader correctly.
DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub) // resource or drawable
		.showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
		.showImageOnFail(R.drawable.ic_error) // resource or drawable
		.resetViewBeforeLoading(false)  // default
		.delayBeforeLoading(1000)
		.cacheInMemory(false) // default
		.cacheOnDisk(false) // default
		.preProcessor(...)
		.postProcessor(...)
		.extraForDownloader(...)
		.considerExifParams(false) // default
		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
		.bitmapConfig(Bitmap.Config.ARGB_8888) // default
		.decodingOptions(...)
		.displayer(new SimpleBitmapDisplayer()) // default
		.handler(new Handler()) // default
		.build();
```

## Usage

### Acceptable URIs examples
``` java
String imageUri = "http://site.com/image.png"; // from Web
String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
String imageUri = "content://media/external/audio/albumart/1"; // from content provider
String imageUri = "assets://image.png"; // from assets
String imageUri = "drawable://" + R.drawable.img; // from drawables (non-9patch images)
```
**NOTE:** Use `drawable://` only if you really need it! Always **consider the native way** to load drawables - `ImageView.setImageResource(...)` instead of using of `ImageLoader`.

### Simple
``` java
// Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view 
//	which implements ImageAware interface)
imageLoader.displayImage(imageUri, imageView);
```
``` java
// Load image, decode it to Bitmap and return Bitmap to callback
imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		// Do whatever you want with Bitmap
	}
});
```
``` java
// Load image, decode it to Bitmap and return Bitmap synchronously
Bitmap bmp = imageLoader.loadImageSync(imageUri);
```

### Complete
``` java
// Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view 
//	which implements ImageAware interface)
imageLoader.displayImage(imageUri, imageView, options, new ImageLoadingListener() {
	@Override
	public void onLoadingStarted(String imageUri, View view) {
		...
	}
	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
		...
	}
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		...
	}
	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		...
	}
}, new ImageLoadingProgressListener() {
	@Override
	public void onProgressUpdate(String imageUri, View view, int current, int total) {
		...
	}
});
```
``` java
// Load image, decode it to Bitmap and return Bitmap to callback
ImageSize targetSize = new ImageSize(80, 50); // result Bitmap will be fit to this size
imageLoader.loadImage(imageUri, targetSize, options, new SimpleImageLoadingListener() {
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		// Do whatever you want with Bitmap
	}
});
```
``` java
// Load image, decode it to Bitmap and return Bitmap synchronously
ImageSize targetSize = new ImageSize(80, 50); // result Bitmap will be fit to this size
Bitmap bmp = imageLoader.loadImageSync(imageUri, targetSize, options);
```


## Applications using Universal Image Loader
**[MediaHouse, UPnP/DLNA Browser](https://play.google.com/store/apps/details?id=com.dbapp.android.mediahouse)** | **[Prezzi Benzina (AndroidFuel)](https://play.google.com/store/apps/details?id=org.vernazza.androidfuel)** | **[ROM Toolbox Lite](https://play.google.com/store/apps/details?id=com.jrummy.liberty.toolbox)**, [Pro](https://play.google.com/store/apps/details?id=com.jrummy.liberty.toolboxpro) | [Stadium Astro](https://play.google.com/store/apps/details?id=com.astro.stadium.activities) | [Chef Astro](https://play.google.com/store/apps/details?id=com.sencha.test) | [Sporee - Live Soccer Scores](https://play.google.com/store/apps/details?id=com.sporee.android) | **[EyeEm - Photo Filter Camera](https://play.google.com/store/apps/details?id=com.baseapp.eyeem)** | [PhotoDownloader for Facebook](https://play.google.com/store/apps/details?id=com.giannz.photodownloader) | **[Topface - meeting is easy](https://play.google.com/store/apps/details?id=com.topface.topface)** | **[reddit is fun](https://play.google.com/store/apps/details?id=com.andrewshu.android.reddit)** | **[Diaro - personal diary](https://play.google.com/store/apps/details?id=com.pixelcrater.Diaro)** |  [WebMoney Keeper Mobile](https://play.google.com/store/apps/details?id=ru.webmoney.keeper.mobile) | **[LoL Memento League of Legends](https://play.google.com/store/apps/details?id=com.buchland.lolmemento)** | **[Meetup](https://play.google.com/store/apps/details?id=com.meetup)** | [Vingle - Magazines by Fans](https://play.google.com/store/apps/details?id=com.vingle.android) | [Anime Music Radio](https://play.google.com/store/apps/details?id=com.maxxt.animeradio) | [WidgetLocker Theme Viewer](https://play.google.com/store/apps/details?id=com.companionfree.WLThemeViewer) | [ShortBlogger for Tumblr](https://play.google.com/store/apps/details?id=com.luckydroid.tumblelog) | [SnapDish Food Camera](https://play.google.com/store/apps/details?id=com.vuzz.snapdish) | **[Twitch](https://play.google.com/store/apps/details?id=tv.twitch.android.viewer)** | [TVShow Time, TV show guide](https://play.google.com/store/apps/details?id=com.tozelabs.tvshowtime) | [Planning Center Services](https://play.google.com/store/apps/details?id=com.ministrycentered.PlanningCenter) | [Daybe - 일기가 되는 SNS](https://play.google.com/store/apps/details?id=com.daybe) | **[Lapse It](https://play.google.com/store/apps/details?id=com.ui.LapseIt)** | [My Cloud Player for SoundCloud](https://play.google.com/store/apps/details?id=com.mycloudplayers.mycloudplayer) | **[SoundTracking](https://play.google.com/store/apps/details?id=com.schematiclabs.soundtracking)** | [LoopLR Social Video](https://play.google.com/store/apps/details?id=com.looplr) | [Reddit Pics HD](https://play.google.com/store/apps/details?id=com.funpokes.redditpics) | [Hír24](https://play.google.com/store/apps/details?id=hu.sanomamedia.hir24) | **[Immobilien Scout24](https://play.google.com/store/apps/details?id=de.is24.android)** | **[Lieferheld - Pizza Pasta Sushi](https://play.google.com/store/apps/details?id=de.lieferheld.android)** | [Loocator: free sex datings](https://play.google.com/store/apps/details?id=com.ivicode.loocator) | [벨팡-개편 이벤트,컬러링,벨소리,무료,최신가요,링투유](https://play.google.com/store/apps/details?id=com.mediahubs.www) | [Streambels AirPlay/DLNA Player](https://play.google.com/store/apps/details?id=com.tuxera.streambels) | [Ship Mate - All Cruise Lines](https://play.google.com/store/apps/details?id=shipmate.carnival) | [Disk & Storage Analyzer](https://play.google.com/store/apps/details?id=com.mobile_infographics_tools.mydrive) | [糗事百科](https://play.google.com/store/apps/details?id=qsbk.app) | [Balance BY](https://play.google.com/store/apps/details?id=com.vladyud.balance) | **[Anti Theft Alarm - Security](https://play.google.com/store/apps/details?id=br.com.verde.alarme)** | **[XiiaLive™ - Internet Radio](https://play.google.com/store/apps/details?id=com.android.DroidLiveLite)** | **[Bandsintown Concerts](https://play.google.com/store/apps/details?id=com.bandsintown)** | **[Save As Web Archive](https://play.google.com/store/apps/details?id=jp.fuukiemonster.webmemo)** | [MCPE STORE -Download MCPE file](https://play.google.com/store/apps/details?id=com.newidea.mcpestore) | **[All-In-One Toolbox (29 Tools)](http://aiotoolbox.com/)** | [Zaim](https://play.google.com/store/apps/details?id=net.zaim.android) | **[Calculator Plus Free](https://play.google.com/store/apps/details?id=com.digitalchemy.calculator.freedecimal)**

## Donation
You can support the project and thank the author for his hard work :)

<a href='https://pledgie.com/campaigns/19144'><img alt='Click here to lend your support to: Universal Image Loader for Android and make a donation at pledgie.com !' src='https://pledgie.com/campaigns/19144.png?skin_name=chrome' border='0' ></a> <a href="http://flattr.com/thing/1110177/nostra13Android-Universal-Image-Loader-on-GitHub" target="_blank"><img src="http://api.flattr.com/button/flattr-badge-large.png" alt="Flattr this" title="Flattr this" border="0" /></a>
* **PayPal** - nostra.uil[at]gmail[dot]com

## Alternative libraries

 * [AndroidQuery : ImageLoading](https://code.google.com/p/android-query/wiki/ImageLoading)
 * [DroidParts : ImageFetcher](http://droidparts.org/image_fetcher.html)
 * [Glide](https://github.com/bumptech/glide)
 * [Picasso](https://github.com/square/picasso)
 * [UrlImageViewHelper](https://github.com/koush/UrlImageViewHelper)
 * [Volley : ImageLoader](https://android.googlesource.com/platform/frameworks/volley/)

## License

If you use Universal Image Loader code in your application you should inform the author about it ( *email: nostra13[at]gmail[dot]com* ) like this:
> **Subject:** UIL usage notification<br />
> **Text:** I use Universal Image Loader {lib_version} in {application_name} - {http://link_to_google_play}.
> I [allow | don't allow] to mention my app in section "Applications using Universal Image Loader" on GitHub.

    Copyright 2011-2014 Sergey Tarasevich

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
