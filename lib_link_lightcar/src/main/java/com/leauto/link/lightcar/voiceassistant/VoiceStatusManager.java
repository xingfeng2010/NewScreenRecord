package com.leauto.link.lightcar.voiceassistant;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/17.
 *
 * 这个类用来管理空调，收音机相关状态
 */
public class VoiceStatusManager {
    private static final String TAG = "VoiceStatusManager";
    private static VoiceStatusManager ourInstance = new VoiceStatusManager();

    private Object mStatusLock = new Object();

    private ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();

    private static final int WAIT_TIME = 100;

    public static VoiceStatusManager getInstance() {
        return ourInstance;
    }

    private static final String JSON_KEY_VALUE = "value";
    private static final String JSON_KEY_COMMAND = "command";
    private static final String JSON_KEY_MIN = "min";
    private static final String JSON_KEY_MAX = "max";
    private static final String JSON_KEY_LEFT = "left";
    private static final String JSON_KEY_RIGHT = "right";

    private int mCurrentRadioStatus;//收音机是否打开
    private int mCurrentAirStatus;//空调是否打开
    private int mCurrenAcMode;//制冷，制热，或者自动
    private int mCurrentInOrOutMode;//内循环,外循环
    private int mCurrentWindDirection;//风向
    private int mCurrentWindSpeed = 20;//当前风速
    private int mAirCurrentTem = 20;//当前空调温度

    private int minTem = 16;//最低温度
    private int maxTem = 35;//最高温度

    private int maxSpeed = 9;//最大风速
    private int minSpeed = 2;//最小风速

    private String[] mQueryStatusArray = new String[] {
            VoiceQueryStatusDefine.AC_STATUS_COMMAND,
            VoiceQueryStatusDefine.AC_MODE_COMMAND,
            VoiceQueryStatusDefine.AC_WIND_MODE_COMMAND,
            VoiceQueryStatusDefine.AC_WIND_DIRECTION_COMMAND,
            VoiceQueryStatusDefine.AC_WIND_SPEED_COMMAND,
            VoiceQueryStatusDefine.AC_TEMP_RANGE_COMMAND,
            VoiceQueryStatusDefine.AC_CURRENT_TEMP_COMMAND
    };

    private VoiceStatusManager() {
    }

    private void sendDataToCar(String command) {
        Map<String,Object> map = new HashMap<>();
        map.put("Type","Interface_Request");
        map.put("Method",VoiceQueryStatusDefine.VOICE_QUERY_METHOD);

        Map<String,Object> content = new HashMap<>();
        content.put("command",command);
        content.put("parameter ","");

        map.put("Parameter",content);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(map);
        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.VOICE_ASSISTANT_APPID,jsonObject);
    }

    public void parseVoiceStateFromData(String str) {
        fixedThreadPool.submit(new ParseVoiceStatusThread(str));
    }

    private void parseStatusFromData(String str) {
        try {
            org.json.JSONObject object = new org.json.JSONObject(str);
            org.json.JSONObject parameter = object.getJSONObject("Parameter");
            String command = parameter.getString(JSON_KEY_COMMAND);
            switch (command) {
                case VoiceQueryStatusDefine.AC_STATUS_COMMAND:
                    mCurrentAirStatus = parameter.optInt(JSON_KEY_VALUE);
                    LogUtils.i(TAG,"parseStatusFromData mCurrentAirStatus:" + mCurrentAirStatus);
                    break;
                case VoiceQueryStatusDefine.AC_MODE_COMMAND:
                    mCurrenAcMode = parameter.optInt(JSON_KEY_VALUE);
                    break;
                case VoiceQueryStatusDefine.AC_WIND_MODE_COMMAND:
                    mCurrentInOrOutMode = parameter.optInt(JSON_KEY_VALUE);
                    break;
                case VoiceQueryStatusDefine.AC_WIND_DIRECTION_COMMAND:
                    mCurrentWindDirection = parameter.optInt(JSON_KEY_VALUE);
                    break;
                case VoiceQueryStatusDefine.AC_WIND_SPEED_COMMAND:
                    mCurrentWindSpeed = parameter.optInt(JSON_KEY_VALUE);
                    break;
                case VoiceQueryStatusDefine.AC_TEMP_RANGE_COMMAND:
                    minTem = parameter.optInt(JSON_KEY_MIN);
                    maxTem = parameter.optInt(JSON_KEY_MAX);
                    break;
                case VoiceQueryStatusDefine.AC_CURRENT_TEMP_COMMAND:
                    /** ?? 此处暂定为左出风口温度 */
                    mAirCurrentTem = parameter.optInt(JSON_KEY_LEFT);
                    LogUtils.i(TAG,"parseStatusFromData mAirCurrentTem:" + mAirCurrentTem);
                    break;
                case VoiceQueryStatusDefine.RADIO_STATUS_COMMAND:
                    mCurrentRadioStatus = parameter.optInt(JSON_KEY_VALUE);
                    LogUtils.i(TAG,"parseStatusFromData mCurrentRadioStatus:" + mCurrentRadioStatus);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            LogUtils.i(TAG,"parseStatusFromData mStatusLock.notify time:" + System.currentTimeMillis());
            mStatusLock.notify();
        }
    }

    public int getCurrentAirStatus() {
        sendDataToCar(VoiceQueryStatusDefine.AC_STATUS_COMMAND);
        LogUtils.i(TAG,"getCurrentAirStatus time:" + System.currentTimeMillis());
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LogUtils.i(TAG,"getCurrentAirStatus mCurrentAirStatus:" + mCurrentAirStatus);
        return mCurrentAirStatus;
    }

    public int getCurrenAcMode() {
        sendDataToCar(VoiceQueryStatusDefine.AC_MODE_COMMAND);
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mCurrenAcMode;
    }


    public int getCurrentInOrOutMode() {
        sendDataToCar(VoiceQueryStatusDefine.AC_WIND_MODE_COMMAND);
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mCurrentInOrOutMode;
    }

    public int getCurrentWindDirection() {
        sendDataToCar(VoiceQueryStatusDefine.AC_WIND_DIRECTION_COMMAND);
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mCurrentWindDirection;
    }

    public int getCurrentWindSpeed() {
        sendDataToCar(VoiceQueryStatusDefine.AC_WIND_SPEED_COMMAND);
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mCurrentWindSpeed;
    }

    public int getAirCurrentTem() {
        sendDataToCar(VoiceQueryStatusDefine.AC_CURRENT_TEMP_COMMAND);
        LogUtils.i(TAG,"getAirCurrentTem time:" + System.currentTimeMillis());
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LogUtils.i(TAG,"getAirCurrentTem mAirCurrentTem:" + mAirCurrentTem);
        return mAirCurrentTem;
    }

    public boolean isMaxWindSpeed() {
        sendDataToCar(VoiceQueryStatusDefine.AC_WIND_SPEED_COMMAND);
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mCurrentWindSpeed == maxSpeed;
    }

    public boolean isMinWindSpeed() {
        sendDataToCar(VoiceQueryStatusDefine.AC_WIND_SPEED_COMMAND);
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mCurrentWindSpeed == minSpeed;
    }

    public int getCurrentRadioStatus() {
        sendDataToCar(VoiceQueryStatusDefine.RADIO_STATUS_COMMAND);
        try {
            synchronized (mStatusLock) {
                mStatusLock.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LogUtils.i(TAG,"getCurrentAirStatus mCurrentRadioStatus:" + mCurrentRadioStatus);
        return mCurrentRadioStatus;
    }

    public class ParseVoiceStatusThread implements Runnable {
        private String mJsonStr;
        public ParseVoiceStatusThread(String str) {
            mJsonStr = str;
        }

        @Override
        public void run() {
            parseStatusFromData(mJsonStr);
        }
    }
}
