package com.xuwt.displaybitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by xuwt on 2014/12/10.
 */
public class BitmapWorkerTask extends AsyncTask<String,Integer,Bitmap> {

    private final WeakReference<ImageView> imageViewReference;

    private Context mContext;

    public String url;


    private BitmapStorageCache mImageStorageCache;

    public BitmapWorkerTask(Context context, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference(imageView);

        this.mContext = context;

        mImageStorageCache = BitmapStorageCache.getInstance();

    }

    @Override
    protected Bitmap doInBackground(String... params) {

        String url = params[0];

        Bitmap bitmap= mImageStorageCache.getBitmap(url);

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
       /* if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }*/

        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    Utils.getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}

