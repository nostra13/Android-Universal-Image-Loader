Change Log
===

v1.8.5 *(30.06.2013)*
---
 * Introduce `ImageLoaderConfiguration.memoryCacheSizePercentage(int)` ([#279](https://github.com/nostra13/Android-Universal-Image-Loader/issues/279))
 * Introduced `DisplayImageOptions.cacheInMemory(boolean)`, `.cacheOnDisc(boolean)`, `.resetViewBeforeLoading(boolean)` ([#252](https://github.com/nostra13/Android-Universal-Image-Loader/issues/252))
 * Added `LoadedFrom` flag to `BitmapDisplayer.display(..., LoadedFrom)` about image source  ([#149](https://github.com/nostra13/Android-Universal-Image-Loader/issues/149), [#239](https://github.com/nostra13/Android-Universal-Image-Loader/issues/239))
 * Added possibility to set bitmap processor for disc cache (`ImageLoaderConfiguration.discCacheExtraOptions(..., BitmapProcessor)` ([#314](https://github.com/nostra13/Android-Universal-Image-Loader/issues/314))
 * Added `L.disableLogging()` and `L.enableLogging()` to off/on logs completely ([#270](https://github.com/nostra13/Android-Universal-Image-Loader/issues/270))
 * Prevent image decoding if image is reused ([#247](https://github.com/nostra13/Android-Universal-Image-Loader/issues/247))
 * Not set cache dir on SD card if no appropriate permission ([#311](https://github.com/nostra13/Android-Universal-Image-Loader/issues/311))
 * Increased buffer size for image downlaods (8 KB -> 32 KB) ([#249](https://github.com/nostra13/Android-Universal-Image-Loader/issues/249))
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
 * Introduced `DisplayImageOptions.handler(Handler)` ([#231](https://github.com/nostra13/Android-Universal-Image-Loader/issues/231))
 * Fixed bugs:
   * `ConcurrentModificationException` in `BaseMemoryCache` ([#229](https://github.com/nostra13/Android-Universal-Image-Loader/issues/229))
   * `NullPointerException` in `LimitedDiscCache` ([#234](https://github.com/nostra13/Android-Universal-Image-Loader/issues/234))
   * `NullPointerException` in `LruMemoryCache` ([#233](https://github.com/nostra13/Android-Universal-Image-Loader/issues/233))
 * Improved work with Strings on UI thread ([#244](https://github.com/nostra13/Android-Universal-Image-Loader/issues/244))

v1.8.3 *(31.03.2013)*
---
 * Android 2.0+ support
 * Added EXIF orientation support ([#172](https://github.com/nostra13/Android-Universal-Image-Loader/issues/172))
 * Introduced `ImageLoaderConfiguration.imageDecoder(ImageDecoder)`
 * Introduced `DisplayImageOptions.decodingOptions(BitmapFactory.Options)`
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
 * Introduced `ImageLoader.taskExecutor(Executor)` and `ImageLoader.taskExecutorForCachedImages(Executor)` ([#187](https://github.com/nostra13/Android-Universal-Image-Loader/issues/187))
 * Introduced `ImageLoader.destroy()`
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
 * Introduced `ImageLoader.denyNetworkDownloads(boolean)`
 * Introduced `ImageLoader.handleSlowNetwork(boolean)`. `FlushedInsputStream` isn't used for downloads by default.
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
 * Introduced `DisplayImageOptions.showImageOnFail(int)`
 * Introduced `DisplayImageOptions.preProcessor(BitmapProcessor)` and `DisplayImageOptions.postProcessor(BitmapProcessor)` ([#151](https://github.com/nostra13/Android-Universal-Image-Loader/issues/151))
 * Introduced `DisplayImageOptions.extraForDownloader(Object)`, allows to pass auxiliary object which will be passed to `ImageDownloader.getStream(URI, Object)` ([#150](https://github.com/nostra13/Android-Universal-Image-Loader/issues/150))
 * Introduced `ImageLoader.denyNetworkDownloads()` and `ImageLoader.allowNetworkDownloads()` ([#148](https://github.com/nostra13/Android-Universal-Image-Loader/issues/148))
 * Introduced `FailReason.UNSUPPORTED_URI_SCHEME` and `FailReason.NETWORK_DENIED`
 * Introduced `ImageScaleType.NONE`
 * Added `DiscCacheUtil`
 * Prepared ImageLoader to be extandable for creation of multiple instances ([#158](https://github.com/nostra13/Android-Universal-Image-Loader/issues/158))
 * Fixed bug "Images aren't loaded after "Clear Cache" in app info" ([#168](https://github.com/nostra13/Android-Universal-Image-Loader/issues/168))
 * Switched to Apache 2.0 license

v1.7.1 *(31.01.2013)*
---
 * Avoid I/O operations on the main thread, prevented ANR ([#129](https://github.com/nostra13/Android-Universal-Image-Loader/issues/129), [#154](https://github.com/nostra13/Android-Universal-Image-Loader/issues/154))
 * Correctly handled every ImageView's scale type in `RoundedBitmapDisplayer` ([#70](https://github.com/nostra13/Android-Universal-Image-Loader/issues/70))
 * Prevented slow precaching modified date in LimitedAgeDiscCache constructor (for large number of images in cache) ([#141](https://github.com/nostra13/Android-Universal-Image-Loader/issues/141))
 * Introduced `ImageLoader.isInited()` method. Throw IllegalStateException on `displayImage(...)`, `loadImage(...)`, `getMemoryCache()`, `clearMemoryCache()`, `getDiscCache()`, `clearDiscCache()` calls if ImageLoader isn't inited with config.
 * Closed OutputStream after Bitmap compressing ([#115](https://github.com/nostra13/Android-Universal-Image-Loader/issues/115))
 * Sample: Refactored resources

v1.7.0 *(27.11.2012)*
---
 * Maven support
 * Introduced `ImageLoader.pause()` and `ImageLoader.resume()` ([#106](https://github.com/nostra13/Android-Universal-Image-Loader/issues/106))
 * Introduced `PauseOnScrollListener` (instead of `OnScrollSmartOptions`) for convenient pause/resume ImageLoader on scroll/fling in list views ([#106](https://github.com/nostra13/Android-Universal-Image-Loader/issues/106))
 * Prevented consuming of lot of memory by cacheKeysForImageViews ([#108](https://github.com/nostra13/Android-Universal-Image-Loader/issues/108))

v1.6.4 *(20.11.2012)*
---
 * Introduced `DisplayImageOptions.bitmapConfig(Bitmap.Config)` ([#101](https://github.com/nostra13/Android-Universal-Image-Loader/issues/101))
 * Introduced `DisplayImageOptions.delayBeforeLoading(int)` ([#103](https://github.com/nostra13/Android-Universal-Image-Loader/issues/103))
 * Introduced `OnScrollSmartOptions` for convenient control of loading delay on fling in ListViews, GridViews
 * Added `FadeInBitmapDisplayer`
 * Prevented recycling of using Bitmap ([#101](https://github.com/nostra13/Android-Universal-Image-Loader/issues/101))

v1.6.3 *(03.11.2012)*
---
 * Introduced `ImageLoaderConfiguration.tasksProcessingOrder(QueueProcessingType)` ([#89](https://github.com/nostra13/Android-Universal-Image-Loader/issues/89))
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
 * Introduced `ImageScaleType.EXACTLY` and `ImageScaleType.EXACTLY_STRETCHED`
 * Prevented `ImageLoadingListener` callbacks firing and displaying image in view after ImageLoader was stopped
 * Fixed bug of calculation of original image scale for decoding

v1.6.0 *(29.09.2012)*
---
 * Introduced `ImageLoader.loadImage(...)` which loads image, decodes it to Bitmap and returns Bitmap in callback ([#51](https://github.com/nostra13/Android-Universal-Image-Loader/issues/51))
 * Avoided unnecessary image downloads ([#44](https://github.com/nostra13/Android-Universal-Image-Loader/issues/44))

v1.5.6 *(20.09.2012)*
---
 * **Changed API:** Changed `FileNameGenerator` to interface (instead of abstract class)
 * Introduced `BitmapDisplayer` and `DisplayImageOptions.displayer(...)`
 * Multithread displaying of cached images (instead of single-thread)
 * Correctly handle UTF-8 symbols and spaces in image URL ([#31](https://github.com/nostra13/Android-Universal-Image-Loader/issues/31))

v1.5.5 *(18.08.2012)*
---
 * **Changed API:**
   * Removed `DisplayImageOptions.transform()`
   * Changed `FileNameGenerator` to abstract class (instead of interface)
 * Fire `ImageLoadingListener` callbacks if image URI is null
