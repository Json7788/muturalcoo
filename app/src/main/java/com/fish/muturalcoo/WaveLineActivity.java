package com.fish.muturalcoo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.fish.muturalcoo.API.stt.JsonParser;
import com.fish.muturalcoo.API.stt.SttController;
import com.fish.muturalcoo.API.tts.TtsController;
import com.fish.muturalcoo.translate.ResultBean;
import com.fish.muturalcoo.translate.TransApi;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WaveLineActivity extends AppCompatActivity implements InitListener, RecognizerListener, View.OnClickListener {

    private static final String TAG = "WaveLineActivity";
    @Bind(R.id.line_wave_view)
    LineWaveVoiceView mLineWaveView;
    //    @Bind(R.id.seekBar)
//    SeekBar mSeekBar;
//    @Bind(R.id.edit_result)
//    EditText editResult;
    @Bind(R.id.btn_lang_first)
    Button btnLangFirst;
    @Bind(R.id.btn_lang_second)
    Button btnLangSecond;
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.ll_talk)
    LinearLayout llTalk;


    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private boolean mTranslateEnable;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private SpeechSynthesizer mTts;
    private String result;
    private String sourceLanguage;
    private int voiceTranslateStatus;
    private ExecutorService executorService;
    private TransApi mBaiduApi;
    private String targetLanguage;

    String path = Environment.getExternalStorageDirectory() + "/msc/tts.wav";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_line);
        ButterKnife.bind(this);


        initData();

        initListener();
    }

    private void initData() {
        if (executorService == null) {

            executorService = Executors.newCachedThreadPool();
        }
        if (mBaiduApi == null) {
            mBaiduApi = new TransApi(App.appId_baidu_fanyi, App.appKey_baidu_fanyi);
        }
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
//        mIat = SpeechRecognizer.createRecognizer(this, this);
        SttController.getInstance().initSttController(this, this);
//        startIat("zh_cn");
        //初始化合成引擎
        TtsController.getInstance().initTtsController(this,this);
//        mTts = SpeechSynthesizer.createSynthesizer(this, this);
    }

    private void initListener() {
        btnLangFirst.setOnClickListener(this);
        btnLangSecond.setOnClickListener(this);
    }

    public static void lunch(Context context) {
        Intent intent = new Intent(context, WaveLineActivity.class);
        context.startActivity(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 识别引擎初始化回调
    ///////////////////////////////////////////////////////////////////////////`
    @Override
    public void onInit(int code) {
        Log.d("WaveLineActivity", "SpeechRecognizer init() code = " + code);
        if (code != ErrorCode.SUCCESS) {
            showTip("初始化失败，错误码：" + code);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 语音识别回调
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onVolumeChanged(int volume, byte[] data) {
//        Log.d("WaveLineActivity", "volume:" + volume);
        mLineWaveView.refreshElement((volume + 0.5f) / 10.f);
        showTip("当前正在说话，音量大小：" + volume);
//        Log.d("WaveLineActivity", "返回音频数据：" + data.length);
    }

    @Override
    public void onBeginOfSpeech() {
        showTip("开始说话");
        refreshVoiceStatus(VoiceTranslateStatus.STATUS_SPEAKING);
    }

    @Override
    public void onEndOfSpeech() {
// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        showTip("结束说话");
        refreshVoiceStatus(VoiceTranslateStatus.STATUS_RECOGNITION);
    }

    @Override
    public void onResult(RecognizerResult results, boolean isLast) {
        Log.d("WaveLineActivity", results.getResultString());
        if (mTranslateEnable) {
            printTransResult(results);
        } else {
            printResult(results);
        }

        if (isLast) {
            // TODO 最后的结果
//            startStt(result, sourceLanguage, true);
            baiduTranslate(result, sourceLanguage, targetLanguage);
            refreshVoiceStatus(VoiceTranslateStatus.STATUS_RECOGNITION_COMPLETE);

        }
    }

    @Override
    public void onError(SpeechError error) {
        refreshVoiceStatus(VoiceTranslateStatus.STATUS_NONE);
// Tips：
        // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
        if (mTranslateEnable && error.getErrorCode() == 14002) {
            showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
        } else {
            showTip(error.getPlainDescription(true));
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {
// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        // 若使用本地能力，会话id为null
        //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
        //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
        //		Log.d(TAG, "session id =" + sid);
        //	}
    }

    ///////////////////////////////////////////////////////////////////////////
    // 合成回调
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
//            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//            Log.d(TAG, "onSpeakProgress() called with: percent = [" + percent + "], beginPos = [" + beginPos + "], endPos = [" + endPos + "]");
            if (percent == 0) {
                refreshVoiceStatus(VoiceTranslateStatus.STATUS_TTS_WORKING);
            } else if (percent >= 90) {
                refreshVoiceStatus(VoiceTranslateStatus.STATUS_TTS_COMLETE);
            }
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 语音识别
     *
     * @param iatLanguageCode
     */
    private void startIat(String iatLanguageCode) {
        voiceTranslateStatus = VoiceTranslateStatus.STATUS_NONE;
        // 设置参数this
//        setParam(iatLanguageCode);
        //拾取声音，开始识别
        int ret = SttController.getInstance().startStt(iatLanguageCode, this);
        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败,错误码：" + ret);
        } else {
            refreshVoiceStatus(VoiceTranslateStatus.STATUS_READY);
            showTip("请说话");
        }
    }

    private void refreshVoiceStatus(int status) {
        voiceTranslateStatus = status;
        refreshTalkView(voiceTranslateStatus);
    }


    private void startTts(String text, String language, boolean isSpeake) {

        int code = TtsController.getInstance().startTts(text, language, isSpeake, path, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败,错误码: " + code);
        }
    }


    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        Log.d("WaveLineActivity", resultBuffer.toString());
        result = resultBuffer.toString();
        tvStatus.setText(resultBuffer.toString());
    }

    private void printTransResult(RecognizerResult results) {
        String trans = JsonParser.parseTransResult(results.getResultString(), "dst");
        String oris = JsonParser.parseTransResult(results.getResultString(), "src");

        if (TextUtils.isEmpty(trans) || TextUtils.isEmpty(oris)) {
            showTip("解析结果失败，请确认是否已开通翻译功能。");
        } else {
            tvStatus.setText("原始语言:\n" + oris + "\n目标语言:\n" + trans);
        }

    }

    private void showTip(String s) {
        ToastUtils.showShort(s);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        SttController.getInstance().release();

        TtsController.getInstance().release();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lang_first:
//                refreshTalkView();
                sourceLanguage = "zh_cn";
                targetLanguage = "en_us";
                startIat("zh_cn");
                result = "";
                break;
            case R.id.btn_lang_second:
                sourceLanguage = "en_us";
                targetLanguage = "zh_cn";
                result = "";
                startIat("en_us");

                break;
        }
    }

    private void refreshTalkView(int status) {
        switch (status) {
            case VoiceTranslateStatus.STATUS_NONE:
            case VoiceTranslateStatus.STATUS_READY:
                llTalk.setVisibility(View.INVISIBLE);
                tvStatus.setText("");
                mLineWaveView.setText("就绪");
                break;

            case VoiceTranslateStatus.STATUS_SPEAKING:
                mLineWaveView.setText("倾听中");
                llTalk.setVisibility(View.VISIBLE);
                break;
            case VoiceTranslateStatus.STATUS_RECOGNITION:
                mLineWaveView.setText("识别中");
                llTalk.setVisibility(View.VISIBLE);
                break;

            case VoiceTranslateStatus.STATUS_TRANSLATE_WORKING:
                mLineWaveView.setText("翻译中");
                llTalk.setVisibility(View.VISIBLE);
                break;
            case VoiceTranslateStatus.STATUS_TTS_WORKING:
                mLineWaveView.setText("合成中");
                llTalk.setVisibility(View.VISIBLE);
                break;
            case VoiceTranslateStatus.STATUS_TTS_COMLETE:
                mLineWaveView.setText("完成");
                llTalk.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 百度翻译
     *
     * @param content        翻译的内容
     * @param sourceLanguage 原始语言
     * @param targetLanguage 目标语言
     */
    private void baiduTranslate(final String content, final String sourceLanguage, final String targetLanguage) {
        refreshVoiceStatus(VoiceTranslateStatus.STATUS_TRANSLATE_WORKING);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //调用百度翻译已经通过语言识别出来的文字信息

                String transResult = mBaiduApi.getTransResult(content, sourceLanguage.split("_")[0], targetLanguage.split("_")[0]);
                Gson gson = new Gson();
                ResultBean resultBean = gson.fromJson(transResult, ResultBean.class);
                if (resultBean == null || resultBean.getTrans_result() == null) {
                    return;
                }
                ResultBean.TransResultBean transResultBean = resultBean.getTrans_result()
                        .get(0);
                final String desText = transResultBean.getDst();
                //
                String targetContent = desText;
                Log.d(TAG, "翻译原文：" + content);
                Log.d(TAG, "翻译结果: " + desText);
//                SaveFileTts.getInstance()
//                        .speak(desText);
                startTts(targetContent, targetLanguage, true);

            }
        });
    }
}
