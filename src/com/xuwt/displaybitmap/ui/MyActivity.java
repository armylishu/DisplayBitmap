package com.xuwt.displaybitmap.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import com.xuwt.displaybitmap.BitmapStorageEngine;
import com.xuwt.displaybitmap.BitmapUtils;
import com.xuwt.displaybitmap.R;

public class MyActivity extends Activity {

    private Context mContext;
    private ImageView mImageView;

    public static final String IMAGE_URL = "http://img.anzhuo.im/public/picture/2012122401/1348137920992.jpg";

    private BitmapStorageEngine mBitmapStrageEngine;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContext=MyActivity.this;

        mImageView=(ImageView)findViewById(R.id.imageView);

        mBitmapStrageEngine=BitmapStorageEngine.getInstance();
        mBitmapStrageEngine.loadBitmaps(IMAGE_URL,mImageView);

        //直接设置
        //setImageView();

        //用线程设置
        //new BitmapWorkerTask(mContext,imageView).execute(R.drawable.jay, 50, 50);


    }

    private void setImageView() {

        mImageView.setImageBitmap(
                BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.jay, 100, 100));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBitmapStrageEngine.saveCache();
    }
}
