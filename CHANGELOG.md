Change Log
===

v1.7.1 *(31.01.2013)*
---
 * Avoid I/O operations on the main thread, prevented ANR (#129, #154)
 * Correctly handled every ImageView's scale type in 'RoundedBitmapDisplayer' (#70)
 * Prevented slow precaching modified date in LimitedAgeDiscCache constructor (for large number of images in cache) (#141)
 * Introduced 'ImageLoader.isInited()' method. Throw IllegalStateException on 'displayImage(...)', 'loadImage(...)', 'getMemoryCache()', 'clearMemoryCache()', 'getDiscCache()', 'clearDiscCache()' calls if ImageLoader isn't inited with config.
 * Closed OutputStream after Bitmap compressing (#115)
 * Sample: Refactored resources

v1.7.0 *(27.11.2012)*
---
 * Maven support
 * Introduced `ImageLoader.pause()` and `ImageLoader.resume()` (#106)
 * Introduced `PauseOnScrollListener` (instead of `OnScrollSmartOptions`) for convenient pause/resume ImageLoader on scroll/fling in list views (#106)
 * Prevented consuming of lot of memory by cacheKeysForImageViews (#108)

v1.6.4 *(20.11.2012)*
---
 * Introduced `DisplayImageOptions.bitmapConfig(Bitmap.Config)` (#101)
 * Introduced `DisplayImageOptions.delayBeforeLoading(int)` (#103)
 * Introduced `OnScrollSmartOptions` for convenient control of loading delay on fling in ListViews, GridViews
 * Added `FadeInBitmapDisplayer`
 * Prevented recycling of using Bitmap (#101)

v1.6.3 *(03.11.2012)*
---
 * Introduced `ImageLoaderConfiguration.tasksProcessingOrder(QueueProcessingType)` (#89)
 * Added `MemoryCacheUtil` for work with memory cache
 * Fixed calculation of size the original image is needed scale to (#93)
 * Allowed to create multiple `ImageLoader` instances (#92)

v1.6.2 *(28.10.2012)*
---
 * Prevented showing wrong bitmap in reused view (#85)
 * Fixed bug "double displaying" if image is cached in memory
 * Prevented "MissingFormatWidthException" (#88)

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
 * Introduced `ImageLoader.loadImage(...)` which loads image, decodes it to Bitmap and returns Bitmap in callback (#51)
 * Avoided unnecessary image downloads (#44)

v1.5.6 *(20.09.2012)*
---
 * **Changed API:** Changed `FileNameGenerator` to interface (instead of abstract class)
 * Introduced `BitmapDisplayer` and `DisplayImageOptions.displayer(...)`
 * Multithread displaying of cached images (instead of single-thread)
 * Correctly handle UTF-8 symbols and spaces in image URL (#31)

v1.5.5 *(18.08.2012)*
---
 * **Changed API:**
   * Removed `DisplayImageOptions.transform()`
   * Changed `FileNameGenerator` to abstract class (instead of interface)
 * Fire `ImageLoadingListener` callbacks if image URI is null
