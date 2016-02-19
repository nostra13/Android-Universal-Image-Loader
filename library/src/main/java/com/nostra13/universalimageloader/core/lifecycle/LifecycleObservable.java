package com.nostra13.universalimageloader.core.lifecycle;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by TimoRD on 2016/2/19.
 */
public class LifecycleObservable {

    public enum Status {
        Bind, UnBind
    }

    private AtomicReference<Status> mStatusRef;
    private Set<LifecycleObserver> mSet;

    public LifecycleObservable() {
        mStatusRef = new AtomicReference<Status>(Status.UnBind);
        mSet = new CopyOnWriteArraySet<LifecycleObserver>();
    }

    public void addObserver(LifecycleObserver observer) {
        mSet.add(observer);
        observer.onLifecycleChanged(mStatusRef.get());
    }

    public void removeObserver(LifecycleObserver observer) {
        mSet.remove(observer);
    }

    public void clear() {
        mSet.clear();
    }

    public void bind() {
        mStatusRef.set(Status.Bind);
        for (LifecycleObserver observer : mSet) {
            observer.onLifecycleChanged(mStatusRef.get());
        }
    }

    public void unbind() {
        mStatusRef.set(Status.UnBind);
        for (LifecycleObserver observer : mSet) {
            observer.onLifecycleChanged(mStatusRef.get());
        }
    }
}
