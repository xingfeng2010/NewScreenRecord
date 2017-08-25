package com.leauto.link.lightcar.voiceassistant;

/**
 * Created by Administrator on 2017/3/17.
 */

public class VoiceQueryStatusDefine {
    /***
     * 请求返回查询结果方法
     */
    public static final String VOICE_QUERY_METHOD = "VoiceSearchCommand";

    /**
     * 查询收音机状态
     */
    public static final String RADIO_STATUS_COMMAND = "RadioStatus";

    /**
     * 查询空调状态
     */
    public static final String AC_STATUS_COMMAND = "ACStatus";

    /**
     * 查询空调模式
     */
    public static final String AC_MODE_COMMAND = "ACMode";

    /**
     * 查询空调进风模式
     */
    public static final String AC_WIND_MODE_COMMAND = "ACWindMode";

    /**
     * 查询空调风向
     */
    public static final String AC_WIND_DIRECTION_COMMAND = "ACWindDirection";

    /**
     * 查询空调风速
     */
    public static final String AC_WIND_SPEED_COMMAND = "ACWindSpeed";

    /**
     * 查询空调风速总档位
     */
    public static final String AC_WIND_RANGE_COMMAND = "ACWindGearRange";

    /**
     * 查询当前空调风速档位
     */
    public static final String AC_CURRENT_WIND_RANGE_COMMAND = "ACWindGear";

    /**
     * 查询空调温度范围
     */
    public static final String AC_TEMP_RANGE_COMMAND = "ACTempRange";

    /**
     * 查询当前空调温度
     */
    public static final String AC_CURRENT_TEMP_COMMAND = "ACTemp";

    public static class RadioStatus {
        /** 收音机关闭 */
        public static final int RADIO_CLOSE = 0;
        /** 收音机打开 */
        public static final int RADIO_OPEN = 1;
    }

    public static class ACStatus {
        /** 空调关闭 */
        public static final int AC_CLOSE = 0;
        /** 空调打开 */
        public static final int AC_OPEN = 1;
    }

    public static class ACModeStatus {
        /** 加热模式 */
        public static final int AC_HOT_MODE = 0;
        /** 制冷模式 */
        public static final int AC_COLD_MODE = 1;
        /** 自动模式 */
        public static final int AC_AUTO_MODE = 2;
    }

    public static class ACWindMode {
        /** 内循环 */
        public static final int AC_INNER_WIND = 1;
        /** 外循环 */
        public static final int AC_OUTER_WIND = 0;
    }

    public static class ACWindDiretion {
        /**  默认无风向 */
        public static final int WIND_DIRECTION_DEFAULT = 0x0;
        /** 水平 */
        public static final int WIND_DIRECTION_HORIZONTAL =0x1;
        /** 向下 */
        public static final int WIND_DIRECTION_DOWN =0x2;
        /** 水平+向下 */
        public static final int WIND_DIRECTION_HORIZONTAL_DOWN =0x3;
        /** 向上 */
        public static final int WIND_DIRECTION_UP =0x4;
        /** 水平+向上 */
        public static final int WIND_DIRECTION_HORIZONTAL_UP =0x5;
        /** 向下+向上 */
        public static final int WIND_DIRECTION_DOWN_UP =0x6;
        /** 水平+向下+向上 */
        public static final int WIND_DIRECTION_HORIZONTAL_DOWN_UP =0x7;

    }
}
