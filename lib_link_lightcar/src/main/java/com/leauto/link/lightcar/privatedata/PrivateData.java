package com.leauto.link.lightcar.privatedata;

/**
 * Created by Jerome on 2017/5/16.
 */

public class PrivateData {

    private PrivateMsgHeader mPrivateMsgHeader;

    private byte[] validData; //有效数据

    public PrivateMsgHeader getPrivateMsgHeader() {
        return mPrivateMsgHeader;
    }

    public void setPrivateMsgHeader(PrivateMsgHeader privateMsgHeader) {
        mPrivateMsgHeader = privateMsgHeader;
    }

    public byte[] getValidData() {
        return validData;
    }

    public void setValidData(byte[] validData) {
        this.validData = validData;
    }
}
