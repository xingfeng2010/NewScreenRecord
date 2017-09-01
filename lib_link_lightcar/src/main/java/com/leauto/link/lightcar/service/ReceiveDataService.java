package com.leauto.link.lightcar.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.leauto.link.lightcar.IAOACallback;
import com.leauto.link.lightcar.IReceiveDataInterface;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.MsgHeader;
import com.leauto.link.lightcar.ThinCarDefine;

/**
 * 这个是位于主应用进程的service，用于读取
 * 从车机传来过的事件给应用进行处理
 */
public class ReceiveDataService extends Service {
    private static final String TAG = "ReceiveDataService";

    private ReceiveDataBinder binder = new ReceiveDataBinder(this);

    public ReceiveDataService() {
    }

    private IAOACallback mIAOACallback;


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setIAOACallback(IAOACallback back) {
        mIAOACallback = back;
    }

    public class ReceiveDataBinder extends IReceiveDataInterface.Stub{
        private Context mContext;

        public ReceiveDataBinder(Context context) {
            mContext = context;
        }

        public ReceiveDataService getService() {
            return ReceiveDataService.this;
        }

        @Override
        public void dispatchDataEvent(MsgHeader header, byte[] data) throws RemoteException {
            if (mIAOACallback != null) {
                mIAOACallback.onReceiveData(header,data);
            }
        }

        @Override
        public void dispatchNullDataEvent(MsgHeader header) throws RemoteException {
            if (mIAOACallback != null) {
                mIAOACallback.onReceiveData(header,null);
            }
        }
    }
}
