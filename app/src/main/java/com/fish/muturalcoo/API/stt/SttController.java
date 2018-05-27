package com.fish.muturalcoo.API.stt;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.fish.muturalcoo.Constant;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;

import static com.fish.muturalcoo.Constant.IAT_PUNC_PREFERENCE;

/**
 * Created by allen on 2018/5/13.
 * 语音识别控制器
 */

public class SttController implements ISttController {
    private static SttController instance;
    private SpeechRecognizer recognizer;

    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private boolean mTranslateEnable;

    private SttController() {

    }

    public static SttController getInstance() {
        if (instance == null) {
            synchronized (SttController.class) {
                if (instance == null) {
                    instance = new SttController();
                }
            }
        }
        return instance;
    }

    @Override
    public void initSttController(Context context, InitListener initListener) {
        recognizer = SpeechRecognizer.createRecognizer(context, initListener);

    }

    @Override
    public int startStt(String languageCode, RecognizerListener recognizerListener) {
        // 设置参数this
        setParam(languageCode);
        //拾取声音，开始识别
        int ret = recognizer.startListening(recognizerListener);
        return ret;
    }


    @Override
    public void release() {
        if (null != recognizer) {
            // 退出时释放连接
            recognizer.cancel();
            recognizer.destroy();
        }
    }


    private void setParam(String languageCode) {

        // 清空参数
        recognizer.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mTranslateEnable = SPUtils.getInstance(Constant.IAT_SETTING).getBoolean(Constant.IAT_TRANSLATABLE, false);
        if (mTranslateEnable) {
            Log.d("WaveLineActivity", "translate enable");
            recognizer.setParameter(SpeechConstant.ASR_SCH, "1");
            recognizer.setParameter(SpeechConstant.ADD_CAP, "translate");
            recognizer.setParameter(SpeechConstant.TRS_SRC, "its");
        }

        if (languageCode.equals("en_us")) {
            // 设置语言
            recognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
            recognizer.setParameter(SpeechConstant.ACCENT, null);

            if (mTranslateEnable) {
                recognizer.setParameter(SpeechConstant.ORI_LANG, "en");
                recognizer.setParameter(SpeechConstant.TRANS_LANG, "cn");
            }
        } else {
            // 设置语言
            recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            recognizer.setParameter(SpeechConstant.ACCENT, languageCode);

            if (mTranslateEnable) {
                recognizer.setParameter(SpeechConstant.ORI_LANG, "cn");
                recognizer.setParameter(SpeechConstant.TRANS_LANG, "en");
            }
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        recognizer.setParameter(SpeechConstant.VAD_BOS, SPUtils.getInstance(Constant.IAT_SETTING).getString(Constant.IAT_VADBOS_PREFERENCE, "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
        recognizer.setParameter(SpeechConstant.VAD_EOS, SPUtils.getInstance(Constant.IAT_SETTING).getString(Constant.IAT_VADEOS_PREFERENCE, "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
//        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        recognizer.setParameter(SpeechConstant.ASR_PTT, SPUtils.getInstance(Constant.IAT_SETTING).getString(IAT_PUNC_PREFERENCE, "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        recognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

}
