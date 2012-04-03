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
// Create configuration for ImageLoader
ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.maxImageWidthForMemoryCache(800)
			.maxImageHeightForMemoryCache(800)
			.httpConnectTimeout(5000)
			.httpReadTimeout(30000)
			.threadPoolSize(5)
			.threadPriority(Thread.MIN_PRIORITY + 2)
			.denyCacheImageMultipleSizesInMemory()
			.memoryCache(new UsingFreqLimitedCache(2000000)) // You can pass your own memory cache implementation
			.discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
			.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
			.build();
// Initialize ImageLoader with created configuration. Do it once.
imageLoader.init(config);

// Creates display image options for custom display task
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
	public void onLoadingFailed() {
		spinner.hide();
	}
    @Override
    public void onLoadingComplete() {
        spinner.hide();
    }
});
```

## Useful info
For memory cache configuration (ImageLoaderConfiguration.Builder.memoryCache(...)) you can use already prepared implementations:

 * UsingFreqLimitedCache (The least frequently used bitmap is deleted when cache size limit is exceeded) - Used by default
 * UsingAgeLimitedCache (Bitmap with the oldest using time is deleted when cache size limit is exceeded)
 * FIFOLimitedCache (FIFO rule is used for deletion when cache size limit is exceeded)
 * LargestLimitedCache (The largest bitmap is deleted when cache size limit is exceeded)
 
 For disc cache configuration (ImageLoaderConfiguration.Builder.discCache(...)) you can use already prepared implementations:

 * UnlimitedDiscCache (The fastest cache, doesn't limit cache size) - Used by default
 * TotalSizeLimitedDiscCache (Cache limited by total cache size. If cache size exceeds specified limit then file with the most oldest last usage date will be deleted)
 * FileCountLimitedDiscCache (Cache limited by file count. If file count in cache directory exceeds specified limit then file with the most oldest last usage date will be deleted. Use it if your cached files are of about the same size.)

## License
Copyright (c) 2011-2012 [Sergey Tarasevich](http://nostra13android.blogspot.com)

Licensed under the [BSD 3-clause](http://www.opensource.org/licenses/BSD-3-Clause)