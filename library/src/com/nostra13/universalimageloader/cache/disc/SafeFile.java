package com.nostra13.universalimageloader.cache.disc;

import java.io.File;
import java.net.URI;

/**
 * Created by NirHartmann on 4/26/15.
 */
public class SafeFile extends File {
    private int refCount = 0;
    private boolean markForDeletion = false;

    public SafeFile(File dir, String name) {
        super(dir, name);
    }

    public SafeFile(String path) {
        super(path);
    }

    public SafeFile(String dirPath, String name) {
        super(dirPath, name);
    }

    public SafeFile(URI uri) {
        super(uri);
    }

    protected synchronized void incRefCount() {
        refCount++;
    }

    protected synchronized void decRefCount() {
        refCount--;
        safeDeleteIfNeeded();
    }

    private boolean safeDeleteIfNeeded() {
        if (markForDeletion && refCount == 0)
            return super.delete();
        return false;
    }

    @Override
    public SafeFile[] listFiles() {
        return filenamesToFiles(list());
    }

    private SafeFile[] filenamesToFiles(String[] filenames) {
        if (filenames == null) {
            return null;
        }
        int count = filenames.length;
        SafeFile[] result = new SafeFile[count];
        for (int i = 0; i < count; ++i) {
            result[i] = new SafeFile(this, filenames[i]);
        }
        return result;
    }

    @Override
    public synchronized boolean delete() {
        markForDeletion = true;
        return safeDeleteIfNeeded();
    }
}
