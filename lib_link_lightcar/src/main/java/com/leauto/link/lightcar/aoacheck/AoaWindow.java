package com.leauto.link.lightcar.aoacheck;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.leauto.link.lightcar.AccesssoryManager;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.leauto.link.lightcar.R;
import com.leauto.link.lightcar.aoacheck.AoaCheckList;

/**
 * Created by Administrator on 2017/3/24.
 */

public class AoaWindow {

    private WindowManager mWindowManager;
    private View contentView;
    private WindowManager.LayoutParams mParams;
    private AccesssoryManager accesssoryManager;
    private boolean isWindowShow;
    private Context mContext;

    private int HIDE_AOA_MSG = 1;

    private Runnable mAoaCheckRunnable = new Runnable() {
        @Override
        public void run() {
            accesssoryManager.sendCheckButtonRange(contentView.getWidth(),contentView.getHeight());
        }
    };

    private Handler mHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            stopAoaCheck();
        }
    };

    public void initAoaWindow(final Context context) {
        mContext = context;
        accesssoryManager = AccesssoryManager.getAccesssoryManager(context);

        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        contentView = LayoutInflater.from(context).inflate(R.layout.aoa_check,null);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.y = 0;
        mParams.x = 0;

        Button button = (Button) contentView.findViewById(R.id.aoa_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // if (accesssoryManager.getIsCarClickBegin()) {
                    accesssoryManager.stopAoaCheck();
                    accesssoryManager.notifyAoaCheckPass();
                    hideAoaWindow();
                    saveAoaCheckPass();
               //}
            }
        });
    }

    public void showAoaWindow() {
        if (androidVersionCheck()) {
            if (AoaCheckList.getCheckResult(mContext.getApplicationContext(),android.os.Build.MODEL)) {
                accesssoryManager.sendCheckButtonRange(contentView.getWidth(),contentView.getHeight());
                accesssoryManager.notifyAoaCheckPass();
                return;
            }

            if (!isWindowShow) {
                mWindowManager.addView(contentView, mParams);
                isWindowShow = true;
            }
            mHander.removeCallbacks(mAoaCheckRunnable);
            mHander.removeMessages(HIDE_AOA_MSG);
            mHander.postDelayed(mAoaCheckRunnable,1000);
            mHander.sendEmptyMessageDelayed(HIDE_AOA_MSG,1500);
        } else {
            accesssoryManager.sendCheckButtonRange(contentView.getWidth(),contentView.getHeight());
        }
    }

    public void hideAoaWindow() {
        if (isWindowShow) {
            mWindowManager.removeViewImmediate(contentView);
        }

        isWindowShow = false;
    }

    private boolean androidVersionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }

        return false;
    }

    public void stopAoaCheck() {
        mHander.removeCallbacks(mAoaCheckRunnable);
        hideAoaWindow();
        accesssoryManager.stopAoaCheck();
    }

    private void saveAoaCheckPass() {
        AoaCheckList.saveCheckResult(mContext.getApplicationContext(), android.os.Build.MODEL,true);
    }
}
