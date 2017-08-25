package com.leauto.link.lightcar.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.leauto.link.lightcar.CarDataParseUtil;
import com.leauto.link.lightcar.IAOACallback;
import com.leauto.link.lightcar.IReceiveDataInterface;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.MsgHeader;
import com.leauto.link.lightcar.ScreenRecordActivity;
import com.leauto.link.lightcar.ScreenRecorderManager;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.module.ThincarDevice;
import com.leauto.link.lightcar.protocol.DataSendManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 这个是位于主应用进程的service，用于读取
 * 从车机传来过的事件给应用进行处理
 */
public class ReceiveDataService extends Service {
    private static final String TAG = "ReceiveDataService";

    public static int DEFAULT_COMMEAN_VALUE = -1;
    private ReceiveDataBinder binder = new ReceiveDataBinder(this);

    public ReceiveDataService() {
    }

    private IAOACallback mIAOACallback;

    private MsgHeader mNaviEvenHeader;
    private int mCommandEvenHeader = DEFAULT_COMMEAN_VALUE;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setIAOACallback(IAOACallback back) {
        mIAOACallback = back;
    }

    public MsgHeader getNaviEvenHeader() {
        return mNaviEvenHeader;
    }

    public int getCommandEvenHeader() {
        return mCommandEvenHeader;
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
            processDataEvent(header, data);
        }

        @Override
        public void dispatchNullDataEvent(MsgHeader header) throws RemoteException {
            processDataEvent(header, null);
        }

        @Override
        public void startScreenRecordActivity() {
            if (mIAOACallback != null) {
                mIAOACallback.startScreenRecordActivity();
            } else {
                LogUtils.i(TAG, "startScreenRecordActivity mIAOACallback is null");
            }
        }

        @Override
        public void onAoaConnectStateChange(int state) {
            if (mIAOACallback != null) {
                mIAOACallback.onAoaConnectStateChange(state);
            } else {
                LogUtils.i(TAG, "onAoaConnectStateChange mIAOACallback is null");
            }
        }

        @Override
        public void onAdbConnectStateChange(int state) {
            if (mIAOACallback != null) {
                mIAOACallback.onAdbConnectStateChange(state);
            } else {
                LogUtils.i(TAG, "onAdbConnectStateChange mIAOACallback is null");
            }
        }

        /**
         * 消息处理和过滤
         *
         * @param command
         * @param params
         * @param params2
         */
        public void handlerMsg(int command, short params, short params2) {
            LogUtils.i(TAG, "handlerMsg command:" + command + "::params" + params);
            if (command == ThinCarDefine.ProtocolFromCarParameter.AUTO_MSG_PARAM) {
                switch (params) {
                    case ThinCarDefine.ProtocolFromCarAction.START_SCREEN_RECORDER:
                    case ThinCarDefine.ProtocolFromCarAction.STOP_SCREEN_RECORDER:
                    case ThinCarDefine.ProtocolFromCarAction.RESTART_SCREEN_RECORDER:
                        break;
                    case ThinCarDefine.ProtocolFromCarAction.REQUEST_AOA_CHECK_RESULT:
//                        mIsCarClickBegin = false;
//                        if (!mHasFinishAoaCheck) {
//                            notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_AOC_CLICE_RECEIVED,
//                                    ThinCarDefine.AOAChecekResult.CHECK_FAIL,0);
//                        } else {
//                            sendPhoneReady();
//                        }
//                        mIAOACallback.onAoaCheckFinish();
                        break;
                    case ThinCarDefine.ProtocolFromCarAction.NOTIFY_AOA_CLICK_BEGIN:
                      //  mIsCarClickBegin = true;
                        break;
                    default:
                        //让上层APP处理
                        LogUtils.i(TAG, "processDataEvent handlerMsg mIAOACallback:"  + mIAOACallback);
                        if (mIAOACallback != null) {
                            mCommandEvenHeader = DEFAULT_COMMEAN_VALUE;
                            mIAOACallback.onCommand(params, params2);
                        } else {
                            mCommandEvenHeader = params;
                        }
                        break;
                }
            }
        }

        private short parseAppid(byte[] data, int len) {
            ByteBuffer bb = ByteBuffer.wrap(data, 0, len);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            bb.get();
            bb.get();

            return bb.getShort();
        }

        private void processDataEvent(MsgHeader header, byte[] data) {
            switch (header.MsgCommand) {
                case ThinCarDefine.ProtocolFromCarCommand.COMMON_EVENT_COMMAND:
                    if (header.MsgParam == ThinCarDefine.ProtocolFromCarParameter.AUTO_MSG_PARAM) {
                        handlerMsg(header.MsgParam, header.unknow, header.unknow1);
                    } else if (header.MsgParam == ThinCarDefine.ProtocolFromCarParameter.NAVI_WINDOW_PARAM) {
                        // mRequestHeight = header.height;
                        LogUtils.i(TAG, "processDataEvent 000 mIAOACallback:"  + mIAOACallback);
                        if (mIAOACallback != null) {
                            mNaviEvenHeader = null;
                            mIAOACallback.onNaviEvent(header.startx, header.starty, header.width, header.height);
                        } else {
                            mNaviEvenHeader = header;
                        }
                    } else if (header.MsgParam == ThinCarDefine.ProtocolFromCarParameter.MSG_TO_ECOLINK_PARAM_GESTRUE) {
                        if (mIAOACallback != null) {
                            mIAOACallback.notifyGesterEvent(header.unknow, header.startx, header.starty, header.width);
                        }
                    }
                    break;
                case ThinCarDefine.ProtocolFromCarCommand.STOP_AUDIO_COMMAND:
                    //mIAOACallback.onPcmPath(targetPath);
                    break;
                case ThinCarDefine.ProtocolFromCarCommand.BT_COMMAND:
                    switch (header.MsgParam) {
                        case ThinCarDefine.ProtocolFromCarParameter.BT_DBT_INFO_PARAM://蓝牙地址
                            CarDataParseUtil.dealWithBlueInfo(data, mIAOACallback);
                            break;
                        case ThinCarDefine.ProtocolFromCarParameter.BT_DISCONNECT_RESULT_PARAM:
                            if (header.unknow == 0) {
                                mIAOACallback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.NOTIFY_CARBLUT_DISCON, null);
                            } else if (header.unknow == -1) {
                            }
                            break;
                    }
                    break;
                case ThinCarDefine.ProtocolToCarCommand.SEND_CAR_DATA_COMMAND:
                    /** 手机与车通讯数据在这里处理，数据中含有封装的OtaMsgHeader 21字节，故多读21字节*/
                    if (header.len > 0) {
                        /** 从21头部数据中解析appid */
                        short appid = parseAppid(data, header.len);
                        if (mIAOACallback != null) {
                            CarDataParseUtil.parseCarData(appid, data, mIAOACallback);
                        } else {
                            LogUtils.i(TAG, "processDataEvent SEND_CAR_DATA_COMMAND mIAOACallback is null !!!");
                        }
                    }
                    break;
                case ThinCarDefine.ProtocolFromCarCommand.SYNC_PCM_DATA_COMMAND:
                    if (header.len > 0) {
                        mIAOACallback.onViceDataObtain(data, header.len);
                    }
                    break;
            }
        }
    }
}
