package com.leauto.link.lightcar;
import android.os.Trace;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.leauto.link.lightcar.module.AVNInfo;
import com.leauto.link.lightcar.privatedata.PrivateData;
import com.leauto.link.lightcar.privatedata.PrivateDataHandler;
import com.leauto.link.lightcar.privatedata.PrivateMsgHeader;
import com.leauto.link.lightcar.voiceassistant.VoiceQueryStatusDefine;
import com.leauto.link.lightcar.voiceassistant.VoiceStatusManager;
import com.leauto.sdk.base.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Administrator on 2016/9/19.
 */
public class CarDataParseUtil {
    private static final String TAG = "CarDataParseUtil";
    public static final String SPLIT_MARK = " ";

    public static void parseCarData(short appid,byte[] data, IAOACallback callback) {
        //header头24字节 21
        PrivateData privateData = new PrivateDataHandler().getPrivateData(data);
//        byte[] datas=new byte[data.length-21];
//        for(int i=21;i<data.length;i++){
//            datas[i-21]=data[i];
//        }
        if(privateData.getPrivateMsgHeader().getType()== PrivateMsgHeader.TYPE_STRING){
            String string = new String(privateData.getValidData());
            parseCarDataAccordAppId(privateData.getPrivateMsgHeader().getAppId(),string,callback);
            LogUtils.d(TAG, "appID="+privateData.getPrivateMsgHeader().getAppId()+";String:"+string);
        }else if(privateData.getPrivateMsgHeader().getType()== 4){  //车机端定义的4
            delaWithCanData(privateData);
        }else{
            if (appid == ThinCarDefine.ProtocolAppId.ADB_DEVICE_APPID) {
                byte[] content = privateData.getValidData();
                ByteBuffer bb = ByteBuffer.wrap(content);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                callback.postAdbDeviceInfo(bb.getInt(),bb.getInt(),bb.getInt());
             }else if(appid== ThinCarDefine.ProtocolAppId.DRVIE_APPID){
                ByteBuffer bb = ByteBuffer.wrap(privateData.getValidData());
                byte value = bb.get();
                //走行规制 复用onCommand接口
                callback.onCommand(ThinCarDefine.ProtocolAppId.DRVIE_APPID, value);
            }
        }

    }

    public static void parseCarDataAccordAppId(short appid,String string,IAOACallback callback) {
        LogUtils.d("thincar", "appID="+appid+"  string ="+string);
        switch (appid){
            case ThinCarDefine.UpgradeAppId.OTA_VERSION_REP:
                dealWithOTAInfo(string,callback);
                break;
            case ThinCarDefine.UpgradeAppId.OTA_FILE_DATA_RSP:
                dealWithOTAUpdataInfo(string,callback);
                break;
            case ThinCarDefine.UpgradeAppId.OTA_UPDATE_RSP:
                dealWithOtaUpdateRsp(string,callback);
                break;
            case ThinCarDefine.UpgradeAppId.OTA_NOWIFI_CONT_RSP:
                dealWithOtaNowifiRsp(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.THIRD_APP_APPID:
                parseThirdAppData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.LE_RADIO_APPID:
                parseLeRadionData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.USER_ACCOUNT_APPID:
                parseUserAccountData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.DEVICE_INFO_APPID:
                parseDeviceInfoData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.WELCOME_PAGE_APPID:
                parseWelcomPageData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.VOICE_ASSISTANT_APPID:
                parseVoiceAssitantData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.NAVI_BAR_APPID:
                parseNaviBarData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.BLUETOOTH_APPID:
                parseBluetoothData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.NAVI_APPID:
                parseNaviInfoData(string,callback);
                break;
            case ThinCarDefine.ProtocolAppId.OTA_APPID:
                //定义为ota升级，不知道why又用来表示CAN数据
                parseCANStringData(string, callback);
                break;
        }
    }

    private static void dealWithOtaNowifiRsp(String string, IAOACallback callback) {
        callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.NOTIFY_OTA_NOWIFI_RSP,string);
    }

    private static void dealWithOtaUpdateRsp(String string, IAOACallback callback) {
        callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.NOTIFY_CAR_UPDATE_ACCEPT,string);

    }

    private static void parseNaviInfoData(String string, IAOACallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_HUD_ACTION.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_HUD_ACTION,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseBluetoothData(String string, IAOACallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_PHONE_BOOK.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_PHONE_BOOK,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_CALL_HISTORY.equals(method)) {
                JSONObject parameter = jsonObject.getJSONObject("Parameter");
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_CALL_HISTORY, parameter.optString("upper_limit"));
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_BLUE_CONNECT.equals(method)) {
                StringBuilder sb = new StringBuilder();
                JSONObject parameter = jsonObject.getJSONObject("Parameter");
                String name = parameter.optString("bt_name");
                String macAddr = buildMacAddress(parameter);
                LogUtils.i(TAG,"buildMacAddress result macAddr:" + macAddr);
                sb.append(name).append(SPLIT_MARK).append(macAddr);
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_BLUE_CONNECT, sb.toString());
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_BLUE_INFO.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_BLUE_INFO, "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String buildMacAddress(JSONObject parameter) throws JSONException{
        LogUtils.i(TAG,"buildMacAddress before:" + parameter.toString());
        String add0 = Integer.toHexString(parameter.optInt("bt_addr[5]"));
        String add1 = Integer.toHexString(parameter.optInt("bt_addr[4]"));
        String add2 = Integer.toHexString(parameter.optInt("bt_addr[3]"));
        String add3 = Integer.toHexString(parameter.optInt("bt_addr[2]"));
        String add4 = Integer.toHexString(parameter.optInt("bt_addr[1]"));
        String add5 = Integer.toHexString(parameter.optInt("bt_addr[0]"));

        StringBuilder sb = new StringBuilder(add0);
        sb.append(":").append(add1).append(":").
                append(add2).append(":").
                append(add3).append(":").
                append(add4).append(":").
                append(add5);

        return sb.toString().toUpperCase().trim();
    }

    private static void parseNaviBarData(String string, IAOACallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_NAVI_BAR_INFO.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_NAVI_BAR_INFO,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_IS_IN_NAVI.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_IS_IN_NAVI,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_START_NAVI.equals(method)) {
                JSONObject parameter = jsonObject.getJSONObject("Parameter");
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_START_NAVI,parameter.optString("StartNaviType"));
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_STOP_NAVI.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_STOP_NAVI,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_START_PREVIEW.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_START_PREVIEW,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_STOP_PREVIEW.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_STOP_PREVIEW,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_QUICK_SEARCH.equals(method)) {
                JSONObject parameter = jsonObject.getJSONObject("Parameter");
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_QUICK_SEARCH,parameter.optString("QuickSearchItem"));
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_SETTING_ADDRESS.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_SETTING_ADDRESS,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_SETTING_HOME.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_SETTING_HOME,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_SETTING_WORK.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_SETTING_WORK,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseVoiceAssitantData(String string, IAOACallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_START_VOICE.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_START_VOICE,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_START_RECORD.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_START_RECORD,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_DETECT_VOICE.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_DETECT_VOICE,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_DETECT_NOVOICE.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_DETECT_NOVOICE,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_STOP_RECORD.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_STOP_RECORD,"");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_STOP_VOICE.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_STOP_VOICE,"");
            } else if (VoiceQueryStatusDefine.VOICE_QUERY_METHOD.equals(method)) {
                VoiceStatusManager.getInstance().parseVoiceStateFromData(string);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseWelcomPageData(String string, IAOACallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_WELCOME_INFO.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_WELCOME_INFO,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseDeviceInfoData(String string, IAOACallback callback) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_PHONE_INFO.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_PHONE_INFO,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseUserAccountData(String string, IAOACallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_USER_INFO.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUESET_USER_INFO,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void dealWithBlueInfo(byte[] data, IAOACallback callback) {
/*        byte[] bytes = new byte[data.length];

        for (int i = 24; i < data.length; i++) {
            bytes[i - 24] = data[i];
            Log.i("TAG", "dealWithBlueInfo: "+i+"》》》"+data[i]);
        }*/

        String string = new String(data);
        try {
            JSONObject jsonObject = new JSONObject(string);
            LogUtils.i("TAG", "车机返回的信息：" + jsonObject.toString());

            String name = jsonObject.optString("AVN_BT_NAME");

            LogUtils.i("TAG", "车机返回名称：" + name);
            int ADDR_0 = jsonObject.optInt("AVN_BT_ADDR_0");
            int ADDR_1 = jsonObject.optInt("AVN_BT_ADDR_1");
            int ADDR_2 = jsonObject.optInt("AVN_BT_ADDR_2");
            int ADDR_3 = jsonObject.optInt("AVN_BT_ADDR_3");
            int ADDR_4 = jsonObject.optInt("AVN_BT_ADDR_4");
            int ADDR_5 = jsonObject.optInt("AVN_BT_ADDR_5");

            String s5= Integer.toHexString(ADDR_5);
            String s4= Integer.toHexString(ADDR_4);
            String s3= Integer.toHexString(ADDR_3);
            String s2= Integer.toHexString(ADDR_2);
            String s1= Integer.toHexString(ADDR_1);
            String s0= Integer.toHexString(ADDR_0);

            StringBuffer buffer=new StringBuffer();
            buffer.append(s5);
            buffer.append(":"+s4);
            buffer.append(":"+s3);
            buffer.append(":"+s2);
            buffer.append(":"+s1);
            buffer.append(":"+s0);

            LogUtils.i("TAG",buffer.toString().toUpperCase());

            callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.NOTIFY_BLUETOOTH_MAC, buffer.toString().toUpperCase());

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        ArrayList<String> datas = new ArrayList<>();
//        byte[] name = new byte[32];//存储设备名字
//        byte[] mac = new byte[6];//存储设备名字
//        for (int i = 24; i < 56; i++) {//名字为24--55
//            name[i - 24] = data[i];
//        }
//        for (int i = 57; i < 63; i++) {//Mac为57--62
//            mac[i - 57] = data[i];
//        }
//        //根据byte16进制数，转为String类型并加“:”
//        String value = "";
//        for (int i = 0; i < mac.length; i++) {
//            String sTemp = Integer.toHexString(0xFF & mac[i]);
//            if (sTemp.length() == 1) {
//                sTemp = "0" + sTemp;//为1位数时前边补0
//            }
//            value = sTemp + ":"+ value ;//把值添加到前边
//        }
//        value = value.substring(0, value.lastIndexOf(":"));//截取mac地址
//        String str_name = new String(name);//转换设备名字
//        datas.add(str_name);
//        datas.add(value);
//        callback.NotifyEvent(ThinCarDefine.Notify_BlueTooth_Mac,value.toUpperCase());
    }

    private static void dealWithOTAInfo(String string, IAOACallback callback) {
        callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.NOTIFY_CAR_OTAINFO,string);
    }
    //车机给手机发送的
    private static void dealWithOTAUpdataInfo(String string, IAOACallback callback) {
        callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.NOTIFY_CAR_UPDATE_INFO,string);
    }

    //解析车机发送的来三方app请求数据
    private static void parseThirdAppData(String string, IAOACallback callback) {
        StringBuilder sb = new StringBuilder();
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_START_APP.equals(method)) {
                JSONObject parameter = jsonObject.getJSONObject("Parameter");
                sb.append(parameter.optString("appid")).append(SPLIT_MARK)
                        .append(parameter.optString("pageid"));
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.LAUNCH_THIRD_APP, sb.toString());
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_APPICON.equals(method)) {
                JSONObject parameter = jsonObject.getJSONObject("Parameter");
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.APP_REQUEST_PIC, parameter.optString("iconid"));
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_ALL_APP_INFO.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_ALL_APP_INFO, "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseLeRadionData(String string, IAOACallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_ALBUM_INFO.equals(method)) {
                JSONObject parameter = jsonObject.optJSONObject("Parameter");
                String songId = parameter.optString("albumid");
                String position = parameter.optString("position");
                StringBuilder sb = new StringBuilder();
                sb.append(songId).append(SPLIT_MARK).append(position);
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_ALBUM_INFO,sb.toString());
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_SONG_IMAGE.equals(method)) {
                JSONObject parameter = jsonObject.optJSONObject("Parameter");
                String imageID = parameter.optString("imageID");
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_SONG_IMAGE, imageID);
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_PLAYER_ACTION.equals(method)) {
                StringBuilder sb = new StringBuilder();
                JSONObject parameter = jsonObject.optJSONObject("Parameter");
                String albumID = parameter.optString("albumid");
                String action = parameter.optString("action");
                sb.append(albumID).append(SPLIT_MARK).append(action);
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_PLAYER_ACTION, sb.toString());
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_ALL_SONG_INFO.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_ALL_SONG_INFO, "");
            } else if (ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_PLAYER_STATUS.equals(method)) {
                callback.NotifyEvent(ThinCarDefine.ProtocolNotifyValue.REQUEST_PLAYER_STATUS, "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理CAN的字符串数据(avn数据和CAN的request)
     * @param string
     * @param callback
     */
    private static void parseCANStringData(String string, IAOACallback callback){
        try {
            JSONObject jsonObject = new JSONObject(string);
            String method = jsonObject.optString("Method");
            if(TextUtils.isEmpty(method)){
                //数据有错误
                LogUtils.e(TAG, "数据有问题");
                return;
            }
            if(method.equals(ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_AVN_INFO)){
                //avn数据
                AVNInfo avnInfo = new AVNInfo();
                JSONObject parameterJson = jsonObject.optJSONObject("Parameter");
                avnInfo.setVin(parameterJson.optString("vin"));
                avnInfo.setSn(parameterJson.optString("sn"));
                avnInfo.setHwver(parameterJson.optString("hwver"));
                avnInfo.setPartnum(parameterJson.optString("partnum"));
                avnInfo.setMode(parameterJson.optString("mode"));
                avnInfo.setSwver(parameterJson.optString("swver"));
                if (callback != null) {
                    callback.onAVNInfo(avnInfo);
                } else {
                    LogUtils.e(TAG,"parseCANStringData onAVNInfo callback is null!!!");
                }
            }else if(method.equals(ThinCarDefine.ProtocolNotifyMethod.METHOD_REQUEST_CAN_FILE_TRANSMIT)){
                //request can file
                JSONObject parameterJson = jsonObject.optJSONObject("Parameter");
                sessionCanPath = parameterJson.optString("name");
                sessionCanSize = parameterJson.optLong("size");
                if (callback != null) {
                    callback.onCANFileTransmit(sessionCanPath, sessionCanSize);
                } else {
                    LogUtils.e(TAG,"parseCANStringData onCANFileTransmit callback is null!!!");
                }
            }else{
                //不知道什么method
                LogUtils.e(TAG, method+":此方法未定义");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //can 数据的session,用于保存文件用
    private static String sessionCanPath;
    private static long sessionCanSize;
    private static String CAN_PATH = Environment.getExternalStorageDirectory() + "/Ecolink/Can/";

    /**
     *  处理传输过来的CAN数据, 保存文件
     * @param privateData
     */
    private static void delaWithCanData(PrivateData privateData){
        try {
            File fileDir = new File(CAN_PATH);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }
            if(!TextUtils.isEmpty(sessionCanPath)){
                File file = new File(CAN_PATH + sessionCanPath);
                if(!file.exists()){
                    file.createNewFile();
                }
                FileUtil.writeToFile(CAN_PATH + sessionCanPath, privateData.getValidData(), true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}