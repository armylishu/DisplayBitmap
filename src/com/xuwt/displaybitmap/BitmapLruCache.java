package com.xuwt.displaybitmap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by xuwt on 2014/12/10.
 */
public class BitmapLruCache {

    private Context mContext;
    private LruCache<String,Bitmap> mMemoryCache;
    public BitmapLruCache(Context context){
        this.mContext=context;
    }

    public void init(){
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager) mContext.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;
        mMemoryCache = new LruCache(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in bytes rather than number of items.
                return bitmap.getByteCount();
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
