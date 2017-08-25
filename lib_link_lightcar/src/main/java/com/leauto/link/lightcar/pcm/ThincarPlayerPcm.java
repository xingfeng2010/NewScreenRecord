package com.leauto.link.lightcar.pcm;

import android.content.Context;
import android.util.Log;

import com.leauto.link.lightcar.AccesssoryManager;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.ota.OtaMsgHeader;
import com.leauto.link.lightcar.ota.OtaThincarUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

public class ThincarPlayerPcm {
    private static final String TAG = "ThincarPlayerPcm";
    private Context mContext;

    private int mChannel;
    private int mSampleByte;
    private int mSampleRate;

    private boolean isFirstFrame = true;

    public void thincar_player_ready(Long comm, Long comm_arg) {
        Log.e(TAG,"thincar_player_ready:");
        player_ready(comm,comm_arg);
    }

    public void postPcmDataFromNative(byte[] data,int channel,int sampleByte,int sampleRate) {
        Log.e(TAG,"postPcmDataFromNative sampleByte:" + sampleByte);
        PcmDataModule dataModule = new PcmDataModule();
        dataModule.mData = data;
        dataModule.mChannel = channel;
        dataModule.mSampleByte = sampleByte;
        dataModule.mSampleRate = sampleRate;
        dataModule.mIsFirsFrame = isFirstFrame;
        PcmDataManager.getInstance().sendPcmData(dataModule);
        isFirstFrame = false;
    }

    static {
        System.loadLibrary("PcmCallBack");
    }

    private native void player_ready(Long comm, Long arg);

    /**
     * 传递采样数据信息
     * @param channel 通道
     * @param sampleByte 采样每一次字节
     * @param sampleRate 采样率
     */
    public void setPcmInfomation(int channel,int sampleByte,int sampleRate) {
        LogUtils.i(TAG,"setPcmInfomation channel:" + channel + " sampleByte:" + sampleByte + " sampleRate:" + sampleRate);
        if (mChannel != channel || mSampleByte != sampleByte || mSampleRate !=sampleRate ) {
            mChannel = channel;
            mSampleByte = sampleByte;
            mSampleRate = sampleRate;

            //AccesssoryManager.getAccesssoryManager(mContext).sendPcmInfo(channel,sampleByte,sampleRate);
        }
    }
}
