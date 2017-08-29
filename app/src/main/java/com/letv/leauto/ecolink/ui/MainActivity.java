package com.letv.leauto.ecolink.ui;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.usb.UsbManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.leauto.link.lightcar.ScreenRecordActivity;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.leauto.link.lightcar.service.ReceiveDataService;
import com.leauto.sdk.SdkManager;

import com.letv.leauto.ecolink.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{
    private Context mContext;
    private UsbStateReceiver mUsbStateReceiver;

    private LeAutoLinkListner mLeAutoLinkListner;
    private Handler mHandler = new Handler();
    protected ThinCarIAOACallback mThinCarIAOACallback;
    private ReceiveDataService mReceiveDataService;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;

    private final int REQUEST_PERMISSION_CODE = 1;

    public static String targetPath = android.os.Environment.getExternalStorageDirectory() + "/" + "screen_recod";

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mReceiveDataService =  ((ReceiveDataService.ReceiveDataBinder) iBinder).getService();
            mReceiveDataService.setIAOACallback(mThinCarIAOACallback);
//            /** 初始化车机SDK */
//            initLeAuto();
            DataSendManager.getInstance().sendAppStateToCar(ThinCarDefine.ProtocolToCarParam.PHONE_READY_REC_EVENT_PARAM);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        mContext = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(this, ReceiveDataService.class);
        unbindService(conn);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("TEST", "Granted");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initThinCar();
        /** 初始化车机SDK */
        initLeAuto();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_PERMISSION_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("MainActivity","Camera permission granted!");

            } else {
                Log.i("MainActivity","Camera permission not granted!");
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        /** 监听usb状态*/
        mUsbStateReceiver = new UsbStateReceiver();
        IntentFilter usbFilter = new IntentFilter(UsbStateReceiver.ACTION_USB_STATE);
        usbFilter.setPriority(1000);
        registerReceiver(mUsbStateReceiver, usbFilter);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceiver(mUsbStateReceiver);
    }

    private class UsbStateReceiver extends BroadcastReceiver {

        /**
         * 监听USB连接状态
         */
        public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
        public static final String USB_CONNECTED = "connected";
        public static final String USB_FUNCTION_ADB = "adb";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_STATE.equals(action)) {
                boolean connect = intent.getBooleanExtra(USB_CONNECTED, false);
                boolean adb = intent.getBooleanExtra(USB_FUNCTION_ADB, false);
                boolean accessory = intent.getBooleanExtra("accessory", false);
                if (!adb) {
                    if (connect) {
                        startRecordActivity(true);
//                        if (!isAoaRecordSuccess) {
//                            startRecordActivity(true);
//                        }
//                        isAoaRecordSuccess = false;
                    } else {
//                        if (mThinCarIAOACallback != null) {
//                            mThinCarIAOACallback.onAoaConnectStateChange(ThinCarDefine.ConnectState.STATE_DISCONNECT);
//                        }
                    }
                } else {
                    if (connect) {
                        startRecordActivity(false);
                    }
                }
            }
        }
    }

    private void startRecordActivity(boolean isThincar) {
        Intent intent = new Intent(mContext, ScreenRecordActivity.class);
        if (isThincar) {
            intent.setAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        } else {
            intent.setAction(ScreenRecordActivity.NORMAL_START_ACTIVITY_ACTION);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void initThinCar() {
        mThinCarIAOACallback = new ThinCarIAOACallback(this);
        /** 表示从aoa过来且连接好了 */
        String action = this.getIntent().getAction();
        if (action != null) {
            if (action.equals(ScreenRecordActivity.AOA_START_ACTIVITY_ACTION)) {
//                if (EcoApplication.mIsRestart) {
//                    changeToNavi();
//                }
                mThinCarIAOACallback.onAoaConnectStateChange(ThinCarDefine.ConnectState.STATE_CONNECT);
            } else if (action.equals(ThinCarIAOACallback.ADB_RESTART_ACTIVITY_ACTION)) {
//                if (EcoApplication.mIsRestart) {
//                    changeToNavi();
//                }
                mThinCarIAOACallback.onAdbConnectStateChange(ThinCarDefine.ConnectState.STATE_CONNECT);
            }
        }

      //  EcoApplication.mIsRestart = false;
        this.getIntent().setAction(Intent.ACTION_MAIN);

        Intent intent = new Intent(this, ReceiveDataService.class);
        this.bindService(intent,conn, Service.BIND_AUTO_CREATE);
    }

    /**
     * 初始化车机SDK竖屏
     */
    public void initLeAuto() {
        mLeAutoLinkListner = new LeAutoLinkListner(this, mHandler, mThinCarIAOACallback);
        SdkManager.getInstance(this).initSdk(mLeAutoLinkListner);
        SdkManager.getInstance(this).setKeyboardRemoteListener(mLeAutoLinkListner);
    }
}
