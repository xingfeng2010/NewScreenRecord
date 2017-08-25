package com.leauto.link.lightcar;

import android.hardware.display.VirtualDisplay;

import com.leauto.link.lightcar.module.AVNInfo;

/**
 * Created by Jerome on 2016/7/7.
 */
public interface IAOACallback {
    /**
     * 车机发送给app的消息
     * 比如： 拉起导航消息
     */
    public void onCommand(int command, int params);

    /**
     * 语音pcm码流路径
     * @param pcmPath
     */
    public void onPcmPath(String pcmPath);

    /**
     * AOA可用
     */
    public void onAoaAttach();

    /**
     * AOA断开
     */
    public void onAoaDettach();

    /**
     * 车机通知事件
     */
    public void NotifyEvent(int event,String string);


    /**
     * 调试使用
     * @param msg
     */
    public void showToast(String msg);


    /**
     * 通知收到语音数据
     * @param data
     */
    public void onViceDataObtain(byte[] data,int len) ;

    /**
     * 通知收到地图点击事件
     * @param x 坐标
     * @param y 坐标
     * @param width 宽度
     * @param height 高度
     */
    public void onNaviEvent(short x,short y,short width,short height) ;

    /**
     * 传递多指手势协议
     * @param event 事件，滑动，缩放，旋转
     * @param x 坐标 x
     * @param y 坐标 y
     * @param parameter 缩放，旋转参数
     */
    public void notifyGesterEvent(int event,int x, int y,int parameter);

    /**
     * 车机的AVNInfo数据,用于CAN数据上传
     * @param avnInfo
     */
    public void onAVNInfo(AVNInfo avnInfo);


    /**
     * CAN数据传输request,收到此数据手机需要response给车机
     * @param name  传输数据name,
     * @param size  数据大小
     */
    public  void onCANFileTransmit(String name, long size);

    /**
     * CAN数据保存文件后文件路径(path+fileName)
     * @param path
     */
    public void onCANDataPath(String path);

    public void startScreenRecordActivity();

    public void onAoaConnectStateChange(int state);

    public void onAdbConnectStateChange(int state);

    public void postAdbDeviceInfo(int pinCode,int width, int height);
}
