package com.leauto.link.lightcar.thirdapp;

/**
 * Created by Administrator on 2016/10/27.
 */
public class ThirdAppModel {

    public String Type;
    public String Method;
    public Parameter Parameter;

    public static class Parameter {
        public ThirdAppInfo[] appinfos;
    }
}
