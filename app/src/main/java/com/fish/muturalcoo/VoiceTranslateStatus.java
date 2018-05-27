package com.fish.muturalcoo;

/**
 * Created by allen on 2018/5/13.
 * 语音翻译工具类
 */

public class VoiceTranslateStatus {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_WAITING = 1;
    public static final int STATUS_READY = 2;
    public static final int STATUS_SPEAKING = 3;
    public static final int STATUS_RECOGNITION = 4;
    public static final int STATUS_RECOGNITION_COMPLETE = 5;
    public static final int STATUS_TRANSLATE_WORKING= 6;
    public static final int STATUS_TRANSLATE_COMPLETE= 7;
    public static final int STATUS_TTS_WORKING= 8;
    public static final int STATUS_TTS_COMLETE= 10;
}
