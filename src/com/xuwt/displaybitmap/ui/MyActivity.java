package com.xuwt.displaybitmap.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import com.xuwt.displaybitmap.BitmapLoadUtils;
import com.xuwt.displaybitmap.BitmapLruCache;
import com.xuwt.displaybitmap.BitmapUtils;
import com.xuwt.displaybitmap.R;

public class MyActivity extends Activity {

    private Context mContext;
    private ImageView mImageView;

    private BitmapLruCache mBitmapLruCache;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContext=MyActivity.this;

        mImageView=(ImageView)findViewById(R.id.imageView);

        mBitmapLruCache=new BitmapLruCache(mContext);

        mBitmapLruCache.init();

        //直接设置
        //setImageView();

        //用线程设置
        //new BitmapWorkerTask(mContext,imageView).execute(R.drawable.jay, 50, 50);

        BitmapLoadUtils bitmapLoadUtils=new BitmapLoadUtils(mContext);
        bitmapLoadUtils.loadAsyncBitmap(R.drawable.jay,mImageView);

    }

    private void setImageView() {

        mImageView.setImageBitmap(
                BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.jay, 100, 100));
    }
}
