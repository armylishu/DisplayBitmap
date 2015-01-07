package com.xuwt.displaybitmap;

import android.app.Application;
import android.content.Context;

/**
 * Created by xuwt on 2015/1/7.
 */
public class BiamapApplication extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        mContext=getApplicationContext();
    }
}
