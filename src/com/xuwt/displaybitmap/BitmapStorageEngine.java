package com.xuwt.displaybitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class BitmapStorageEngine {

    private BitmapStorageCache mImageStorageCache;
    private Context mContext;


    public BitmapStorageEngine(Context context) {
        mImageStorageCache = new BitmapStorageCache(context);

        this.mContext = context;
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
     */
    public void loadBitmaps(String imageUrl,ImageView imageView) {
        Bitmap bitmap = null;
        bitmap = mImageStorageCache.getBitmapFromMemoryCache(imageUrl);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(imageUrl, imageView)) {

                Bitmap mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.jay);

                final BitmapWorkerTask task = new BitmapWorkerTask(mContext,imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
                //设置一个默认图片
                imageView.setImageDrawable(asyncDrawable);
                task.execute(imageUrl);
            }
        }

    }

    public boolean cancelPotentialWork(String url, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = Utils.getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapUrl = bitmapWorkerTask.url;
            if (bitmapUrl != url) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public void saveCache() {
        mImageStorageCache.fluchCache();
    }

 }
