package com.leauto.link.lightcar.ota;

import com.leauto.link.lightcar.ThinCarDefine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Administrator on 2016/10/10.
 */
public class OtaThincarUtils {

    public static  ByteBuffer objectToBuffer(OtaMsgHeader header) {
        ByteBuffer buffer = ByteBuffer.allocate(21);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(header.getMsgCommand());
        buffer.putShort(header.getAppId());
        buffer.putShort(header.getTotalPacket());
        buffer.putShort(header.getIndexPacket());
        buffer.put(header.getType());
        buffer.putInt(header.getContentLength());
        buffer.putLong(header.getExtendLength());
        return buffer;
    }

    /**
     * 请求车机版本信息
     * @return
     */
    public static byte[] getVersion(){
        OtaMsgHeader msgHeader =new OtaMsgHeader();
        msgHeader.setMsgCommand(new byte[]{(byte) 0xFF, (byte) 0xEE});
        msgHeader.setAppId(ThinCarDefine.UpgradeAppId.OTA_VERSION_REQ);
        msgHeader.setTotalPacket((short) 1);
        msgHeader.setIndexPacket((short) 1);
        msgHeader.setType((byte) 2);
        msgHeader.setContentLength(0);
        msgHeader.setExtendLength(0L);
        ByteBuffer Otasendheader = OtaThincarUtils.objectToBuffer(msgHeader);
        return  Otasendheader.array();
    }


    /**
     * 请求升级列表
     * @param data
     * @return
     */
    public static byte[] sendWifiReq(byte[] data){
        OtaMsgHeader msgHeader =new OtaMsgHeader();
        msgHeader.setMsgCommand(new byte[]{(byte) 0xFF, (byte) 0xEE});
        msgHeader.setAppId(ThinCarDefine.UpgradeAppId.OTA_NOWIFI_CONT_REQ);
        msgHeader.setTotalPacket((short) 1);
        msgHeader.setIndexPacket((short) 1);
        msgHeader.setType((byte) 1);
        msgHeader.setContentLength(data.length);
        msgHeader.setExtendLength(data.length);
        ByteBuffer Otasendheader = OtaThincarUtils.objectToBuffer(msgHeader);
        byte[] newdata = new byte[21+data.length];
        System.arraycopy(Otasendheader.array(),0,newdata,0,21);//把21字节消息头放到newdata
        System.arraycopy(data,0,newdata,21,data.length);//把数据放到newdata
        return  newdata;
    }


    /**
     * 请求升级列表
     * @param data
     * @return
     */
    public static byte[] sendOtaList(byte[] data){
        OtaMsgHeader msgHeader =new OtaMsgHeader();
        msgHeader.setMsgCommand(new byte[]{(byte) 0xFF, (byte) 0xEE});
        msgHeader.setAppId(ThinCarDefine.UpgradeAppId.OTA_UPDATE_REQ);
        msgHeader.setTotalPacket((short) 1);
        msgHeader.setIndexPacket((short) 1);
        msgHeader.setType((byte) 1);
        msgHeader.setContentLength(data.length);
        msgHeader.setExtendLength(data.length);
        ByteBuffer Otasendheader = OtaThincarUtils.objectToBuffer(msgHeader);
        byte[] newdata = new byte[21+data.length];
        System.arraycopy(Otasendheader.array(),0,newdata,0,21);//把21字节消息头放到newdata
        System.arraycopy(data,0,newdata,21,data.length);//把数据放到newdata
        return  newdata;
    }

    /**
     * 给车机发送Ota zip 包
     * @param data
     * @param length
     * @return
     */
  public static byte[] sendOtaZip(byte[] data,int length){
        OtaMsgHeader msgHeader =new OtaMsgHeader();
        msgHeader.setMsgCommand(new byte[]{(byte) 0xFF, (byte) 0xEE});
        msgHeader.setAppId(ThinCarDefine.UpgradeAppId.OTA_FILE_DATA_REQ);
        msgHeader.setTotalPacket((short) 1);
        msgHeader.setIndexPacket((short) 1);
        msgHeader.setType((byte) 0);
        msgHeader.setContentLength(data.length);
        msgHeader.setExtendLength(data.length);
        ByteBuffer Otasendheader = OtaThincarUtils.objectToBuffer(msgHeader);

        byte[] newdata = new byte[21+data.length];
        System.arraycopy(Otasendheader.array(),0,newdata,0,21);//把21字节消息头放到newdata
        System.arraycopy(data,0,newdata,21,data.length);//把数据放到newdata
        return  newdata;
    }

}
