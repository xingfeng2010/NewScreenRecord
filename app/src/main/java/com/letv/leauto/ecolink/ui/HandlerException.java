package com.letv.leauto.ecolink.ui;

import android.content.Context;
import android.os.Looper;

/**
 * Created by Administrator on 2016/11/2.
 */
public class HandlerException implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "HandlerException";

    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static HandlerException mHandlerException = new HandlerException();

    public static HandlerException getInstance() {
        return mHandlerException;
    }

    public void init(Context context) {
        this.mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        //如果没有处理异常，则设置默认的异常处理机制
        if (!handlerException(throwable) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            // 如果自己处理了异常，退出app
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {

            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    public boolean handlerException(Throwable e) {
        if (e == null)//无需处理
            return false;
        final String msg = e.getLocalizedMessage();
        final StackTraceElement[] stack = e.getStackTrace();
        final String message = e.getMessage();

        new Thread() {
            @Override
            public void run() {
                // Toast 显示需要出现在一个线程的消息队列中
                Looper.prepare();
                //Toast.makeText(mContext, "程序出错啦:" + msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        //将异常log保存在手机本地
        ExceptionLogUtil.getInstance().collectDeviceInfo(mContext,e);
        return false;
    }
}
