package com.leauto.link.lightcar.pcm;

import android.content.Context;
import android.util.Log;

import com.leauto.link.lightcar.AccesssoryManager;
import com.leauto.link.lightcar.protocol.DataSendManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Administrator on 2017/3/29.
 */
public class PcmDataManager {
    private static final String TAG = "ThincarPlayerPcm";

    private static int EACH_PACKAGE_DATA_LENGTH = 1024+24;

    private long mSendRate;

    private int mChannel;
    private int mSampleByte;
    private int mSampleRate;

    public static String targetPath = android.os.Environment.getExternalStorageDirectory() + "/" + "leradio_pcm.pcm";

    private static PcmDataManager ourInstance = new PcmDataManager();

    public static PcmDataManager getInstance() {
        return ourInstance;
    }

    /**
     * 读取数据缓冲队列
     */
    private LinkedBlockingDeque<PcmDataModule> mPcmDataQueue = new LinkedBlockingDeque<>();

    private Context mContext;

    public void initPcmDataManager(Context context) {
        mContext = context;
    }

    private PcmDataManager() {
        new DataSendModule().start();
    }


    /**
     * 内部分包发送数据
     */
    private void subpackageSendData(byte[] data,int channel,int sampleByte,int sampleRate,boolean firstFrame) throws InterruptedException{
        Log.e(TAG,"subpackageSendData len:" + data.length);
        if (mChannel != channel || mSampleByte != sampleByte || mSampleRate != sampleRate) {
            adjustSendSpeed(channel,sampleByte,sampleRate);
        }
        int sendblockMax = EACH_PACKAGE_DATA_LENGTH - 24;
        int sendRequestSize = 0;
        int leftBytes = 0;

        boolean tempIsFirst = firstFrame;

        if (data.length <= sendblockMax) {
            DataSendManager.getInstance().sendPcmInfo(data,channel,sampleByte,sampleRate,tempIsFirst);
            //writePcmToFile(data,0,data.length);
            tempIsFirst = false;
            //Thread.sleep(20);
        } else {
            leftBytes = data.length;
            while (leftBytes > 0) {
                if (leftBytes > sendblockMax) {
                    sendRequestSize = sendblockMax;
                } else {
                    sendRequestSize = leftBytes;
                }

                byte[] buff = new byte[sendRequestSize];
                System.arraycopy(data, data.length - leftBytes, buff, 0, sendRequestSize);
                DataSendManager.getInstance().sendPcmInfo(buff,channel,sampleByte,sampleRate,tempIsFirst);
                //writePcmToFile(buff,0,buff.length);
                tempIsFirst = false;
                leftBytes = leftBytes - sendRequestSize;
                //Thread.sleep(20);
            }
        }
    }

    private void adjustSendSpeed(int channel, int sampleByte, int sampleRate) {
        mChannel = channel;
        mSampleByte = sampleByte;
        mSampleRate = sampleRate;

        mSendRate = channel * sampleByte * sampleRate;
        Log.e(TAG,"adjustSendSpeed mSendRate:" + mSendRate);
    }

    public class DataSendModule extends Thread{

        @Override
        public void run() {
            while (true) {
                try {
                    PcmDataModule dataModule = mPcmDataQueue.take();
                    subpackageSendData(dataModule.mData,dataModule.mChannel,dataModule.mSampleByte,dataModule.mSampleRate,dataModule.mIsFirsFrame);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendPcmData(PcmDataModule dataModule) {
        try {
            mPcmDataQueue.put(dataModule);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writePcmToFile(byte[] buff, int src, int re_lenght) {
        File file = new File(targetPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "rw");
            accessFile.seek(file.length());
            if (re_lenght < 0) {
                return;
            }
            accessFile.write(buff, src, re_lenght);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
