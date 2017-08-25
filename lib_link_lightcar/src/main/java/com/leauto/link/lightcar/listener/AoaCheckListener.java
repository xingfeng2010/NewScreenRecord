package com.leauto.link.lightcar.listener;

/**
 * Created by Administrator on 2017/7/5.
 */

public interface AoaCheckListener {
    /**
     * 通知开始aoa检测
     */
    public void onLaunchAoaCheck();

    /**
     * 通知开始aoa检测
     */
    public void onAoaCheckFinish(boolean checkPass);


    public void finishActivity();
}
