package com.fish.muturalcoo.API.tts;

import android.content.Context;
import android.support.annotation.NonNull;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by allen on 2018/5/13.
 * 语音合成控制器
 */

public class TtsController implements ITtsController {

    private static TtsController instance;
    private SpeechSynthesizer synthesizer;

    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private TtsController() {

    }

    public static TtsController getInstance() {
        if (instance == null) {
            synchronized (TtsController.class) {
                if (instance == null) {
                    instance = new TtsController();
                }
            }
        }
        return instance;
    }

    @Override
    public void initTtsController(Context context, InitListener initListener) {
        synthesizer = SpeechSynthesizer.createSynthesizer(context, initListener);
    }

    @Override
    public int startTts(String text, @NonNull String languageCode, boolean isRead, String path, SynthesizerListener synthesizerListener) {
        String voicer = "";
        int code = -11;
        if (languageCode.equals("zh_cn")) {
            voicer = "xiaoyan";
        } else {
            voicer = "catherine";
        }

        // 设置参数
        setTtsParam(voicer, path);

        if (isRead) {

            code = synthesizer.startSpeaking(text, synthesizerListener);
        } else {

            /**
             //			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
             //			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
             //			*/
//            String path = Environment.getExternalStorageDirectory() + "/tts.ico";
            code = synthesizer.synthesizeToUri(text, path, synthesizerListener);
        }
        return code;
    }

    @Override
    public void release() {

        if (null != synthesizer) {
            synthesizer.stopSpeaking();
            // 退出时释放连接
            synthesizer.destroy();
        }
    }


    private void setTtsParam(String voicer, String path) {
        // 清空参数
        synthesizer.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            synthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            synthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            synthesizer.setParameter(SpeechConstant.SPEED, "50");
            //设置合成音调
            synthesizer.setParameter(SpeechConstant.PITCH, "50");
            //设置合成音量
            synthesizer.setParameter(SpeechConstant.VOLUME, "50");
        } else {
            synthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            synthesizer.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        synthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        synthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        synthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        synthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
        synthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, path);
    }
}
