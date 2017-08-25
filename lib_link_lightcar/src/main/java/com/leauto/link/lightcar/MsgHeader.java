package com.leauto.link.lightcar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jerome on 2016/7/8.
 */
public class MsgHeader implements Parcelable{

    public int MsgCommand;
    public int MsgParam;
    public short unknow;//MirrorDataMark or x
    public short unknow1;//Type or y
    public short startx;
    public short starty;
    public short width;
    public short height;
    public int len;

    public MsgHeader() {
    }

    public MsgHeader(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        this.MsgCommand = in.readInt();
        this.MsgParam = in.readInt();
        this.unknow = (short)in.readInt();
        this.unknow1 = (short)in.readInt();
        this.startx = (short)in.readInt();
        this.starty = (short)in.readInt();
        this.width = (short)in.readInt();
        this.height = (short)in.readInt();
        this.len = in.readInt();
    }

    public int getMsgParam() {
        return MsgParam;
    }

    public void setMsgParam(int msgParam) {
        MsgParam = msgParam;
    }

    public short getUnknow() {
        return unknow;
    }

    public void setUnknow(short unknow) {
        this.unknow = unknow;
    }

    public int getMsgCommand() {
        return MsgCommand;
    }

    public void setMsgCommand(int msgCommand) {
        MsgCommand = msgCommand;
    }

    public short getUnknow1() {
        return unknow1;
    }

    public void setUnknow1(short unknow1) {
        this.unknow1 = unknow1;
    }

    public short getStartx() {
        return startx;
    }

    public void setStartx(short startx) {
        this.startx = startx;
    }

    public short getStarty() {
        return starty;
    }

    public void setStarty(short starty) {
        this.starty = starty;
    }

    public short getWidth() {
        return width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public short getHeight() {
        return height;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.MsgCommand);
        dest.writeInt(this.MsgParam);
        dest.writeInt(this.unknow);
        dest.writeInt(this.unknow1);
        dest.writeInt(this.startx);
        dest.writeInt(this.starty);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.len);
    }

    public static final Creator<MsgHeader> CREATOR = new Creator<MsgHeader>() {
        @Override
        public MsgHeader createFromParcel(Parcel source) {
            return new MsgHeader(source);
        }

        @Override
        public MsgHeader[] newArray(int size) {
            return new MsgHeader[size];
        }
    };
}
