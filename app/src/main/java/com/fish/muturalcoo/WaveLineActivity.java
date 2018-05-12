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
import android.widget.EditText;
import android.widget.SeekBar;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.fish.muturalcoo.Constant.IAT_PUNC_PREFERENCE;

public class WaveLineActivity extends AppCompatActivity implements InitListener, RecognizerListener,View.OnClickListener {

    @Bind(R.id.line_wave_view)
    LineWaveVoiceView mLineWaveView;
    @Bind(R.id.seekBar)
    SeekBar mSeekBar;
    @Bind(R.id.edit_result)
    EditText editResult;
    @Bind(R.id.btn_lang_first)
    Button btnLangFirst;
    @Bind(R.id.btn_lang_second)
    Button btnLangSecond;


    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private boolean mTranslateEnable;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_line);
        ButterKnife.bind(this);


        initData();

        initListener();
    }

    private void initData() {

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(this, this);
//        startIat("zh_cn");
    }

    private void initListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("WaveLineActivity", "进度：" + i);
                mLineWaveView.refreshElement(i + 0.5f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
//        showTip("当前正在说话，音量大小：" + volume);
//        Log.d("WaveLineActivity", "返回音频数据：" + data.length);
    }

    @Override
    public void onBeginOfSpeech() {
        showTip("开始说话");
    }

    @Override
    public void onEndOfSpeech() {
// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        showTip("结束说话");
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
        }
    }

    @Override
    public void onError(SpeechError error) {
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


    private void startIat(String iatLanguageCode) {
        // 设置参数this
        setParam(iatLanguageCode);
        //拾取声音，开始识别
        int ret = mIat.startListening(this);
        if (ret != ErrorCode.SUCCESS) {

            showTip("听写失败,错误码：" + ret);
        } else {
            showTip("请说话");
        }
    }


    private void setParam(String iatLanguageCode) {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mTranslateEnable = SPUtils.getInstance(Constant.IAT_SETTING).getBoolean(Constant.IAT_TRANSLATABLE, false);
        if (mTranslateEnable) {
            Log.d("WaveLineActivity", "translate enable");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }

        if (iatLanguageCode.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "en");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "cn");
            }
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, iatLanguageCode);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
            }
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, SPUtils.getInstance(Constant.IAT_SETTING).getString(Constant.IAT_VADBOS_PREFERENCE, "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
        mIat.setParameter(SpeechConstant.VAD_EOS, SPUtils.getInstance(Constant.IAT_SETTING).getString(Constant.IAT_VADEOS_PREFERENCE, "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
//        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        mIat.setParameter(SpeechConstant.ASR_PTT, SPUtils.getInstance(Constant.IAT_SETTING).getString(IAT_PUNC_PREFERENCE, "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
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
        editResult.setText(resultBuffer.toString());
        editResult.setSelection(editResult.length());
    }

    private void printTransResult(RecognizerResult results) {
        String trans = JsonParser.parseTransResult(results.getResultString(), "dst");
        String oris = JsonParser.parseTransResult(results.getResultString(), "src");

        if (TextUtils.isEmpty(trans) || TextUtils.isEmpty(oris)) {
            showTip("解析结果失败，请确认是否已开通翻译功能。");
        } else {
            editResult.setText("原始语言:\n" + oris + "\n目标语言:\n" + trans);
        }

    }

    private void showTip(String s) {
        ToastUtils.showShort(s);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lang_first:
                startIat("zh_cn");
                break;
            case R.id.btn_lang_second:
                startIat("en_us");

                break;
        }
    }
}
