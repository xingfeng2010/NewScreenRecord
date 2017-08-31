package com.letv.leauto.ecolink.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.letv.leauto.ecolink.R;
import com.leauto.link.lightcar.IAOACallback;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.leauto.sdk.SdkManager;
import com.leauto.sdk.data.CarNaviRemoteDataListener;
import com.leauto.sdk.data.DeviceInfo;
import com.leauto.sdk.data.KeyboardRemoteControlListener;

/**
 * Created by Administrator on 2017/8/21.
 */

public class LeAutoLinkListner implements CarNaviRemoteDataListener,KeyboardRemoteControlListener {

    private Toast mToast;
    private IAOACallback mCallBack;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mToast.setText((String) msg.obj);
            mToast.show();
        }
    };

    public LeAutoLinkListner(Context context, Handler handler, IAOACallback callback) {
        mCallBack = callback;
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }


    /**
     * @param
     */
    private void showToast(String str) {
        mHandler.sendMessage(Message.obtain(null, 0, str));
    }

    @Override
    public void NotifyConnectStatus(int state) {
        if (SdkManager.LINK_CONNECTED == state) {
            RecordApplication.isAdbConnect = true;
            showToast("连上");
            DataSendManager.getInstance().notifyCarConnect();
        }
        if (SdkManager.LINK_DISCONNECTED == state) {
            RecordApplication.isAdbConnect = false;
            showToast("断开");
            DataSendManager.getInstance().notifyCarDisConnect();
        }
    }

    @Override
    public void remoteDataListener(int i, int i1, int i2) {

    }

    @Override
    public void remoteDataListener(byte[] bytes, int i) {
        mCallBack.onReceiveData(bytes);
    }

    @Override
    public void onDeviceInfo(DeviceInfo deviceInfo) {

    }

    @Override
    public void onDongFengControl(int i) {

    }
}
