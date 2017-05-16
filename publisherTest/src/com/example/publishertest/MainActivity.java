package com.example.publishertest;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, android.widget.CompoundButton.OnCheckedChangeListener {

	public final static String TAG = "MainActivity";
	private Button mBtnSetOK;
	EditText mUrlText,mPlayUrlText;
	public RadioButton mRadio480, mRadio360,mRadio720;
	public RadioButton mRadioEncHW, mRadioEncSW;
	public RadioButton mPublishH, mPublishV;
	private String mRtmpUrl ="rtmp://rtmppush.ejucloud.com/ehoush/liuy1";
	private static String mPlayUrl = "http://10.0.60.99:8099/testroom13/test3/ss.mp4";
//	private String mPlayUrl ="rtmp://rtmppush.ejucloud.com/ehoush/liuy2";
	CheckBox mWeeknet,mAutoRotate;
	private List<CheckBox> checkBoxs=new ArrayList<CheckBox>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mBtnSetOK = (Button) findViewById(R.id.btn_ok);
		mBtnSetOK.setOnClickListener(this);
		
		mUrlText =(EditText)findViewById(R.id.editText1);
		mUrlText.setText(mRtmpUrl.toCharArray(), 0, mRtmpUrl.length());  
		
		mPlayUrlText =(EditText)findViewById(R.id.editText2);
		mPlayUrlText.setText(mPlayUrl.toCharArray(), 0, mPlayUrl.length());  
		
		RadioGroup groupencoder = (RadioGroup)this.findViewById(R.id.radioGroup);
		mRadioEncHW =  (RadioButton) findViewById(R.id.radioHW);
	    mRadioEncSW =  (RadioButton) findViewById(R.id.radioSW);
		
        //绑定一个匿名监听器
		groupencoder.setOnCheckedChangeListener(new OnCheckedChangeListener() {
             
             @Override
             public void onCheckedChanged(RadioGroup arg0, int arg1) {
                 // TODO Auto-generated method stub
            	 if(arg1 == mRadioEncSW.getId())
            	 {
            		 liveActivity.setEncodeMode(0);
            	 }            		 
            	 if(arg1 == mRadioEncHW.getId())
            	 {
            		 liveActivity.setEncodeMode(1);
            	 }           		              
             }
         });
	    mWeeknet = (CheckBox) findViewById(R.id.checkNet);
	    mWeeknet.setOnCheckedChangeListener(this);
	    
	    mAutoRotate =  (CheckBox) findViewById(R.id.autoRotate);
	    mAutoRotate.setOnCheckedChangeListener(this);
	    
	    checkBoxs.add(mWeeknet);
	    checkBoxs.add(mAutoRotate);
		
	    RadioGroup groupdefinition = (RadioGroup)this.findViewById(R.id.radioGroup2);
	    mRadio360 = (RadioButton) findViewById(R.id.radio360);
		mRadio480 = (RadioButton) findViewById(R.id.radio480);
		mRadio720 = (RadioButton) findViewById(R.id.radio720);
		groupdefinition.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
           	 if(arg1 == mRadio360.getId())
           		 liveActivity.setDefinitionMode(0);
           	 if(arg1 == mRadio480.getId())
           		 liveActivity.setDefinitionMode(1);
           	 if(arg1 == mRadio720.getId())
           		liveActivity.setDefinitionMode(2);
            }
        });
		
		RadioGroup groupOrientation = (RadioGroup)this.findViewById(R.id.radioGroup3);
		mPublishH = (RadioButton) findViewById(R.id.radioH);
		mPublishV = (RadioButton) findViewById(R.id.radioV);
		groupOrientation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
           	 if(arg1 == mPublishH.getId())
           		liveActivity.setVideoOrientation(0);
           	 if(arg1 == mPublishV.getId())
           		liveActivity.setVideoOrientation(1); 
            }
        });
		mRadioEncHW.setChecked(true);
		mRadio480.setChecked(true);
		mPublishH.setChecked(true);
		mWeeknet.setChecked(true);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.i(TAG, "start live code");
		String strUrl="";  
		strUrl=mUrlText.getText().toString();
		if(strUrl.equals(""))
		{
			  Toast toast = Toast.makeText(MainActivity.this, 
                      "推流地址不能为空",                     
                      Toast.LENGTH_SHORT);                  
			  toast.show();
			  return;
		}
		mRtmpUrl = strUrl;
	    liveActivity.setRtmpUrl(strUrl);
	        
		String playUrl="";  
		playUrl=mPlayUrlText.getText().toString();
		liveActivity.setPlayUrl(playUrl);
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,liveActivity.class);
		startActivity(intent);
		finish();
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if(R.id.checkNet == buttonView.getId())
		{
			if(isChecked){ 
				liveActivity.setWeaknetOptition(true);
	        }else{ 
	        	liveActivity.setWeaknetOptition(false);
	        } 
		}
		if(R.id.autoRotate == buttonView.getId())
		{
			if(isChecked){ 
				 liveActivity.setAutoRotateState(true);
	        }else{ 
	        	 liveActivity.setAutoRotateState(false);
	        } 
		}
	}
}
