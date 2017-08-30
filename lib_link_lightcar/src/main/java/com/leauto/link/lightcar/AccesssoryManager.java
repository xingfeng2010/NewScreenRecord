package com.leauto.link.lightcar;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.listener.AoaCheckListener;
import com.leauto.link.lightcar.service.ReceiveDataService;
import com.leauto.link.lightcar.voiceassistant.LaunchActivityListener;
import com.leauto.link.lightcar.module.ThincarDevice;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.leauto.link.lightcar.server.MySocketServer;
import com.leauto.link.lightcar.service.ScreenRecorderService;
import com.leauto.link.lightcar.server.ListeningThread.SocketConnectListener;
import com.leauto.link.lightcar.thread.BaseThread;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Jerome on 2016/7/7.
 */
public class AccesssoryManager {
    private static final String TAG = "AccesssoryManager";

    private boolean isCarConnect;

    /**
     * 记录adb 与瘦车机10200端口socket通道是否建立
     */
    private boolean isAdbSocketConnect;

    private static final String ACTION_USB_PERMISSION = "serenegiant.newstream.action.USB_PERMISSION";
    /**
     * 监听USB连接状态
     */
    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    public static final String USB_CONNECTED = "connected";
    public static final String USB_FUNCTION_ADB = "adb";


    private static AccesssoryManager instance;
    private Context mContext;
    private UsbManager mUsbManager;
    private UsbAccessory mAccessory;
    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream mInputStream;
    //Output-Operation
    private FileOutputStream mOutputStream;
    private boolean mPermissionRequestPending;
    private PendingIntent mPermissionIntent;
    private AoaCheckListener mAoaCheckListener;
    public Lock sendlock = new ReentrantLock();

    private MySocketServer mServerSocket;
    private final int SOCKET_PORT = 10200;

    public static final int LAND_DIRECTION  = 1;
    public static final int PORTRAIT_DIRECTION  = 0;

    private int isShuPing = 0;//1为横屏，0为竖屏
    public byte[] readbuffer = new byte[1024 * 4];
    public byte[] writebuffer = new byte[1024 * 4];
    public byte[] readerMgsHeader = new byte[512];

    private BroadcastReceiver mUsbAccReceiver;

    private short mHasUsbPermission = 0x0;
    private short mHasRecordPermission = 0x0;

    /** 用来标记aoa反控检测是否通过*/
    private boolean mHasAoaCheckPass = false;

    /**
     * 发送数据缓冲队列
     */
    private LinkedBlockingDeque<byte[]> mSendDataQueue = new LinkedBlockingDeque<>();

    /** 代码连接上的车机类型 */
    private ThincarDevice mThincarDevice;

    /**
     * 读取数据缓冲队列
     */
    private LinkedBlockingDeque<byte[]> mReadDataQueue = new LinkedBlockingDeque<>();

    /**
     * 读取数据缓冲队列
     */
    private ConcurrentLinkedQueue<byte[]> mLeftDataQueue = new ConcurrentLinkedQueue<>();
    private BaseThread mReceiveThread;
    private BaseThread mSendDataThread;
    private BaseThread mProcessReadDataThread;

    /**
     * 车机点击前发一个消息过来
     */
    private volatile boolean mIsCarClickBegin = false;

    /**
     * 这两个值用于adb横屏切竖屏前传来的action，
     * 在切到竖屏后，再把请求结果一次发给车机
     */

    private IReceiveDataInterface mReceiveDataService;

    public void setAccessContext(Context context) {
        this.mContext = context;
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mReceiveDataService = IReceiveDataInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private AccesssoryManager(Context context) {
        this.mContext = context;
        mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                }
            }
        };
    }

    /**
     * 一个MainActivity 对就一个AccesssoryManager实例，其它地方不要创建
     *
     * @param context
     */
    public static void createNewInstance(Context context) {
        instance = new AccesssoryManager(context);
    }

    public static AccesssoryManager getAccesssoryManager(Context context) {
        if (instance == null) {
            instance = new AccesssoryManager(context);
        }
        return instance;
    }

    private Handler mHandler;

    public void notifyAoaCheckPass() {
        mHasAoaCheckPass = true;
        notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_AOC_CLICE_RECEIVED,ThinCarDefine.AOAChecekResult.CHECK_SUCCESS,0);
    }

    public static class UsbAccReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.i(TAG, "UsbAccReceiver action:" + action);
            AccesssoryManager manager = getAccesssoryManager(context);
            if (ACTION_USB_PERMISSION.equals(action)) synchronized (this) {
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    manager.mHasUsbPermission = 0x1;
                    manager.openAccessory(accessory);
                } else {
                    LogUtils.i(TAG, "permission denied for accessory " + accessory);
                    manager.mHasUsbPermission = 0x0;
                }
                manager.mPermissionRequestPending = false;
            }
            else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(manager.mAccessory)) {
                    manager.closeAccessory();
                }
            } else if (ACTION_USB_STATE.equals(action)) {
                boolean connect = intent.getBooleanExtra(USB_CONNECTED, false);
                boolean adb = intent.getBooleanExtra(USB_FUNCTION_ADB, false);
                boolean accessory = intent.getBooleanExtra("accessory", false);
                LogUtils.e(TAG,"UsbAccReceiver  openAccessory accessory:" + accessory);
                if (!adb) {//adb关闭状态下
                    if (connect) {
                        manager.openAccessory();
                    } else {
                        manager.closeAccessory();
                        manager.exitRecordProcess();
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void initAccessory(AoaCheckListener callback) {
        /**
         * 启动横竖屏检测
         */
        Intent intent = new Intent(mContext, OriService.class);
        mContext.startService(intent);

        initDataThread();
        startServerSocket();

        mAoaCheckListener = callback;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        registerReceiver();

        initSendService();
    }

    private void initSendService() {
        Intent intent = new Intent(mContext, ReceiveDataService.class);

        mContext.bindService(intent,conn, Service.BIND_AUTO_CREATE);
    }

    private void initDataThread() {
        /** 启动接收数据线程 */
        mReceiveThread = new ReceiveThread();
        /** 启动发送数据线程 */
        mSendDataThread = new SendDataThread();
        /** 启动处理数据线程 */
        mProcessReadDataThread = new ProcessReadDataThread();
    }

    private void destroyDataThread() {
        mReceiveThread = null;
        mSendDataThread = null;
        mProcessReadDataThread = null;
    }

    private void stopAllThread() {
        if (mReceiveThread != null) {
            mReceiveThread.stopThread();
        }

        if (mSendDataThread != null) {
            mSendDataThread.stopThread();
        }

        if (mProcessReadDataThread != null) {
            mProcessReadDataThread.stopThread();
        }

        destroyDataThread();
    }

    private void startAllThread() {
        if (mReceiveThread == null) {
            mReceiveThread = new ReceiveThread();
        }
        mReceiveThread.startThread();

        if (mSendDataThread == null) {
            mSendDataThread = new SendDataThread();
        }
        mSendDataThread.startThread();

        if (mProcessReadDataThread == null) {
            mProcessReadDataThread = new ProcessReadDataThread();
        }
        mProcessReadDataThread.startThread();
    }

    /**
     * 打开与车机通信usb通道
     */
    public synchronized void openAccessory() {
        LogUtils.i(TAG,"openAccessory mInputStream:" + mInputStream);
        if (mInputStream != null && mOutputStream != null) {
            return;
        }

        if (mUsbManager == null) {
            mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        }
        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (mUsbManager.hasPermission(accessory)) {
                mHasUsbPermission = 0x1;
                openAccessory(accessory);
            } else {
                mHasUsbPermission = 0x0;
                synchronized (this) {
                    if (!mPermissionRequestPending) {
                        mUsbManager.requestPermission(accessory,
                                mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        }
    }

    /**
     * 通过activity控制AccessoryManager内部生命周期
     */
    public void onDestroy() {
        //去掉动态注册
        try {
            stopAllThread();
            mContext.unregisterReceiver(mUsbAccReceiver);
            mContext.unbindService(conn);

            if (mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }

            instance = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Accessory Transfer Property
    private void openAccessory(UsbAccessory accessory) {
        LogUtils.i(TAG, "openAccessory isCarConnect:" + isCarConnect);
        ScreenRecorderManager.getScreenRecorderManager(mContext).setHasStartRecord(false);
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        LogUtils.i(TAG, "openAccessory mFileDescriptor:" + mFileDescriptor);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            if (!isCarConnect) {
                startInputStream();

                clearQueueData();
                startAllThread();
            }

        } else {
            LogUtils.i(TAG, "accessory open fail");
        }
    }


    private void startInputStream() {
        FileDescriptor fd = mFileDescriptor.getFileDescriptor();
        //Input from Remote Device
        mInputStream = new FileInputStream(fd);
        //Out to Remote Device
        mOutputStream = new FileOutputStream(fd);
    }

    /**
     * AOA close
     */
    private void closeAccessory() {
        //mIAOACallback.showToast("closeAccessory");
        if (mInputStream == null && mOutputStream == null) {
            return;
        }
        LogUtils.i(TAG, "closeAccessory");
        //退出录频
        mHandler.sendEmptyMessage(ThinCarDefine.ProtocolFromCarAction.STOP_SCREEN_RECORDER);


        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mOutputStream != null) {
                mOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mInputStream = null;
            mOutputStream = null;
            mFileDescriptor = null;
            mAccessory = null;
            stopAllThread();
            ScreenRecorderManager.getScreenRecorderManager(mContext).stopScreenRecorder();
        }

//        try {
//            mReceiveDataService.onAoaConnectStateChange(ThinCarDefine.ConnectState.STATE_DISCONNECT);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        clearQueueData();

        mHasRecordPermission = 0x0;
        mHasUsbPermission = 0x0;
        mHasAoaCheckPass = false;
    }

    /**
     * 建立服务端Server,监听连接
     */
    public void startServerSocket() {
        /** 已经启动 */
        if (mServerSocket != null) {
            return;
        }

        SocketConnectListener listener = new SocketConnectListener() {

            @Override
            public void onSocketAccepted() {
                LogUtils.i(TAG,"startServerSocket onSocketAccepted 111");
                clearQueueData();
                if (mSendDataThread == null) {
                    mSendDataThread = new SendDataThread();
                }
                mSendDataThread.startThread();
                if (mProcessReadDataThread == null) {
                    mProcessReadDataThread = new ProcessReadDataThread();
                }
                mProcessReadDataThread.startThread();


                Activity activity = (Activity) mContext;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isAdbSocketConnect = true;
                        sendConnectWhenAdb();
                        notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.CONTROL_LIGHT_AVN_PARAM,
                                ThinCarDefine.ProtocolToCarAction.LAUNCH_LE_RADION_ACTION, 0);

                        if (mReceiveDataService != null) {
                            try {
                                mReceiveDataService.onAdbConnectStateChange(ThinCarDefine.ConnectState.STATE_CONNECT);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            LogUtils.i(TAG,"mReceiveDataService is null !!");
                        }
                    }
                });
            }
        };

        if (mServerSocket == null) {
            mServerSocket = MySocketServer.getInstance();
            mServerSocket.initMySocketServer(SOCKET_PORT, mContext, listener);
        }
    }

    /**
     * usb连接断开，关掉与车机端的SOCKET通道
     */
    public void stopServerSocket() {
        if (isAdbSocketConnect) {
            clearQueueData();
            if (mSendDataThread != null) {
                mSendDataThread.stopThread();
                mSendDataThread = null;
            }

            if (mProcessReadDataThread != null) {
                mProcessReadDataThread.stopThread();
                mProcessReadDataThread = null;
            }

            if (mServerSocket != null) {
                mServerSocket.stopCurrentConnection();
            }

//            try {
//                mReceiveDataService.onAdbConnectStateChange(ThinCarDefine.ConnectState.STATE_DISCONNECT);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        isAdbSocketConnect = false;
    }

    private int len = -1;

    public void sendAdbData(byte[] data) {
        mServerSocket.sendMsg(data);
    }

    /**
     * 接受数据线程
     */
    public class ReceiveThread extends BaseThread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }

            LogUtils.i(TAG,"ReceiveThread run mAoaCheckListener:" + mAoaCheckListener);
            if (mAoaCheckListener != null) {
                mIsCarClickBegin = false;
                mAoaCheckListener.onLaunchAoaCheck();
            }

            while (isRunning) {
                readDataFromStream(mInputStream);
            }
        }
    }

    /**
     * send h264 key frame
     *
     * @param data
     */
    public void sendKeyFrame(byte[] data) {
        if (mOutputStream != null) {
            postFrameToCar(data, data.length, ThinCarDefine.FLAG_KEY_FRAME);
        }
    }

    public void sendNotKeyFrame(byte[] data) {
        if (mOutputStream != null) {
            postFrameToCar(data, data.length, ThinCarDefine.FLAG_NOT_KEY_FRAME);
        }
    }

    /**
     * send h264 frame to lightcar
     *
     * @param data
     * @param len
     * @param flag
     */
    private void postFrameToCar(byte[] data, int len, short flag) {
        try {
            MsgHeader msg = new MsgHeader();
            msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.DEVICE_AOA_ADB_CONNECTED_COMMAND;
            msg.len = len;
            msg.unknow = (short) isShuPing;
            msg.unknow1 = flag;
            ByteBuffer sendheader = objectToBuffer(msg);
            byte[] newdata = new byte[data.length + 24];

            System.arraycopy(sendheader.array(), 0, newdata, 0, 24);
            System.arraycopy(data, 0, newdata, 24, data.length);
            sendDataToQue(newdata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * aoa 通道写入数据
     *
     * @param data
     * @return
     */
    private int usbPostData(byte[] data) {
        if (isAdbSocketConnect) {//所有给AOA发也给ADB发，当ADB打开时就用ADB来发
            if (mServerSocket != null) {
                mServerSocket.sendMsg(data);
            }
            return 0;
        }

        //synchronized
        int sendblockMax = 4096;
        int sendRequestSize = 0;
        int leftBytes = 0; //剩余的byte
        sendlock.lock();
        try {
            if (data.length <= sendblockMax) {
                if (mOutputStream != null) {
                    try {
                        mOutputStream.write(data, 0, data.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
//                    Log.i(TAG, "usbPostData: " + data.length);
                }

            } else {
                leftBytes = data.length;//4097
                while (leftBytes > 0) {
                    if (leftBytes > sendblockMax) {
                        sendRequestSize = sendblockMax;
                    } else {
                        sendRequestSize = leftBytes;
                    }
                    if (mOutputStream != null) {
                        mOutputStream.write(data, (data.length - leftBytes), sendRequestSize);
                    }
                    leftBytes = leftBytes - sendRequestSize;//4097 - 4096 = 1
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sendlock.unlock();
        }
        return 0;
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

    public void setShuping(int shuping) {
        this.isShuPing = shuping;
    }

    public void notifyCarConnect() {
        isCarConnect = true;
        ScreenRecorderManager.getScreenRecorderManager(mContext).stopScreenRecorder();
    }

    public void notifyCarDisConnect() {
        isCarConnect = false;
        stopServerSocket();
    }

    public boolean isCarConnected() {
        return isCarConnect;
    }

    public void setIsAdbSocketConnect(boolean value) {
        isAdbSocketConnect = value;
        if (isAdbSocketConnect) {
            if (mServerSocket == null) {
                mServerSocket = MySocketServer.getInstance();
            }
        }
    }

    public void notifyRecordPermission(boolean value) {
        if (value) {
            mHasRecordPermission = 0x1;
        } else {
            mHasRecordPermission = 0x0;
            requestStartScreenRecord();
        }
    }

    private void requestStartScreenRecord() {
        final MediaProjectionManager manager
                = (MediaProjectionManager) mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        final Intent permissionIntent = manager.createScreenCaptureIntent();
        Activity activity = (Activity)mContext;
        activity.startActivityForResult(permissionIntent, ScreenRecorderManager.REQUEST_CODE_SCREEN_CAPTURE);
    }

    public void sendDataToQue(byte[] data) {
        try {
            mSendDataQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class SendDataThread extends BaseThread {

        @Override
        public void run() {
            try {
                while (isRunning) {
                    byte[] sendData = mSendDataQueue.take();
                    if (sendData != null && sendData .length > 0) {
                        usbPostData(sendData);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ThincarDevice getConnectDevice() {
        return mThincarDevice;
    }

    public void readDataFromStream(InputStream stream) {
        try {
            int len = stream.read(readbuffer);
            if (len > 0) {
                byte[] readData = new byte[len];
                System.arraycopy(readbuffer,0,readData,0,len);
                mReadDataQueue.put(readData);
            }
        } catch (IOException e) {
            LogUtils.i(TAG,"readDataFromStream IOException:" + e);
            if (mReceiveThread != null) {
                mReceiveThread.stopThread();
            }
        } catch (InterruptedException e2) {
            LogUtils.i(TAG,"readDataFromStream e2:" + e2);
        }
    }

    private void parseReadData(byte[] receiveData) throws InterruptedException{
        if (receiveData.length >= 24) {
            byte[] headData = new byte[24];
            System.arraycopy(receiveData,0,headData,0,24);
            /** 2.解析24字节 */
            MsgHeader header = buildHeader(headData, 24);

            if (header.len > 0) {/** 带有payload数据 */
                if (24 + header.len < receiveData.length) {
                    /** 本次payload数据全都读取出来，直接进行处理*/
                    byte[] contentData = new byte[header.len];
                    System.arraycopy(receiveData,24,contentData,0,header.len);
                    dispatchDataEvent(header,contentData);

                    int readLen = 24 + header.len;
                    int leftLen = receiveData.length - readLen;
                    byte[] leftData = new byte[leftLen];
                    System.arraycopy(receiveData,readLen,leftData,0,leftLen);
                    parseReadData(leftData);
                } else if (24 + header.len > receiveData.length) {
                    /** 本次payload数据没有读取出来，放到mLeftDataQueue中，继续读数据，直接payload数据全部读出才处理*/
                    mLeftDataQueue.clear();
                    mLeftDataQueue.offer(receiveData);
                } else {
                    /** 本次数据没有多的，不用再放到mLeftDataQueue中了 */
                    byte[] contentData = new byte[header.len];
                    System.arraycopy(receiveData,24,contentData,0,header.len);
                    dispatchDataEvent(header,contentData);
                }
            } else {/** 不带有payload数据 */
                dispatchDataEvent(header,null);

                int leftLen = receiveData.length - 24;
                byte[] leftData = new byte[leftLen];
                System.arraycopy(receiveData,24,leftData,0,leftLen);
                parseReadData(leftData);
            }
        } else {
            mLeftDataQueue.clear();
            mLeftDataQueue.offer(receiveData);
        }
    }

    public class ProcessReadDataThread extends BaseThread {

        @Override
        public void run() {
            try {
                while (isRunning) {
                    byte[] receiveData = mReadDataQueue.take();
                    if (receiveData.length > 0) {
                        byte[] leftData = mLeftDataQueue.poll();
                        if (leftData != null && leftData.length > 0) {
                            byte[] newData= new byte[receiveData.length + leftData.length];
                            System.arraycopy(leftData,0,newData,0,leftData.length);
                            System.arraycopy(receiveData,0,newData,leftData.length,receiveData.length);
                            parseReadData(newData);
                        } else {
                            parseReadData(receiveData);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 分发数据含有的事件和数据
     * @param header
     * @param data
     */
    private void dispatchDataEvent(MsgHeader header,byte[] data) {
        LogUtils.i("MainActivity", "dispatchDataEvent Sheader.MsgCommand:"+header.MsgCommand);
        LogUtils.i("MainActivity", "dispatchDataEvent Sheader.MsgParam:"+header.MsgParam);
        try {
            switch (header.MsgCommand) {
                case ThinCarDefine.ProtocolFromCarCommand.COMMON_EVENT_COMMAND:
                    if (header.MsgParam == ThinCarDefine.ProtocolFromCarParameter.AUTO_MSG_PARAM) {
                        handleMessageNext(header.unknow);
                    }
                    break;
                case ThinCarDefine.ProtocolFromCarCommand.UPDATE_MOUSE_COMMAND:
                    mThincarDevice = new ThincarDevice();
                    mThincarDevice.setCarWidth(header.getUnknow());
                    mThincarDevice.setCarHeight(header.getUnknow1());
                    mThincarDevice.setCarCode(header.getStartx());
                    changeDirectinValue(mThincarDevice);
                    LogUtils.i(TAG, "UPDATE_MOUSE_COMMAND ThincarDevice:" + mThincarDevice.toString());

                    ScreenRecorderManager.getScreenRecorderManager(mContext).startScreenCaptureIntent((Activity) mContext);
                    break;
            }

            if (mReceiveDataService == null) {
                return;
            }

            if (data == null) {
                mReceiveDataService.dispatchNullDataEvent(header);
            } else {
                mReceiveDataService.dispatchDataEvent(header,data);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void handleMessageNext(short unknow) {
        switch (unknow) {
            case ThinCarDefine.ProtocolFromCarAction.START_SCREEN_RECORDER:
                if (!androidVersionCheck() || isCarConnect) {
                    return;
                }

                LogUtils.i("MainActivity", "handlerMsg START_SCREEN_RECORDER mContext:"+mContext);
                mHandler.sendEmptyMessage(ThinCarDefine.ProtocolFromCarAction.START_SCREEN_RECORDER);
                break;
            case ThinCarDefine.ProtocolFromCarAction.STOP_SCREEN_RECORDER:
                mHandler.sendEmptyMessage(ThinCarDefine.ProtocolFromCarAction.STOP_SCREEN_RECORDER);
                break;
            case ThinCarDefine.ProtocolFromCarAction.RESTART_SCREEN_RECORDER:
                if (!androidVersionCheck() || isCarConnect) {
                    return;
                }
                mHandler.sendEmptyMessage(ThinCarDefine.ProtocolFromCarAction.RESTART_SCREEN_RECORDER);
                break;
            case ThinCarDefine.ProtocolFromCarAction.REQUEST_AOA_CHECK_RESULT:
                mIsCarClickBegin = false;
                if (!mHasAoaCheckPass) {
                    notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_AOC_CLICE_RECEIVED,
                            ThinCarDefine.AOAChecekResult.CHECK_FAIL,0);
                } else {
                    sendPhoneReady();
                }
                mAoaCheckListener.onAoaCheckFinish(mHasAoaCheckPass);
                break;
            case ThinCarDefine.ProtocolFromCarAction.NOTIFY_AOA_CLICK_BEGIN:
                    mIsCarClickBegin = true;
                break;
        }
    }

    private MsgHeader buildHeader(byte[] readbuffer, int length) {
        MsgHeader msg = new MsgHeader();
        ByteBuffer bb = ByteBuffer.wrap(readbuffer, 0, length);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        msg.MsgCommand = bb.getInt();
        msg.MsgParam = bb.getInt();
        msg.unknow = bb.getShort();
        msg.unknow1 = bb.getShort();
        msg.startx = bb.getShort();
        msg.starty = bb.getShort();
        msg.width = bb.getShort();
        msg.height = bb.getShort();
        msg.len = bb.getInt();

        LogUtils.i(TAG,"buildHeader  MsgCommand:" + msg.MsgCommand + "  MsgParam:" + msg.MsgParam + " msg.len:" + msg.len);
        return msg;
    }

    private void clearQueueData() {
        mSendDataQueue.clear();
        mReadDataQueue.clear();
        mLeftDataQueue.clear();
    }

    public boolean getIsCarClickBegin() {
        return mIsCarClickBegin;
    }

    public void stopAoaCheck() {
        mIsCarClickBegin = false;
    }

    public void registerReceiver() {
        mUsbAccReceiver = new UsbAccReceiver();
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(ACTION_USB_STATE);
        filter.setPriority(1000);
        try {
            mContext.registerReceiver(mUsbAccReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAdbSocketConnect() {
        return isAdbSocketConnect;
    }

    private LaunchActivityListener mLaunchActivityListener;

    public void setLaunchActivityListener(LaunchActivityListener listener) {
        mLaunchActivityListener = listener;
    }

    public void startMainActivity() {
        mLaunchActivityListener.startMainActivity();
    }

    public void sendCheckButtonRange(int width,int height) {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.NOTIFY_EVENT_TO_CAR_COMMAND;
        msg.MsgParam = ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_AOA_RANGE;
        msg.unknow = (short)isShuPing;
        msg.startx = (short) ThincarUtils.getSystemWidth(mContext);
        msg.starty = (short) ThincarUtils.getSystemHeight(mContext);
        msg.width = (short) width;
        msg.height = (short) height;
        ByteBuffer sendheader = objectToBuffer(msg);

        sendDataToQue(sendheader.array());
    }

    private boolean androidVersionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }

        return false;
    }

    /**
     * 给车机发送命令
     *
     * @param Event
     * @param P0
     * @param P1
     * @return
     */
    private int notifyCarNaviEvent(int Event, int P0, int P1) {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.NOTIFY_EVENT_TO_CAR_COMMAND;
        msg.MsgParam = Event;
        msg.startx = (short) P0;
        msg.starty = (short) P1;
        msg.len = 0;
        msg.unknow = 1;
        msg.width = (short) ThincarUtils.getSystemWidth(mContext);
        msg.height = (short) ThincarUtils.getSystemHeight(mContext);
        ByteBuffer sendheader = objectToBuffer(msg);

        sendDataToQue(sendheader.array());
        return 0;
    }

    private void sendPhoneReady() {
        // if (mInputStream != null) {
        sendAppId();
        sendDisplayInfo();
        //}

        int screenState = ThincarUtils.isScreenLock(mContext) ? ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_SCREEN_LOCK:ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_SCREEN_UNLOCK;
        notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.CONTROL_LIGHT_AVN_PARAM, ThinCarDefine.ProtocolToCarAction.LAUNCH_LE_RADION_ACTION, screenState);
    }

    /**
     * 发送屏幕信息给车机
     *
     * @return
     */
    private int sendDisplayInfo() {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.SEND_SCREEN_SIZE_COMMAND;
        msg.len = 0;
        msg.unknow = 1;
        msg.width = (short) ThincarUtils.getSystemWidth(mContext);
        msg.height = (short) ThincarUtils.getSystemHeight(mContext);
        ByteBuffer sendheader = objectToBuffer(msg);
        sendDataToQue(sendheader.array());
        return 0;
    }

    private void sendAppId() {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.EVENT_LEPHONE_SYNC_COMMAND;
        msg.MsgParam = ThinCarDefine.ProtocolToCarParam.LEPHONE_SYNC_APP_PARAM;
        msg.len = 0;
        msg.unknow = 1;
        ByteBuffer sendheader = objectToBuffer(msg);
        sendDataToQue(sendheader.array());
    }

    private void sendConnectWhenAdb() {
        MsgHeader msg = new MsgHeader();
        msg.MsgCommand = ThinCarDefine.ProtocolToCarCommand.ADB_CONNECTED_COMMAND;
        ByteBuffer sendheader = objectToBuffer(msg);
        sendDataToQue(sendheader.array());

        LogUtils.i(TAG,"startServerSocket sendConnectWhenAdb");
    }

    public void notifyAoaConnected() {
        try {
            if (mReceiveDataService != null) {
                mReceiveDataService.onAoaConnectStateChange(ThinCarDefine.ConnectState.STATE_CONNECT);
            } else {
                LogUtils.i(TAG,"notifyAoaConnected mReceiveDataService is null!!!");
            }
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    public void exitRecordProcess() {
        if (mAoaCheckListener != null) {
            mAoaCheckListener.finishActivity();
        }

        System.exit(0);
    }

    private void changeDirectinValue(ThincarDevice device) {
        isShuPing = device.getCarHeight() > device.getCarWidth() ? PORTRAIT_DIRECTION: LAND_DIRECTION;
    }
}