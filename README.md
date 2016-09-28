EIL易居互动直播云平台采集推流Android SDK使用说明

EIL_publisher_android SDK是Android 平台上使用的软件开发工具包(SDK), 负责视频直播的采集、编码和推流。  
一. 功能特点

* [x] 支持软编和硬编
* [x] 弱网处理：可根据实际弱网情况主动丢帧
* [x] 音频编码：AAC
* [x] 视频编码：H.264
* [x] 推流协议：RTMP
* [x] 视频分辨率： 可设 （需要取手机支持的摄像头预览分辨率）
* [x] 视频码率：可设
* [x] 支持固定横屏推流
* [x] 支持前、后置摄像头动态切换

二. 运行环境

* 最低支持版本为Android 4.4 (API level 19)
* 支持的cpu架构：armv7
  
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
    - libs/[armeabi-v7a]: so库
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
<!-- 硬件特性 -->
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />
````

 3.4 简单推流示例

具体可参考demo工程中的`com.example.publishertest.liveActivity`类

- 在布局文件中加入预览View
````xml
 <SurfaceView
        android:id="@+id/surfaceView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_alignParentLeft="true"
        android:layout_alignParentRight="true"
        android:layout_alignParentTop="true" />
````
- 初始化SurfaceView
````java
SurfaceView mSurfaceView=(SurfaceView)findViewById(R.id.surfaceView);
````
- LivePushConfig
推流过程中不可动态改变的参数需要在创建该类的对象时指定。
````java
LivePushConfig mLivePushConfig = new LivePushConfig();
// 设置推流url
mLivePushConfig.setRtmpUrl("rtmp://test.uplive.xxx.com/live/{streamName}");
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
// 创建LivePushConfig对象
LivePushConfig mLivePushConfig = new LivePushConfig();
````
- 创建推流事件监听，可以收到推流过程中的异步事件。
LiveEventInterface mCaptureStateListener = new LiveEventInterface() 可以接收到错误通知，一般是发生了严重错误，比如断网等,SDK内部会停止推流。
重连时只需要重新开始推流即可，重连的策略可以自定义。

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

````
-------- 初始化推流
````java
 LiveInterface.getInstance().init(mSurfaceView , mLivePushConfig);
````
-------- 开始推流  
**注意：请在初始化推流成功后调用**
````java
LiveInterface.getInstance().start();   
````
-------- 推流过程中可动态设置的常用方法
````java
// 切换前后摄像头
LiveInterface.getInstance().switchCamera();
````
-------- 获取上传速度
````java
LiveInterface.getInstance().getUploadRate();
````
-------- 停止推流
````java
LiveInterface.getInstance().stop();
