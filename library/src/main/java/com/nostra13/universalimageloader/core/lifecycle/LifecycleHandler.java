package com.nostra13.universalimageloader.core.lifecycle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.nostra13.universalimageloader.utils.L;

/**
 * Created by TimoRD on 2016/2/17.
 */
public class LifecycleHandler extends Handler {

    private volatile boolean mBind;

    public LifecycleHandler() {
    }

    public LifecycleHandler(Callback callback) {
        super(callback);
    }

    public LifecycleHandler(Looper looper) {
        super(looper);
    }

    public LifecycleHandler(Looper looper, Callback callback) {
        super(looper, callback);
    }

    public void bind() {
        mBind = true;
        L.d("Lifecycle bind");
    }

    public void unbind() {
        mBind = false;
        L.d("Lifecycle unbind");
    }

    @Override
    public void dispatchMessage(Message msg) {
        if (!mBind) {
            L.d("Intercept DisplayBitmapTask");
            return;
        }

        L.d("Run DisplayBitmapTask");
        super.dispatchMessage(msg);
    }
}
