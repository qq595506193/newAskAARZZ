package com.dmsj.newask.utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;


public class SpeechUtils implements SynthesizerListener {
    private static final String TAG = "SpeechUtils";
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 语记安装助手类
    // 默认发音人
    private String voicer = "xiaoyan";
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private String str = "";

    // 设置合成语速
    int SPEED_LENGTH = 50;
    int PITCH_LENGTH = 50;
    int VOLUME_LENGTH = 50;
    String STREAM_TYPE_LENGTH = "3";
    Activity activity;

    public SpeechUtils(Activity context) {
        // 初始化合成对象
        activity = context;
        mTts = SpeechSynthesizer.createSynthesizer(context, null);
        setParam();
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            //Log.d("", "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                // showTip("初始化失败,错误码："+code);
                MKLog.e(TAG,"初始化失败,错误码："+code);
            } else {
                MKLog.e(TAG,"初始化成功："+code);
                if (!TextUtils.isEmpty(str)) {
                    int codeS = mTts.startSpeaking(str, SpeechUtils.this);
                    if (codeS == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                        //未安装则跳转到提示安装页面
                    }
                }
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * @param red
     * @param img
     */
    public View img;

    public void startPeed(final String red, View img) {
        MKLog.e("a", red);
        String reds = red.replace("</br>", "").replace("<br>", "").replace("br", "").replace("&#62;", ">").replace("&#60;", "<");
        str = "";
        if (this.img != null) {
            this.img.setSelected(false);
        }
        if (img != null)
            this.img = img;
        if (this.img != null) {

            this.img.setSelected(!mTts.isSpeaking());
        }
        if (img != null) {

            if (mTts.isSpeaking()) {
                mTts.stopSpeaking();
                return;
            }
        }
        MKLog.e("a", reds);
        int code = mTts.startSpeaking(reds, this);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //未安装则跳转到提示安装页面
            } else if (code == ErrorCode.ERROR_ENGINE_INIT_FAIL) {
//				showTip("语音合成失败,错误码: " + code);
                str = reds;
                mTts = SpeechSynthesizer.createSynthesizer(activity, mTtsInitListener);
                setParam();

            }
        }
    }


    public void stopPeed() {
        if (this.img != null) {
            this.img.setSelected(false);
        }
        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {


        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }

        mTts.setParameter(SpeechConstant.SPEED, "" + SPEED_LENGTH);
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "" + PITCH_LENGTH);
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "" + VOLUME_LENGTH);
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, STREAM_TYPE_LENGTH);

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }


    public void destory() {
        if (mTts != null) {
            stopPeed();
            mTts.destroy();
            mTts = null;
        }
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {
        if (this.img != null) {
            this.img.setSelected(false);
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }


    public void changeSpeed(boolean isAdd) {
        if (isAdd) {

            SPEED_LENGTH += 20;
        } else {
            SPEED_LENGTH -= 20;

        }
        changeSpeed(SPEED_LENGTH);
    }

    public void changeVolume(boolean isAdd) {
        if (isAdd) {

            VOLUME_LENGTH += 20;
        } else {
            VOLUME_LENGTH -= 20;

        }
        changeVolume(VOLUME_LENGTH);
    }

    public static int MAXSPEED = 100;
    public static int MINSPEED = 0;
    public static int MAXVOLUME = 100;
    public static int MINVOLUME = 0;

    public void changeSpeed(int speed) {

        SPEED_LENGTH = speed;
        if (SPEED_LENGTH > 100) {
            SPEED_LENGTH = 100;
        }
        if (SPEED_LENGTH < 0) {
            SPEED_LENGTH = 0;
        }
        mTts.setParameter(SpeechConstant.SPEED, "" + SPEED_LENGTH);
    }

    public void changeVolume(int volume) {

        VOLUME_LENGTH = volume;
        if (VOLUME_LENGTH > 90) {
            VOLUME_LENGTH = 90;
        }
        if (VOLUME_LENGTH < 10) {
            VOLUME_LENGTH = 10;
        }
        mTts.setParameter(SpeechConstant.VOLUME, "" + VOLUME_LENGTH);
    }
}
