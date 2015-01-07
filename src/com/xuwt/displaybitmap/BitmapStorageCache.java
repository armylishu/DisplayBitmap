package com.xuwt.displaybitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;

import java.io.*;

public class BitmapStorageCache {

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 图片硬盘缓存核心类。
     */
    private DiskLruCache mDiskLruCache;

    private static BitmapStorageCache instance;
    private final static Object syncLock=new Object();

    public static BitmapStorageCache getInstance(){
        if(instance==null){
            synchronized (syncLock) {

                if(instance==null){
                    instance=new BitmapStorageCache();
                }

            }
        }

        return instance;
    }


    public BitmapStorageCache() {
        // TODO Auto-generated constructor stub

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;

        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

        try {
            // 获取图片缓存路径
            File cacheDir = Utils.getDiskCacheDir(BiamapApplication.mContext, "thumb");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // 创建DiskLruCache实例，初始化缓存数据
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, Utils.getAppVersion(BiamapApplication.mContext), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(String imageUrl) {

        Bitmap bitmap = null;

        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        Snapshot snapShot = null;
        try {
            // 生成图片URL对应的key
            final String key = Utils.hashKeyForDisk(imageUrl);
            // 查找key对应的缓存
            snapShot = mDiskLruCache.get(key);
            if (snapShot == null) {
                // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (Utils.downloadUrlToStream(imageUrl, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                    mDiskLruCache.flush();
                }
                // 缓存被写入后，再次查找key对应的缓存
                snapShot = mDiskLruCache.get(key);
            }
            if (snapShot != null) {
                fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            // 将缓存数据解析成Bitmap对象

            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
            if(bitmap!=null){
                // 将Bitmap对象添加到内存缓存当中
                addBitmapToMemoryCache(imageUrl, bitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
        }

        return bitmap;
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 将缓存记录同步到journal文件中。
     */
    public void fluchCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
