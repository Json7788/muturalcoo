package com.fish.muturalcoo.API.tts;

import android.content.Context;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by allen on 2018/5/13.
 * 语音合成接口
 */

public interface ITtsController {
    /**
     * 初始化控制器
     */
    void initTtsController(Context context, InitListener initListener);

    /**
     * 合成语音
     * @param text 合成内容
     * @param languageCode 合成内容的语言
     * @param isRead 是否需要朗读
     * @param path 合成语音保存路径
     * @param synthesizerListener 合成回调
     * @return
     */
    int startTts(String text, String languageCode, boolean isRead, String path, SynthesizerListener synthesizerListener);


    /**
     * 释放控制器
     */
    void release();
}
