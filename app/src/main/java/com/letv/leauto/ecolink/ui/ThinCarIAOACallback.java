package com.letv.leauto.ecolink.ui;

import android.content.Context;

import com.letv.leauto.ecolink.R;
import com.leauto.link.lightcar.IAOACallback;
import com.leauto.link.lightcar.module.AVNInfo;

/**
 * Created by Administrator on 2017/8/21.
 */

public class ThinCarIAOACallback implements IAOACallback{
    public static final String ADB_RESTART_ACTIVITY_ACTION = "com.leauto.ecolink.adb.restart.activity";

    public ThinCarIAOACallback(Context context) {

    }

    @Override
    public void onCommand(int command, int params) {

    }

    @Override
    public void onPcmPath(String pcmPath) {

    }

    @Override
    public void onAoaAttach() {

    }

    @Override
    public void onAoaDettach() {

    }

    @Override
    public void NotifyEvent(int event, String string) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void onViceDataObtain(byte[] data, int len) {

    }

    @Override
    public void onNaviEvent(short x, short y, short width, short height) {

    }

    @Override
    public void notifyGesterEvent(int event, int x, int y, int parameter) {

    }

    @Override
    public void onAVNInfo(AVNInfo avnInfo) {

    }

    @Override
    public void onCANFileTransmit(String name, long size) {

    }

    @Override
    public void onCANDataPath(String path) {

    }

    @Override
    public void startScreenRecordActivity() {

    }

    @Override
    public void onAoaConnectStateChange(int state) {

    }

    @Override
    public void onAdbConnectStateChange(int state) {

    }

    @Override
    public void postAdbDeviceInfo(int pinCode, int width, int height) {

    }
}
