# ![Logo](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/sample/src/main/res/drawable-mdpi/ic_launcher.png) Universal Image Loader [![Build Status](https://travis-ci.org/nostra13/Android-Universal-Image-Loader.svg?branch=master)](https://travis-ci.org/nostra13/Android-Universal-Image-Loader) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nostra13.universalimageloader/universal-image-loader/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.nostra13.universalimageloader/universal-image-loader)

Android library **[#1](https://www.gitrep.com/search?utf8=✓&omni_search=&public_tags%5B%5D=android&description=&search=true&sort=star_count&commit=Search)** on GitHub.
UIL aims to provide a powerful, flexible and highly customizable instrument for image loading, caching and displaying. It provides a lot of configuration options and good control over the image loading and caching process.

![Screenshot](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/UniversalImageLoader.png)

## Project News 
 * Really have no time for development... so I stop project maintaining since Nov 27 :(
 * UIL [27.11.2011 - 27.11.2015]
 * Thanks to all developers for your support :)

## Features
 * Multithread image loading (async or sync)
 * Wide customization of ImageLoader's configuration (thread executors, downloader, decoder, memory and disk cache, display image options, etc.)
 * Many customization options for every display image call (stub images, caching switch, decoding options, Bitmap processing and displaying, etc.)
 * Image caching in memory and/or on disk (device's file system or SD card)
 * Listening loading process (including downloading progress)

Android 2.0+ support

## Downloads
 * **[universal-image-loader-1.9.5.jar](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-1.9.5.jar)**
 * [<img src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" height="45px" />](https://play.google.com/store/apps/details?id=com.nostra13.universalimageloader.sample) [![QR Code](https://lh3.ggpht.com/csXEddxiLgQ6FxckefjQnP1PVugbaAYOdcuTa3vVtGV1PlWbFu2dYggoH8rI1w2RdEz1=w50)](http://chart.apis.google.com/chart?chs=300x300&cht=qr&chld=|1&chl=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.nostra13.universalimageloader.sample) [<img src="http://mobway.in/image/apk.png" height="45px" />](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/downloads/universal-image-loader-sample-1.9.5.apk)

## [Documentation](https://github.com/nostra13/Android-Universal-Image-Loader/wiki)
 * **[Quick Setup](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Quick-Setup)**
 * **[Configuration](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Configuration)**
 * **[Display Options](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Display-Options)**
 * [Useful Info](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Useful-Info) - Read it before asking a question
 * [User Support](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/User-Support) - Read it before creating new issue
 * [Sample project](https://github.com/nostra13/Android-Universal-Image-Loader/tree/master/sample) - Learn it to understand the right way of library usage
 * [ChangeLog](https://github.com/nostra13/Android-Universal-Image-Loader/blob/master/CHANGELOG.md) - Info about API changes is here

## Usage

### Acceptable URIs examples
``` java
"http://site.com/image.png" // from Web
"file:///mnt/sdcard/image.png" // from SD card
"file:///mnt/sdcard/video.mp4" // from SD card (video thumbnail)
"content://media/external/images/media/13" // from content provider
"content://media/external/video/media/13" // from content provider (video thumbnail)
"assets://image.png" // from assets
"drawable://" + R.drawable.img // from drawables (non-9patch images)
```
**NOTE:** Use `drawable://` only if you really need it! Always **consider the native way** to load drawables - `ImageView.setImageResource(...)` instead of using of `ImageLoader`.

### Simple
``` java
ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
```
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

## Load & Display Task Flow
![Task Flow](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/wiki/UIL_Flow.png)


## Applications using Universal Image Loader
**[MediaHouse, UPnP/DLNA Browser](https://play.google.com/store/apps/details?id=com.dbapp.android.mediahouse)** | **[Prezzi Benzina (AndroidFuel)](https://play.google.com/store/apps/details?id=org.vernazza.androidfuel)** | **[ROM Toolbox Lite](https://play.google.com/store/apps/details?id=com.jrummy.liberty.toolbox)**, [Pro](https://play.google.com/store/apps/details?id=com.jrummy.liberty.toolboxpro) | [Stadium Astro](https://play.google.com/store/apps/details?id=com.astro.stadium.activities) | [Chef Astro](https://play.google.com/store/apps/details?id=com.sencha.test) | [Sporee - Live Soccer Scores](https://play.google.com/store/apps/details?id=com.sporee.android) | **[EyeEm - Photo Filter Camera](https://play.google.com/store/apps/details?id=com.baseapp.eyeem)** | **[Topface - meeting is easy](https://play.google.com/store/apps/details?id=com.topface.topface)** | **[reddit is fun](https://play.google.com/store/apps/details?id=com.andrewshu.android.reddit)** | **[Diaro - personal diary](https://play.google.com/store/apps/details?id=com.pixelcrater.Diaro)** | **[Meetup](https://play.google.com/store/apps/details?id=com.meetup)** | [Vingle - Magazines by Fans](https://play.google.com/store/apps/details?id=com.vingle.android) | [Anime Music Radio](https://play.google.com/store/apps/details?id=com.maxxt.animeradio) | [WidgetLocker Theme Viewer](https://play.google.com/store/apps/details?id=com.companionfree.WLThemeViewer) | [ShortBlogger for Tumblr](https://play.google.com/store/apps/details?id=com.luckydroid.tumblelog) | [SnapDish Food Camera](https://play.google.com/store/apps/details?id=com.vuzz.snapdish) | **[Twitch](https://play.google.com/store/apps/details?id=tv.twitch.android.viewer)** | [TVShow Time, TV show guide](https://play.google.com/store/apps/details?id=com.tozelabs.tvshowtime) | [Planning Center Services](https://play.google.com/store/apps/details?id=com.ministrycentered.PlanningCenter) | **[Lapse It](https://play.google.com/store/apps/details?id=com.ui.LapseIt)** | [My Cloud Player for SoundCloud](https://play.google.com/store/apps/details?id=com.mycloudplayers.mycloudplayer) | **[SoundTracking](https://play.google.com/store/apps/details?id=com.schematiclabs.soundtracking)** | [LoopLR Social Video](https://play.google.com/store/apps/details?id=com.looplr) | [Hír24](https://play.google.com/store/apps/details?id=hu.sanomamedia.hir24) | **[Immobilien Scout24](https://play.google.com/store/apps/details?id=de.is24.android)** | **[Lieferheld - Pizza Pasta Sushi](https://play.google.com/store/apps/details?id=de.lieferheld.android)** | [Loocator: free sex datings](https://play.google.com/store/apps/details?id=com.ivicode.loocator) | [벨팡-개편 이벤트,컬러링,벨소리,무료,최신가요,링투유](https://play.google.com/store/apps/details?id=com.mediahubs.www) | [Streambels AirPlay/DLNA Player](https://play.google.com/store/apps/details?id=com.tuxera.streambels) | [Ship Mate - All Cruise Lines](https://play.google.com/store/apps/details?id=shipmate.carnival) | [Disk & Storage Analyzer](https://play.google.com/store/apps/details?id=com.mobile_infographics_tools.mydrive) | [糗事百科](https://play.google.com/store/apps/details?id=qsbk.app) | [Balance BY](https://play.google.com/store/apps/details?id=com.vladyud.balance) | **[Anti Theft Alarm - Security](https://play.google.com/store/apps/details?id=br.com.verde.alarme)** | **[XiiaLive™ - Internet Radio](https://play.google.com/store/apps/details?id=com.android.DroidLiveLite)** | **[Bandsintown Concerts](https://play.google.com/store/apps/details?id=com.bandsintown)** | **[Save As Web Archive](https://play.google.com/store/apps/details?id=jp.fuukiemonster.webmemo)** | [MCPE STORE -Download MCPE file](https://play.google.com/store/apps/details?id=com.newidea.mcpestore) | **[All-In-One Toolbox (29 Tools)](http://aiotoolbox.com/)** | [Zaim](https://play.google.com/store/apps/details?id=net.zaim.android) | **[Calculator Plus Free](https://play.google.com/store/apps/details?id=com.digitalchemy.calculator.freedecimal)** | [Truedialer by Truecaller](https://play.google.com/store/apps/details?id=com.truecaller.phoneapp) | [DoggCatcher Podcast Player](https://play.google.com/store/apps/details?id=com.snoggdoggler.android.applications.doggcatcher.v1_0) | [PingTools Network Utilities](https://play.google.com/store/apps/details?id=ua.com.streamsoft.pingtools) | [The Traveler](https://play.google.com/store/apps/details?id=edu.bsu.android.apps.traveler) | [minube: travel photo album](https://play.google.com/store/apps/details?id=com.minube.app) | [Wear Store for Wear Apps](https://play.google.com/store/apps/details?id=goko.ws2) | [Cast Store for Chromecast Apps](https://play.google.com/store/apps/details?id=goko.gcs) | [WebMoney Keeper](https://play.google.com/store/apps/details?id=com.webmoney.my)

## Donation
You can support the project and thank the author for his hard work :)

<a href='https://pledgie.com/campaigns/19144'><img alt='Click here to lend your support to: Universal Image Loader for Android and make a donation at pledgie.com !' src='https://pledgie.com/campaigns/19144.png?skin_name=chrome' border='0' ></a> <a href="http://flattr.com/thing/1110177/nostra13Android-Universal-Image-Loader-on-GitHub" target="_blank"><img src="http://api.flattr.com/button/flattr-badge-large.png" alt="Flattr this" title="Flattr this" border="0" /></a>
* **PayPal** - nostra.uil[at]gmail[dot]com

## Alternative libraries

 * [AndroidQuery : ImageLoading](https://code.google.com/p/android-query/wiki/ImageLoading)
 * [DroidParts : ImageFetcher](http://droidparts.org/image_fetcher.html)
 * [Fresco](https://github.com/facebook/fresco)
 * [Glide](https://github.com/bumptech/glide)
 * [Picasso](https://github.com/square/picasso)
 * [UrlImageViewHelper](https://github.com/koush/UrlImageViewHelper)
 * [Volley : ImageLoader](https://android.googlesource.com/platform/frameworks/volley/)

## License

    Copyright 2011-2015 Sergey Tarasevich

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
