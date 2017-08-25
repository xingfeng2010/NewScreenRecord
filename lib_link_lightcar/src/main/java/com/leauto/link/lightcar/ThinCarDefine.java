package com.leauto.link.lightcar;

/**
 * Created by lishixing on 2017/01/03.
 */
public class ThinCarDefine {

    /**
     * define app id about thincar upgrade
     */
    public static class UpgradeAppId {
        /**
         * request car version info
         */
        public static final short OTA_VERSION_REQ = 0x0101;

        /**
         * 获取车机版本信息
         */
        public static final short OTA_VERSION_REP = 0x0201;

        /**
         * request car ota lsitz
         */
        public static final short OTA_UPDATE_REQ = 0x0301;


        public static final short OTA_UPDATE_RSP=0x401;

        /**
         * 获取车机要升级列表
         */
        public static final short OTA_FILE_DATA_RSP = 0x701;


        public static final  short OTA_NOWIFI_CONT_REQ=0x501;


        public static final short OTA_NOWIFI_CONT_RSP=0x601;

        public static final short OTA_FILE_ERROR_NOTI=0x901;


        public static final short OTA_FILE_DATA_REQ = 0x801;
    }

 /**
     * 与车机通讯协议app id的具体分配与定义
     */
    public static class ProtocolAppId {
        /**
         * LeRadio
         */
        public static final short LE_RADIO_APPID = 0x05;

        /**
         * 第三方应用
         */
        public static final short THIRD_APP_APPID = 0x06;

        /**
         * 用户帐户信息
         */
        public static final short USER_ACCOUNT_APPID = 0x07;

        /**
         * 手机设备信息
         */
        public static final short DEVICE_INFO_APPID = 0x08;

        /**
         * 欢迎页
         */
        public static final short WELCOME_PAGE_APPID = 0x09;

        /**
         * 蓝牙
         */
        public static final short BLUETOOTH_APPID = 0x0A;

        /**
         * 导航
         */
        public static final short NAVI_APPID = 0x0B;

        /**
         * 语音助手
         */
        public static final short VOICE_ASSISTANT_APPID = 0x0C;

        /**
         * 导航条信息
         */
        public static final short NAVI_BAR_APPID = 0x0D;

        /**
         * CAN数据上传
         */
        public static final short OTA_APPID = 0x0E;

        /**
         * ADB传递车机屏幕信息
         */
        public static final short ADB_DEVICE_APPID = 0x0F;

        /**
         * 用于传输pcm data
         */
        public static final short DRVIE_APPID = 0x10;
    }

    public static class ProtocolToCarCommand {
        /**
         * 向车机发送手机屏幕参数
         */
        public static final int SEND_SCREEN_SIZE_COMMAND = 0x201;

        /**
         * 手机向车机发送事件
         */
        public static final int NOTIFY_EVENT_TO_CAR_COMMAND = 0x202;

        /**
         * 手机车机互发送数据
         */
        public static final int SEND_CAR_DATA_COMMAND = 0x107;

        public static final int DEVICE_AOA_ADB_CONNECTED_COMMAND = 0x104;

        /**
         * 手机车机开始同步数据
         */
        public static final int EVENT_LEPHONE_SYNC_COMMAND = 0x862;


        /**
         * 表示adb连接通道建立
         */
        public static final short ADB_CONNECTED_COMMAND = 0x1FF;
    }

    public static class ProtocolToCarParam {
        /**
         * 应用处于前台
         */
        public static final int PHONE_NOTIFY_APP_FORGROUND = 0x104;

        /**
         * 应用处于后台
         */
        public static final int PHONE_NOTIFY_APP_BACKGROUND = 0x105;

        /**
         * 应用正常退出
         */
        public static final int PHONE_NOTIFY_APP_EXIT = 0x106;

        /**
         * 按下HOME键
         */
        public static final int PHONE_NOTIFY_APP_HOME_PRESSED = 0x107;

        /**
         * 应用锁屏
         */
        public static final int PHONE_NOTIFY_APP_SCREEN_LOCK = 0x108;

        /**
         * 通知当前手机所处界面
         */
        public static final int PHONE_NOTIFY_APP_CURRENT_PAGE = 0x109;

        /**
         * 通知车机aoa检测button区域
         */
        public static final int PHONE_NOTIFY_APP_AOA_RANGE = 0x110;

        /**
         * 通知车机收到點擊事件
         */
        public static final int PHONE_NOTIFY_AOC_CLICE_RECEIVED = 0x111;

        /**
         * 通知车机PCM数据信息
         */
        public static final int PHONE_NOTIFY_PCM_INFO = 0x112;

        /**
         * 应用解屏
         */
        public static final int PHONE_NOTIFY_APP_SCREEN_UNLOCK = 0x113;

        /**
         * 手机无网络
         */
        public static final int PHONE_NOTIFY_APP_NO_NETWORK = 0x114;

        /**
         * 手机有网络
         */
        public static final int PHONE_NOTIFY_APP_HAS_NETWORK = 0x115;

        /**
         * 手机给车机发送命令
         */
        public static final int NOTIFY_FROM_PHONE_PARAM = 0x300;

        /**
         * 手机控制车机
         */
        public static final int CONTROL_LIGHT_AVN_PARAM = 0x700;

        /**
         * 手机已经切换到半屏
         */
        public static final int PHONE_CHANGE_HALF_MODE_PARAM = 0x701;

        /**
         * 手机已经切换到全屏
         */
        public static final int PHONE_CHANGE_FULL_MODE_PARAM = 0x702;

        /*
        * 手机车机开始同步数据参数
        */
        public static final int LEPHONE_SYNC_APP_PARAM = 0x786;

        public static final int PHONE_READY_REC_EVENT_PARAM =0x322;
    }

    public static class ProtocolToCarAction {
        /**
         * 通知显示底部导航栏
         */
        public static final int SHOW_BOTTOM_BAR = 0x10;

        /**
         * 通知隐藏
         */
        public static final int HIDE_BOTTOM_BAR = 0x11;

        /**
         * 语音通知打开乐听界面 or ANDROID_READY
         */
        public static final int LAUNCH_LE_RADION_ACTION = 0x12;

        /**
         * 语音通知打开导航界面
         */
        public static final int LAUNCH_NAVI_ACTION = 0x13;
    }

    public static class ProtocolFromCarCommand {
        /**
         * 接收车机发过来的消息，包括车机屏幕信息和车机编码
         */
        public static final int UPDATE_MOUSE_COMMAND  = 0x102;

        public static final int STOP_AUDIO_COMMAND = 0x229;

        /**
         * 车机发送给手机蓝牙相关命令
         */
        public static final int BT_COMMAND = 0x230;

        /**
         * 车机发送给手机PCM数据命令
         */
        public static final int SYNC_PCM_DATA_COMMAND = 0x290;

        /**
         * 车机给手机发送通用事件命令
         */
        public static final int COMMON_EVENT_COMMAND = 0x300;

        /**
         * 测试鼠标事件命令
         */
        public static final int EVENT_MOUSE_DEBUG_COMMAND = 0x301;
    }

    public static class ProtocolFromCarParameter {
        /**
         * 传送蓝牙相关信息
         */
        public static final int BT_DBT_INFO_PARAM = 0x300;

        /**
         * 蓝牙断开连接结果
         */
        public static final int BT_DISCONNECT_RESULT_PARAM = 0x301;

        /**
         * 车机传送过来的通用消息
         */
        public static final int AUTO_MSG_PARAM = 0x500;

        /**
         * 车机传送过来的地图事件
         */
        public static final int NAVI_WINDOW_PARAM = 0x504;

        /**
         * 多点手势触控
         */
        public static final int MSG_TO_ECOLINK_PARAM_GESTRUE = 0x800;
    }

    public static class ProtocolFromCarAction {
        /**
         * 拉起Live
         */
        public static final int SHOW_LIVE = 0x220;

        /**
         * 隐藏Live
         */
        public static final int HIDE_LIVE = 0x221;

        /**
         * 拉起导航
         */
        public static final int SHOW_NAVI = 0x222;

        /**
         * 隐藏导航
         */
        public static final int HIDE_NAVI = 0x223;

        /**
         * 显示leradio界面
         */
        public static final int SHOW_LERADIO = 0x224;

        /**
         * 拉起语音界面
         */
        public static final int SHOW_SPEECH = 0x225;

        /**
         * 开始截屏消息
         */
        public static final int START_SCREEN_RECORDER = 0x226;

        /**
         * 停止截屏消息
         */
        public static final int STOP_SCREEN_RECORDER = 0x227;

        /**
         * 重新开始截屏消息
         */
        public static final int RESTART_SCREEN_RECORDER = 0x228;

        /**
         * 显示leradio local界面
         */
        public static final int SHOW_LERADIO_LOCAL = 0x291;

        /**
         * 滑动手势
         */
        public static final int P0_PHONE_GESTURE_MOVE = 0x300;

        /**
         * 缩放手势
         */
        public static final int P0_PHONE_GESTURE_SCALE  =   0x301;

        /**
         * 旋转手势
         */
        public static final int P0_PHONE_GESTURE_ROTATE =   0x302;

        /**
         * 车机向手机请求AOA反控检测成功或者失败
         */
        public static final int REQUEST_AOA_CHECK_RESULT = 0x232;

        /**
         * 车机点击之前向手机发一个事件
         */
        public static final int NOTIFY_AOA_CLICK_BEGIN = 0x233;
    }

    public static final int HALF_TOP_MARGIN = 0;//屏幕半屏高度
    public static final int HALF_CAR_HEIGHT = 410;//屏幕半屏高度
    public static final int FULL_CAR_HEIGHT = 1280;//屏幕全屏高度
    public static final int FULL_CAR_WIDTH = 720;//屏幕全屏宽度

    public static final int HALF_NAVI_CAR_HEIGHT = 454;//导航半屏高度
    public static final int FULL_NAVI_CAR_HEIGHT = 1280;//导航全屏高度

    public final static short FLAG_KEY_FRAME = 0xFF;
    public final static short FLAG_NOT_KEY_FRAME = 0;

    public static final int THIN_CAR_HALF_HEIGHT = 450;//导航半屏高度

    public static final String Interface_Notify = "Interface_Notify";
    public static final String Interface_Request = "Interface_Request";

    /**
     * 定义的手机车机请求方法
     */
    public static class ProtocolNotifyMethod {
        /**
         * 手机端通知车机端图标信息
         */
        public static final String METHOD_NOTIFY_APP = "NotifyAppInfo";

        /**
         * 启动应用
         */
        public static final String METHOD_START_APP = "StartupApp";

        /**
         * 请求图标数据
         */
        public static final String METHOD_REQUEST_APPICON = "RequestAppIconByID";

        /**
         * 添加应用
         */
        public static final String METHOD_ADD_APP = "AddNewApp";

        /**
         * 删除应用
         */
        public static final String METHOD_DEL_APP = "DeleteApp";

        /**
         * 车机请求图片数据
         */
        public static final String METHOD_REQUEST_SONG_IMAGE = "RequestImageByID";

        /**
         * 车机端请求手机操作
         */
        public static final String METHOD_REQUEST_PLAYER_ACTION = "RequestPlayerToDo";

        /**
         * 车机请求获取歌曲信息
         */
        public static final String METHOD_REQUEST_ALBUM_INFO = "RequestAlbumInfo";

        /**
         * 车机请求用户信息
         */
        public static final String METHOD_REQUEST_USER_INFO = "RequestUserInfo";

        /**
         * 车机请求手机设备信息
         */
        public static final String METHOD_REQUEST_PHONE_INFO = "RequestPhoneInfo";

        /**
         * 车机请求欢迎页信息
         */
        public static final String METHOD_REQUEST_WELCOME_INFO = "RequestWelcomeInfo";

        /**
         * 车机通知启动语音助手
         */
        public static final String METHOD_REQUEST_START_VOICE = "StartVoiceAssistant";

        /**
         * 车机通知开始录音
         */
        public static final String METHOD_REQUEST_START_RECORD = "StartVoiceRecord";

        /**
         * 车机通知检测到有人开始说话
         */
        public static final String METHOD_REQUEST_DETECT_VOICE = "DetectVoiceInput";

        /**
         * 车机通知检测到说话结束
         */
        public static final String METHOD_REQUEST_DETECT_NOVOICE = "DetectNoVoiceInput";

        /**
         * 车机通知结束录音
         */
        public static final String METHOD_REQUEST_STOP_RECORD = "StopVoiceRecord";

        /**
         * 退出语音助手
         */
        public static final String METHOD_REQUEST_STOP_VOICE = "StopVoiceAssistant";

        /**
         * 请求导航条信息
         */
        public static final String METHOD_REQUEST_NAVI_BAR_INFO = "RequestNaviBarInfo";

        /**
         * 车机请求是否在导航中
         */
        public static final String METHOD_REQUEST_IS_IN_NAVI = "RequestIsNaving";

        /**
         * 车机请求手机开始导航
         */
        public static final String METHOD_REQUEST_START_NAVI = "NotifyStartNavi";

        /**
         * 车机请求手机结束导航
         */
        public static final String METHOD_REQUEST_STOP_NAVI = "NotifyEndNavi";

        /**
         * 车机请求手机开始预览
         */
        public static final String METHOD_REQUEST_START_PREVIEW = "NotifyStartPreview";

        /**
         * 车机请求手机结束预览
         */
        public static final String METHOD_REQUEST_STOP_PREVIEW = "NotifyStopPreview";

        /**
         * 车机请求手机通知快捷搜索
         */
        public static final String METHOD_REQUEST_QUICK_SEARCH = "NotifyStartQuickSearch";

        /**
         * 车机请求所有歌曲信息
         */
        public static final String METHOD_REQUEST_ALL_SONG_INFO = "RequestAllSongsInfo";

        /**
         * 车机请求所有应用信息
         */
        public static final String METHOD_REQUEST_ALL_APP_INFO = "RequestAllAppsInfo";

        /**
         * 车机请求通讯录
         */
        public static final String METHOD_REQUEST_PHONE_BOOK = "PhoneBook";

        /**
         * 车机请求通话记录
         */
        public static final String METHOD_REQUEST_CALL_HISTORY = "CallHistory";

        /**
         * 车机通知HUD显示或者不显示
         */
        public static final String METHOD_REQUEST_HUD_ACTION = "RequestHudToDo";

        /**
         * 车机发出自动连接请求
         */
        public static final String METHOD_REQUEST_BLUE_CONNECT = "AutoConnect";

        /**
         * 车机请求蓝牙名称和地址
         */
        public static final String METHOD_REQUEST_BLUE_INFO = "PhoneBTNameAndAddr";

        /**
         * 车机请求设置家或者工作地址
         */
        public static final String METHOD_REQUEST_SETTING_ADDRESS = "NotifySetting";

        /**
         * 车机请求设置家地址
         */
        public static final String METHOD_REQUEST_SETTING_HOME = "NotifyHome";

        /**
         * 车机请求设置工作地址
         */
        public static final String METHOD_REQUEST_SETTING_WORK = "NotifyWork";

        /**
         * 车机请求手机当前播放状态
         */
        public static final String METHOD_REQUEST_PLAYER_STATUS = "RequestPlayerStatus";

        /**
         * 车机通知手机avn info
         *
         */
        public static final String METHOD_REQUEST_AVN_INFO = "AVNInfo";
        /**
         * 车机请求手机接受CAN数据
         */
        public static final String METHOD_REQUEST_CAN_FILE_TRANSMIT = "FileTransmit";
    }

    /**
     * 定义thincar向应用层会传递的action值
     */
    public static class ProtocolNotifyValue {
        public static final int NOTIFY_BLUETOOTH_MAC = 1;
        public static final int NOTIFY_CARBLUT_DISCON = 2;
        public static final int NOTIFY_CAR_OTAINFO = 3;
        public static final int NOTIFY_CAR_UPDATE_INFO = 4;
        public static final int NOTIFY_CAR_UPDATE_ACCEPT=5;
        public static final int NOTIFY_OTA_NOWIFI_RSP=6;

        /**
         * 启动三方应用
         */
        public static final short LAUNCH_THIRD_APP = 100;

        /**
         * 请求三方应用图片
         */
        public static final short APP_REQUEST_PIC = 101;

        /**
         * 车机请求图片数据
         */
        public static final short REQUEST_SONG_IMAGE = 102;

        /**
         * 车机端请求手机操作
         */
        public static final short REQUEST_PLAYER_ACTION = 103;

        /**
         * 车机请求歌曲信息
         */
        public static final short REQUEST_ALBUM_INFO = 104;

        /**
         * 车机请求用户信息
         */
        public static final short REQUESET_USER_INFO = 105;

        /**
         * 车机请求手机设备信息
         */
        public static final short REQUEST_PHONE_INFO = 106;

        /**
         * 车机请求欢迎页信息
         */
        public static final short REQUEST_WELCOME_INFO = 107;

        /**
         * 车机通知启动语音助手
         */
        public static final short REQUEST_START_VOICE = 108;

        /**
         * 车机通知开始录音
         */
        public static final short REQUEST_START_RECORD = 109;

        /**
         * 车机通知检测到有人开始说话
         */
        public static final short REQUEST_DETECT_VOICE = 110;

        /**
         * 车机通知检测到说话结束
         */
        public static final short REQUEST_DETECT_NOVOICE = 111;

        /**
         * 车机通知结束录音
         */
        public static final short REQUEST_STOP_RECORD = 112;

        /**
         * 退出语音助手
         */
        public static final short REQUEST_STOP_VOICE = 113;

        /**
         * 请求导航条信息
         */
        public static final short REQUEST_NAVI_BAR_INFO = 114;

        /**
         * 车机请求是否在导航中
         */
        public static final short REQUEST_IS_IN_NAVI = 115;

        /**
         * 车机请求手机开始导航
         */
        public static final short REQUEST_START_NAVI = 116;

        /**
         * 车机请求手机结束导航
         */
        public static final short REQUEST_STOP_NAVI = 117;

        /**
         * 车机请求手机开始预览
         */
        public static final short REQUEST_START_PREVIEW = 118;

        /**
         * 车机请求手机结束预览
         */
        public static final short REQUEST_STOP_PREVIEW = 119;

        /**
         * 车机请求手机通知快捷搜索
         */
        public static final short REQUEST_QUICK_SEARCH = 120;

        /**
         * 车机请求所有歌曲信息
         */
        public static final short REQUEST_ALL_SONG_INFO = 121;

        /**
         * 车机请求所有应用信息
         */
        public static final short REQUEST_ALL_APP_INFO = 122;

        /**
         * 车机请求通讯录
         */
        public static final short REQUEST_PHONE_BOOK = 123;

        /**
         * 车机请求通话记录
         */
        public static final short REQUEST_CALL_HISTORY = 124;

        /**
         * 车机通知HUD显示或者不显示
         */
        public static final short REQUEST_HUD_ACTION = 125;

        /**
         * 车机发出自动连接请求
         */
        public static final short REQUEST_BLUE_CONNECT = 126;

        /**
         * 车机请求蓝牙名称和地址
         */
        public static final short REQUEST_BLUE_INFO = 127;

        /**
         * 车机请求设置家或者工作地址
         */
        public static final short REQUEST_SETTING_ADDRESS = 128;

        /**
         * 车机请求设置家地址
         */
        public static final short REQUEST_SETTING_HOME = 129;

        /**
         * 车机请求设置工作地址
         */
        public static final short REQUEST_SETTING_WORK = 130;

        /**
         * 车机请求手机当前播放状态
         */
        public static final short REQUEST_PLAYER_STATUS = 131;
    }

    public static class PageIndexDefine{
        public static int HALF_MAP_PAGE = 0x400;
        public static int FULL_MAP_PAGE = 0x401;
        public static int LOCAL_PAGE = 0x402;
        public static int LERADIO_PAGE = 0x403;
        public static int THIRAD_APP_PAGE = 0x404;
    }

    public static class AOAChecekResult {
        /** 失败 */
        public static int CHECK_FAIL = 0;
        /** 成功 */
        public static int CHECK_SUCCESS = 1;
    }

    public static class ConnectState {
        /** 连接上 */
        public static final int STATE_CONNECT = 1;
        /** 断开 */
        public static final int STATE_DISCONNECT = 2;
    }
}