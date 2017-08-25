package com.leauto.link.lightcar.module;

/**
 * 这个类代码连接的车机相关信息
 * Created by Administrator on 2017/4/18.
 */

public class ThincarDevice {
    /**
     * 车机屏幕宽度
     */
    private int mCarWidth;

    /**
     * 车机屏幕高度
     */
    private int mCarHeight;

    /**
     * 车机编码
     */
    private int mCarCode;

    public int getCarWidth() {
        return mCarWidth;
    }

    public void setCarWidth(int carWidth) {
        this.mCarWidth = carWidth;
    }

    public int getCarHeight() {
        return mCarHeight;
    }

    public void setCarHeight(int carHeight) {
        this.mCarHeight = carHeight;
    }

    public int getCarCode() {
        return mCarCode;
    }

    public void setCarCode(int carCode) {
        this.mCarCode = carCode;
    }

    @Override
    public String toString() {
        return "ThincarDevice{" +
                "mCarWidth=" + mCarWidth +
                ", mCarHeight=" + mCarHeight +
                ", mCarCode=" + mCarCode +
                '}';
    }
}
