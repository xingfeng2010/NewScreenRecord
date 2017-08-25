package com.leauto.link.lightcar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Bundle;
import android.widget.Toast;

import com.leauto.link.lightcar.aoacheck.AoaCheckList;
import com.leauto.link.lightcar.aoacheck.AoaWindow;
import com.leauto.link.lightcar.listener.AoaCheckListener;
import com.leauto.link.lightcar.module.ThincarDevice;

public class ScreenRecordActivity extends Activity implements AoaCheckListener {

    /** 传递给主应用程序是否需要调整为竖屏 */
    public static final String TURN_CAR_DIRECTION = "TURN_CAR_DIRECTION";
    /** 把应用调整为横屏*/
    public static final int TURN_CAR_LAND = 1;
    /** 把应用调整为竖屏*/
    public static final int TURN_CAR_PORTRAIT = 2;

    private ScreenRecorderManager recoderManager;
    private AccesssoryManager mAccesssoryManager;
    private static final String TAG = "ScreenRecord";
    private AoaWindow mAoaWindow;

    public static final String AOA_START_ACTIVITY_ACTION = "com.leauto.link.lightcar.aoa.start.activity";
    public static final String NORMAL_START_ACTIVITY_ACTION = "com.leauto.link.lightcar.normal.start.activity";

    Handler mHandler = new Handler();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.i(TAG,"onActivityResult resultCode:" + resultCode);
        if (ScreenRecorderManager.REQUEST_CODE_SCREEN_CAPTURE == requestCode) {
            mAccesssoryManager.notifyRecordPermission(true);
            recoderManager.startScreenRecorder(resultCode, data);//开始录屏

            startMainActivity(true);

            mAccesssoryManager.notifyAoaConnected();
        } else {
            mAccesssoryManager.notifyRecordPermission(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG,"onCreate");

        initThinCar();

        mAoaWindow = new AoaWindow();
        mAoaWindow.initAoaWindow(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i(TAG,"onStart");

    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = this.getIntent();
        LogUtils.i(TAG,"onResume intent:" + intent);
        if (intent != null && intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)) {
            mAccesssoryManager.openAccessory();
        } else if (intent != null && intent.getAction().equals(NORMAL_START_ACTIVITY_ACTION)){
           // startMainActivity(false);
        }
    }

    private void initThinCar() {
        recoderManager = ScreenRecorderManager.getScreenRecorderManager(this);
        recoderManager.setRecorderContext(this);

       AccesssoryManager.createNewInstance(this);
        mAccesssoryManager = AccesssoryManager.getAccesssoryManager(this);
        mAccesssoryManager.setAccessContext(this);
        mAccesssoryManager.initAccessory(this);
    }

    @Override
    public void onLaunchAoaCheck() {
        LogUtils.i(TAG,"onLaunchAoaCheck");
        ScreenRecordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /** 确实需要进行反控检测，则把activity拉到前台来 */
                if (!AoaCheckList.getCheckResult(ScreenRecordActivity.this.getApplicationContext(),android.os.Build.MODEL)) {
                    bringActivityToFront();
                }
                mAoaWindow.showAoaWindow();
            }
        });
    }

    private void bringActivityToFront() {
        LogUtils.i(TAG,"bringActivityToFront");
        Intent start = new Intent(ScreenRecordActivity.this.getApplicationContext(),ScreenRecordActivity.class);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(start);
    }

    @Override
    public void onAoaCheckFinish(final boolean checkPass) {
        LogUtils.i(TAG,"onAoaCheckFinish");
        ScreenRecordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAoaWindow.hideAoaWindow();
                if (!checkPass) {
                    startMainActivity(false);
                }
            }
        });

    }

    @Override
    public void finishActivity() {
        ScreenRecordActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        LogUtils.i(TAG,"onDestroy");
        if (mAccesssoryManager != null) {
            mAccesssoryManager.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                LogUtils.i(TAG, "竖屏");
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                LogUtils.i(TAG, "横屏");
            default:
                break;
        }
    }

    private void startMainActivity(boolean aoaStart) {
        ThincarDevice device = mAccesssoryManager.getConnectDevice();
        Intent intent = new Intent();
        if (aoaStart) {
            intent.setAction(AOA_START_ACTIVITY_ACTION);
        } else {
            intent.setAction(NORMAL_START_ACTIVITY_ACTION);
        }

        int value = 0;
        if (device != null) {
           value = device.getCarHeight() > device.getCarWidth() ? TURN_CAR_PORTRAIT:TURN_CAR_LAND;
            intent.putExtra(TURN_CAR_DIRECTION,value);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.startActivity(intent);
    }
}
