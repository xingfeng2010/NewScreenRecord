package com.leauto.link.lightcar.media;
/*
 * ScreenRecordingSample
 * Sample project to cature and save audio from internal and video from screen as MPEG4 file.
 *
 * Copyright (c) 2015 saki t_saki@serenegiant.com
 *
 * File name: MediaScreenEncoder.java
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

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.projection.MediaProjection;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import com.leauto.link.lightcar.AccesssoryManager;
import com.leauto.link.lightcar.glutils.EglTask;
import com.leauto.link.lightcar.glutils.FullFrameRect;
import com.leauto.link.lightcar.glutils.Texture2dProgram;
import com.leauto.link.lightcar.glutils.WindowSurface;
import com.leauto.link.lightcar.server.MySocketServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediaScreenEncoder extends MediaVideoEncoderBase {
	private static final boolean DEBUG = false;	// TODO set false on release
	private static final String TAG = "MediaScreenEncoder";

	private static final String MIME_TYPE = "video/avc";
	// parameters for recording
    private static final int FRAME_RATE = 25;

	private MediaProjection mMediaProjection;
    private final int mDensity;
    private Surface mSurface;
    private final Handler mHandler;
	private MySocketServer mServerSocket;

	private AtomicBoolean mQuit = new AtomicBoolean(false);

	public MediaScreenEncoder(Context context, final MediaMuxerWrapper muxer, final MediaEncoderListener listener,
							  final MediaProjection projection, final int width, final int height, final int density) {

		super(context, muxer, listener, width, height);

		//initServer();
		mMediaProjection = projection;
		mDensity = density;
		final HandlerThread thread = new HandlerThread(TAG);
		thread.start();
		mHandler = new Handler(thread.getLooper());
	}
	private void initServer() {
		//mServerSocket = new MySocketServer(10011);
	}

	private void uninitServer() {
		mServerSocket.close();
	}

	public void sendData(byte[] data)
	{
		mServerSocket.sendMsg(data);
	}
	@Override
	protected void release()
	{
		mQuit.set(true);
		mHandler.getLooper().quit();
		super.release();
	}

	@Override
	void prepare() throws IOException {
		if (DEBUG) Log.i(TAG, "prepare: ");
		mSurface = prepare_surface_encoder(MIME_TYPE, FRAME_RATE);
        mMediaCodec.start();
        mIsCapturing = true;
        new Thread(mScreenCaptureTask, "ScreenCaptureThread").start();
        if (DEBUG) Log.i(TAG, "prepare finishing");
        if (mListener != null) {
        	try {
        		mListener.onPrepared(this);
        	} catch (final Exception e) {
        		Log.e(TAG, "prepare:", e);
        	}
        }
	}

	@Override
	void stopRecording() {
		if (DEBUG) Log.v(TAG,  "stopRecording:");
		super.stopRecording();

		synchronized (mSync) {
			mIsCapturing = false;
			mSync.notifyAll();
		}

	}

	private boolean requestDraw;
	private final DrawTask mScreenCaptureTask = new DrawTask(null, 0);

	private final class DrawTask extends EglTask
	{
		private VirtualDisplay display;
		private long intervals;
		private int mTexId;
		private SurfaceTexture mSourceTexture;
		private Surface mSourceSurface;
    	private WindowSurface mEncoderSurface;
    	private FullFrameRect mDrawer;
    	private final float[] mTexMatrix = new float[16];

    	public DrawTask(final EGLContext shared_context, final int flags)
		{
    		super(shared_context, flags);
    	}

		@Override
		protected void onStart() {
		    if (DEBUG) Log.d(TAG,"mScreenCaptureTask#onStart:");
	    	mDrawer = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
			mTexId = mDrawer.createTextureObject();
			mSourceTexture = new SurfaceTexture(mTexId);
			mSourceTexture.setDefaultBufferSize(mWidth, mHeight);
			mSourceSurface = new Surface(mSourceTexture);
			mSourceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener, mHandler);
			mEncoderSurface = new WindowSurface(getEglCore(), mSurface);

	    	if (DEBUG) Log.d(TAG, "setup VirtualDisplay");
			intervals = (long)(1000f / FRAME_RATE);
		    display = mMediaProjection.createVirtualDisplay(
		    	"Capturing Display",
		    	mWidth, mHeight, mDensity,
		    	DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
		    	mSourceSurface, null, null);
			if (DEBUG) Log.v(TAG,  "screen capture loop:display=" + display);
			queueEvent(mDrawTask);
			//new Thread(new SendEnCodeDataToRemote()).start();
		}

		@Override
		protected void onStop() {
			if (mDrawer != null) {
				mDrawer.release();
				mDrawer = null;
			}
			if (mSourceSurface != null) {
				mSourceSurface.release();
				mSourceSurface = null;
			}
			if (mSourceTexture != null) {
				mSourceTexture.release();
				mSourceTexture = null;
			}
			if (mEncoderSurface != null) {
				mEncoderSurface.release();
				mEncoderSurface = null;
			}
			makeCurrent();
			if (DEBUG) Log.v(TAG, "mScreenCaptureTask#onStop:");
			if (display != null) {
				if (DEBUG) Log.v(TAG,  "release VirtualDisplay");
				display.release();
			}
			if (DEBUG) Log.v(TAG,  "tear down MediaProjection");
		    if (mMediaProjection != null) {
	            mMediaProjection.stop();
	            mMediaProjection = null;
	        }
		}

		@Override
		protected boolean onError(final Exception e) {
			if (DEBUG) Log.w(TAG, "mScreenCaptureTask:", e);
			return false;
		}

		@Override
		protected boolean processRequest(final int request, final int arg1, final Object arg2) {
			return false;
		}

		private final OnFrameAvailableListener mOnFrameAvailableListener = new OnFrameAvailableListener() {
			@Override
			public void onFrameAvailable(final SurfaceTexture surfaceTexture)
			{
				if (mIsCapturing && shouldNotifyData) {
					synchronized (mSync) {
						requestDraw = true;
						mSync.notifyAll();
					}
				}
			}
		};

		private long mLastFrameTime = System.currentTimeMillis();
		private final Runnable mDrawTask = new Runnable() {
			@Override
			public void run() {
				boolean local_request_pause;
				boolean local_request_draw;
				synchronized (mSync) {
					local_request_pause = mRequestPause;
					local_request_draw = requestDraw;
					if (!requestDraw) {
						try {
							mSync.wait(intervals);
							local_request_pause = mRequestPause;
							local_request_draw = requestDraw;
							requestDraw = false;
						} catch (final InterruptedException e) {
							return;
						}
					}
				}

				if (mIsCapturing)
				{
					if (local_request_draw)
					{
						mSourceTexture.updateTexImage();
						mSourceTexture.getTransformMatrix(mTexMatrix);
					}
					long l = System.currentTimeMillis();
					if ( (l - DrawTask.this.mLastFrameTime) > DrawTask.this.intervals)
					{
						DrawTask.this.mLastFrameTime = l;
						if (!local_request_pause)
						{
							mEncoderSurface.makeCurrent();
							mDrawer.drawFrame(mTexId, mTexMatrix);
							mEncoderSurface.swapBuffers();
						}
					}
					makeCurrent();
					GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
					GLES20.glFlush();

					frameAvailableSoon();
					queueEvent(this);
				}
				else
				{
					releaseSelf();
				}
			}
		};

	};

	public class SendEnCodeDataToRemote implements Runnable
	{
		public final static int TIMEOUT_US = 10000;
		private byte[] mSpsAndPps = null;
		@Override
		public void run()
		{
			recordVirtualDisplay();
		}
		private void encodeToVideoTrack(int index)
		{
			ByteBuffer encodedData = mMediaCodec.getOutputBuffer(index);
			if (encodedData != null)
			{
				encodedData.position(mBufferInfo.offset);
				encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
				byte[] outData = new byte[mBufferInfo.size];
				encodedData.get(outData);

				if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
				{
//					Log.e(TAG, "SPS  PPS DATA");
					ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
					if (spsPpsBuffer.getInt() == 0x00000001)
					{
						mSpsAndPps = new byte[outData.length];
						System.arraycopy(outData, 0, mSpsAndPps, 0, outData.length);
					}
				} else if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
					//关键帧
//					Log.e(TAG, "Key Frame");
					byte[] data = new byte[mSpsAndPps.length + outData.length];
					System.arraycopy(mSpsAndPps, 0, data, 0, mSpsAndPps.length);
					System.arraycopy(outData, 0, data, mSpsAndPps.length, outData.length);
					sendData(data);
				} else {
//					Log.e(TAG, "========not Key Frame");
					//非关键帧

					sendData(outData);
				}
			}
		}
		private void recordVirtualDisplay()
		{
			while (!mQuit.get())
			{
				int index = mMediaCodec.dequeueOutputBuffer( mBufferInfo, TIMEOUT_US );
//				Log.i(TAG, "dequeue output buffer index=" + index);
				if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
				{

				} else if (index == MediaCodec.INFO_TRY_AGAIN_LATER)
				{
//					Log.d(TAG, "retrieving buffers time out!");
					try {
						// wait 10ms
						Thread.sleep(10);
					} catch (InterruptedException e)
					{
					}
				} else if (index >= 0)
				{
					encodeToVideoTrack(index);
					mMediaCodec.releaseOutputBuffer(index, false);
				}
			}
		}
	}
}
