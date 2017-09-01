package com.leauto.link.lightcar;

import android.hardware.display.VirtualDisplay;

import com.leauto.link.lightcar.module.AVNInfo;

/**
 * Created by Jerome on 2016/7/7.
 */
public interface IAOACallback {
   void onReceiveData(MsgHeader headers,byte[] data);
}
