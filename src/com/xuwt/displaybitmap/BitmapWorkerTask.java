package com.xuwt.displaybitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by xuwt on 2014/12/10.
 */
public class BitmapWorkerTask extends AsyncTask<Integer,Integer,Bitmap>{

    private final WeakReference<ImageView> imageViewReference;

    private Context mContext;

    public int data = 0;

    private int width=100;
    private int height=100;


    private BitmapLruCache mBitmapLruCache;

    public BitmapWorkerTask(Context context,ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference(imageView);

        this.mContext=context;

        mBitmapLruCache=new BitmapLruCache(mContext);

    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
       /* width=params[1];
        height=params[2];*/
        final Bitmap bitmap=
                BitmapUtils.decodeSampledBitmapFromResource(mContext.getResources(), data, width, height)
        mBitmapLruCache.addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);

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
                    BitmapLoadUtils.getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}