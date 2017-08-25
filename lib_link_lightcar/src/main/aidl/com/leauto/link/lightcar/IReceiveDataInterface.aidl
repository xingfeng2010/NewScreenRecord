// IReceiveDataInterface.aidl
package com.leauto.link.lightcar;

// Declare any non-default types here with import statements

import com.leauto.link.lightcar.MsgHeader;

interface IReceiveDataInterface {
    void dispatchDataEvent(in MsgHeader header,in byte[] data);
    void dispatchNullDataEvent(in MsgHeader header);
    void startScreenRecordActivity();
    void onAoaConnectStateChange(int state);
    void onAdbConnectStateChange(int state);
}
