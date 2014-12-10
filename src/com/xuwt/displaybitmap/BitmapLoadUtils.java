package com.xuwt.displaybitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by xuwt on 2014/12/10.
 */
public class BitmapLoadUtils {

    private Context mContext;
    private Bitmap mPlaceHolderBitmap;

    private BitmapLruCache mBitmapLruCache;

    public BitmapLoadUtils(Context context){
        this.mContext=context;
        mBitmapLruCache=new BitmapLruCache(mContext);

    }

    public void loadAsyncBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);
        final Bitmap bitmap = mBitmapLruCache.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(resId, imageView)) {

                mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);

                final BitmapWorkerTask task = new BitmapWorkerTask(mContext,imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(resId);
            }
        }


    }

    public boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            if (bitmapData != data) {
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

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}
