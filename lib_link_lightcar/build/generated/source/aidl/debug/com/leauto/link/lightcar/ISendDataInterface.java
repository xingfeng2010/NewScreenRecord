/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\project\\thincar_code\\NewScreenRecord\\lib_link_lightcar\\src\\main\\aidl\\com\\leauto\\link\\lightcar\\ISendDataInterface.aidl
 */
package com.leauto.link.lightcar;
// Declare any non-default types here with import statements

public interface ISendDataInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.leauto.link.lightcar.ISendDataInterface
{
private static final java.lang.String DESCRIPTOR = "com.leauto.link.lightcar.ISendDataInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.leauto.link.lightcar.ISendDataInterface interface,
 * generating a proxy if needed.
 */
public static com.leauto.link.lightcar.ISendDataInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.leauto.link.lightcar.ISendDataInterface))) {
return ((com.leauto.link.lightcar.ISendDataInterface)iin);
}
return new com.leauto.link.lightcar.ISendDataInterface.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_sendDataToCar:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
this.sendDataToCar(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_sendCheckButtonRange:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.sendCheckButtonRange(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyCarConnect:
{
data.enforceInterface(DESCRIPTOR);
this.notifyCarConnect();
reply.writeNoException();
return true;
}
case TRANSACTION_notifyCarDisConnect:
{
data.enforceInterface(DESCRIPTOR);
this.notifyCarDisConnect();
reply.writeNoException();
return true;
}
case TRANSACTION_notifyRecordExit:
{
data.enforceInterface(DESCRIPTOR);
this.notifyRecordExit();
reply.writeNoException();
return true;
}
case TRANSACTION_stopScreenRecorder:
{
data.enforceInterface(DESCRIPTOR);
this.stopScreenRecorder();
reply.writeNoException();
return true;
}
case TRANSACTION_resumeScreenRecorder:
{
data.enforceInterface(DESCRIPTOR);
this.resumeScreenRecorder();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.leauto.link.lightcar.ISendDataInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void sendDataToCar(byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_sendDataToCar, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void sendCheckButtonRange(int width, int height) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(width);
_data.writeInt(height);
mRemote.transact(Stub.TRANSACTION_sendCheckButtonRange, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void notifyCarConnect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyCarConnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void notifyCarDisConnect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyCarDisConnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void notifyRecordExit() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyRecordExit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopScreenRecorder() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopScreenRecorder, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void resumeScreenRecorder() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resumeScreenRecorder, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_sendDataToCar = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_sendCheckButtonRange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_notifyCarConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_notifyCarDisConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_notifyRecordExit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_stopScreenRecorder = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_resumeScreenRecorder = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
public void sendDataToCar(byte[] data) throws android.os.RemoteException;
public void sendCheckButtonRange(int width, int height) throws android.os.RemoteException;
public void notifyCarConnect() throws android.os.RemoteException;
public void notifyCarDisConnect() throws android.os.RemoteException;
public void notifyRecordExit() throws android.os.RemoteException;
public void stopScreenRecorder() throws android.os.RemoteException;
public void resumeScreenRecorder() throws android.os.RemoteException;
}
