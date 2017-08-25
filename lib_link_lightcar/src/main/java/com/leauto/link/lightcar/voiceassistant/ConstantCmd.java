package com.leauto.link.lightcar.voiceassistant;


import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 2016/12/9.
 */
public class ConstantCmd {

    /**
     * key Type
     */
    public static final String KEY_TYPE = "Type";

    /**
     * key Method
     */
    public static final String KEY_METHOD = "Method";

    /**
     * key Method
     */
    public static final String KEY_PARAMETER = "Parameter";


    /**
     *  notify
     */
    public static final String CMD_NOTIFY = "Interface_Notify";
    /**
     * request
     */
    public static final String CMD_REQUEST = "Interface_Request";
    /**
     * response
     */
    public static final String CMD_RESPONSE = "Interface_Response";


    /**
     * method: StartVoiceAssistant 启动语音助手
     */
    public static final String METHOD_START_VOICE_ASSISTANT = "StartVoiceAssistant";

    /**
     * method: StopVoiceAssistant    退出语音助手
     */
    public static final String METHOD_STOP_VOICE_ASSISTANT = "StopVoiceAssistant";

    /**
     * method: StartVoiceRecord   开始录音
     */
    public static final String METHOD_START_VOICE_RECORD = "StartVoiceRecord";

    /**
     * method: DetectVoiceInput  检测到有人开始说话
     */
    public static final String METHOD_DETECT_VOICE_INPUT = "DetectVoiceInput";

    /**
     * method: DetectNoVoiceInput  检测到说话结束
     */
    public static final String METHOD_DETECT_VOICE_NO = "DetectNoVoiceInput";

    /**
     * method: StopVoiceRecord  结束录音
     */
    public static final String METHOD_STOP_VOICE_RECORD = "StopVoiceRecord";

    /**
     * method: StartTTS     TTS播报
     */
    public static final String METHOD_START_TTS = "StartTTS";

    /**
     * method: StartSearching     开始搜索
     */
    public static final String METHOD_START_SEARCHING = "StartSearching";

    /**
     * method: DisplayTextInfo  显示文本内容
     */
    public static final String METHOD_DISPLAY_TEXTINFO = "DisplayTextInfo";

    /**
     * method: VoiceCommand   手机发给车机的控制指令
     */
    public static final String METHOD_VOICE_COMMAND = "VoiceCommand";

    /**
     * parameter: command   手机发给车机指令
     */
    public static final String PARAM_COMMAND = "command";

    /**
     * parameter: parameter   参数
     */
    public static final String PARAM_PARAMETER = "parameter";

    /**
     * parameter: text   文本信息
     */
    public static final String PARAM_TEXT = "text";


    /**
     * 构造Request消息
     * @param method
     * @param params
     * @return
     */
    public static JSONObject buildRequest(String method, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TYPE, CMD_REQUEST);
        map.put(KEY_METHOD, method);
        if (params == null) {
            map.put(KEY_PARAMETER, "");
        } else {
            map.put(KEY_PARAMETER, params);
        }


        JSONObject obj = new JSONObject(map);
        return obj;
    }

        /**
     * 构造Response消息
     * @param method
     * @param params
     * @return
     */
    public static JSONObject buildResponse(String method, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TYPE, CMD_RESPONSE);
        map.put(KEY_METHOD, method);
        if (params == null) {
            map.put(KEY_PARAMETER, "");
        } else {
            map.put(KEY_PARAMETER, params);
        }


        JSONObject obj = new JSONObject(map);
        return obj;
    }

    /**
     * 构造Notify消息
     * @param method
     * @param params
     * @return
     */
    public static JSONObject buildNotify(String method, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TYPE, CMD_NOTIFY);
        map.put(KEY_METHOD, method);
        if (params == null) {
            map.put(KEY_PARAMETER, "");
        } else {
            map.put(KEY_PARAMETER, params);
        }

        JSONObject obj = new JSONObject(map);
        return obj;
    }

}
