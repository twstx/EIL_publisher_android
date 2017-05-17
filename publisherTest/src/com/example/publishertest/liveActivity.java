package com.example.publishertest;
import java.util.ArrayList;
import java.util.List;

import com.eil.eilpublisher.interfaces.LiveCallbackInterface.LiveNetStateInterface;
import com.eil.eilpublisher.interfaces.LiveCallbackInterface.LiveEventInterface;
import com.eil.eilpublisher.interfaces.LiveInterface;
import com.eil.eilpublisher.liveConstants.LiveConstants;
import com.eil.eilpublisher.media.LivePushConfig;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.AudioFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class liveActivity extends Activity implements OnClickListener, OnCheckedChangeListener,OnSeekBarChangeListener{

	public final static String TAG = "LiveActivity";
	private GLSurfaceView mSurfaceView;
	
	private Button mBtnStartLive;
	private Button mBtnStopLive;
	private Button mBtnSwitchCam;
	private Button mBtnRecord;
	private CheckBox mLight;
	private CheckBox mMirror;
	private CheckBox mTitle;
	private TextView mText;
	CheckBox mWatermark0;
	CheckBox mWatermark1;
	CheckBox mWatermark2;
	CheckBox mWatermark3;
	private Button mBtnOpenPlay;
	private Button mBtnPlay;
	private Button mBtnResize;
	private static TextView mNetInfoTv;
	private CheckBox mMute;
	private TextView mTextVersion;
	
	private LivePushConfig mLivePushConfig;
	static String mRtmpUrl = "rtmp://rtmppush.ejucloud.com/ehoush/liuy1";
	static String mPlayUrl = "rtmp://rtmppush.ejucloud.com/ehoush/liuy2";
	static int mDefinitionMode = 0;//默认480分辨
	static int mEncodeMode = 1;//默认硬编码
	private boolean mRecording = false;
	private boolean mPublishing = false;
	private boolean mOpenPlaying = false;
	private boolean mPlaying = false;
	private boolean mPicturing = false;
	static boolean mWeaknetOptition = true;
	static int mPublishOrientation = 1;
	static boolean mAutoRotate = false;
	private SeekBar mFilterLevelBar;
	private static int mPicPos = 0;
	
	Handler mHandler = null; 
	Runnable mRunnable;

	private float lastDestance;//开始距离  
	private float currentDestance;//结束距离
	private int curZoomLevel = 1;
	private ArrayAdapter<String> adapterPic;
    private Spinner spinnerPic;
    private List<String> list = new ArrayList<String>();   

    static EditText mTitleText;
    
	public static final int PUBLISH_EVENTINFO_MSG=1;
	public static final int PUBLISH_NETINFO_MSG=2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
		setContentView(R.layout.activity_live);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
//		if(0 == mPublishOrientation%2)
//		{
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		}else
//		{
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		}
		mBtnStartLive = (Button) findViewById(R.id.btn_start);
		mBtnStartLive.setOnClickListener(this);
		mBtnStopLive = (Button) findViewById(R.id.btn_stop);
		mBtnStopLive.setOnClickListener(this);
		mBtnStopLive.setEnabled(false);
		mBtnSwitchCam = (Button) findViewById(R.id.btn_switch);
		mBtnSwitchCam.setOnClickListener(this);
		mBtnRecord = (Button) findViewById(R.id.btn_record);
		mBtnRecord.setOnClickListener(this);
		mBtnOpenPlay = (Button) findViewById(R.id.btn_openPlay);
		mBtnOpenPlay.setOnClickListener(this);
		mBtnPlay = (Button) findViewById(R.id.btn_play);
		mBtnPlay.setOnClickListener(this);
		mBtnPlay.setEnabled(false);
		mBtnResize = (Button) findViewById(R.id.btn_resize);
		mBtnResize.setOnClickListener(this);
		mBtnResize.setEnabled(false);
		mLight = (CheckBox) findViewById(R.id.check_light);
		mLight.setOnCheckedChangeListener(this);
		mMirror = (CheckBox) findViewById(R.id.check_mirror);
		mMirror.setOnCheckedChangeListener(this);
		mTitle = (CheckBox) findViewById(R.id.cbTitle);
		mTitle.setOnCheckedChangeListener(this);
		
		mText = (TextView)findViewById(R.id.tv);
		mText.setVisibility(View.INVISIBLE);
		mWatermark0=(CheckBox)findViewById(R.id.checkBox1);
		mWatermark0.setOnCheckedChangeListener(this);
		mWatermark1=(CheckBox)findViewById(R.id.checkBox2);
		mWatermark1.setOnCheckedChangeListener(this);
		mWatermark2=(CheckBox)findViewById(R.id.checkBox3);
		mWatermark2.setOnCheckedChangeListener(this);
		mWatermark3=(CheckBox)findViewById(R.id.checkBox4);
		mWatermark3.setOnCheckedChangeListener(this);
		
		mNetInfoTv=(TextView) findViewById(R.id.tv_net);
		
		mMute=(CheckBox)findViewById(R.id.checkMute);
		mMute.setOnCheckedChangeListener(this);
		
		mTextVersion = (TextView)findViewById(R.id.tv_version);
				
		list.add("none");    
	    list.add("pic1");    
	    list.add("pic2");    
	    list.add("pic3");       
	        
		spinnerPic = (Spinner)findViewById(R.id.spinner1);
		adapterPic =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
		spinnerPic.setAdapter(adapterPic);   
		spinnerPic.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, 
                    int pos, long id) {
            	parent.setVisibility(View.VISIBLE); 
                updatePic(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            	parent.setVisibility(View.VISIBLE);  
            }
        });
		
		mHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
	    			case PUBLISH_EVENTINFO_MSG:
	    				int statusCode = (Integer)msg.obj;
	    				 switch (statusCode) {
		                    case LiveConstants.PUSH_ERR_NET_DISCONNECT:
		                    	showMessage("连接断开，自动重连中");
		                        break;
		                    case LiveConstants.PUSH_ERR_NET_CONNECT_FAIL:
		                    	showMessage("连接失败");
		                   	 	updateUI(false);
		                        break;
		                    case LiveConstants.PUSH_ERR_AUDIO_ENCODE_FAIL:
		                    	updateUI(false);
		                        showMessage("音频发送失败");
		                        break;
		                    case LiveConstants.PUSH_ERR_VIDEO_ENCODE_FAIL:
		                    	updateUI(false);
		                        showMessage("视频发送失败");
		                        break;
		                    case LiveConstants.PUSH_EVT_CONNECT_SUCC:
		                    	showMessage("连接成功");
		                        break;
		                    case LiveConstants.PUSH_EVT_PUSH_BEGIN:
		                    	updateUI(true);
		                    	showMessage("开始推流");
		                        break;
		                    case LiveConstants.PUSH_EVT_PUSH_END:
		                    	updateUI(false);
		                    	showMessage("结束推流");
		                        break;
		                    case LiveConstants.PLAY_ERR_NET_DECODE_FAIL:
		                    	showMessage("媒体输入打开失败");
		               		 	//mBtnResize.setEnabled(false);
		               		 	//mBtnPlay.setText("play");
		               		 	//mPlaying = false; 
		                        break;
		                    case LiveConstants.PUSH_ERR_OPEN_MIC_FAIL:
		                    	updateUI(false);
		                    	showMessage("麦克风打开失败");
		                    	break;
		                    case LiveConstants.PUSH_ERR_PUSH_FAIL:
		                    	updateUI(false);
		                    	showMessage("推流失败");
		                    	break;
		                    case LiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL:
		                    	updateUI(false);
		                    	showMessage("摄像头开启失败");
		                    	break;
		                    case LiveConstants.PUSH_ERR_NET_RECONNECT_FAIL:
		                    	showMessage("重连失败");
		                    	break;
		                    case LiveConstants.PUSH_ERR_NET_RECONNECT_SUCC:
		                    	showMessage("重连成功");
		                    	break;
		                    default:
		                        break;
	    				 }
	    				 break;
		            case PUBLISH_NETINFO_MSG:
		            	String[] infos=(String[]) msg.obj;
		            	float bitrate = Float.parseFloat(infos[0]);
						float lostFrame = Float.parseFloat(infos[1]);
						float lostFrameRate = Float.parseFloat(infos[2]);						
						mNetInfoTv.setText(String.format("bitrate: %6.2f kbps\n lostFrame: %6.2f\n lostFrameRate:%6.2f", bitrate, lostFrame, lostFrameRate));
                }
            }
        };
        
		mFilterLevelBar = (SeekBar) findViewById(R.id.seekBar1);
        mFilterLevelBar.setOnSeekBarChangeListener(this);
        
        String sv = LiveInterface.getInstance().getVersion();
        mTextVersion.setText(sv);
        
        mLivePushConfig = new LivePushConfig();
		updatePushConfig();
		mSurfaceView=(GLSurfaceView)findViewById(R.id.surfaceView1);
		LiveInterface.getInstance().init(mSurfaceView , mLivePushConfig);
		
	}
	
	protected void updatePic(int id) {
		// TODO Auto-generated method stub
		 Bitmap picImage ;
		 Resources r = this.getResources();
		switch(id)
		{
		case 0:
			if(mPicturing)
			{
				LiveInterface.getInstance().stopPic();
				mPicturing = false;
			}			
			break;
		case 1:
			picImage = BitmapFactory.decodeResource(r, R.drawable.tim1);
    		LiveInterface.getInstance().startPic(picImage);
    		mPicturing = true;
			break;
		case 2:
			picImage = BitmapFactory.decodeResource(r, R.drawable.tim2);
    		LiveInterface.getInstance().startPic(picImage);
    		mPicturing = true;
			break;
		case 3:
			picImage = BitmapFactory.decodeResource(r, R.drawable.tim3);
    		LiveInterface.getInstance().startPic(picImage);
    		mPicturing = true;
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LiveInterface.getInstance().resume();
		LiveInterface.getInstance().start(mRtmpUrl);
		mPublishing = true;	
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
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		 if(mRecording)
    	 {
    		 LiveInterface.getInstance().stopRecord();
    		 mBtnRecord.setText("record");
    		 mRecording = false;
    		 mText.setVisibility(View.INVISIBLE);
    	 }
		 if(mPublishing)
		 {
			 LiveInterface.getInstance().stop();
			 updateUI(false);
			 mPublishing = false;
		 }
		 if(mPlaying)
		 {
			 LiveInterface.getInstance().stopPlay();
			 mBtnResize.setEnabled(false);
			 mBtnPlay.setText("play");
			 mPlaying = false;
		 }
		 mWatermark0.setChecked(false);
		 mWatermark1.setChecked(false);
		 mWatermark2.setChecked(false);
		 mWatermark3.setChecked(false);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        	LiveInterface.getInstance().setLandscape();
        	mPublishOrientation=0;
        	updatePushConfig();
        	LiveInterface.getInstance().updateConfig(mLivePushConfig);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        //	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        	LiveInterface.getInstance().setPortrait();
        	mPublishOrientation=1;
        	updatePushConfig();
        	LiveInterface.getInstance().updateConfig(mLivePushConfig);

        }
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 switch (v.getId()) {
		 case R.id.btn_start:
			 LiveInterface.getInstance().start(mRtmpUrl);
			 mPublishing = true;
			
             break;
         case R.id.btn_stop:
        	 LiveInterface.getInstance().stop();
        	 mPublishing = false;
        	 updateUI(false);
        	
             break;
         case R.id.btn_switch:
        	 LiveInterface.getInstance().switchCamera();
             break;
         case R.id.btn_record:
        	 if(mRecording)
        	 {
        		 LiveInterface.getInstance().stopRecord();
        		 mBtnRecord.setText("record");
        		 mRecording = false;
        		 mText.setVisibility(View.INVISIBLE);
        	 }else
        	 {
        		 LiveInterface.getInstance().startRecord();
        		 mBtnRecord.setText("endrecord");
        		 mRecording = true;
        		 mText.setVisibility(View.VISIBLE);
        	 }
        	 
             break;
         case R.id.btn_openPlay:
        	 if(mOpenPlaying)
        	 {
        		 LiveInterface.getInstance().closePlay();
        		 mBtnOpenPlay.setText("openPlay");
        		 mOpenPlaying = false;
        		 mBtnPlay.setEnabled(false);
        	 }else
        	 {
        		 LiveInterface.getInstance().openPlay(mPlayUrl);
        		 mBtnOpenPlay.setText("closePlay");
        		 mOpenPlaying = true;
        		 mBtnPlay.setEnabled(true);       		 
        	 }
        	 
             break;
         case R.id.btn_play:
        	 if(mPlaying)
        	 {
        		 LiveInterface.getInstance().stopPlay();       		 
        		 mBtnResize.setEnabled(false);
        		 mBtnPlay.setText("play");
        		 mPlaying = false; 
        	 }else
        	 {
        		 LiveInterface.getInstance().startPlay();
        		 mBtnResize.setEnabled(true);
        		 mBtnPlay.setText("stopPlay");
        		 mPlaying = true;
        	 }
        	 break;
         case R.id.btn_resize:
        	 if(mPlaying)
        	 {
        		 LiveInterface.getInstance().resize();
        	 }
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
			mNetInfoTv.setText("");
		}
	}
	
	private LiveEventInterface mCaptureStateListener = new LiveEventInterface() {
        @Override
        public void onStateChanged(int eventId) {
        	if(null == mHandler)
        	{
        		return;
        	}
        	Message msg = Message.obtain(mHandler, PUBLISH_EVENTINFO_MSG);
        	
            switch (eventId) {
                case LiveConstants.PUSH_ERR_NET_DISCONNECT://断线
               // case LiveConstants.PUSH_ERR_NET_CONNECT_FAIL://连接失败
                	 Log.i(TAG, "LiveEventInterface disconnect and reconnect"); 
                	 msg.obj = LiveConstants.PUSH_ERR_NET_DISCONNECT;
                	 msg.sendToTarget();
//                	 msg.what = LiveConstants.PUSH_ERR_NET_DISCONNECT;
//                	 mHandler.sendMessage(msg);               	 
                	 //重连
					 mHandler.post(mRunnable);					
	                 break;
                case LiveConstants.PUSH_ERR_NET_CONNECT_FAIL://连接失败
                    Log.i(TAG, "LiveEventInterface connect failed");
                    msg.obj = LiveConstants.PUSH_ERR_NET_CONNECT_FAIL;
               	 	msg.sendToTarget();
                    break;
                case LiveConstants.PUSH_ERR_AUDIO_ENCODE_FAIL://音频采集编码线程启动失败
                    Log.i(TAG, "LiveEventInterface audio encoder failed");
                    msg.obj = LiveConstants.PUSH_ERR_AUDIO_ENCODE_FAIL;
                    msg.sendToTarget();
                    break;
                case LiveConstants.PUSH_ERR_VIDEO_ENCODE_FAIL://视频采集编码线程启动失败
                    Log.i(TAG, "LiveEventInterface video encoder failed");
                    msg.obj = LiveConstants.PUSH_ERR_VIDEO_ENCODE_FAIL;
                    msg.sendToTarget();
                    break;
                case LiveConstants.PUSH_EVT_CONNECT_SUCC: //连接成功
                	Log.i(TAG, "LiveEventInterface connect ok");
                	msg.obj = LiveConstants.PUSH_EVT_CONNECT_SUCC;
                	msg.sendToTarget();
                    break;
                case LiveConstants.PUSH_EVT_PUSH_BEGIN: //开始推流
                	Log.i(TAG, "LiveEventInterface begin ok");
                	msg.obj = LiveConstants.PUSH_EVT_PUSH_BEGIN;
                	msg.sendToTarget();
                    break;
                case LiveConstants.PUSH_EVT_PUSH_END: //推流结束
                	Log.i(TAG, "LiveEventInterface publish end");
                	msg.obj = LiveConstants.PUSH_EVT_PUSH_END;
                	msg.sendToTarget();
                    break;
                case LiveConstants.PLAY_ERR_NET_DECODE_FAIL: //媒体输入打开失败
                	Log.i(TAG, "LiveEventInterface player failed");
                	msg.obj = LiveConstants.PLAY_ERR_NET_DECODE_FAIL;
                	msg.sendToTarget();
                    break; 
                case LiveConstants.PUSH_ERR_OPEN_MIC_FAIL:
                	Log.i(TAG, "LiveEventInterface open mic failed");
                	msg.obj = LiveConstants.PUSH_ERR_OPEN_MIC_FAIL;
                	msg.sendToTarget();
                	break;
                case LiveConstants.PUSH_ERR_PUSH_FAIL:
                	Log.i(TAG, "LiveEventInterface publish failed");
                	msg.obj = LiveConstants.PUSH_ERR_PUSH_FAIL;
                	msg.sendToTarget();
                	break;
                case LiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL:
                	Log.i(TAG, "LiveEventInterface open camera failed");
                	msg.obj = LiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL;
                	msg.sendToTarget();
                	break;
                case LiveConstants.PUSH_ERR_NET_RECONNECT_SUCC:
                	Log.i(TAG, "LiveEventInterface reconnect success");
                	msg.obj = LiveConstants.PUSH_ERR_NET_RECONNECT_SUCC;
                	msg.sendToTarget();
                	break;
                case LiveConstants.PUSH_ERR_NET_RECONNECT_FAIL:
                	Log.i(TAG, "LiveEventInterface reconnect failed");
                	msg.obj = LiveConstants.PUSH_ERR_NET_RECONNECT_FAIL;
                	msg.sendToTarget();
                	break;
            }
        }
    };

    private LiveNetStateInterface mPublishNetstateListener = new LiveNetStateInterface() {

		@Override
		public void onNetStateBack(String state) {
			// TODO Auto-generated method stub
			Message msg =Message.obtain(mHandler, PUBLISH_NETINFO_MSG);
			if(state != null){
				String[] strMsg = state.split("\\|");
				for(int i=0; i < strMsg.length; i++){
					Log.i("ss","________________________msg["+i+"]:"+strMsg[i]);
				}
				Log.i(TAG, "LiveNetStateInterface back"); 
				msg.obj=strMsg;
				msg.sendToTarget();
			}
		}
    	
    };
    
	private void updatePushConfig()
	{
		mLivePushConfig.setRtmpUrl(mRtmpUrl);
		Resources r = this.getResources();
		Bitmap watermarkImage = BitmapFactory.decodeResource(r, R.drawable.mark);//BitmapFactory.decodeFile("/sdcard/mark.png");
		mLivePushConfig.setWatermark(watermarkImage,80,100,200,100);
		Bitmap Image0 = BitmapFactory.decodeResource(r, R.drawable.erweima);
		mLivePushConfig.setWatermark(Image0,800,100,200,200,false);
		Bitmap Image1 = BitmapFactory.decodeResource(r, R.drawable.image3);
		mLivePushConfig.setWatermark(Image1,100,700,200,200,false);
		Bitmap Image2 = BitmapFactory.decodeResource(r, R.drawable.image1);
		mLivePushConfig.setWatermark(Image2,800,600,200,200,false);
		mLivePushConfig.setRecordPath("/sdcard/");
		mLivePushConfig.setEventInterface(mCaptureStateListener);
		mLivePushConfig.setAppContext(this);
		mLivePushConfig.setAudioChannels(AudioFormat.CHANNEL_IN_MONO);
		mLivePushConfig.setAudioSampleRate(44100);
		mLivePushConfig.setWeaknetOptition(mWeaknetOptition);
		mLivePushConfig.setVideoResolution(mPublishOrientation);
		mLivePushConfig.setAutoRotation(mAutoRotate);
		mLivePushConfig.setNetstateInterface(mPublishNetstateListener);
		mLivePushConfig.setTextView(mTitleText);
		if(0 == mPicPos)
		{
			mLivePushConfig.setPlayerPosition(0, 200, 480, 270);
		}else{
			mLivePushConfig.setPlayerPosition(600, 200, 480, 270);
		}
		
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
//				mLivePushConfig.setDefinition(LivePushConfig.DEFINITION_STANDARD);
				if(0 == mPublishOrientation%2)
				{
					mLivePushConfig.setVideoSize(640,360);
				}else
				{
					mLivePushConfig.setVideoSize(360,640);
				}
				
				mLivePushConfig.setVideoFPS(20);
				mLivePushConfig.setVideoBitrate(800);
				break;
			case 1:
//				mLivePushConfig.setDefinition(LivePushConfig.DEFINITION_HIGH);
				if(0 == mPublishOrientation%2)
				{
					mLivePushConfig.setVideoSize(848,480);
				}else
				{
					mLivePushConfig.setVideoSize(480,848);
				}
				
				mLivePushConfig.setVideoFPS(20);
				mLivePushConfig.setVideoBitrate(1000);
				break;
			case 2:
//				mLivePushConfig.setDefinition(LivePushConfig.DEFINITION_SUPER);
				if(0 == mPublishOrientation%2)
				{
					mLivePushConfig.setVideoSize(1280,720);
				}else
				{
					mLivePushConfig.setVideoSize(720,1280);
				}
				
				mLivePushConfig.setVideoFPS(15);
				mLivePushConfig.setVideoBitrate(1200);
				break;	
			case 3:
//				mLivePushConfig.setDefinition(LivePushConfig.DEFINITION_SUPER);
				if(0 == mPublishOrientation%2)
				{
					mLivePushConfig.setVideoSize(1920,1080);
				}else
				{
					mLivePushConfig.setVideoSize(1080,1920);
				}
				
				mLivePushConfig.setVideoFPS(15);
				mLivePushConfig.setVideoBitrate(1800);
				break;	
		}		
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

	public static void setWeaknetOptition(boolean state)
	{
		mWeaknetOptition = state;
	}
	
	public static void setVideoOrientation(int orientation)
	{
		mPicPos = orientation;
	}
	
	public static void setAutoRotateState(boolean state)
	{
		mAutoRotate = state;
	}
	
	public static void setTitleText(EditText tv)
	{
		mTitleText = tv;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch(buttonView.getId())
		{
			case R.id.checkBox1:
				 if(isChecked){ 
					 LiveInterface.getInstance().setWaterMarkState(true, 0);
		         }else{ 
		        	 LiveInterface.getInstance().setWaterMarkState(false, 0);
		         } 
				 break;
			case R.id.checkBox2:
				 if(isChecked){ 
					 LiveInterface.getInstance().setWaterMarkState(true, 1);
		         }else{ 
		        	 LiveInterface.getInstance().setWaterMarkState(false, 1);
		         } 
				 break;
			case R.id.checkBox3:
				 if(isChecked){ 
					 LiveInterface.getInstance().setWaterMarkState(true, 2);
		         }else{ 
		        	 LiveInterface.getInstance().setWaterMarkState(false, 2);
		         } 
				 break;
			case R.id.checkBox4:
				 if(isChecked){ 
					 LiveInterface.getInstance().setWaterMarkState(true, 3);
		         }else{ 
		        	 LiveInterface.getInstance().setWaterMarkState(false, 3);
		         } 
				 break;
			case R.id.check_light:
				if(isChecked){ 
					 LiveInterface.getInstance().setFlashLightState(true);
		         }else{ 
		        	 LiveInterface.getInstance().setFlashLightState(false);
		         }
				break;
			case R.id.check_mirror:
				if(isChecked){
					LiveInterface.getInstance().setMirrorState(true);
				}else{
					LiveInterface.getInstance().setMirrorState(false);
				}
				break;
			case R.id.checkMute:
				if(isChecked){
					LiveInterface.getInstance().setMuteModeState(true);
				}else{
					LiveInterface.getInstance().setMuteModeState(false);
				}
				break;
			case R.id.cbTitle:
				if(isChecked){
					LiveInterface.getInstance().setTitleMode(true);
				}else{
					LiveInterface.getInstance().setTitleMode(false);
				}
				break;
			default:
				break;
		}
		
	}	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
        	 Intent intent = new Intent();
     		intent.setClass(liveActivity.this,MainActivity.class);
     		startActivity(intent);
     		finish();
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		LiveInterface.getInstance().setFilterLevel(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean onTouchEvent(MotionEvent e){
		switch (e.getAction()&MotionEvent.ACTION_MASK)
		{  
		   case MotionEvent.ACTION_DOWN://手指下压   
			   Point point = new Point(0,0);			  
			   point.x =  (int) e.getX();
			   point.y = (int) e.getY();
			   LiveInterface.getInstance().setManualFocus(point);
			    break;  
		   case MotionEvent.ACTION_MOVE://手指在屏幕移动，改事件会不断被调用  
			   if (e.getPointerCount()>=2)
               {
                   float offSetX =e.getX(0)-e.getX(1);//获取
                   float offSetY =e.getY(0)-e.getY(1);
                   currentDestance = (float) Math.sqrt(offSetX*offSetX+offSetY*offSetY);//获取两点间距离 然后运用勾股定理 算出两个手指点之间的距离

                   if (lastDestance<0) {
                      lastDestance= (float)currentDestance;//将第一次获取的两点距离  复制给lastDestance
                   }
                   else {
                       if (currentDestance-lastDestance>20)
                       {
                    	   curZoomLevel += 1;
                    	   Log.i(TAG, "onTouchEvent zoom in" + curZoomLevel);
                           LiveInterface.getInstance().setZoomLevel(curZoomLevel);
                           lastDestance =currentDestance;//将这一次的两点间距离赋值给lastDestance  以便每次比较
                       }
                       else if (lastDestance-currentDestance>20){
                    	   Log.i(TAG, "onTouchEvent zoom out"+ curZoomLevel);
                           curZoomLevel -= 1;
                           LiveInterface.getInstance().setZoomLevel(curZoomLevel);
                           lastDestance =currentDestance;
                       }
                   }
               }
			   break;			   
		   default:  
			    break;  
		}  
		return true;		
	}
	public static void setPlayUrl(String playUrl) {
		// TODO Auto-generated method stub
		mPlayUrl = playUrl;
	}  
}
