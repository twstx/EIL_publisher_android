EIL易居互动直播云平台采集推流Android SDK使用说明

EIL_publisher_android SDK是Android 平台上使用的软件开发工具包(SDK), 负责视频直播的采集、编码和推流。  
一. 功能特点

* [x] 支持软编和硬编
* [x] 弱网处理：可根据实际弱网情况主动丢帧降码率
* [x] 音频编码：AAC
* [x] 视频编码：H.264
* [x] 推流协议：RTMP
* [x] 视频分辨率： 可设 （需要取手机支持的摄像头预览分辨率）
* [x] 视频码率：可设
* [x] 支持固定横竖屏推流,视频方向自适应
* [x] 支持前、后置摄像头动态切换
* [x] 支持本地录像
* [x] 支持多水印（最多4个）
* [x] 支持全局美颜滤镜
* [x] 支持镜头变焦功能:变焦级别可设(需要取手机支持的摄像头变焦范围)
* [x] 支持媒体输入功能:rtmp流输入画中画显示（目前只有横屏推流模式支持），画中画断线后自动重连
* [x] 支持画中画画面切换功能
* [x] 支持闪光灯开关功能
* [x] 支持镜像推流功能
* [x] 支持断线后自动重连功能
* [x] 支持静音功能
* [x] 支持预览画面横竖屏切换
* [x] 支持图片导播

二. 运行环境

* 最低支持版本为Android 4.4 (API level 19)
* 支持的cpu架构：armv5、armv7a、arm64、x86
  
三. 快速集成

本章节提供一个快速集成推流SDK基础功能的示例。  
具体可以参考publisherTest工程中的相应文件。

 3.1 下载工程

 3.1.1 github下载
从github下载SDK及demo工程


 3.2 工程目录结构

- publisherTest: 示例工程，演示本SDK主要接口功能的使用
- doc: SDK说明文档
- libs: 集成SDK需要的所有库文件
    - libs: so库
    - libs/EILPublisher.jar: 推流SDK jar包

 3.3 配置项目

引入目标库, 将libs目录下的库文件引入到目标工程中并添加依赖。

可参考下述配置方式（以eclipse为例）：
- 将libs目录copy到目标工程的根目录下；
````
- 在AndroidManifest.xml文件中申请相应权限
````xml
<!-- 使用权限 -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_SINTERNETWIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.FLASHLIGHT" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.READ_LOGS" />
<uses-permission android:name="android.permission.FLASHLIGHT"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>
<!-- 硬件特性 -->
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />

````

 3.4 简单推流示例

具体可参考demo工程中的`com.example.publishertest.liveActivity`类

- 在布局文件中加入预览View
````xml
 <android.opengl.GLSurfaceView
        android:id="@+id/surfaceView"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:layout_alignParentBottom="true"
        android:layout_gravity="top|left|right|bottom" />
````
- 初始化GLSurfaceView
````java
GLSurfaceView mSurfaceView=(GLSurfaceView)findViewById(R.id.surfaceView);
````
- LivePushConfig
推流过程中不可动态改变的参数需要在创建该类的对象时指定。
````java
LivePushConfig mLivePushConfig = new LivePushConfig();
//设置推流APP context
mLivePushConfig.setAppContext(this);
// 设置推流url
mLivePushConfig.setRtmpUrl("rtmp://test.uplive.xxx.com/live/{streamName}");
//设置清晰度（支持3档清晰度：DEFINITION_STANDARD标清  DEFINITION_HIGH高清  DEFINITION_SUPER超清）
//用户也可自定义分辨率、码率及帧率，同时调用优先支持清晰度设置。
mLivePushConfig.setDefinition(LivePushConfig.DEFINITION_STANDARD);
// 设置推流分辨率,需要手机支持的分辨率，sdk内部会进行判断，如果设置的分辨率手机不支持，会无法推流
mLivePushConfig.setVideoSize(640,480);
// 设置视频帧率
mLivePushConfig.setVideoFPS(15);
// 设置视频码率(单位为kbps)
mLivePushConfig.setVideoBitrate(800);
// 设置音频采样率
mLivePushConfig.setAudioSampleRate(44100);
//设置音频采样声道（部分手机无法支持双声道，使用双声道时请先判断手机是否支持，否则会出错）
mLivePushConfig.setAudioChannels(AudioFormat.CHANNEL_IN_MONO);
/**
 * 设置编码模式(软编、硬编), 支持的类型：
 */
mLivePushConfig.setHWVideoEncode(true);//开启硬编码
mLivePushConfig.setHWVideoEncode(false);//开启软编码
//设置消息回调接口
mLivePushConfig.setEventInterface(mCaptureStateListener);
//设置录像文件存储路径
mLivePushConfig.setRecordPath("/sdcard/");
//设置水印以及显示位置
Bitmap watermarkImage = BitmapFactory.decodeFile("/sdcard/mark.png");
mLivePushConfig.setWatermark(watermarkImage,100,100,200,200);
mLivePushConfig.setWatermark(watermarkImage,800,100,200,100);
//设置弱网优化开关
mLivePushConfig.setWeaknetOptition(true);(true：打开；false：关闭）默认为打开状态
//设置推流视频方向
mLivePushConfig.setVideoResolution(mPublishOrientation);（0：横屏；1：竖屏）默认为横屏
//设置推流视频方向自适应开关
mLivePushConfig.setAutoRotation(true);(true：打开；false：关闭 )默认为关闭状态
//设置画中画显示位置
mLivePushConfig.setPlayerPosition(960, 180, 320, 180);
// 创建LivePushConfig对象
LivePushConfig mLivePushConfig = new LivePushConfig();
````
- 创建推流事件监听，可以收到推流过程中的异步事件。
LiveEventInterface mCaptureStateListener = new LiveEventInterface() 可以接收到错误通知，一般是发生了严重错误，如发生断网,SDK内部会自动重连。

**注意：所有回调直接运行在产生事件的各工作线程中，不要在该回调中做任何耗时的操作，或者直接调用推流API。**
````java
private LiveEventInterface mCaptureStateListener = new LiveEventInterface() {
        @Override
        public void onStateChanged(int eventId) {
 
            switch (eventId) {
                case LiveConstants.PUSH_ERR_NET_DISCONNECT://断线
                case LiveConstants.PUSH_ERR_NET_CONNECT_FAIL://连接失败
                .....
            }
        }
    };

- 创建推流码率丢包率监听，可以收到推流过程中实时码率和丢包率。
LiveNetStateInterface mPublishNetstateListener = new LiveNetStateInterface() 可以接收到实时的码率和丢包率信息。

**注意：所有回调直接运行在产生事件的各工作线程中，不要在该回调中做任何耗时的操作，或者直接调用推流API。**
````java
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
	
````
-------- 初始化推流，开启摄像头
````java
 LiveInterface.getInstance().init(mSurfaceView , mLivePushConfig);
````
-------- 开始推流  
**注意：请在初始化推流成功后调用**
````java
LiveInterface.getInstance().start();   
````
//动态修改推流地址
````java
LiveInterface.getInstance().start(mRtmpUrl);   
````
-------- 推流过程中可动态设置的常用方法
````java
// 切换前后摄像头
LiveInterface.getInstance().switchCamera();
````
-------- 开始录像  
````java
LiveInterface.getInstance().startRecord();  
````
-------- 停止录像 
````java
LiveInterface.getInstance().stopRecord(); 
````
-------- 设置水印状态（true：显示；false：隐藏） 
````java
LiveInterface.getInstance().setWaterMarkState(true); 
````
-------- 设置滤镜级别 
````java
LiveInterface.getInstance().setFilterLevel(level); 
````
-------- 设置镜头变焦级别
````java
LiveInterface.getInstance().setZoomLevel(ZoomLevel);
````
-------- 闪光灯开关
````java
LiveInterface.getInstance().setFlashLightState(true/false);
````
-------- 镜像推流开关
````java
LiveInterface.getInstance().setMirrorState(true/false);
````
-------- 静音开关
````java
LiveInterface.getInstance().setMuteModeState(true/false);
````
-------- 手动对焦
````java
LiveInterface.getInstance().setManualFocus(point);
````
-------- 开启图片导播
````java
LiveInterface.getInstance().startPic(Bitmap);
````
-------- 关闭图片导播
````java
LiveInterface.getInstance().stopPic();
````
-------- 设置横屏预览
````java
LiveInterface.getInstance().setLandscape();
````
-------- 设置竖屏预览
````java
LiveInterface.getInstance().setPortrait();
````
-------- 设置参数更新
````java
LiveInterface.getInstance().updateConfig(mLivePushConfig);
````
-------- 获取上传速度
````java
LiveInterface.getInstance().getUploadRate();
````
-------- 打开媒体输入
````java
 LiveInterface.getInstance().openPlay(mPlayUrl);
````
-------- 关闭媒体输入
````java
LiveInterface.getInstance().closePlay();
````
-------- 开始播放媒体输入
````java
 LiveInterface.getInstance().startPlay();
````
-------- 结束播放媒体输入
````java
LiveInterface.getInstance().stopPlay();
````
-------- 画中画窗口切换
````java
LiveInterface.getInstance().resize();
````
-------- 停止推流
````java
LiveInterface.getInstance().stop();
````
-------- 关闭摄像头，释放资源
````java
LiveInterface.getInstance().uninit();
-------- 获取版本号
````java
LiveInterface.getInstance().getVersion();
