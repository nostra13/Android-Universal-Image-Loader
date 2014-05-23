# ![Logo](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/sample/res/drawable-mdpi/ic_launcher.png) Universal Image Loader for Android

This project aims to provide a reusable instrument for asynchronous image loading, caching and displaying. It is originally based on [Fedor Vlasov's project](https://github.com/thest1/LazyList) and has been vastly refactored and improved since then.

![Screenshot](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/UniversalImageLoader.png)

## Project News
 **Upcoming changes in new UIL version (1.9.2)**
 * Possibility to call ImageLoader out of UI thread
 * New Disk Cache API (more flexible). New `LruDiskCache` based on Jake Wharton's `DiskLruCache`. 

## Features
 * Multithread image loading
 * Possibility of wide tuning ImageLoader's configuration (thread executors, downloader, decoder, memory and disk cache, display image options, and others)
 * Possibility of image caching in memory and/or on device's file system (or SD card)
 * Possibility to "listen" loading process
 * Possibility to customize every display image call with separated options
 * Widget support

Android 2.0+ support

## Downloads
 * **[universal-image-loader-1.9.1.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.1.jar)** (library; contains *.class files)
 * **[universal-image-loader-1.9.1-sources.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.1-sources.jar)** (sources; contains *.java files)
 * **[universal-image-loader-1.9.1-javadoc.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.1-javadoc.jar)** (Java docs; contains *.html files)
 * **[universal-image-loader-1.9.1-with-sources.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.1-with-sources.jar)** (library with sources inside; contains *.class and *.java files)<br />_Prefer to use this JAR so you can see Java docs in Eclipse tooltips._
 * **[universal-image-loader-sample-1.9.1.apk](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-sample-1.9.1.apk)** (sample application)

Latest snapshot of the library - **[here](https://github.com/nostra13/Android-Universal-Image-Loader/tree/master/sample/libs)**

## Documentation (outdated, new one is in progress...)
 * Universal Image Loader. Part 1 - Introduction [[RU](http://nostra13android.blogspot.com/2012/03/4-universal-image-loader-part-1.html)]
 * Universal Image Loader. Part 2 - Configuration [[RU](http://nostra13android.blogspot.com/2012/03/5-universal-image-loader-part-2.html) | [EN](http://www.intexsoft.com/blog/item/72-universal-image-loader-part-2.html)]
 * Universal Image Loader. Part 3 - Usage [[RU](http://nostra13android.blogspot.com/2012/03/6-universal-image-loader-part-3-usage.html) | [EN](http://www.intexsoft.com/blog/item/74-universal-image-loader-part-3.html)]

### [Changelog](https://github.com/nostra13/Android-Universal-Image-Loader/blob/master/CHANGELOG.md)

### User Support
 1. Look into **[Useful Info](https://github.com/nostra13/Android-Universal-Image-Loader#useful-info)**
 2. Search problem solution on **[StackOverFlow](http://stackoverflow.com/questions/tagged/universal-image-loader)**
 3. Ask your own question on **[StackOverFlow](http://stackoverflow.com/questions/tagged/universal-image-loader)**.<br />
    Be sure to mention following information in your question:
   - UIL version (e.g. 1.9.1)
   - Android version tested on (e.g. 2.1)
   - your configuration (`ImageLoaderConfiguration`)
   - display options (`DisplayImageOptions`)
   - `getView()` method code of your adapter (if you use it)
   - XML layout of your ImageView you load image into

**Bugs** and **feature requests** put **[here](https://github.com/nostra13/Android-Universal-Image-Loader/issues/new)**.<br />
If you have some **issues on migration** to newer library version - be sure to ask for help **[here](https://github.com/nostra13/Android-Universal-Image-Loader/issues/169)**

## Quick Setup

#### 1. Include library

**Manual:**
 * [Download JAR](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.1-with-sources.jar)
 * Put the JAR in the **libs** subfolder of your Android project

or

**Maven dependency:**
``` xml
<dependency>
	<groupId>com.nostra13.universalimageloader</groupId>
	<artifactId>universal-image-loader</artifactId>
	<version>1.9.1</version>
</dependency>
```

#### 2. Android Manifest
``` xml
<manifest>
	<uses-permission android:name="android.permission.INTERNET" />
	<!-- Include next permission if you want to allow UIL to cache images on SD card -->
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	...
	<application android:name="MyApplication">
		...
	</application>
</manifest>
```

#### 3. Application class
``` java
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		// Create global configuration and initialize ImageLoader with this configuration
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			...
			.build();
		ImageLoader.getInstance().init(config);
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
File cacheDir = StorageUtils.getCacheDirectory(context);
ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.memoryCacheExtraOptions(480, 800) // default = device screen dimensions
		.diskCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
		.taskExecutor(...)
		.taskExecutorForCachedImages(...)
		.threadPoolSize(3) // default
		.threadPriority(Thread.NORM_PRIORITY - 1) // default
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
String imageUri = "content://media/external/audio/albumart/13"; // from content provider
String imageUri = "assets://image.png"; // from assets
String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
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
imageLoader.displayImage(imageUri, imageView, displayOptions, new ImageLoadingListener() {
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
ImageSize targetSize = new ImageSize(120, 80); // result Bitmap will be fit to this size
imageLoader.loadImage(imageUri, targetSize, displayOptions, new SimpleImageLoadingListener() {
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		// Do whatever you want with Bitmap
	}
});
```
``` java
// Load image, decode it to Bitmap and return Bitmap synchronously
ImageSize targetSize = new ImageSize(120, 80); // result Bitmap will be fit to this size
Bitmap bmp = imageLoader.loadImageSync(imageUri, targetSize, displayOptions);
```

### ImageLoader Helpers
Other useful methods and classes to consider.
<pre>
ImageLoader |
			| - getMemoryCache()
			| - clearMemoryCache()
			| - getDiskCache()
			| - clearDiskCache()
			| - denyNetworkDownloads(boolean)
			| - handleSlowNetwork(boolean)
			| - pause()
			| - resume()
			| - stop()
			| - destroy()
			| - getLoadingUriForView(ImageView)
			| - getLoadingUriForView(ImageAware)
			| - cancelDisplayTask(ImageView)
			| - cancelDisplayTask(ImageAware)

MemoryCacheUtils |
				| - findCachedBitmapsForImageUri(...)
				| - findCacheKeysForImageUri(...)
				| - removeFromCache(...)

DiskCacheUtils |
			  | - findInCache(...)
			  | - removeFromCache(...)

StorageUtils |
			 | - getCacheDirectory(Context)
			 | - getIndividualCacheDirectory(Context)
			 | - getOwnCacheDirectory(Context, String)

PauseOnScrollListener

ImageAware |
		   | - getWidth()
		   | - getHeight()
		   | - getScaleType()
		   | - getWrappedView()
		   | - isCollected()
		   | - getId()
		   | - setImageDrawable(Drawable)
		   | - setImageBitmap(Bitmap)
</pre>
Also look into more detailed **[Library Map](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Library-Map)**

## Useful Info
1. **Caching is NOT enabled by default.** If you want loaded images will be cached in memory and/or on disk then you should enable caching in DisplayImageOptions this way:
``` java
// Create default options which will be used for every 
//  displayImage(...) call if no options will be passed to this method
DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			...
            .cacheInMemory(true)
            .cacheOnDisk(true)
            ...
            .build();
ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            ...
            .defaultDisplayImageOptions(defaultOptions)
            ...
            .build();
ImageLoader.getInstance().init(config); // Do it on Application start
```
``` java
// Then later, when you want to display image
ImageLoader.getInstance().displayImage(imageUrl, imageView); // Default options will be used
```
or this way:
``` java
DisplayImageOptions options = new DisplayImageOptions.Builder()
			...
            .cacheInMemory(true)
            .cacheOnDisk(true)
            ...
            .build();
ImageLoader.getInstance().displayImage(imageUrl, imageView, options); // Incoming options will be used
```

2. If you enabled disk caching then UIL try to cache images on external storage (/sdcard/Android/data/[package_name]/cache). If external storage is not available then images are cached on device's filesystem.
To provide caching on external storage (SD card) add following permission to AndroidManifest.xml:
``` java
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

3. How UIL define Bitmap size needed for exact ImageView? It searches defined parameters:
 * Get actual measured width and height of ImageView
 * Get `android:layout_width` and `android:layout_height` parameters
 * Get `android:maxWidth` and/or `android:maxHeight` parameters
 * Get maximum width and/or height parameters from configuration (`memoryCacheExtraOptions(int, int)` option)
 * Get width and/or height of device screen

 So **try to set** `android:layout_width`|`android:layout_height` or `android:maxWidth`|`android:maxHeight` parameters for ImageView if you know approximate maximum size of it. It will help correctly compute Bitmap size needed for this view and **save memory**.

4. If you often got **OutOfMemoryError** in your app using Universal Image Loader then try next (all of them or several):
 - Reduce thread pool size in configuration (`.threadPoolSize(...)`). 1 - 5 is recommended.
 - Use `.bitmapConfig(Bitmap.Config.RGB_565)` in display options. Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.
 - Use `.memoryCache(new WeakMemoryCache())` in configuration or disable caching in memory at all in display options (don't call `.cacheInMemory()`).
 - Use `.imageScaleType(ImageScaleType.IN_SAMPLE_INT)` in display options. Or try `.imageScaleType(ImageScaleType.EXACTLY)`.

5. For memory cache configuration (`ImageLoaderConfiguration.memoryCache(...)`) you can use already prepared implementations.
 * Cache using **only strong** references:
     * `LruMemoryCache` (Least recently used bitmap is deleted when cache size limit is exceeded) - **Used by default**
 * Caches using **weak and strong** references:
     * `UsingFreqLimitedMemoryCache` (Least frequently used bitmap is deleted when cache size limit is exceeded)
     * `LRULimitedMemoryCache` (Least recently used bitmap is deleted when cache size limit is exceeded)
     * `FIFOLimitedMemoryCache` (FIFO rule is used for deletion when cache size limit is exceeded)
     * `LargestLimitedMemoryCache` (The largest bitmap is deleted when cache size limit is exceeded)
     * `LimitedAgeMemoryCache` (Decorator. Cached object is deleted when its age exceeds defined value)
 * Cache using **only weak** references:
     * `WeakMemoryCache` (Unlimited cache)

6. For disk cache configuration (`ImageLoaderConfiguration.diskCache(...)`) you can use already prepared implementations:
 * `UnlimitedDiscCache` (The fastest cache, doesn't limit cache size) - **Used by default**
 * `LruDiskCache` (Cache limited by total cache size and/or by file count. If cache size exceeds specified limit then least-recently used file will be deleted)
 * `LimitedAgeDiscCache` (Size-unlimited cache with limited files' lifetime. If age of cached file exceeds defined limit then it will be deleted from cache.)
 
 **NOTE:** UnlimitedDiscCache is pretty faster than other limited disk cache implementations.

7. To display bitmap (`DisplayImageOptions.displayer(...)`) you can use already prepared implementations: 
 * `RoundedBitmapDisplayer` (Displays bitmap with rounded corners)
 * `FadeInBitmapDisplayer` (Displays image with "fade in" animation)

8. To avoid list (grid, ...) scrolling lags you can use `PauseOnScrollListener`:
``` java
boolean pauseOnScroll = false; // or true
boolean pauseOnFling = true; // or false
PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);
listView.setOnScrollListener(listener);
```

9. If you see in logs some strange supplement at the end of image URL (e.g. `http://anysite.com/images/image.png_230x460`) then it doesn't mean this URL is used in requests. This is just "URL + target size", also this is key for Bitmap in memory cache. This postfix (`_230x460`) is **NOT used in requests**.


## Applications using Universal Image Loader
**[MediaHouse, UPnP/DLNA Browser](https://play.google.com/store/apps/details?id=com.dbapp.android.mediahouse)** | **[Prezzi Benzina (AndroidFuel)](https://play.google.com/store/apps/details?id=org.vernazza.androidfuel)** | **[ROM Toolbox Lite](https://play.google.com/store/apps/details?id=com.jrummy.liberty.toolbox)**, [Pro](https://play.google.com/store/apps/details?id=com.jrummy.liberty.toolboxpro) | [Stadium Astro](https://play.google.com/store/apps/details?id=com.astro.stadium.activities) | [Chef Astro](https://play.google.com/store/apps/details?id=com.sencha.test) | [Sporee - Live Soccer Scores](https://play.google.com/store/apps/details?id=com.sporee.android) | **[EyeEm - Photo Filter Camera](https://play.google.com/store/apps/details?id=com.baseapp.eyeem)** | [PhotoDownloader for Facebook](https://play.google.com/store/apps/details?id=com.giannz.photodownloader) | **[Topface - meeting is easy](https://play.google.com/store/apps/details?id=com.topface.topface)** | **[reddit is fun](https://play.google.com/store/apps/details?id=com.andrewshu.android.reddit)** | **[Diaro - personal diary](https://play.google.com/store/apps/details?id=com.pixelcrater.Diaro)** |  [WebMoney Keeper Mobile](https://play.google.com/store/apps/details?id=ru.webmoney.keeper.mobile) | **[LoL Memento League of Legends](https://play.google.com/store/apps/details?id=com.buchland.lolmemento)** | **[Meetup](https://play.google.com/store/apps/details?id=com.meetup)** | [Vingle - Magazines by Fans](https://play.google.com/store/apps/details?id=com.vingle.android) | [Anime Music Radio](https://play.google.com/store/apps/details?id=com.maxxt.animeradio) | [WidgetLocker Theme Viewer](https://play.google.com/store/apps/details?id=com.companionfree.WLThemeViewer) | [ShortBlogger for Tumblr](https://play.google.com/store/apps/details?id=com.luckydroid.tumblelog) | [SnapDish Food Camera](https://play.google.com/store/apps/details?id=com.vuzz.snapdish) | **[Twitch](https://play.google.com/store/apps/details?id=tv.twitch.android.viewer)** | [TVShow Time, TV show guide](https://play.google.com/store/apps/details?id=com.tozelabs.tvshowtime) | [Planning Center Services](https://play.google.com/store/apps/details?id=com.ministrycentered.PlanningCenter) | [Daybe - 일기가 되는 SNS](https://play.google.com/store/apps/details?id=com.daybe) | **[Lapse It](https://play.google.com/store/apps/details?id=com.ui.LapseIt)** | [My Cloud Player for SoundCloud](https://play.google.com/store/apps/details?id=com.mycloudplayers.mycloudplayer) | **[SoundTracking](https://play.google.com/store/apps/details?id=com.schematiclabs.soundtracking)** | [LoopLR Social Video](https://play.google.com/store/apps/details?id=com.looplr) | [Reddit Pics HD](https://play.google.com/store/apps/details?id=com.funpokes.redditpics) | [Hír24](https://play.google.com/store/apps/details?id=hu.sanomamedia.hir24) | **[Immobilien Scout24](https://play.google.com/store/apps/details?id=de.is24.android)** | **[Lieferheld - Pizza Pasta Sushi](https://play.google.com/store/apps/details?id=de.lieferheld.android)** | [Loocator: free sex datings](https://play.google.com/store/apps/details?id=com.ivicode.loocator) | [벨팡-개편 이벤트,컬러링,벨소리,무료,최신가요,링투유](https://play.google.com/store/apps/details?id=com.mediahubs.www) | [Streambels AirPlay/DLNA Player](https://play.google.com/store/apps/details?id=com.tuxera.streambels) | [Ship Mate - All Cruise Lines](https://play.google.com/store/apps/details?id=shipmate.carnival) | [Disk & Storage Analyzer](https://play.google.com/store/apps/details?id=com.mobile_infographics_tools.mydrive) 

## Donation
You can support the project and thank the author for his hard work :)

<a href='https://pledgie.com/campaigns/19144'><img alt='Click here to lend your support to: Universal Image Loader for Android and make a donation at pledgie.com !' src='https://pledgie.com/campaigns/19144.png?skin_name=chrome' border='0' ></a> <a href="http://flattr.com/thing/1110177/nostra13Android-Universal-Image-Loader-on-GitHub" target="_blank"><img src="http://api.flattr.com/button/flattr-badge-large.png" alt="Flattr this" title="Flattr this" border="0" /></a>
* **PayPal** - nostra.uil[at]gmail[dot]com
* **[WebMoney](http://www.webmoney.ru/)** - Z417203268219
* **[GitTip](https://www.gittip.com/nostra13/)** - nostra13

## Alternative libraries

 * [Picasso](https://github.com/square/picasso)
 * [Glide](https://github.com/bumptech/glide)

## License

If you use Universal Image Loader code in your application you should inform the author about it ( *email: nostra13[at]gmail[dot]com* ) like this:
> **Subject:** UIL usage notification<br />
> **Text:** I use Universal Image Loader &lt;lib_version> in &lt;application_name> - http://link_to_google_play.
> I [allow | don't allow] to mention my app in section "Applications using Universal Image Loader" on GitHub.

Also I'll be grateful if you mention UIL in application UI with string **"Using Universal Image Loader (c) 2011-2014, Sergey Tarasevich"** (e.g. in some "About" section).

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
