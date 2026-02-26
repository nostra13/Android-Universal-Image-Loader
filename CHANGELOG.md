Change Log
===

v1.9.5 *(27.11.2015)*
---
 * **New API:**
   * `ImageLoader.displayImage(..., ImageSize targetImageSize, ...)`
 * CircleBitmapDisplayer
 * Better rendering of scaled images.
 * Fixed bugs:
   * inPurgeable and inInputShareable causes file descriptor leak on KitKat ([#1020](https://github.com/nostra13/Android-Universal-Image-Loader/issues/1020))
   * markSupported() ([#1026](https://github.com/nostra13/Android-Universal-Image-Loader/issues/1026))

v1.9.4 *(29.05.2015)*
---
 * **New API:**
   * `ImageLoader.setDefaultLoadingListener(ImageLoadingListener)`
 * "Disc -> Disk" migration (deleted DiscCacheAware, MemoryCacheAware)
 * Video thumbnails support (`file://...`)
 * Fixed 0-length files problem

v1.9.3 *(06.09.2014)*
---
 * Introduced `ImageScaleType.NONE_SAFE`
 * Video thumbnails support (`content://...`)
 * Animated drawables support (for `.showImageOnLoading()`, `.showImageOnFail()`, `.showImageForEmptyUri()`)
 * Fixed bugs:
   * `loadImageSync(...)` bug ([#636](https://github.com/nostra13/Android-Universal-Image-Loader/issues/636))
   * NPE if no free space while init disk cache
   * "Bitmap too large ..." for all ImageScaleTypes
   * contacts photo considering

v1.9.2 *(24.05.2014)*
---
 * New Disk cache API (preparing renaming `disc` -> `disk`)
 * ImageLoader can be called out of the Main thread. Callback will be delivered on separate thread.
 * Prevented broken image files (#511)
 * Interrupt non-actual tasks
 * `LruDiscCache` is default limited cache
 * Renaming: `ImageNonViewAware` -> `NonViewAware`. Extracted `ViewAware` from `ImageViewAware`.
 * Introduced `DiskCache` and `MemoryCache` interfaces instead of deprecated `DiscCacheAware` and `MemoryCacheAware`.
 * Removed `LimitedDiscCache`, `TotalSizeLimitedDiscCache`, `FileCountLimitedDiscCache`. Use `LruDisckCache` instead.

v1.9.1 *(27.12.2013)*
---
 * **Changed API:**
   * `BitmapDisplayer.display(...) : Bitmap` -> `BitmapDisplayer.display(...) : void`
 * **New API:**
   * Added possibility to listen image loading progress by listener - `ImageLoadingProgressListener`
 * Non-actual downloads are interrupted (if loaded less than 75%)
 * Re-designed `RoundedBitmapDisplayer`. Added `RoundedVignetteBitmapDisplayer`.
   **NOTE:** New `RoundedBitmapDisplayer`'s behaviour can vary from old one. Also consider ["RoundedImageView" project](https://github.com/vinc3m1/RoundedImageView) for usage if new `RoundedBitmapDisplayer` doesn't work for you.
 * Maximum GL texture size is considered while decode images ([#281](https://github.com/nostra13/Android-Universal-Image-Loader/issues/281))
 * `loadImage(...)` call cancels previous task for the same image URI ([#475](https://github.com/nostra13/Android-Universal-Image-Loader/issues/475))
 * Fixed StrictMode warning `Explicit termination method 'close' not called` ([#482](https://github.com/nostra13/Android-Universal-Image-Loader/issues/482))
 * `LruMemoryCache` is default memory cache for Android < 2.3 too.

v1.9.0 *(27.11.2013)*
---
 * **Changed API:**
   * `BitmapDisplayer.display(..., ImageView, ...)` -> `BitmapDisplayer.display(..., ImageAware, ...)`
 * **New API:**
   * `ImageAware`
   * `ImageLoader.displayImage(..., ImageAware, ...)`
   * `ImageLoader.loadImageSync(...) : Bitmap` for synchronous loading of image
   * `DisplayImageOptions.considerExifParams(boolean)`
   * `DisplayImageOptions.showImageOnLoading(...)` (instead of `.showStubImage(...)`)
 * `ImageLoader` can process any view (or any other object) which implements `ImageAware` interface. E.g. `ImageViewAware` is adapter of `ImageView` for `ImageAware`
 * EXIF parameters of image are not considered by default anymore. Use new API to enable it.
 * Optimized image loading (prevented double-request on image loading, reuse existing stream)
 * Fixed bugs:
   * `loadImage(...)` bug (frequent `onLoadingCancelled()`) ([#356](https://github.com/nostra13/Android-Universal-Image-Loader/issues/356))
   * Prevented NPE if `Context.getCacheDir()` returns `null` ([#392](https://github.com/nostra13/Android-Universal-Image-Loader/issues/392))

v1.8.6 *(25.07.2013)*
---
 * **Changed API:** `ImageLoaderConfiguration.enableLogging()` -> `ImageLoaderConfiguration.writeDebugLogs()`
 * **Fixed memory leak** ([#263](https://github.com/nostra13/Android-Universal-Image-Loader/issues/263))
 * Added the bug of `loadImage(...)` method (`onLoadingCancelled()` is fired always) :) 

v1.8.5 *(30.06.2013)*
---
 * **Changed API:** `ImageLoaderConfiguration.discCacheExtraOptions(...)` -> `ImageLoaderConfiguration.discCacheExtraOptions(..., BitmapProcessor)` ([#314](https://github.com/nostra13/Android-Universal-Image-Loader/issues/314))
 * **New API:**
   * `ImageLoaderConfiguration.memoryCacheSizePercentage(int)` ([#279](https://github.com/nostra13/Android-Universal-Image-Loader/issues/279))
   * `DisplayImageOptions.cacheInMemory(boolean)`, `.cacheOnDisc(boolean)`, `.resetViewBeforeLoading(boolean)` ([#252](https://github.com/nostra13/Android-Universal-Image-Loader/issues/252))
 * Added `LoadedFrom` flag to `BitmapDisplayer.display(..., LoadedFrom)` about image source  ([#149](https://github.com/nostra13/Android-Universal-Image-Loader/issues/149), [#239](https://github.com/nostra13/Android-Universal-Image-Loader/issues/239))
 * Added `L.disableLogging()` and `L.enableLogging()` to off/on logs completely ([#270](https://github.com/nostra13/Android-Universal-Image-Loader/issues/270))
 * Prevent image decoding if image is reused ([#247](https://github.com/nostra13/Android-Universal-Image-Loader/issues/247))
 * Not set cache dir on SD card if no appropriate permission ([#311](https://github.com/nostra13/Android-Universal-Image-Loader/issues/311))
 * Increased buffer size for image downloads (8 KB -> 32 KB) ([#249](https://github.com/nostra13/Android-Universal-Image-Loader/issues/249))
 * Fixed bugs:
   * Prevent recycling of cached in memory images ([#259](https://github.com/nostra13/Android-Universal-Image-Loader/issues/259))
   * ConcurrentModificationException in `LruMemoryCache` ([#265](https://github.com/nostra13/Android-Universal-Image-Loader/issues/265))
   * File counting if cached files disappeared `LimitedDiscCache` ([#316](https://github.com/nostra13/Android-Universal-Image-Loader/issues/316))    
   * NPE for ImageView without LayoutParams ([#272](https://github.com/nostra13/Android-Universal-Image-Loader/issues/272))
   * NPE in `LoadAndDisplayImageTask` ([#271](https://github.com/nostra13/Android-Universal-Image-Loader/issues/271))
   * NPE in ImageLoaderEngine ([#301](https://github.com/nostra13/Android-Universal-Image-Loader/issues/301))
   * RoundedBitmapDisplayer doesn't display round corner correctly for CENTER_CROP ([#315](https://github.com/nostra13/Android-Universal-Image-Loader/issues/315))

v1.8.4 *(13.04.2013)*
---
 * Travis CI, added Unit tests ([#189](https://github.com/nostra13/Android-Universal-Image-Loader/issues/189))
 * **New API:** `DisplayImageOptions.handler(Handler)` ([#231](https://github.com/nostra13/Android-Universal-Image-Loader/issues/231))
 * Fixed bugs:
   * `ConcurrentModificationException` in `BaseMemoryCache` ([#229](https://github.com/nostra13/Android-Universal-Image-Loader/issues/229))
   * `NullPointerException` in `LimitedDiscCache` ([#234](https://github.com/nostra13/Android-Universal-Image-Loader/issues/234))
   * `NullPointerException` in `LruMemoryCache` ([#233](https://github.com/nostra13/Android-Universal-Image-Loader/issues/233))
 * Improved work with Strings on UI thread ([#244](https://github.com/nostra13/Android-Universal-Image-Loader/issues/244))

v1.8.3 *(31.03.2013)*
---
 * Android 2.0+ support
 * Added EXIF orientation support ([#172](https://github.com/nostra13/Android-Universal-Image-Loader/issues/172))
 * **New API:** 
   * `ImageLoaderConfiguration.imageDecoder(ImageDecoder)`
   * `DisplayImageOptions.decodingOptions(BitmapFactory.Options)`
 * Handled disc cache non-availability
 * Use `LruMemoryCache` as default memory cache for API >= 9, `LRULimitedMemoryCache` - for API < 9. Default memory cache size - 1/8 of available app memory.
 * Improved `LimitedDiscCache` and `FuzzyKeyMemoryCache` performance
 * Fixed bugs:
    * `.denyCacheImageMultipleSizesInMemory` doesn't work if own memory cache is set
	* `java.lang.NoSuchMethodError` in sample app ([#206](https://github.com/nostra13/Android-Universal-Image-Loader/issues/206))

v1.8.2 *(13.03.2013)*
---
 * **Changed API:**
   * `ImageDownloader.getStream***(URI, ...)` -> `ImageDownloader.getStream***(String, ...)`
   * Made `FailReason` as a class instead of enum. Can be used in switches: `FailReason.getType()`
   * Removed `ImageLoader.offOutOfMemoryHandling()`. ImageLoader doesn't handle OutOfMemoryError by default anymore (but still catches it for callbacks).
 * **New API:**
   * `ImageLoader.taskExecutor(Executor)` and `ImageLoader.taskExecutorForCachedImages(Executor)` ([#187](https://github.com/nostra13/Android-Universal-Image-Loader/issues/187))
   * `ImageLoader.destroy()`
 * Handled SD card unmount ([#170](https://github.com/nostra13/Android-Universal-Image-Loader/issues/170))
 * Added `Scheme` class
 * Fixed bugs:
   * problem of loading of local files with encoded symbols in path ([#179](https://github.com/nostra13/Android-Universal-Image-Loader/issues/179))
   * minor mistake in `getImageSizeScaleTo()` method ([#200](https://github.com/nostra13/Android-Universal-Image-Loader/issues/200))
   * possible concurrency issue in memory caches ([#116](https://github.com/nostra13/Android-Universal-Image-Loader/issues/116))
   * wrong visibility of methods `ImageLoader.denyNetworkDownloads(boolean)` and `ImageLoader.handleSlowNetworks(boolean)` 

v1.8.1 *(08.03.2013)*
---
 * **Changed API:**
   * `ImageLoader.denyNetworkDownloads()` -> `ImageLoader.denyNetworkDownloads(true)`
   * `ImageLoader.allowNetworkDownloads()` -> `ImageLoader.denyNetworkDownloads(false)`
 * **New API:** 
   * `ImageLoader.denyNetworkDownloads(boolean)`
   * `ImageLoader.handleSlowNetwork(boolean)`. `FlushedInsputStream` isn't used for downloads by default.
 * Handled HTTP(S) redirects
 * Added `LruMemoryCache` (based on Android's LruCache), uses only strong references.
 * Fixed `DisplayImageOptions.cloneFrom(...)` ([#173](https://github.com/nostra13/Android-Universal-Image-Loader/issues/173))
 * Fixed ConcurrentModification issue in `MemoryCacheUtil. findCacheKeysForImageUri(...)` ([#174](https://github.com/nostra13/Android-Universal-Image-Loader/issues/174))
 * Fixed issue "Disc Cache can't find image by URI with special/local UTF-8 characters"
 * Improved calculation of target image size to scale (consider measured View width and height)

v1.8.0 *(10.02.2013)*
---
 * **Changed API:**
   * Signatures:
     * `ImageLoader.loadImage(Context, ...)` -> `ImageLoader.loadImage(...)`
     * `ImageDownloader.getStream(URI)` -> `ImageDownloader.getStream(URI, Object)` ([#150](https://github.com/nostra13/Android-Universal-Image-Loader/issues/150))
	 * `ImageLoadingListener.onLoading***(...)` -> `ImageLoadingListener.onLoading***(String, View, ...)` ([#130](https://github.com/nostra13/Android-Universal-Image-Loader/issues/130))
     * Constructor `PauseOnScrollListener(...)` -> `PauseOnScrollListener(ImageLoader, ...)`
   * `ImageDownloader` became interface, `URLConnectionImageDownloader` + `ExtendedImageDownloader` -> `BaseImageDownloader`
   * Renaming: `FileUtil` -> `IoUtil`
   * Removed deprecated `ImageScaleType.POWER_OF_2` and `ImageScaleType.EXACT` 
 * Support of "content://", "assets://", "drawable://" URI schemes out of the box ([#162](https://github.com/nostra13/Android-Universal-Image-Loader/issues/162))
 * **New API:** 
   * `DisplayImageOptions.showImageOnFail(int)`
   * `DisplayImageOptions.preProcessor(BitmapProcessor)` and `DisplayImageOptions.postProcessor(BitmapProcessor)` ([#151](https://github.com/nostra13/Android-Universal-Image-Loader/issues/151))
   * `DisplayImageOptions.extraForDownloader(Object)`, allows to pass auxiliary object which will be passed to `ImageDownloader.getStream(URI, Object)` ([#150](https://github.com/nostra13/Android-Universal-Image-Loader/issues/150))
   * `ImageLoader.denyNetworkDownloads()` and `ImageLoader.allowNetworkDownloads()` ([#148](https://github.com/nostra13/Android-Universal-Image-Loader/issues/148))
   * `FailReason.UNSUPPORTED_URI_SCHEME` and `FailReason.NETWORK_DENIED`
   * `ImageScaleType.NONE`
 * Added `DiscCacheUtil`
 * Prepared ImageLoader to be extandable for creation of multiple instances ([#158](https://github.com/nostra13/Android-Universal-Image-Loader/issues/158))
 * Fixed bug "Images aren't loaded after "Clear Cache" in app info" ([#168](https://github.com/nostra13/Android-Universal-Image-Loader/issues/168))
 * Switched to Apache 2.0 license

v1.7.1 *(31.01.2013)*
---
 * Avoid I/O operations on the main thread, prevented ANR ([#129](https://github.com/nostra13/Android-Universal-Image-Loader/issues/129), [#154](https://github.com/nostra13/Android-Universal-Image-Loader/issues/154))
 * Correctly handled every ImageView's scale type in `RoundedBitmapDisplayer` ([#70](https://github.com/nostra13/Android-Universal-Image-Loader/issues/70))
 * Prevented slow precaching modified date in LimitedAgeDiscCache constructor (for large number of images in cache) ([#141](https://github.com/nostra13/Android-Universal-Image-Loader/issues/141))
 * **New API:** `ImageLoader.isInited()` method. Throw IllegalStateException on `displayImage(...)`, `loadImage(...)`, `getMemoryCache()`, `clearMemoryCache()`, `getDiscCache()`, `clearDiscCache()` calls if ImageLoader isn't inited with config.
 * Closed OutputStream after Bitmap compressing ([#115](https://github.com/nostra13/Android-Universal-Image-Loader/issues/115))
 * Sample: Refactored resources

v1.7.0 *(27.11.2012)*
---
 * Maven support
 * **New API:** 
   * `ImageLoader.pause()` and `ImageLoader.resume()` ([#106](https://github.com/nostra13/Android-Universal-Image-Loader/issues/106))
   * `PauseOnScrollListener` (instead of `OnScrollSmartOptions`) for convenient pause/resume ImageLoader on scroll/fling in list views ([#106](https://github.com/nostra13/Android-Universal-Image-Loader/issues/106))
 * Prevented consuming of lot of memory by cacheKeysForImageViews ([#108](https://github.com/nostra13/Android-Universal-Image-Loader/issues/108))

v1.6.4 *(20.11.2012)*
---
 * **New API:** 
   * `DisplayImageOptions.bitmapConfig(Bitmap.Config)` ([#101](https://github.com/nostra13/Android-Universal-Image-Loader/issues/101))
   * `DisplayImageOptions.delayBeforeLoading(int)` ([#103](https://github.com/nostra13/Android-Universal-Image-Loader/issues/103))
   * `OnScrollSmartOptions` for convenient control of loading delay on fling in ListViews, GridViews
 * Added `FadeInBitmapDisplayer`
 * Prevented recycling of using Bitmap ([#101](https://github.com/nostra13/Android-Universal-Image-Loader/issues/101))

v1.6.3 *(03.11.2012)*
---
 * **New API:** `ImageLoaderConfiguration.tasksProcessingOrder(QueueProcessingType)` ([#89](https://github.com/nostra13/Android-Universal-Image-Loader/issues/89))
 * Added `MemoryCacheUtil` for work with memory cache
 * Fixed calculation of size the original image is needed scale to ([#93](https://github.com/nostra13/Android-Universal-Image-Loader/issues/93))
 * Allowed to create multiple `ImageLoader` instances ([#92](https://github.com/nostra13/Android-Universal-Image-Loader/issues/92))

v1.6.2 *(28.10.2012)*
---
 * Prevented showing wrong bitmap in reused view ([#85](https://github.com/nostra13/Android-Universal-Image-Loader/issues/85))
 * Fixed bug "double displaying" if image is cached in memory
 * Prevented "MissingFormatWidthException" ([#88](https://github.com/nostra13/Android-Universal-Image-Loader/issues/88))

v1.6.1 *(02.10.2012)*
---
 * **Changed API:** Renaming:
   * `ImageScaleType.POWER_OF_2` -> `IN_SAMPLE_POWER_OF_2`
   * `ImageScaleType.EXACT` -> `IN_SAMPLE_INT`
 * **New API:** `ImageScaleType.EXACTLY` and `ImageScaleType.EXACTLY_STRETCHED`
 * Prevented `ImageLoadingListener` callbacks firing and displaying image in view after ImageLoader was stopped
 * Fixed bug of calculation of original image scale for decoding

v1.6.0 *(29.09.2012)*
---
 * **New API:** `ImageLoader.loadImage(...)` which loads image, decodes it to Bitmap and returns Bitmap in callback ([#51](https://github.com/nostra13/Android-Universal-Image-Loader/issues/51))
 * Avoided unnecessary image downloads ([#44](https://github.com/nostra13/Android-Universal-Image-Loader/issues/44))

v1.5.6 *(20.09.2012)*
---
 * **Changed API:** Changed `FileNameGenerator` to interface (instead of abstract class)
 * **New API:** `BitmapDisplayer` and `DisplayImageOptions.displayer(...)`
 * Multithread displaying of cached images (instead of single-thread)
 * Correctly handle UTF-8 symbols and spaces in image URL ([#31](https://github.com/nostra13/Android-Universal-Image-Loader/issues/31))

v1.5.5 *(18.08.2012)*
---
 * **Changed API:**
   * Removed `DisplayImageOptions.transform()`
   * Changed `FileNameGenerator` to abstract class (instead of interface)
 * Fire `ImageLoadingListener` callbacks if image URI is null
