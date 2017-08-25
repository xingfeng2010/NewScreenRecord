package com.leauto.link.lightcar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.util.Log;

import com.leauto.link.lightcar.module.ThincarDevice;
import com.leauto.link.lightcar.service.ScreenRecorderService;


/**
 * Created by Jerome on 2016/7/7.
 */
public class ScreenRecorderManager {

    public static final int REQUEST_CODE_SCREEN_CAPTURE = 1;
    private static ScreenRecorderManager instance = null;
    private Context mContext;
    private boolean mHasStartRecord;

    private ScreenRecorderManager(Context context) {
        mContext = context;
    }

    public void setRecorderContext(Context context) {
        mContext = context;
    }

    public static ScreenRecorderManager getScreenRecorderManager(Context context) {
        if (instance == null) {
            instance = new ScreenRecorderManager(context);
        }
        return instance;
    }

    /**
     * media projection 权限申请result
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ScreenRecorderManager.REQUEST_CODE_SCREEN_CAPTURE == requestCode) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            //申请结果后开始录频
           startScreenRecorder(resultCode, data);
        }
    }

    public void queryRecordingStatus() {
        final Intent intent = new Intent(mContext, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_QUERY_STATUS);
        mContext.startService(intent);
    }

    public void startScreenRecorder(final int resultCode, final Intent data) {
        ThincarDevice device = AccesssoryManager.getAccesssoryManager(mContext).getConnectDevice();
        final Intent intent = new Intent(mContext, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_START);
        intent.putExtra(ScreenRecorderService.EXTRA_RESULT_CODE, resultCode);
        intent.putExtra(ScreenRecorderService.EXTRA_DEVICE_WIDTH, device.getCarWidth());
        intent.putExtra(ScreenRecorderService.EXTRA_DEVICE_HEIGHT, device.getCarHeight());
        intent.putExtras(data);
        mContext.startService(intent);
    }

    public void startScreenCaptureIntent(Activity activity) {
        //如果已经开始录屏，不再申请权限
        LogUtils.i("ScreenRecorderManager","startScreenCaptureIntent mHasStartRecord:" + mHasStartRecord);
        if (mHasStartRecord) {
            return;
        }
        mHasStartRecord = true;
        //LogUtils.i("ScreenRecorderManager","startScreenCaptureIntent mHasStartRecord:" + mHasStartRecord);
        final MediaProjectionManager manager
                = (MediaProjectionManager) mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        final Intent permissionIntent = manager.createScreenCaptureIntent();
        activity.startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE);
    }


    public void stopScreenRecorder() {
        final Intent intent = new Intent(mContext, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_STOP);
        mContext.startService(intent);

        mHasStartRecord = false;
    }

    public void pauseScreenRecorder() {
        final Intent intent = new Intent(mContext, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_PAUSE);
        mContext.startService(intent);
    }

    public void resumeScreenRecorder() {
        final Intent intent = new Intent(mContext, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_RESUME);
        mContext.startService(intent);
    }

    public void setHasStartRecord(boolean value) {
        mHasStartRecord = value;
    }

    public void pauseThincarScreenData() {
        final Intent intent = new Intent(mContext, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_PAUSE_SCRERN_DATA);
        mContext.startService(intent);
    }

    public void resumeThincarScreenData() {
        final Intent intent = new Intent(mContext, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_RESUME_SCRERN_DATA);
        mContext.startService(intent);
    }
}
