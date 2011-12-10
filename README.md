# Universal Image Loader for Android

Image loader for Android which ca be used for wide cases of asynchronous image displaying.

## Usage

``` java
ImageView imageView = ...
String imageUrl = "http://site.com/image.png"; // or "file:///mnt/sdcard/images/image.jpg"
ProgressBar spinner = ...

ImageLoader imageLoader = ImageLoader.getInstance(context);
DisplayImageOptions options = new DisplayImageOptions.Builder()
                                       .showStubImage(R.drawable.stub_image)
                                       .cacheInMemory()
                                       .cacheOnDisc()
                                       .build();
imageLoader.displayImage(imageUrl, imageView, options, new ImageLoadingListener() {
    @Override
    public void onLoadingStarted() {
       spinner.show();
    }
    @Override
    public void onLoadingComplete() {
        spinner.hide();
    }
}); // or simple imageLoader.displayImage(imageUrl, imageView);
```

## License
Copyright (c) 2011 [Sergey Tarasevich](http://nostra13android.blogspot.com)

Licensed under the [BSD 3-clause](http://www.opensource.org/licenses/BSD-3-Clause)