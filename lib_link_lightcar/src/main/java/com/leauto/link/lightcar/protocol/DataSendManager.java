package com.leauto.link.lightcar.protocol;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.AccesssoryManager;
import com.leauto.link.lightcar.ISendDataInterface;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.MsgHeader;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.ThincarUtils;
import com.leauto.link.lightcar.ota.OtaMsgHeader;
import com.leauto.link.lightcar.ota.OtaThincarUtils;
import com.leauto.link.lightcar.service.SendDataService;
import com.leauto.sdk.SdkManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/16.
 */
public class DataSendManager {
    /**
     * pcm 保存路径
     */
    public static String targetPath = android.os.Environment.getExternalStorageDirectory() + "/" + "voice_record.pcm";

    private static final String TAG = "DataSendManager";
    private static DataSendManager ourInstance = new DataSendManager();
    private Context mContext;

    public static final byte DATA_TYPE_PICTURE = 0x0;
    public static final byte DATA_TYPE_STRING = 0x1;
    public static final byte RECEND_APP_DATA_TYPE_PICTURE = 0x2;//最近使用的图片发送用这个类型

    private static int MAX_DATA_SPEED = 200 * 1024;
    private static int sendDataLength = 0;
    private static long lastSengTime = 0;
    private static final int ONE_MINUTE = 1000;

    private ISendDataInterface mSendDataService;

    /**
     * 用来记录adb是否连接上
     */
    private volatile boolean isCarConnect = false;

    public void initDataSendManager(Context context) {
        mContext = context;
        initSendDataService();
    }

    private void initSendDataService() {
        Intent intent = new Intent(mContext, SendDataService.class);
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mSendDataService = ISendDataInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        mContext.bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    public static DataSendManager getInstance() {
        return ourInstance;
    }

    public void sendJsonDataToCar(short appid,JSONObject object) {
        String arrayStr = object.toString();
        byte[] strByte = arrayStr.getBytes();
        subpackageSendData(appid,strByte,DATA_TYPE_STRING);
    }

    public synchronized void sendPicDataToCar(short appid,byte[] data,byte type) {
        subpackageSendData(appid,data,type);
    }

    private void subpackageSendData(short appid ,byte[] data, byte dataType) {
        subpackageSendData(appid,data,dataType,0);
    }

    /**
     * 内部分包发送数据
     *
     * @param appid     协议定义的appid
     * @param data      要发送的总的数据
     * @param dataType  发送数据类型
     * @param fileName  图片名称
     */
    public void subpackageSendData(short appid ,byte[] data, byte dataType,long fileName) {
        int sendblockMax = 4096 - 21 - 24;
        /**
         * 走AOA通道，头字节45字节
         * 走ADB通道，头字节21字节
         */

        if (isAdbConnect()) {
            sendblockMax = 4096-21;
        }

        int sendRequestSize = 0;
        int leftBytes = 0;

        int totalPakcet = 1;
        int indexPakcet = 1;


        if (data.length <= sendblockMax) {
            sendDataDirect(addHeadForData(appid,data, totalPakcet, indexPakcet,dataType,fileName));
        } else {
            leftBytes = data.length;
            int temp = data.length % sendblockMax;
            if (temp > 0) {
                totalPakcet = (data.length / sendblockMax) + 1;
            } else {
                totalPakcet = (data.length / sendblockMax);
            }
            while (leftBytes > 0) {
                if (leftBytes > sendblockMax) {
                    sendRequestSize = sendblockMax;
                } else {
                    sendRequestSize = leftBytes;
                }

                byte[] buff = new byte[sendRequestSize];
                System.arraycopy(data, data.length - leftBytes, buff, 0, sendRequestSize);
                sendDataDirect(addHeadForData(appid,buff, totalPakcet, indexPakcet,dataType,fileName));
                indexPakcet = indexPakcet + 1;
                leftBytes = leftBytes - sendRequestSize;
            }
        }
    }

    /**
     * 添加21字节头部到发送数据前边
     * @param appid 协议定义的appid
     * @param data  待发送数据
     * @param totalPacket 总包数
     * @param packetIndex 当前发送是第包
     * @param dataType 发送数据类型
    * @return 返回封装好的二进制数据
     */
    private byte[] addHeadForData(short appid, byte[] data, int totalPacket, int packetIndex,byte dataType,long name) {
        OtaMsgHeader msgHeader = new OtaMsgHeader();
        msgHeader.setMsgCommand(new byte[]{(byte) 0xFF, (byte) 0xEE});
        msgHeader.setAppId(appid);
        msgHeader.setTotalPacket((short) totalPacket);
        msgHeader.setIndexPacket((short) packetIndex);
        msgHeader.setType(dataType);
        msgHeader.setContentLength(data.length);
        msgHeader.setExtendLength(name);
        ByteBuffer Otasendheader = OtaThincarUtils.objectToBuffer(msgHeader);
        byte[] newdata = new byte[21 + data.length];
        System.arraycopy(Otasendheader.array(), 0, newdata, 0, 21);//把21字节消息头放到newdata
        System.arraycopy(data, 0, newdata, 21, data.length);//把数据放到newdata

        return newdata;
    }

    /**
     * 给车机发送消息,包含两个通道，
     * AOA 走:sendToCarInfo，
     * ADB 走：sendCustomData
     */
    public synchronized void sendDataDirect(byte[] data) {
        /**
         * adb 打开的情况下，IS_CAR_CONNECT为true，走adb通道，
         * adb 关掉的情况下，IS_CAR_CONNECT为false，走AOA通道
         */
        if (!isAdbConnect()) {
            sendToCarInfo(data);
        } else {
            SdkManager.getInstance(mContext).sendCustomData(0,data,data.length);
        }

        limitSendSpeed(data.length);
    }

    /**
     * 限速,如果1s内发送数据大于200k,sleep(50)
     * @param length
     */
    private void limitSendSpeed(int length) {
        if (sendDataLength == 0) {//保存第一次发送数据时的时间
            lastSengTime = System.currentTimeMillis();
        }
        sendDataLength += length;

        if (sendDataLength >= MAX_DATA_SPEED) {
            sendDataLength = 0;
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastSengTime < ONE_MINUTE) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 给车机发送数据
     *
     * @param Event
     * @param P0
     * @param P1
     * @param data
     * @return
     */
    public int sendMsgToCar(int Event, int P0, int P1, byte[] data) {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.NOTIFY_EVENT_TO_CAR_COMMAND;
        msg.MsgParam = Event;//0x300
        msg.startx = (short) P0;
        msg.starty = (short) P1;
        msg.len = data.length;
        msg.unknow = 1;
        msg.width = (short) ThincarUtils.getSystemWidth(mContext);
        msg.height = (short) ThincarUtils.getSystemHeight(mContext);
        ByteBuffer sendheader = objectToBuffer(msg);
        byte[] newdata = new byte[data.length + 24];
        System.arraycopy(sendheader.array(), 0, newdata, 0, 24);
        System.arraycopy(data, 0, newdata, 24, data.length);
        sendDataToQue(newdata);

        return 0;
    }

    private void sendDataToQue(byte[] newdata) {
        try {
            if (mSendDataService != null) {
                mSendDataService.sendDataToCar(newdata);
            } else {
                LogUtils.e("lishixing","DataSendManager mSendDataService is null not init!!!");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 给车机发送命令
     *
     * @param Event
     * @param P0
     * @param P1
     * @return
     */
    public int notifyCarNaviEvent(int Event, int P0, int P1) {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.NOTIFY_EVENT_TO_CAR_COMMAND;
        msg.MsgParam = Event;
        msg.startx = (short) P0;
        msg.starty = (short) P1;
        msg.len = 0;
        msg.unknow = 1;
        if (mContext != null) {
            msg.width = (short) ThincarUtils.getSystemWidth(mContext);
            msg.height = (short) ThincarUtils.getSystemHeight(mContext);
        } else {
            LogUtils.e(TAG,"notifyCarNaviEvent error mContext is null !!");
        }

        ByteBuffer sendheader = objectToBuffer(msg);

        sendDataToQue(sendheader.array());
        return 0;
    }

    /**
     * 给车机发送数据,在数据外添加24头字节
     *
     * @param data
     * @return
     */
    public int sendToCarInfo(byte[] data) {
        //24字节消息头
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.SEND_CAR_DATA_COMMAND;
        msg.MsgParam = 0;
        msg.startx = (short) 0;
        msg.starty = (short) 0;
        msg.len = data.length;//传输数据长度 包含消息的消息头21字节
        msg.unknow = 1;
        msg.width = (short) ThincarUtils.getSystemWidth(mContext);
        msg.height = (short) ThincarUtils.getSystemHeight(mContext);
        ByteBuffer sendheader = objectToBuffer(msg);

        byte[] newdata = new byte[24 + data.length];
        System.arraycopy(sendheader.array(), 0, newdata, 0, 24);
        System.arraycopy(data, 0, newdata, 24, data.length);

        sendDataToQue(newdata);
        return 0;
    }

    /**
     * 通知车机截屏销毁
     */
    public void sendEncoderDestory() {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.NOTIFY_EVENT_TO_CAR_COMMAND;
        msg.MsgParam = 0x32;//0x300
        msg.len = 0;
        ByteBuffer sendheader = objectToBuffer(msg);
        sendDataToQue(sendheader.array());
    }

    /**
     * 给车机发送消息通知当前应用状态
     *
     * @param event 事件消息
     */
    public void sendAppStateToCar(int event) {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.NOTIFY_EVENT_TO_CAR_COMMAND;
        msg.MsgParam = event;
        ByteBuffer sendheader = objectToBuffer(msg);
        sendDataToQue(sendheader.array());
    }

    private ByteBuffer objectToBuffer(MsgHeader header) {
        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(header.getMsgCommand());
        buffer.putInt(header.getMsgParam());
        buffer.putShort(header.getUnknow());
        buffer.putShort(header.getUnknow1());
        buffer.putShort(header.getStartx());
        buffer.putShort(header.getStarty());
        buffer.putShort(header.getWidth());
        buffer.putShort(header.getHeight());
        buffer.putInt(header.getLen());
        return buffer;
    }

    public void sendPcmInfo(byte[] data,int channel,int sampleByte,int sampleRate,boolean isFirstFrame) {
        Log.e(TAG,"sendPcmInfo isFirstFrame:" + isFirstFrame);
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolFromCarCommand.SYNC_PCM_DATA_COMMAND;
        msg.MsgParam = sampleRate;
        msg.unknow = (short)channel;
        msg.unknow1 = (short) sampleByte;
        msg.len = data.length;
        if (isFirstFrame) {
            msg.startx = 1;
        } else {
            msg.startx = 0;
        }
        ByteBuffer sendheader = objectToBuffer(msg);

        byte[] newdata = new byte[24 + data.length];
        System.arraycopy(sendheader.array(), 0, newdata, 0, 24);
        System.arraycopy(data, 0, newdata, 24, data.length);

        sendDataToQue(newdata);
    }

    public void notifyCarConnect() {
        isCarConnect = true;
        LogUtils.i(TAG,"notifyCarConnect isCarConnect:" + isCarConnect);
        LogUtils.i(TAG,"notifyCarConnect mSendDataService:" + mSendDataService);
        try {
            if (mSendDataService!= null) {
                mSendDataService.notifyCarConnect();
            }
        } catch (Exception e) {
            LogUtils.i(TAG,"notifyCarConnect Exception:" + e);
            e.printStackTrace();
        }

        notifyCarToConnect();
    }

    public void notifyCarDisConnect() {
        isCarConnect = false;
        LogUtils.i(TAG,"notifyCarDisConnect isCarConnect:" + isCarConnect);
        try {
            mSendDataService.notifyCarDisConnect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 10200 server建立，通知车机来连
     */
    private void notifyCarToConnect() {
        LogUtils.i(TAG,"request notifyCarToConnect 000");

        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Notify");
        map.put("Method", "NotifySocket");
        map.put("Parameter", null);

        JSONObject obj = (JSONObject) JSON.toJSON(map);
        sendJsonDataToCar(ThinCarDefine.ProtocolAppId.DEVICE_INFO_APPID, obj);
    }

    /**
     * 判断adb连接是连接上
     * @return
     */
    private boolean isAdbConnect() {
        return isCarConnect;
    }

    public void notifyRecordExit() {
        try {
            mSendDataService.notifyRecordExit();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopScreenRecorder() {
        try {
            if (mSendDataService != null) {
                mSendDataService.stopScreenRecorder();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void resumeScreenRecorder() {
        try {
            if (mSendDataService != null) {
                mSendDataService.resumeScreenRecorder();
            }
        } catch (RemoteException e) {
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
