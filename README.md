# Universal Image Loader for Android

This project aims to provide a reusable instrument for asynchronous image loading, caching and displaying. It is originally based on [Fedor Vlasov's project](https://github.com/thest1/LazyList) and has been vastly refactored and improved since then.

![Screenshot](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/UniversalImageLoader.png)

## Features
 * Multithread image loading
 * Possibility of wide tuning ImageLoader's configuration (thread pool size, HTTP options, memory and disc cache, display image options, and others)
 * Possibility of image caching in memory and/or on device's file sysytem (or SD card)
 * Possibility to "listen" loading process
 * Possibility to customize every display image call with separated options
 * Widget support
 
Android 1.5+ support

## **[Downloads](https://github.com/nostra13/Android-Universal-Image-Loader/tree/master/downloads)**
 * **[universal-image-loader-1.7.0.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.7.0.jar)** (library; contains *.class files)
 * **[universal-image-loader-1.7.0-sources.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.7.0-sources.jar)** (sources; contains *.java files)
 * **[universal-image-loader-1.7.0-javadoc.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.7.0-javadoc.jar)** (Java docs; contains *.html files)
 * **[universal-image-loader-1.7.0-with-sources.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.7.0-with-sources.jar)** (library with sources inside; contains *.class and *.java files) _Prefer to use this JAR so you can see Java docs in Eclipse tooltips._
 * **[universal-image-loader-sample-1.7.0.apk](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-sample-1.7.0.apk)** (sample application)

## Documentation*
 * Universal Image Loader. Part 1 - Introduction [[RU](http://nostra13android.blogspot.com/2012/03/4-universal-image-loader-part-1.html) | [EN](http://www.intexsoft.com/blog/item/68-universal-image-loader-part-1.html)]
 * Universal Image Loader. Part 2 - Configuration [[RU](http://nostra13android.blogspot.com/2012/03/5-universal-image-loader-part-2.html) | [EN](http://www.intexsoft.com/blog/item/72-universal-image-loader-part-2.html)]
 * Universal Image Loader. Part 3 - Usage [[RU](http://nostra13android.blogspot.com/2012/03/6-universal-image-loader-part-3-usage.html) | [EN](http://www.intexsoft.com/blog/item/74-universal-image-loader-part-3.html)]

(*) a bit outdated

### [Changelog](https://github.com/nostra13/Android-Universal-Image-Loader/blob/master/CHANGELOG.md)

### [User Support](http://stackoverflow.com/questions/tagged/universal-image-loader)
 * First look at [Useful Info](https://github.com/nostra13/Android-Universal-Image-Loader#useful-info).
 * Search problem solution on [StackOverFlow](http://stackoverflow.com/questions/tagged/universal-image-loader)
 * If you didn't found the answer you can ask your own question [here](http://stackoverflow.com/questions/tagged/universal-image-loader).<br />
   Be sure to mention following information in your question:
   - your configuration (ImageLoaderConfiguration)
   - display options (DisplayImageOptions)
   - ```getView()``` method code of your adapter (if you use it)
   - XML layout of your ImageView you load image into
 * **Bugs** and **feature requests** place **[here](https://github.com/nostra13/Android-Universal-Image-Loader/issues/new)**.

## Quick Setup

#### 1. Include library

**Manual:**
 * [Download JAR](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.7.0-with-sources.jar)
 * Put the JAR in the **libs** subfolder of your Android project

or

**Maven dependency:**
``` xml
<dependency>
	<groupId>com.nostra13.universalimageloader</groupId>
	<artifactId>universal-image-loader</artifactId>
	<version>1.7.0</version>
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

### 3. Application class
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

 * ImageLoader **Configuration (`ImageLoaderConfiguration`) is global** for application. You should set it once.
 * **Display Options (`DisplayImageOptions`) are local** for every display task (`ImageLoader.displayImage(...)`).

### Configuration
All options in Configuration builder are optional. Use only those you really want to customize.<br />*See default values for config options in Java docs for every option.*
``` java
// DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of ALL options using.
File cacheDir = StorageUtils.getCacheDirectory(context);
ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.memoryCacheExtraOptions(480, 800) // default = device screen dimensions
		.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
		.threadPoolSize(3) // default
		.threadPriority(Thread.NORM_PRIORITY - 1) // default
		.denyCacheImageMultipleSizesInMemory()
		.offOutOfMemoryHandling()
		.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // default
		.discCache(new UnlimitedDiscCache(cacheDir)) // default
		.discCacheSize(50 * 1024 * 1024)
		.discCacheFileCount(100)
		.discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
		.imageDownloader(new URLConnectionImageDownloader()) // default
		.tasksProcessingOrder(QueueProcessingType.FIFO) // default
		.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
		.enableLogging()
		.build();
```

### Display Options
Display Options can be applied to every display task (`ImageLoader.displayImage(...)` call).

**Note:** If Display Options wasn't passed to `ImageLoader.displayImage(...)`method then default Display Options from configuration (`ImageLoaderConfiguration.defaultDisplayImageOptions(...)`) will be used.
``` java
// DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of ALL options using.
DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub_image)
		.showImageForEmptyUri(R.drawable.image_for_empty_url)
		.resetViewBeforeLoading()
		.cacheInMemory()
		.cacheOnDisc()
		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
		.bitmapConfig(Bitmap.Config.ARGB_8888) // default
		.delayBeforeLoading(1000)
		.displayer(new SimpleBitmapDisplayer()) // default
		.build();
```

## Usage

### Simple
``` java
// Load image, decode it to Bitmap and display Bitmap in ImageView
imageLoader.displayImage(imageUri, imageView);
```
``` java
// Load image, decode it to Bitmap and return Bitmap to callback
imageLoader.loadImage(context, imageUri, new SimpleImageLoadingListener() {
	@Override
	public void onLoadingComplete(Bitmap loadedImage) {
		// Do whatever you want with loaded Bitmap
	}
});
```

### Complete
``` java
// Load image, decode it to Bitmap and display Bitmap in ImageView
imageLoader.displayImage(imageUri, imageView, displayOptions, new ImageLoadingListener() {
	@Override
	public void onLoadingStarted() {
		...
	}
	@Override
	public void onLoadingFailed(FailReason failReason) {
		...
	}
	@Override
	public void onLoadingComplete(Bitmap loadedImage) {
		...
	}
	@Override
	public void onLoadingCancelled() {
		...
	}
});
```
``` java
// Load image, decode it to Bitmap and return Bitmap to callback
ImageSize targetSize = new ImageSize(120, 80); // result Bitmap will be fit to this size
imageLoader.loadImage(context, imageUri, targetSize, displayOptions, new SimpleImageLoadingListener() {
	@Override
	public void onLoadingComplete(Bitmap loadedImage) {
		// Do whatever you want with loaded Bitmap
	}
});
```

### ImageLoader Helpers
Other useful methods and classes to consider.
<pre>
ImageLoader |
			| - getMemoryCache()
			| - clearMemoryCache()
			| - getDiscCache()
			| - clearDiscCache()
			| - pause()
			| - resume()
			| - stop()
			| - getLoadingUriForView(ImageView)
			| - cancelDisplayTask(ImageView)

MemoryCacheUtil |
				| - findCachedBitmapsForImageUri(...)
				| - findCacheKeysForImageUri(...)
				| - removeFromCache(...)

StorageUtils |
			 | - getCacheDirectory(Context)
			 | - getIndividualCacheDirectory(Context)
			 | - getOwnCacheDirectory(Context, String)

PauseOnScrollListener
</pre>
Also look into more detailed **[Library Map](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Library-Map)**

## Useful Info
1. **Caching is NOT enabled by default.** If you want loaded images will be cached in memory and/or on disc then you should enable caching in DisplayImageOptions this way:
``` java
// Create default options which will be used for every 
//  displayImage(...) call if no options will be passed to this method
DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			...
            .cacheInMemory()
            .cacheOnDisc()
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
            .cacheInMemory()
            .cacheOnDisc()
            ...
            .build();
ImageLoader.getInstance().displayImage(imageUrl, imageView, options); // Incoming options will be used
```

2. If you enabled disc caching then UIL try to cache images on external storage (/sdcard/Android/data/[package_name]/cache). If external storage is not available then images are cached on device's filesytem.
To provide caching on external storage (SD card) add following permission to AndroidManifest.xml:
``` java
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

3. How UIL define Bitmap size needed for exact ImageView? It searches defined parameters:
 * Get ```android:layout_width``` and ```android:layout_height``` parameters
 * Get ```android:maxWidth``` and/or ```android:maxHeight``` parameters
 * Get maximum width and/or height parameters from configuration (```memoryCacheExtraOptions(int, int)``` option)
 * Get width and/or height of device screen

 So **try to set** ```android:layout_width```|```android:layout_height``` or ```android:maxWidth```|```android:maxHeight``` parameters for ImageView if you know approximate maximum size of it. It will help correctly compute Bitmap size needed for this view and **save memory**.

4. If you often got **OutOfMemoryError** in your app using Universal Image Loader then try next (all of them or several):
 - Reduce thread pool size in configuration (```.threadPoolSize(...)```). 1 - 5 is recommended.
 - Use ```.bitmapConfig(Bitmap.Config.RGB_565)``` in display options. Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.
 - Use ```.memoryCache(new WeakMemoryCache())``` in configuration or disable caching in memory at all in display options (don't call ```.cacheInMemory()```).
 - Use ```.imageScaleType(ImageScaleType.IN_SAMPLE_INT)``` in display options. Or try ```.imageScaleType(ImageScaleType.EXACTLY)```.
 - Avoid using RoundedBitmapDisplayer. It creates new Bitmap object with ARGB_8888 config for displaying during work.
 
5. For memory cache configuration (ImageLoaderConfiguration.Builder.memoryCache(...)) you can use already prepared implementations:
 * UsingFreqLimitedMemoryCache (The least frequently used bitmap is deleted when cache size limit is exceeded) - Used by default
 * LRULimitedMemoryCache (Least recently used bitmap is deleted when cache size limit is exceeded)
 * FIFOLimitedMemoryCache (FIFO rule is used for deletion when cache size limit is exceeded)
 * LargestLimitedMemoryCache (The largest bitmap is deleted when cache size limit is exceeded)
 * LimitedAgeMemoryCache (Decorator. Cached object is deleted when its age exceeds defined value)
 * WeakMemoryCache (Memory cache with only weak references to bitmaps)

6. For disc cache configuration (ImageLoaderConfiguration.Builder.discCache(...)) you can use already prepared implementations:
 * UnlimitedDiscCache (The fastest cache, doesn't limit cache size) - Used by default
 * TotalSizeLimitedDiscCache (Cache limited by total cache size. If cache size exceeds specified limit then file with the most oldest last usage date will be deleted)
 * FileCountLimitedDiscCache (Cache limited by file count. If file count in cache directory exceeds specified limit then file with the most oldest last usage date will be deleted. Use it if your cached files are of about the same size.)
 * LimitedAgeDiscCache (Size-unlimited cache with limited files' lifetime. If age of cached file exceeds defined limit then it will be deleted from cache.)
 
 **NOTE:** UnlimitedDiscCache is 30%-faster than other limited disc cache implementations.

7. To display bitmap (DisplayImageOptions.Builder.displayer(...)) you can use already prepared implementations: 
 * RoundedBitmapDisplayer (Displays bitmap with rounded corners)
 * FadeInBitmapDisplayer (Displays image with "fade in" animation)

8. To avoid list (grid, ...) scrolling lags you can use ```PauseOnScrollListener```:
``` java
boolean pauseOnScroll = false; // or true
boolean pauseOnFling = true; // or false
PauseOnScrollListener listener = new PauseOnScrollListener(pauseOnScroll, pauseOnFling);
listView.setOnScrollListener(listener);
```
 
## Applications using Universal Image Loader
**[MediaHouse, UPnP/DLNA Browser](https://play.google.com/store/apps/details?id=com.dbapp.android.mediahouse)** | [Деловой Киров](https://play.google.com/store/apps/details?id=ru.normakirov.dknorma) | [Бизнес-завтрак](https://play.google.com/store/apps/details?id=ru.normakirov.businesslunch) | [Menu55](http://www.free-lance.ru/users/max475imus/viewproj.php?prjid=3152141) | [SpokenPic](http://spokenpic.com) | [Kumir](https://play.google.com/store/apps/details?id=ru.premiakumir.android) | [EUKO 2012](https://play.google.com/store/apps/details?id=de.netlands.emsapp) | [TuuSo Image Search](https://play.google.com/store/apps/details?id=com.tuuso) | [Газета Стройка](https://play.google.com/store/apps/details?id=ru.normakirov.stroyka) | **[Prezzi Benzina (AndroidFuel)](https://play.google.com/store/apps/details?id=org.vernazza.androidfuel)** | [Quiz Guess The Guy] (https://play.google.com/store/apps/details?id=com.game.guesstheguy) | [Volksempfänger (alpha)](http://volksempfaenger.0x4a42.net) | **[ROM Toolbox Lite](https://play.google.com/store/apps/details?id=com.jrummy.liberty.toolbox), [Pro](https://play.google.com/store/apps/details?id=com.jrummy.liberty.toolboxpro)** | [London 2012 Games](https://play.google.com/store/apps/details?id=com.mbwasi.london) | [카톡 이미지 - 예쁜 프로필 이미지](https://play.google.com/store/apps/details?id=com.bydoori.firstbasea) | [dailyPen](https://play.google.com/store/apps/details?id=com.bydoori.dailypen) | [Mania!](https://play.google.com/store/apps/details?id=com.astro.mania.activities) | **[Stadium Astro](https://play.google.com/store/apps/details?id=com.astro.stadium.activities)** | **[Chef Astro](https://play.google.com/store/apps/details?id=com.sencha.test)** | [Lafemme Fashion Finder](https://play.google.com/store/apps/details?id=me.getlafem.lafemme2) | [FastPaleo](https://play.google.com/store/apps/details?id=com.mqmobile.droid.fastpaleo) | **[Sporee - Live Soccer Scores](https://play.google.com/store/apps/details?id=com.sporee.android)** | [friendizer](https://play.google.com/store/apps/details?id=com.teamagly.friendizer) | [LowPrice lowest book price](https://play.google.com/store/apps/details?id=com.binarybricks.lowprice) | [bluebee](https://play.google.com/store/apps/details?id=mobi.bluebee.android.app) | [Game PromoBox](https://play.google.com/store/apps/details?id=com.gamepromobox) | **[EyeEm - Photo Filter Camera](https://play.google.com/store/apps/details?id=com.baseapp.eyeem)** | [Festival Wallpaper](https://play.google.com/store/apps/details?id=com.cs.fwallpaper) | [Gaudi Hall](https://play.google.com/store/apps/details?id=ru.normakirov.gaudihall) | [Spocal](https://play.google.com/store/apps/details?id=net.spocal.android) | [PhotoDownloader for Facebook](https://play.google.com/store/apps/details?id=com.giannz.photodownloader) | [Вкладыши](https://play.google.com/store/apps/details?id=com.banjen.app.gumimages) | [Dressdrobe](https://play.google.com/store/apps/details?id=com.dressdrobe.mario) | [mofferin](https://play.google.com/store/apps/details?id=com.mmobile.mofferin) | [WordBoxer](http://www.wordboxer.com/) | [EZ Imgur](https://play.google.com/store/apps/details?id=com.ezimgur) | [Ciudad en línea](https://play.google.com/store/apps/details?id=com.aliadosweb.android.cel) | [Urbanismo en línea](https://play.google.com/store/apps/details?id=com.aliadosweb.android.opel) | [Waypost](https://play.google.com/store/apps/details?id=com.brushfire.waypost) | [Moonrise Kingdom Wallpapers HD](https://play.google.com/store/apps/details?id=net.dnlk.moonrisekingdom.gallery) | [Chic or Shock?](https://play.google.com/store/apps/details?id=com.chicorshock) | [Auto Wallpapers](https://play.google.com/store/apps/details?id=ru.evgsd.autowallpapers) | [Heyou](https://play.google.com/store/apps/details?id=heyou.pythagorapps.heyou) | [Brasil Notícias](https://play.google.com/store/apps/details?id=com.acerolamob.android.brasilnoticias) | [ProfiAuto’s VideoBlog](https://play.google.com/store/apps/details?id=pl.profiauto.android.videoblog) | [CarteleraApp (Cine)](https://play.google.com/store/apps/details?id=com.jcminarro.android.tools), [AdsFree](https://play.google.com/store/apps/details?id=com.jcminarro.android.tools.carteleraApp) | [Listonic - Zamów Zakupy](https://play.google.com/store/apps/details?id=com.listonic.shop) | **[Topface - meeting is easy](https://play.google.com/store/apps/details?id=com.topface.topface)** | [Name The Meme](https://play.google.com/store/apps/details?id=it.fi.appstyx.namethememe) | [Name The World](https://play.google.com/store/apps/details?id=it.fi.appstyx.nametheworld) | [Pregnancy Tickers - Widget](https://play.google.com/store/apps/details?id=com.romkuapps.tickers) | [Hindi Movies & More](https://play.google.com/store/apps/details?id=info.mediatree.hindimoviesandmore) | [Telugu Movies & More](https://play.google.com/store/apps/details?id=info.mediatree.telugu) | [Jessica Alba HD Wallpaper](https://play.google.com/store/apps/details?id=com.maxtra_jessicaalba) | [User Manager ROOT Android 4.2](https://play.google.com/store/apps/details?id=com.ramdroid.usermanagerpro) | [DNSHmob](https://play.google.com/store/apps/details?id=era.ndroid.dnshmob) | [Theke](https://play.google.com/store/apps/details?id=com.sh.theke) | [SensibleJournal](https://play.google.com/store/apps/details?id=dk.dtu.imm.sensiblejournal) | [PiCorner for Flickr, Instagram](https://play.google.com/store/apps/details?id=com.gmail.charleszq.picorner) | [Survey-n-More - Paid Surveys](https://play.google.com/store/apps/details?id=com.surveynmore.paidsurveyapp) | [STROBEL Verlag Basic](https://play.google.com/store/apps/details?id=de.nexoma.android.strobel.basic) | **[reddit is fun](https://play.google.com/store/apps/details?id=com.andrewshu.android.reddit)**, [golden platinum](https://play.google.com/store/apps/details?id=com.andrewshu.android.redditdonation) | [iDukan Diet Tracker](https://play.google.com/store/apps/details?id=com.harptreesoftware.idukan)

## Donation
You can support the project and thank the author for his hard work :)
* **[GitTip](https://www.gittip.com/nostra13/)**
* **[WebMoney](http://www.webmoney.ru/)** (Z417203268219)

## License
Copyright (c) 2011, [Sergey Tarasevich](http://nostra13android.blogspot.com)

If you use Universal Image Loader code in your application you must inform the author about it ( *email: nostra13[at]gmail[dot]com* ) like this:
> I use Universal Image Loader in [ApplicationName] - http://link_to_google_play.
> I [allow|don't allow] to mention my app in "Applications using Universal Image Loader" on GitHub.

Also you should mention it (but it is not required) in application UI with string **"Used Universal-Image-Loader (c) 2011, Sergey Tarasevich"** (e.g. in some "About" section).

Licensed under the [BSD 3-clause](http://www.opensource.org/licenses/BSD-3-Clause)
