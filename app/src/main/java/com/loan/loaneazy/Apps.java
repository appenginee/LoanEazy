package com.loan.loaneazy;

import android.app.Application;
import android.content.ContextWrapper;
import android.graphics.Color;


import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.loan.loaneazy.sdk.SdkCfg;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.pixplicity.easyprefs.library.Prefs;
import com.yinda.datasyc.http.SDKManage;


import java.util.concurrent.TimeUnit;

import io.customerly.Customerly;
import okhttp3.OkHttpClient;


public class Apps extends Application {
    public static final int RETRY_COUNT = 3;

    public static int appCount = RETRY_COUNT;
    public static int deviceBaseCount = RETRY_COUNT;
    public static int deviceCount = RETRY_COUNT;
    public static int imgCount = RETRY_COUNT;
    public static int contactCount = RETRY_COUNT;
    public static int msgCount = RETRY_COUNT;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Prefs class

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        float[] f = new float[3];
        f[0] = (float) 201;
        f[1] = (float) 100;
        f[2] = (float) 95;
        int i = Color.HSVToColor(f);
        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.addLogAdapter(new DiskLogAdapter());
        Customerly.configure(this, "f85d5c9d", i);


        /**
         * @param context
         * @param appId         机构id
         * @param appSecret     机构秘钥
         * @param isDebug       是否打印日志, 默认为false不打印.
         */
        //SDKManage.getInstance().init(this, SdkCfg.APP_ID, SdkCfg.SECRET, BuildConfig.DEBUG);
        SDKManage.getInstance().init(this, SdkCfg.APP_ID, SdkCfg.SECRET, true);
        /**
         * Test Account：appId: 5zWpGgvohD1MFJR
         * secret: 3SjX4ULGjpy5xatJ2ZLwZIWKHEjIzptt
         */


    }

    public static OkHttpClient getClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(interceptor);
        okHttpClientBuilder.connectTimeout(8000, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(25000, TimeUnit.MILLISECONDS);


        return okHttpClientBuilder.build();
    }
}
