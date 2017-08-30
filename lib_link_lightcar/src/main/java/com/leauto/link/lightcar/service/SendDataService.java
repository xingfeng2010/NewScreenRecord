package com.leauto.link.lightcar.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.leauto.link.lightcar.AccesssoryManager;
import com.leauto.link.lightcar.ISendDataInterface;
import com.leauto.link.lightcar.ScreenRecorderManager;

/**
 * 这个service要运行在录屏进程中，用于接收
 * 主应用进程发过来的消息，并发送给车机端
 */
public class SendDataService extends Service {
    private LocalBinder binder = new LocalBinder(this);
    public SendDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private static class LocalBinder extends ISendDataInterface.Stub {
        private Context mContext;
        LocalBinder(Context context) {
            mContext = context;
        }

        @Override
        public void sendDataToCar(byte[] data) throws RemoteException {
            AccesssoryManager.getAccesssoryManager(mContext).sendDataToQue(data);
        }

        @Override
        public void sendCheckButtonRange(int width, int height) throws RemoteException {
           AccesssoryManager.getAccesssoryManager(mContext).sendCheckButtonRange(width,height);
        }

        @Override
        public void notifyCarConnect() {
            AccesssoryManager.getAccesssoryManager(mContext).notifyCarConnect();
        }

        @Override
        public void notifyCarDisConnect() {
            AccesssoryManager.getAccesssoryManager(mContext).notifyCarDisConnect();
        }

        @Override
        public void notifyRecordExit() {
            AccesssoryManager.getAccesssoryManager(mContext).exitRecordProcess();
        }

        @Override
        public void stopScreenRecorder() {
            ScreenRecorderManager.getScreenRecorderManager(mContext).pauseScreenRecorder();
        }

        @Override
        public void resumeScreenRecorder() {
            ScreenRecorderManager.getScreenRecorderManager(mContext).resumeScreenRecorder();
        }
    }
}
