package com.leauto.link.lightcar.privatedata;

/**
 * Created by Jerome on 2017/5/16.
 * 私有通道的21个字节头部
 */

public class PrivateMsgHeader {
    public final static short HEADER_LENGTH = 21;
    public final static byte TYPE_STRING = 0x1;
    public final static byte TYPE_DATA = 0x0;


    private short HEAD = (short) 0xEEFF;  //2字节
    private short appId;  //2字节
    private short totalPacket;   //2字节
    private short indexPacket;   //2字节
    private byte type;    //1字节
    private int contentLength;  //4字节
    private byte[] extendLength = new byte[8];  //8个字节

    public short getHEAD() {
        return HEAD;
    }

    public short getAppId() {
        return appId;
    }

    public void setAppId(short appId) {
        this.appId = appId;
    }

    public short getTotalPacket() {
        return totalPacket;
    }

    public void setTotalPacket(short totalPacket) {
        this.totalPacket = totalPacket;
    }

    public short getIndexPacket() {
        return indexPacket;
    }

    public void setIndexPacket(short indexPacket) {
        this.indexPacket = indexPacket;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getExtendLength() {
        return extendLength;
    }

    public void setExtendLength(byte[] extendLength) {
        this.extendLength = extendLength;
    }
}
