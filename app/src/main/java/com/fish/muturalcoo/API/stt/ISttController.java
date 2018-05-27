package com.fish.muturalcoo.API.stt;

import android.content.Context;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;

/**
 * Created by allen on 2018/5/13.
 * 语音识别借口
 */

public interface ISttController {
    /**
     * 初始化控制器
     */
    void initSttController(Context context, InitListener initListener);

    /**
     * 识别语音内容
     * @param languageCode 语言类型
     * @param recognizerListener 识别回调
     */
    int startStt(String languageCode, RecognizerListener recognizerListener);

    /**
     * 释放控制器
     */
    void release();
}
