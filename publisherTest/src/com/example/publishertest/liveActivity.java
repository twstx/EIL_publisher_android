package com.example.publishertest;

import com.eil.eilpublisher.interfaces.LiveEventInterface;
import com.eil.eilpublisher.interfaces.LiveInterface;
import com.eil.eilpublisher.liveConstants.LiveConstants;
import com.eil.eilpublisher.media.LivePushConfig;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class liveActivity extends Activity implements OnClickListener{

	public final static String TAG = "LiveActivity";
	private SurfaceView mSurfaceView;
	
	private Button mBtnStartLive;
	private Button mBtnStopLive;
	private Button mBtnSwitchCam;
	
	static int mDefinitionMode = 0;//默认480分辨率
	static String mRtmpUrl = "";
	static int mEncodeMode = 1;//默认硬编码
	private LivePushConfig mLivePushConfig;
	Handler mHandler = null; 
	Runnable mRunnable;
	 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
		setContentView(R.layout.activity_live);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
		
		mBtnStartLive = (Button) findViewById(R.id.btn_start);
		mBtnStartLive.setOnClickListener(this);
		mBtnStopLive = (Button) findViewById(R.id.btn_stop);
		mBtnStopLive.setOnClickListener(this);
		mBtnStopLive.setEnabled(false);
		mBtnSwitchCam = (Button) findViewById(R.id.btn_switch);
		mBtnSwitchCam.setOnClickListener(this);
		
		mLivePushConfig = new LivePushConfig();
		updatePushConfig();
		mSurfaceView=(SurfaceView)findViewById(R.id.surfaceView);
		LiveInterface.getInstance().init(mSurfaceView , mLivePushConfig);
		
		mHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case LiveConstants.PUSH_ERR_NET_DISCONNECT:
                    	showMessage("连接失败，等待重连");
                   	 	updateUI(false);
                        break;
                    case LiveConstants.PUSH_ERR_AUDIO_ENCODE_FAIL:
                    	updateUI(false);
                        showMessage("音频采集编码线程启动失败");
                        break;
                    case LiveConstants.PUSH_ERR_VIDEO_ENCODE_FAIL:
                    	updateUI(false);
                        showMessage("视频采集编码线程启动失败");
                        break;
                    case LiveConstants.PUSH_EVT_CONNECT_SUCC: 
                    	showMessage("连接成功");
                        break;
                    default:
                        break;
                }
            }

        };
		mRunnable = new Runnable() {
            public void run() {
            	 Log.i(TAG, "LiveEventInterface disconnect and reconnect init"); 
            	 try {
					   	Thread.sleep(8000);
					 } catch (InterruptedException e) {
						 // TODO Auto-generated catch block
						 e.printStackTrace();
					 }
				 int ret = LiveInterface.getInstance().start(); 
				 if(ret < 0 )
				 {
					 showMessage("重连失败");
					 Log.i(TAG, "reconnect start failed");
					 updateUI(false);					   
				 }else
				 {
					 updateUI(true);
				 }
             }
        };
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LiveInterface.getInstance().resume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LiveInterface.getInstance().uninit();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LiveInterface.getInstance().pause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 switch (v.getId()) {
		 case R.id.btn_start:
			 int ret = LiveInterface.getInstance().start();   
			 if(ret < 0)
			 {
				 Log.i(TAG, "connect failed");
				 showMessage("连接失败");
				 updateUI(false);
				 break;
			 }
			 updateUI(true);
             break;
         case R.id.btn_stop:
        	 LiveInterface.getInstance().stop();
        	 updateUI(false);
             break;
         case R.id.btn_switch:
        	 LiveInterface.getInstance().switchCamera();
             break;         
		 }
	}
	
	private void showMessage(String message)
	{
		 Toast toast = Toast.makeText(liveActivity.this, 
				 message,                     
                 Toast.LENGTH_SHORT);                  
		  	 toast.show();
	}
	
	private void updateUI(boolean state)
	{
		if(state)
		{
			mBtnStartLive.setEnabled(false);
			mBtnStopLive.setEnabled(true);
		}else
		{
			mBtnStartLive.setEnabled(true);
			mBtnStopLive.setEnabled(false);
		}
	}
	
	private LiveEventInterface mCaptureStateListener = new LiveEventInterface() {
        @Override
        public void onStateChanged(int eventId) {
        	Message msg =new Message();
        	
            switch (eventId) {
                case LiveConstants.PUSH_ERR_NET_DISCONNECT://断线
               // case LiveConstants.PUSH_ERR_NET_CONNECT_FAIL://连接失败
                	 Log.i(TAG, "LiveEventInterface disconnect and reconnect"); 
                	 msg.what = LiveConstants.PUSH_ERR_NET_DISCONNECT;
                	 mHandler.sendMessage(msg);
                	 //LiveInterface.getInstance().stop();
                	 //重连
					 mHandler.post(mRunnable);
					
	                 break;
                case LiveConstants.PUSH_ERR_AUDIO_ENCODE_FAIL://音频采集编码线程启动失败
                    Log.i(TAG, "LiveEventInterface audio encoder failed");
                    msg.what = LiveConstants.PUSH_ERR_AUDIO_ENCODE_FAIL;
               	 	mHandler.sendMessage(msg);
                    break;
                case LiveConstants.PUSH_ERR_VIDEO_ENCODE_FAIL://视频采集编码线程启动失败
                    Log.i(TAG, "LiveEventInterface video encoder failed");
                    msg.what = LiveConstants.PUSH_ERR_VIDEO_ENCODE_FAIL;
               	 	mHandler.sendMessage(msg);
                    break;
                case LiveConstants.PUSH_EVT_CONNECT_SUCC: //连接成功
                	Log.i(TAG, "LiveEventInterface connect ok");
                	msg.what = LiveConstants.PUSH_EVT_CONNECT_SUCC;
                	mHandler.sendMessage(msg);
                    break;
            }
        }
    };
    
	private void updatePushConfig()
	{
		mLivePushConfig.setRtmpUrl(mRtmpUrl);
		mLivePushConfig.setEventInterface(mCaptureStateListener);
		mLivePushConfig.setAppContext(this);
		mLivePushConfig.setAudioChannels(AudioFormat.CHANNEL_IN_MONO);
		mLivePushConfig.setAudioSampleRate(44100);
		if(0 == mEncodeMode)
		{
			mLivePushConfig.setHWVideoEncode(false);
		}else
		{
			mLivePushConfig.setHWVideoEncode(true);
		}
		
		switch(mDefinitionMode)
		{
			case 0:
				mLivePushConfig.setVideoSize(640,480);
				mLivePushConfig.setVideoFPS(15);
				mLivePushConfig.setVideoBitrate(800);
				break;
			case 1:
				mLivePushConfig.setVideoSize(960,540);
				mLivePushConfig.setVideoFPS(18);
				mLivePushConfig.setVideoBitrate(1000);
				break;
			case 2:
				mLivePushConfig.setVideoSize(1280,720);
				mLivePushConfig.setVideoFPS(15);
				mLivePushConfig.setVideoBitrate(1200);
				break;	
		}
	}
	
    private void back() 
    {
	    Intent myIntent = new Intent();  
        myIntent = new Intent(liveActivity.this, MainActivity.class);  
        startActivity(myIntent);  
        this.finish();
    }

	public static void setDefinitionMode(int i) {
		// TODO Auto-generated method stub
		mDefinitionMode = i;
	}

	public static void setRtmpUrl(String strUrl) {
		// TODO Auto-generated method stub
		mRtmpUrl = strUrl;
	}

	public static void setEncodeMode(int i) {
		// TODO Auto-generated method stub
		mEncodeMode = i;
	}	
	
}
