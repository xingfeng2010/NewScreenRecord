package com.leauto.link.lightcar.voiceassistant;

/**
 * 语音控制瘦车相关command定义
 *
 * Created by Administrator on 2017/2/21.
 */

public class VoiceCommandDefine {
    /**
     * 页面跳转
     */
    public static class RedirectToPage {
        public static final String VOICE_CMD_REDIRECT = "RedirectToPage";

        public static class RedirectToPageParam {
            public static final String VOICE_PARAM_LERADIO = "LeRadio";
            public static final String VOICE_PARAM_LELIVE = "LeLive";
            public static final String VOICE_PARAM_VOICEASSIST = "VoiceAssistant";
            public static final String VOICE_PARAM_LENAVI = "LeNavi";
        }
    }

    /**
     * 打开收音机
     */
    public static final String VOICE_CMD_OPEN_RADIO = "OpenRadio";

    /**
     * 自动搜台
     */
    public static final String VOICE_CMD_AUDO_SEEK = "AutoSeek";


    /**
     * 改变频道
     */
    public static class ChangeRadioChannel {
        public static final String VOICE_CMD_CHANGE_RADIO_CHANNEL = "ChangeRadioChannel";

        public static class ChangeRadioChannelParam {
            /**
             * 上一个频道
             */
            public static final String VOICE_PARAM_LAST = "Last";

            /**
             * 下一个频道
             */
            public static final String VOICE_PARAM_NEXT = "Next";
        }
    }

    /**
     * 设置频道
     */
    public static class SetRadioChannel {
        public static final String VOICE_CMD_SET_RADIO_CHANNEL = "SetRadioChannel";

        public static class SetRadioChannelParam {
            public static final String VOICE_PARAM_DEFAULT = "Default";
            public static final String VOICE_PARAM_FM = "FM";
            public static final String VOICE_PARAM_AM = "AM";
        }
    }

    /**
     * 关闭收音机
     */
    public static final String VOICE_CMD_CLOSE_RADIO = "CloseRadio";

    /**
     * 打开空调
     */
    public static final String VOICE_CMD_OPEN_AC = "OpenAC";

    /**
     * 关闭空调
     */
    public static final String VOICE_CMD_CLOSE_AC = "CloseAC";

    /**
     * 调高温度
     */
    public static final String VOICE_CMD_HIGHER_TEMP = "HigherTemp";

    /**
     * 调低温度
     */
    public static final String VOICE_CMD_LOWER_TEMP = "LowerTemp";

    /**
     * 空调加热
     */
    public static final String VOICE_CMD_HEAT_MODE = "HeatMode";

    /**
     * 空调制冷
     */
    public static final String VOICE_CMD_COOL_MODE = "CoolMode";

    /**
     * 自动模式
     */
    public static final String VOICE_CMD_AUTO_MODE = "AutoMode";

    /**
     * 内循环
     */
    public static final String VOICE_CMD_INNER_LOOP = "InnerLoop";

    /**
     * 外循环
     */
    public static final String VOICE_CMD_OUTER_LOOP = "OuterLoop";

    /**
     * 向上吹风
     */
    public static final String VOICE_CMD_UPWARD_BLOW = "UpwardBlowing";

    /**
     * 向下吹风
     */
    public static final String VOICE_CMD_DOWN_BLOW = "DownwardBlowing";

    /**
     * 水平吹风
     */
    public static final String VOICE_CMD_HORIZONTAL_BLOW = "HorizontalBlowing";

    /**
     * 增大风速
     */
    public static final String VOICE_CMD_INCREASE_BLOW = "IncreaseWindSpeed";

    /**
     * 减小风速
     */
    public static final String VOICE_CMD_REDUCE_BLOW = "ReducingWindSpeed";

    /**
     * 最大风速
     */
    public static final String VOICE_CMD_MAX_WIND = "MaxWindSpeed";

    /**
     * 最小风速
     */
    public static final String VOICE_CMD_MIN_WIND = "MinWindSpeed";

    /**
     * 设定空调温度
     */
    public static final String VOICE_CMD_SET_AC_TEM = "SetACTemperature";
}