package com.letv.leauto.ecolink.ui;

import android.app.Application;

/**
 * Created by Administrator on 2017/8/29.
 */

public class RecordApplication extends Application {
    public static RecordApplication instance;

    public static boolean isAdbConnect = false;

    public static synchronized RecordApplication getInstance() {
        if (null == instance) {
            instance = new RecordApplication();
        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
        HandlerException handlerException = new HandlerException();
        handlerException.init(getApplicationContext());
    }
}
