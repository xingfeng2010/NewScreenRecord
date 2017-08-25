package com.leauto.link.lightcar.ota;

/**
 * Created by Administrator on 2016/10/10.
 */
public class OtaMsgHeader {

    public byte[] MsgCommand;
    public short AppId;
    public short totalPacket;
    public short indexPacket;
    public byte type;
    public int contentLength;
    public long  extendLength;

    public OtaMsgHeader() {
    }

    public byte[] getMsgCommand() {
        return MsgCommand;
    }

    public void setMsgCommand(byte[] msgCommand) {
        MsgCommand = msgCommand;
    }

    public short getAppId() {
        return AppId;
    }

    public void setAppId(short appId) {
        AppId = appId;
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

    public long getExtendLength() {
        return extendLength;
    }

    public void setExtendLength(long extendLength) {
        this.extendLength = extendLength;
    }
}
