##  需要掌握的第三方库
Android-Universal-Image-Loader
1 干嘛用的？
获取图片并显示在相应的控件上
2 怎么使用

3 具体例子
1 配置初始化
	//可包括图片最大尺寸、线程池、缓存、下载器、解码器等等。 
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app
		
		ImageLoader.getInstance().init(config.build());
	}
2 ListView中的使用
private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
private DisplayImageOptions options;

	options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.displayer(new RoundedBitmapDisplayer(20)).build();
					
ImageLoader.getInstance().displayImage(IMAGE_URLS[position], holder.image, options, animateFirstListener);

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
下载图片，解析为 Bitmap 传递给回调接口。 
imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        // 图片处理
    }
});

4 源码解析
https://github.com/android-cn/android-open-project-analysis/tree/master/universal-image-loader
总体设计图
	ImageLoader
		displayImage loadImage
	ImageLoaderEngine
		LoadAndDisplayImageTask ProcessAndDisplayImageTask
	Get Data Interface
		MemoryCache DiskCache ImageDownloader
	Data
		Memory FileSystem Server

整个库分为ImageLoaderEngine，Cache及ImageDownloader，ImageDecoder，BitmapDisplayer，BitmapProcessor五大模块，
其中Cache分为MemoryCache和DiskCache两部分。
简单的讲就是ImageLoader收到加载及显示图片的任务，并将它交给ImageLoaderEngine，ImageLoaderEngine分发任务到具体线程池去执行，任务通过Cache及ImageDownloader获取图片，
中间可能经过BitmapProcessor和ImageDecoder处理，最终转换为Bitmap交给BitmapDisplayer在ImageAware中显示


5 常见问题


