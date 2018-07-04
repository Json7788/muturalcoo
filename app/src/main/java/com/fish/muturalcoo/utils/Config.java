package com.fish.muturalcoo.utils;

import android.os.Environment;

import java.io.File;

/**
 * 系统级别的配置常量
 */
public class Config {


    public static final String PATH_LOCAL_TEMP = "muturalcooDemo";
    public static final int USER_DEFAULT_AGE = 18;
    public static final double USER_DEFAULT_WEIGHT = 50;
    public static final double USER_DEFAULT_HEIGHT = 175;
    public static final int DOWNLOAD_FORMAT_MUSIC = 1;
    public static final int DOWNLOAD_FORMAT_PICTURE = 2;
    public static final int DOWNLOAD_FORMAT_CLUB_COVER = 3;
    public static final int DOWNLOAD_FORMAT_ALBUM_COVER = 4;
    public static final int DOWNLOAD_FORMAT_AVATAR = 5;
    public static final int DOWNLOAD_LOCAL_COVER = 6;
    public static final int DOWNLOAD_FORMAT_PICTURE2 = 7;
    public static final int DOWNLOAD_FORMAT_TRAIL = 8;
    public static final int DOWNLOAD_FORMAT_STEP = 9;
    public static final String PATH_WATCH = "geekeryWatch";
    /**
     * 根文件夹名称
     */
    public static  String PRODUCT_ROOT_DIR = "fitmixsdk";

    public static String getProductRootDir() {
        return PRODUCT_ROOT_DIR;
    }

    public static void setProductRootDir(String productRootDir) {
        PRODUCT_ROOT_DIR = productRootDir;
    }

    /**
     * 乐享动根文件夹路径
     */
    public static  String PATH_APP_STORAGE = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + PRODUCT_ROOT_DIR + File.separator;

    /**
     * 乐享动运动轨迹下载文件夹路径
     */
    public static  String PATH_DOWN_TRAIL = PATH_APP_STORAGE + "Trail" + File.separator;
    /**
     * 乐享动运动步数下载文件夹路径
     */
    public static  String PATH_DOWN_STEP = PATH_APP_STORAGE + "Step" + File.separator;




    /**
     * 用户信息模块SharedPreferences文件名
     */
    public static final String PREFS_USER = "prefs_user";

    /**
     * 服务器返回的API token SP键名,String型
     */
    public static final String SP_KEY_API_TOKEN = "api_token";

    /**
     * IRON CLOUD手表设置信息(WatchSetting)json字符串,String型
     */
    public static final String SP_KEY_WATCH_SETTING = "watch_setting";

    /**
     * 手表是否开启来电提醒,boolean型
     */
    public static final String SP_KEY_WATCH_CALL_NOTIFY = "watch_call_notify";

    /**
     * 手表是否开启短信提醒,boolean型
     */
    public static final String SP_KEY_WATCH_SMS_NOTIFY = "watch_sms_notify";

    /**
     * 手表APP消息通知设置(WatchAppNotify)json字符串,String型
     */
    public static final String SP_KEY_WATCH_APP_NOTIFY = "watch_app_notify";

    /**
     * 手表天气城市设置(WatchWeatherCity)json字符串,String型
     */
    public static final String SP_KEY_WATCH_WEATHER_CITY = "watch_weather_city";

    /**
     * 手表大文件发送信息(WatchBigFile)json字符串,String型
     */
    public static final String SP_KEY_WATCH_BIG_FILE = "watch_big_file";

    /**
     * IRON CLOUD手表日常数据信息(WatchDailyData)json字符串,String型
     */
    public static final String SP_KEY_WATCH_DAILY = "watch_daily";

    /**
     * 性别,1:男
     */
    public static final int GENDER_MALE = 1;
    /**
     * 性别,2:女
     */
    public static final int GENDER_FEMALE = 2;




    //region =================================== 其它参数 ===================================


    /**
     * 网络状态,没有网络连接
     */
    public static final int NETWORK_TYPE_NONE = 5;
    /**
     * 网络状态,手机网络类型
     */
    public static final int NETWORK_TYPE_MOBILE = 6;
    /**
     * 网络状态,wifi网络类型
     */
    public static final int NETWORK_TYPE_WIFI = 7;


    /**
     * BLE功能请求打开GPS
     */
    public static final int BLE_REQUEST_ENABLE_GPS = 19;

    /**
     * 请求打开GPS
     */
    public static final int REQUEST_ENABLE_GPS = 20;

    /**
     * 请求蓝牙BLE权限
     */
    public final static int REQUEST_ENABLE_BLUETOOTH = 21;


    //region ======================== 手表相关 ========================

//    /**
//     * 手表文件夹路径
//     */
//    public static final String PATH_WATCH = PATH_APP_STORAGE + "Watch" + File.separator;
    /**
     * 手表传过来的运动记录或sensor文件转换后的文件夹路径
     */
    public static  String PATH_WATCH_LOG_DATA = PATH_APP_STORAGE + "Log" + File.separator;
    /**
     * 手表传过来的大文件原始数据文件夹路径
     */
    public static  String PATH_WATCH_RAW_DATA = PATH_APP_STORAGE + "Raw" + File.separator;
    /**
     * 手表GPS记录文件路径
     */
    public static  String PATH_WATCH_SENSOR_GPS = PATH_APP_STORAGE + "WatchSensorGps" + File.separator;
    /**
     * 手表GSensor记录文件路径
     */
    public static  String PATH_WATCH_SENSOR_GSENSOR = PATH_APP_STORAGE + "WatchSensorGSensor" + File.separator;
    /**
     * 手表心率记录文件路径
     */
    public static  String PATH_WATCH_SENSOR_HR = PATH_APP_STORAGE + "WatchSensorHR" + File.separator;
    /**
     * 手表温度文件路径
     */
    public static  String PATH_WATCH_SENSOR_TEMPERATURE = PATH_APP_STORAGE + "WatchSensorTemperature" + File.separator;
    /**
     * 手表气压文件路径
     */
    public static  String PATH_WATCH_SENSOR_PRESSURE = PATH_APP_STORAGE + "WatchSensorPressure" + File.separator;
    /**
     * 手表湿度文件路径
     */
    public static  String PATH_WATCH_SENSOR_HUMIDITY = PATH_APP_STORAGE + "WatchSensorHumidity" + File.separator;
    /**
     * 手表指南针文件路径
     */
    public static  String PATH_WATCH_SENSOR_COMPASS = PATH_APP_STORAGE + "WatchSensorCompass" + File.separator;
    /**
     * 手表陀螺仪文件路径
     */
    public static  String PATH_WATCH_SENSOR_GYRO = PATH_APP_STORAGE + "WatchSensorGYRO" + File.separator;

    /**
     * 手表不同类型传感器文件的后缀名
     */
    public static final String PATH_SENSOR_GPS_END = ".gps";
    public static final String PATH_SENSOR_GSENSOR_END = ".gsensor";
    public static final String PATH_SENSOR_HR_END = ".hr";
    public static final String PATH_SENSOR_TEMPERATURE_END = ".temperature";
    public static final String PATH_SENSOR_PRESSURE_END = ".pressure";
    public static final String PATH_SENSOR_HUMIDITY_END = ".humidity";
    public static final String PATH_SENSOR_COMPASS_END = ".compass";
    public static final String PATH_SENSOR_GYRO_END = ".gyro";
    public static final String PATH_SENSOR_DISTANCE_END = ".distance";


    /**
     * 手表图表文件后缀名
     */
    public static final String PRESSURE_CHART_SUFFIX = ".pressureChart";//气压图表
    public static final String TEMP_CHART_SUFFIX = ".temperatureChart";//气温图表
    public static final String HR_CHART_SUFFIX = ".hrChart";//心率图表
    public static final String ALTITUDE_CHART_SUFFIX = ".altitudeChart";//海拔图表
    public static final String BPM_CHART_SUFFIX = ".bpmChart";//步频图表
    public static final String SPEED_CHART_SUFFIX = ".speedChart";//速度图表

    //endregion ======================== 手表相关 ========================

}
