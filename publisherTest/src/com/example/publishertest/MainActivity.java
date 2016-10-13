package com.example.publishertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener  {
	public final static String TAG = "MainActivity";
	private Button mBtnSetOK;
	EditText mUrlText;
	public RadioButton mRadio480, mRadio540,mRadio720;
	public RadioButton mRadioEncHW, mRadioEncSW;
	private String mRtmpUrl ="rtmp://rtmppush.ejucloud.com/ehoush/liuy";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mBtnSetOK = (Button) findViewById(R.id.btn_ok);
		mBtnSetOK.setOnClickListener(this);
		
		mUrlText =(EditText)findViewById(R.id.editText1);
		mUrlText.setText(mRtmpUrl.toCharArray(), 0, mRtmpUrl.length());  
		 
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
		
		mRadio480 = (RadioButton) findViewById(R.id.radio480);
		mRadio540 = (RadioButton) findViewById(R.id.radio540);
		mRadio720 = (RadioButton) findViewById(R.id.radio720);
		
		RadioGroup groupdefinition = (RadioGroup)this.findViewById(R.id.radioGroup2);
        //绑定一个匿名监听器
		groupdefinition.setOnCheckedChangeListener(new OnCheckedChangeListener() {
             
             @Override
             public void onCheckedChanged(RadioGroup arg0, int arg1) {
                 // TODO Auto-generated method stub
            	 if(arg1 == mRadio480.getId())
            	 {
            		  liveActivity.setDefinitionMode(0);
            	 }            		
            	 if(arg1 == mRadio540.getId())
            	 {
            		  liveActivity.setDefinitionMode(1);
            	 }           		
            	 if(arg1 == mRadio720.getId())
            	 {
            		 liveActivity.setDefinitionMode(2);
            	 }           		 
             }
         });
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
	        
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,liveActivity.class);
		startActivity(intent);
		finish();
	}
}
