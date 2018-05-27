package com.fish.muturalcoo;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by allen on 2018/5/12.
 */

public class App extends Application {

    public static String appId_baidu_fanyi = "20170825000076984";
    public static String appKey_baidu_fanyi = "O_N9QlHxU4P5R5rnc9fP";
    @Override
    public void onCreate() {
        SpeechUtility.createUtility(App.this, "appid=" + Constant.XF_APPID);
        Utils.init(this);
        super.onCreate();
    }
}
