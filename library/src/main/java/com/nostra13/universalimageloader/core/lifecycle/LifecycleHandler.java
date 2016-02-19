package com.nostra13.universalimageloader.core.lifecycle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.nostra13.universalimageloader.utils.L;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by TimoRD on 2016/2/17.
 */
public class LifecycleHandler extends Handler implements LifecycleObserver {

    private AtomicReference<LifecycleObservable.Status> mStatusRef;

    public LifecycleHandler() {
        init();
    }

    public LifecycleHandler(Callback callback) {
        super(callback);
        init();
    }

    public LifecycleHandler(Looper looper) {
        super(looper);
        init();
    }

    public LifecycleHandler(Looper looper, Callback callback) {
        super(looper, callback);
        init();
    }

    private void init() {
        mStatusRef = new AtomicReference<LifecycleObservable.Status>();
    }

    @Override
    public void dispatchMessage(Message msg) {
        if (LifecycleObservable.Status.UnBind.equals(mStatusRef.get())) {
            L.d("Intercept DisplayBitmapTask");
            return;
        }

        L.d("Run DisplayBitmapTask");
        super.dispatchMessage(msg);
    }

    @Override
    public void onLifecycleChanged(LifecycleObservable.Status status) {
        mStatusRef.set(status);
    }
}
