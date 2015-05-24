package com.nostra13.universalimageloader.cache.disc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by NirHartmann on 4/26/15.
 */
public class SafeFileInputStream extends FileInputStream {

    private SafeFile mFile;

    public SafeFileInputStream(String path) throws FileNotFoundException {
        this(new SafeFile(path));
    }

    public SafeFileInputStream(SafeFile file) throws FileNotFoundException {
        super(file);
        mFile = file;
        mFile.incRefCount();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (mFile != null) {
            mFile.decRefCount();
            mFile = null;
        }
    }

    @Override
    protected void finalize() throws IOException {
        try {
            if (mFile != null) {
                mFile.decRefCount();
                mFile = null;
            }
        } finally {
            super.finalize();
        }
    }
}
