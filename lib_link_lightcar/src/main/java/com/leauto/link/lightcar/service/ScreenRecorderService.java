package com.leauto.link.lightcar.service;
/*
 * ScreenRecordingSample
 * Sample project to cature and save audio from internal and video from screen as MPEG4 file.
 *
 * Copyright (c) 2015 saki t_saki@serenegiant.com
 *
 * File name: ScreenRecorderService.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
*/

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.leauto.link.lightcar.AccesssoryManager;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.media.MediaAudioEncoder;
import com.leauto.link.lightcar.media.MediaEncoder;
import com.leauto.link.lightcar.media.MediaMuxerWrapper;
import com.leauto.link.lightcar.media.MediaScreenEncoder;
import com.leauto.link.lightcar.protocol.DataSendManager;

import java.io.IOException;

public class ScreenRecorderService extends IntentService {
    private static final boolean DEBUG = false;
    private static final String TAG = "ScreenRecorderService";

    private static final String BASE = "com.serenegiant.service.ScreenRecorderService.";
    public static final String ACTION_START = BASE + "ACTION_START";
    public static final String ACTION_STOP = BASE + "ACTION_STOP";
    public static final String ACTION_PAUSE = BASE + "ACTION_PAUSE";
    public static final String ACTION_RESUME = BASE + "ACTION_RESUME";

    public static final String ACTION_PAUSE_SCRERN_DATA = BASE + "ACTION_PAUSE_SCRERN_DATA";
    public static final String ACTION_RESUME_SCRERN_DATA = BASE + "ACTION_RESUME_SCRERN_DATA";

    public static final String ACTION_QUERY_STATUS = BASE + "ACTION_QUERY_STATUS";
    public static final String ACTION_QUERY_STATUS_RESULT = BASE + "ACTION_QUERY_STATUS_RESULT";
    public static final String EXTRA_RESULT_CODE = BASE + "EXTRA_RESULT_CODE";
    public static final String EXTRA_QUERY_RESULT_RECORDING = BASE + "EXTRA_QUERY_RESULT_RECORDING";
    public static final String EXTRA_QUERY_RESULT_PAUSING = BASE + "EXTRA_QUERY_RESULT_PAUSING";
    public static final String EXTRA_DEVICE_WIDTH = BASE + "EXTRA_DEVICE_WIDTH";
    public static final String EXTRA_DEVICE_HEIGHT = BASE + "EXTRA_DEVICE_HEIGHT";


    private static Object sSync = new Object();
    private static MediaMuxerWrapper sMuxer;


    private MediaProjectionManager mMediaProjectionManager;
    public static MediaScreenEncoder mediaScreenEncoder = null;

    public ScreenRecorderService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.v(TAG, "onCreate:");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (DEBUG) Log.v(TAG, "onHandleIntent:intent=" + intent);
        final String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            startScreenRecord(intent);
            updateStatus();
        } else if (ACTION_STOP.equals(action)) {
            stopScreenRecord();
            updateStatus();
            DataSendManager.getInstance().sendEncoderDestory();
        } else if (ACTION_QUERY_STATUS.equals(action)) {
            updateStatus();
        } else if (ACTION_PAUSE.equals(action)) {
            pauseScreenRecord();
        } else if (ACTION_RESUME.equals(action)) {
            resumeScreenRecord();
        } else if (ACTION_PAUSE_SCRERN_DATA.equals(action)) {
            pauseThincarScreenData();
        }  else if (ACTION_RESUME_SCRERN_DATA.equals(action)) {
            resumeThincarScreenData();
        }
    }

    private void updateStatus() {
        final boolean isRecording, isPausing;
        synchronized (sSync) {
            isRecording = (sMuxer != null);
            isPausing = isRecording ? sMuxer.isPaused() : false;
        }
        final Intent result = new Intent();
        result.setAction(ACTION_QUERY_STATUS_RESULT);
        result.putExtra(EXTRA_QUERY_RESULT_RECORDING, isRecording);
        result.putExtra(EXTRA_QUERY_RESULT_PAUSING, isPausing);
        if (DEBUG)
            Log.v(TAG, "sendBroadcast:isRecording=" + isRecording + ",isPausing=" + isPausing);
        sendBroadcast(result);
    }

    /**
     * start screen recording as .mp4 file
     *
     * @param intent
     */
    private void startScreenRecord(final Intent intent) {
        if (DEBUG) Log.v(TAG, "startScreenRecord:sMuxer=" + sMuxer);
        synchronized (sSync) {
            if (sMuxer == null) {
                final int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
                // get MediaProjection
                final MediaProjection projection = mMediaProjectionManager.getMediaProjection(resultCode, intent);
                if (projection != null) {
                    final DisplayMetrics metrics = getResources().getDisplayMetrics();
                    final int density = metrics.densityDpi;

                    if (DEBUG) Log.v(TAG, "startRecording:");
                    try {
                        sMuxer = new MediaMuxerWrapper(".mp4");    // if you record audio only, ".m4a" is also OK.
                        if (true) {
                            // for screen capturing
                            int width = intent.getIntExtra(EXTRA_DEVICE_WIDTH, 720);
                            int height = intent.getIntExtra(EXTRA_DEVICE_HEIGHT,1280);
                            if (mediaScreenEncoder == null) {
                                mediaScreenEncoder = new MediaScreenEncoder(ScreenRecorderService.this, sMuxer, mMediaEncoderListener,
                                        projection, width,height, density);
                            }
                        }
                        if (false) {
                            // for audio capturing
                            new MediaAudioEncoder(ScreenRecorderService.this, sMuxer, mMediaEncoderListener);
                        }
                        sMuxer.prepare();
                        sMuxer.startRecording();
                    } catch (final IOException e) {
                        Log.e(TAG, "startScreenRecord:", e);
                    }
                }
            }
        }
    }

    /**
     * stop screen recording
     */
    private void stopScreenRecord() {
        if (DEBUG) Log.v(TAG, "stopScreenRecord:sMuxer=" + sMuxer);
        synchronized (sSync) {
            mediaScreenEncoder = null;

            if (sMuxer != null) {
                sMuxer.stopRecording();
                sMuxer = null;
                // you should not wait here
            }
        }
    }

    private void pauseScreenRecord() {
        synchronized (sSync) {
            if (sMuxer != null) {
                sMuxer.pauseRecording();
            }
        }
    }

    private void resumeScreenRecord() {
        synchronized (sSync) {
            if (sMuxer != null) {
                sMuxer.resumeRecording();
            }
        }
    }

    /**
     * callback methods from encoder
     */
    private static final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
        }
    };


    private void pauseThincarScreenData() {
        synchronized (sSync) {
            if (sMuxer != null) {
                sMuxer.pauseThincarScreenData();
            }
        }
    }

    private void resumeThincarScreenData() {
        synchronized (sSync) {
            if (sMuxer != null) {
                sMuxer.resumeThincarScreenData();
            }
        }
    }
}
