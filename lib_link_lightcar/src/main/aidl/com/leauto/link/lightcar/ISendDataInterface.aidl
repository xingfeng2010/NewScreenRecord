// ISendDataInterface.aidl
package com.leauto.link.lightcar;

// Declare any non-default types here with import statements

interface ISendDataInterface {
    void sendDataToCar(in byte[] data);
    void sendCheckButtonRange(int width, int height);
    void notifyCarConnect();
    void notifyCarDisConnect();
    void notifyRecordExit();
}
