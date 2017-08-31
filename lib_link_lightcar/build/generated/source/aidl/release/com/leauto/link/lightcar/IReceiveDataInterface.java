/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\project\\thincar_code\\NewScreenRecord\\lib_link_lightcar\\src\\main\\aidl\\com\\leauto\\link\\lightcar\\IReceiveDataInterface.aidl
 */
package com.leauto.link.lightcar;
public interface IReceiveDataInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.leauto.link.lightcar.IReceiveDataInterface
{
private static final java.lang.String DESCRIPTOR = "com.leauto.link.lightcar.IReceiveDataInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.leauto.link.lightcar.IReceiveDataInterface interface,
 * generating a proxy if needed.
 */
public static com.leauto.link.lightcar.IReceiveDataInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.leauto.link.lightcar.IReceiveDataInterface))) {
return ((com.leauto.link.lightcar.IReceiveDataInterface)iin);
}
return new com.leauto.link.lightcar.IReceiveDataInterface.Stub.Proxy(obj);
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
case TRANSACTION_dispatchDataEvent:
{
data.enforceInterface(DESCRIPTOR);
com.leauto.link.lightcar.MsgHeader _arg0;
if ((0!=data.readInt())) {
_arg0 = com.leauto.link.lightcar.MsgHeader.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
byte[] _arg1;
_arg1 = data.createByteArray();
this.dispatchDataEvent(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_dispatchNullDataEvent:
{
data.enforceInterface(DESCRIPTOR);
com.leauto.link.lightcar.MsgHeader _arg0;
if ((0!=data.readInt())) {
_arg0 = com.leauto.link.lightcar.MsgHeader.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.dispatchNullDataEvent(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.leauto.link.lightcar.IReceiveDataInterface
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
@Override public void dispatchDataEvent(com.leauto.link.lightcar.MsgHeader header, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((header!=null)) {
_data.writeInt(1);
header.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_dispatchDataEvent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void dispatchNullDataEvent(com.leauto.link.lightcar.MsgHeader header) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((header!=null)) {
_data.writeInt(1);
header.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dispatchNullDataEvent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_dispatchDataEvent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_dispatchNullDataEvent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void dispatchDataEvent(com.leauto.link.lightcar.MsgHeader header, byte[] data) throws android.os.RemoteException;
public void dispatchNullDataEvent(com.leauto.link.lightcar.MsgHeader header) throws android.os.RemoteException;
}
