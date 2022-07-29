package com.ictkholdings.nftpufsample;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

//import com.google.android.gms.common.util.ArrayUtils;


public class App extends Application {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Debug Variable
    public static final boolean D = false; //디버그
    public static String LOG_TAG = "MobileId";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Global Variable
    public static final String URL = "http://aiotsrv.iptime.org:22222";
    public static final long FINISH_INTERVAL_TIME = 2000;
    public static long backPressedTime = 0;
    public static Context context ;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // G3 Variable


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** onCreate()
     * 액티비티, 리시버, 서비스가 생성되기전 어플리케이션이 시작 중일때
     * Application onCreate() 메서드가 만들어 진다고 나와 있습니다.
     * by. Android Developer Site
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //UsimPufHandler


    }

    // 인텐트 정보를 이용하여 액티비티를 실행시킨다.
    public static void page_change(Activity act, Class<?> target, boolean ended ) {
        Intent intent = new Intent(act, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        act.startActivity(intent);
        if (ended)
            act.finish();
    }
    //Tsm ConnectListener



}
