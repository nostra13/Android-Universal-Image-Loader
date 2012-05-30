# Universal Image Loader for Android

This project aims to provide a reusable instrument for asynchronous image loading, caching and displaying. It is originally based on [Fedor Vlasov's project](https://github.com/thest1/LazyList) and has been vastly refactored and improved since then.

![Screenshot](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/UniversalImageLoader.png)

## Features
 * Multithread image loading
 * Possibility of wide tuning ImageLoader's configuration (thread pool size, HTTP options, memory and disc cache, display image options, and others)
 * Possibility of image caching in memory and/or on device's file sysytem (or SD card)
 * Possibility to "listen" loading process
 * Possibility to customize every display image call with separated options

## Documentation
 * Universal Image Loader. Part 1 - Introduction [[RU](http://nostra13android.blogspot.com/2012/03/4-universal-image-loader-part-1.html) | [EN](http://www.intexsoft.com/blog/item/68-universal-image-loader-part-1.html)]
 * Universal Image Loader. Part 2 - Configuration [[RU](http://nostra13android.blogspot.com/2012/03/5-universal-image-loader-part-2.html) | [EN](http://www.intexsoft.com/blog/item/72-universal-image-loader-part-2.html)]
 * Universal Image Loader. Part 3 - Usage [[RU](http://nostra13android.blogspot.com/2012/03/6-universal-image-loader-part-3-usage.html) | [EN](http://www.intexsoft.com/blog/item/74-universal-image-loader-part-3.html)]
 
## Usage

### Simple

``` java
ImageView imageView = ...
String imageUrl = "http://site.com/image.png"; // or "file:///mnt/sdcard/images/image.jpg"

// Get singletone instance of ImageLoader
ImageLoader imageLoader = ImageLoader.getInstance();
// Initialize ImageLoader with configuration. Do it once.
imageLoader.init(ImageLoaderConfiguration.createDefault(context));
// Load and display image asynchronously
imageLoader.displayImage(imageUrl, imageView);
```

### Most detailed
``` java
ImageView imageView = ...
String imageUrl = "http://site.com/image.png"; // or "file:///mnt/sdcard/images/image.jpg"
ProgressBar spinner = ...
File cacheDir = new File(Environment.getExternalStorageDirectory(), "UniversalImageLoader/Cache");

// Get singletone instance of ImageLoader
ImageLoader imageLoader = ImageLoader.getInstance();
// Create configuration for ImageLoader (all options are optional)
ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.maxImageWidthForMemoryCache(480)
			.maxImageHeightForMemoryCache(800)
			.threadPoolSize(5)
			.threadPriority(Thread.MIN_PRIORITY + 2)
			.denyCacheImageMultipleSizesInMemory()
			.offOutOfMemoryHandling()
			.memoryCache(new UsingFreqLimitedCache(2000000)) // You can pass your own memory cache implementation
			.discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
			.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
			.imageDownloader(new DefaultImageDownloader(5000, 30000)) // connectTimeout (5 s), readTimeout (30 s)
			.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
			.build();
// Initialize ImageLoader with created configuration. Do it once.
imageLoader.init(config);

// Creates display image options for custom display task (all options are optional)
DisplayImageOptions options = new DisplayImageOptions.Builder()
                                       .showStubImage(R.drawable.stub_image)
									   .showImageForEmptyUrl(R.drawable.image_for_empty_url)
                                       .cacheInMemory()
                                       .cacheOnDisc()
									   .decodingType(DecodingType.MEMORY_SAVING)
                                       .build();
// Load and display image
imageLoader.displayImage(imageUrl, imageView, options, new ImageLoadingListener() {
    @Override
    public void onLoadingStarted() {
       spinner.show();
    }
	@Override
	public void onLoadingFailed(FailReason failReason) {
		spinner.hide();
	}
    @Override
    public void onLoadingComplete() {
        spinner.hide();
    }
	@Override
    public void onLoadingCancelled() {
        // Do nothing
    }
});
```

## Useful info
For memory cache configuration (ImageLoaderConfiguration.Builder.memoryCache(...)) you can use already prepared implementations:

 * UsingFreqLimitedMemoryCache (The least frequently used bitmap is deleted when cache size limit is exceeded) - Used by default
 * LRULimitedMemoryCache (Least recently used bitmap is deleted when cache size limit is exceeded)
 * FIFOLimitedMemoryCache (FIFO rule is used for deletion when cache size limit is exceeded)
 * LargestLimitedMemoryCache (The largest bitmap is deleted when cache size limit is exceeded)
 * LimitedAgeMemoryCache (Decorator. Cached object is deleted when its age exceeds defined value)

For disc cache configuration (ImageLoaderConfiguration.Builder.discCache(...)) you can use already prepared implementations:

 * UnlimitedDiscCache (The fastest cache, doesn't limit cache size) - Used by default
 * TotalSizeLimitedDiscCache (Cache limited by total cache size. If cache size exceeds specified limit then file with the most oldest last usage date will be deleted)
 * FileCountLimitedDiscCache (Cache limited by file count. If file count in cache directory exceeds specified limit then file with the most oldest last usage date will be deleted. Use it if your cached files are of about the same size.)
 * LimitedAgeDiscCache (Unlimited cache with limited files' lifetime. If age of cached file exceeds defined limit then it will be deleted from cache.)

## Applications using Universal Image Loader
* [MediaHouse, UPnP/DLNA Browser](https://play.google.com/store/apps/details?id=com.dbapp.android.mediahouse)
* [Деловой Киров](https://play.google.com/store/apps/details?id=ru.normakirov.dknorma)
* [Бизнес-завтрак](https://play.google.com/store/apps/details?id=ru.normakirov.businesslunch)
* [Menu55](http://www.free-lance.ru/users/max475imus/viewproj.php?prjid=3152141)
* [SpokenPic](http://spokenpic.com)

## License
Copyright (c) 2011-2012, [Sergey Tarasevich](http://nostra13android.blogspot.com)

If you use Universal Image Loader code in your application you have to inform the author about it (*email: nostra13[at]gmail[dot]com*). Also you should (but you don't have to) mention it in application UI with string **"Used Universal-Image-Loader (c) 2011-2012, Sergey Tarasevich"** (e.g. in some "About" section).

Licensed under the [BSD 3-clause](http://www.opensource.org/licenses/BSD-3-Clause)