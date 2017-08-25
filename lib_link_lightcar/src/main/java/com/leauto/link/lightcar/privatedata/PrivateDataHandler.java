package com.leauto.link.lightcar.privatedata;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Jerome on 2017/5/16.
 */

public class PrivateDataHandler {

    /**
     * 创建capacity大小的ByteBuffer
     *
     * @param capacity
     * @return
     */
    public static ByteBuffer createByteBuffer(int capacity) {
        ByteBuffer bb = ByteBuffer.allocate(capacity);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb;
    }

    /**
     * 把ProtocolHeader创建成ByteBufer
     *
     * @param header
     * @return
     */
    public ByteBuffer getPrivateHeaderBuffer(PrivateMsgHeader header) {
        ByteBuffer bb = createByteBuffer(PrivateMsgHeader.HEADER_LENGTH);
        bb.putShort(0, header.getHEAD());
        bb.putShort(2, header.getAppId());
        bb.putShort(4, header.getTotalPacket());
        bb.putShort(6, header.getIndexPacket());
        bb.put(8, header.getType());
        bb.putInt(9, header.getContentLength());
        bb.position(13);
        bb.put(header.getExtendLength());
        return bb;
    }

    /**
     * @param byteData
     *            包含header的原始数据
     * @return
     */
    public PrivateData getPrivateData(byte[] byteData) {
        PrivateData privateData = new PrivateData();
        ByteBuffer bb = ByteBuffer.wrap(byteData);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        PrivateMsgHeader header = getPrivateMsgHeader(bb);
        privateData.setPrivateMsgHeader(header);

        byte[] validData = new byte[header.getContentLength()];
        bb.position(21);
        bb.get(validData, 0, validData.length);
        privateData.setValidData(validData);
        return privateData;
    }

    /**
     * 根据ByteBuffer获取ProyHeader
     *
     * @param buffer
     * @return
     */
    public PrivateMsgHeader getPrivateMsgHeader(ByteBuffer buffer) {
        PrivateMsgHeader header = new PrivateMsgHeader();
        header.setAppId(buffer.getShort(2));
        header.setTotalPacket(buffer.getShort(4));
        header.setIndexPacket(buffer.getShort(6));
        header.setType(buffer.get(8));
        header.setContentLength(buffer.getInt(9));
        byte[] extend = new byte[8];
        buffer.position(13);
        buffer.get(extend, 0, 8);
        header.setExtendLength(extend);
        return header;
    }

    /**
     * 把protocolData 变成 byte[]
     *
     * @param
     * @return
     */
    public byte[] getPrivateData(PrivateData privateData) {
        ByteBuffer bb = createByteBuffer(PrivateMsgHeader.HEADER_LENGTH
                + privateData.getPrivateMsgHeader().getContentLength());
        ByteBuffer temp = getPrivateHeaderBuffer(privateData.getPrivateMsgHeader());
        bb.position(0);
        bb.put(temp.array());
        bb.position(21);
        bb.put(privateData.getValidData());
        return bb.array();
    }
}
