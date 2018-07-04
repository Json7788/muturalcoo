package com.fish.muturalcoo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FitmixUtil {

    //private static final int iBufferSize = 1024 * 1024 * 2;//判断文件是否能够播放的中间值,即头尾部分是否下载完成

    /**
     * 判断是否是有效的电子邮箱
     *
     * @param email 电子邮箱
     * @return true:有效,false:无效
     */
    public static boolean isEmail(String email) {
        if (email == null || email.isEmpty())
            return false;
        if (email.contains(".."))
            return false;
        if (!email.contains("@"))
            return false;
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        if (p == null)
            return false;
        Matcher m = p.matcher(email);
        if (m == null)
            return false;
        return m.matches();
    }

    /**
     * 判断是否是有效的手机号码
     *
     * @param mobiles 手机号码
     * @return true:有效,false:无效
     */
    public static boolean isMobileNumber(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        Pattern p = Pattern
                .compile("^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断是否是有效的http网址
     *
     * @param url http网址
     * @return true:有效,false:无效
     */
    public static boolean isValidHttpUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        final Pattern pattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        Matcher m = pattern.matcher(url);
        return m.matches();
    }

    /**
     * 身份证验证
     */
    public static boolean isIdentity(String identity) {
        int[] zoneNum = {11, 12, 13, 14, 15, 21, 22, 23, 31, 32, 33, 34, 35, 36, 37, 41, 42, 43, 44, 45, 46, 50, 51, 52, 53, 54, 61, 62, 63, 64, 71, 81, 82, 91};
        int[] PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int[] POWER_LIST = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

        if (identity == null || (identity.length() != 15 && identity.length() != 18))
            return false;
        final char[] cs = identity.toUpperCase().toCharArray();
        //校验位数
        int power = 0;
        for (int i = 0; i < cs.length; i++) {
            if (i == cs.length - 1 && cs[i] == 'X')
                break;//最后一位可以 是X或x
            if (cs[i] < '0' || cs[i] > '9')
                return false;
            if (i < cs.length - 1) {
                power += (cs[i] - '0') * POWER_LIST[i];
            }
        }
        //校验区位码
        if (Arrays.binarySearch(zoneNum, Integer.parseInt(identity.substring(0, 2))) <= 0)
            return false;

        //校验年份
        String year = identity.length() == 15 ? getIdcardCalendar() + identity.substring(6, 8) : identity.substring(6, 10);

        final int iYear = Integer.parseInt(year);
        if (iYear < 1900 || iYear > Calendar.getInstance().get(Calendar.YEAR))
            return false;//1900年的PASS,超过今年的PASS

        //校验月份
        String month = identity.length() == 15 ? identity.substring(8, 10) : identity.substring(10, 12);
        final int iMonth = Integer.parseInt(month);
        if (iMonth < 1 || iMonth > 12) {
            return false;
        }

        //校验天数
        String day = identity.length() == 15 ? identity.substring(10, 12) : identity.substring(12, 14);
        final int iDay = Integer.parseInt(day);
        if (iDay < 1 || iDay > 31)
            return false;

        //校验"校验码"
        if (identity.length() == 15)
            return true;
        return cs[cs.length - 1] == PARITYBIT[power % 11];
    }

    private static int getIdcardCalendar() {
        GregorianCalendar curDay = new GregorianCalendar();
        int curYear = curDay.get(Calendar.YEAR);
        return Integer.parseInt(String.valueOf(curYear).substring(2));
    }

    /**
     * 从html字符串中查找第一个img的src信息
     */
    public static String getFirstImgSrcFromHtml(String html) {
        String img;
        Pattern pattern;
        Matcher matcher;
        String regexImg = "<img.*src\\s*=\\s*(.*?)[^>]*?>";//图片链接地址
        pattern = Pattern.compile
                (regexImg, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(html);
        while (matcher.find()) {
            img = matcher.group();// 得到<img />数据
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);// 匹配<img>中的src数据
            while (m.find()) {
                return m.group(1);
            }
        }
        return "";
    }

    /**
     * 获取临时图片文件绝对路径
     */
    public static String getTempPhotoFile() {
        return createDirByAbsolutePath(Config.PATH_LOCAL_TEMP) + "temp.jpg";
    }

//    /**
//     * 根据音乐专辑id获取专辑封面图标文件绝对路径
//     *
//     * @param id 音乐专辑id
//     */
//    public static String getPlayListPhotoFile(int id) {
//        return createDirByAbsolutePath(Config.PATH_LOCAL_COVER) + id + ".jpg";
//    }

    /**
     * 删除临时图片
     */
    public static void deleteTempPhotoFile() {
        try {
            File file = new File(getTempPhotoFile());
            if (file.exists()) file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static boolean checkDownloadParamValid(String sPath, boolean bCheckPlay) {
//        File f = new File(sPath + ".down");
//        if (f.exists()) {
//            return f.length() > iBufferSize;
//        } else {
//            return false;
//        }
//    }

//    /**
//     * 过滤掉本地不存在而数据库表中写着有的歌曲
//     *
//     * @param list
//     * @return
//     */
//    public static List<Music> filterDownloadedList(List<Music> list) {
//        if (list == null || list.size() == 0)
//            return list;
//        boolean bExist;
//        List<Music> afterFilterMusicList = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            Music info = list.get(i);
//            if (info == null)
//                continue;
//            bExist = isExistCacheFile(info.getUrl(), info.getId(),
//                    Config.DOWNLOAD_FORMAT_MUSIC);
//            if (bExist) {
//                afterFilterMusicList.add(info);
//            } else {//本地如果不存在,则将DownloadInfo数据库表删除该项
//                DownloadInfo item = DownloadInfoHelper.getDownloadInfoById(info.getUrl());
//                DownloadInfoHelper.asyncDeleteDownloadInfo(item);
//            }
//        }
//        return afterFilterMusicList;
//    }

//    /**
//     * 判断音乐是否存在本地
//     *
//     * @return
//     */
//    public static boolean ifMusicInLocal(Music music) {
//        return isExistCacheFile(music.getUrl(), music.getId(),
//                Config.DOWNLOAD_FORMAT_MUSIC);
//    }
//
//    /**
//     * 判断当前网络状态是否能够播放该音乐
//     */
//    public static boolean checkNetworkStateForMusic(Music music) {
//        if (music == null)
//            return true;
//        String sLocalFile = getLocalFilePath(music.getUrl(), music.getId(),
//                Config.DOWNLOAD_FORMAT_MUSIC);
//
//        if (sLocalFile == null || sLocalFile.isEmpty())
//            return false;
//        File file = new File(sLocalFile);
//        if (!file.exists()) {
//            if (ApiUtils.getNetworkType() == com.fitmix.sdk.Config.NETWORK_TYPE_NONE) {
//                return false;
//            }
//        }
//        return true;
//    }

//    /**
//     * 消息推送
//     *
//     * @param messagePush true:开启消息推送,false:关闭消息推送
//     */
//    public static void applyMessagePush(boolean messagePush) {
//        /** 信鸽方式 */
//        if (messagePush) {
//            // 开启logcat输出，方便debug，发布时请关闭
//            // XGPushConfig.enableDebug(this, true);
//            // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
//            // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
//            // 具体可参考详细的开发指南
//            // 传递的参数为ApplicationContext
//            Context context = MixApp.getContext();
//            //XGPushManager.registerPush(context);
//            //自定义通知样式
//            XGPushNotificationBuilder notificationBuilder = XGPushManager.getDefaultNotificationBuilder(context);
//            if (notificationBuilder != null) {
//                notificationBuilder.setNotificationLargeIcon(R.drawable.logo);//设置默认大图标,不要圆角
//                notificationBuilder.setSmallIcon(R.drawable.logo);//设置默认小图标,不要圆角
//                XGPushManager.setDefaultNotificationBuilder(context, notificationBuilder);
//            }
//            //注册
//            XGPushManager.registerPush(context, new XGIOperateCallback() {
//                @Override
//                public void onSuccess(Object token, int flag) {
//                    Logger.i(Logger.XG_TAG, "XGPushManager-->registerPush onSuccess token:" + token + ",flag:" + flag);
//                }
//
//                @Override
//                public void onFail(Object token, int flag, String s) {
//                    Logger.e(Logger.XG_TAG, "XGPushManager-->registerPush onFail token:" + token + ",flag:" + flag);
//                }
//            });
//        } else {
//            XGPushManager.unregisterPush(MixApp.getContext());
//            Logger.i(Logger.XG_TAG, "XGPushManager-->unregisterPush");
//        }

        /** 友盟方式*/
//        PushAgent pushAgent = PushAgent.getInstance(MixApp.getContext());
//        pushAgent.disable();
//        if (messagePush) {
//            pushAgent.enable();
//        } else {
//            pushAgent.disable();
//        }
//    }

//    /**
//     * 获取UUID
//     *
//     * @param context 上下文
//     */
//    public static String generateUUID(Context context) {
//        if (context == null)
//            return null;
//        final TelephonyManager tm = (TelephonyManager) context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        final String tmDevice, tmSerial, timeid;
//        String radom = "" + ((Math.random() * 0xFFFFFFFF) % 0xFFFFFFFF);
//        timeid = "" + Calendar.getInstance().getTimeInMillis();
//
//        tmDevice = "" + tm.getDeviceId();
//        tmSerial = "" + tm.getSimSerialNumber();
//        UUID deviceUuid = new UUID(((long) timeid.hashCode() << 32) | radom.hashCode(),
//                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
//        return deviceUuid.toString();
//    }

    //region ========================================== 运动相关 ==========================================

    /**
     * 判断是否开启了GPS
     *
     * @param context 上下文
     * @return true:是,false:否
     */
    public static boolean isGpsEnable(Context context) {
        if (context == null)
            return false;
        LocationManager locationManager = ((LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE));
        if (locationManager == null)
            return false;
        boolean bEnable = false;
        try {
            bEnable = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bEnable;
    }

    /**
     * 开启GPS
     */
    public static void enableGPS(Context context) {
        /** java.lang.SecurityException: Permission Denial: starting Intent { act=android.settings.LOCATION_SOURCE_SETTINGS
         * cmp=com.android.settings/.Settings$LocationSettingsActivity }
         * from ProcessRecord{e27602c 27531:com.fitmix.sdk/u0a91} (pid=27531, uid=10091) not exported from uid 1000*/
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            if (intent != null && intent.resolveActivity(context.getPackageManager()) != null) {
                ((Activity) context).startActivityForResult(intent, Config.REQUEST_ENABLE_GPS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * BLE功能开启GPS
     */
    public static void enableBleGPS(Context context) {
        /** java.lang.SecurityException: Permission Denial: starting Intent { act=android.settings.LOCATION_SOURCE_SETTINGS
         * cmp=com.android.settings/.Settings$LocationSettingsActivity }
         * from ProcessRecord{e27602c 27531:com.fitmix.sdk/u0a91} (pid=27531, uid=10091) not exported from uid 1000*/
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            if (intent != null && intent.resolveActivity(context.getPackageManager()) != null) {
                ((Activity) context).startActivityForResult(intent, Config.BLE_REQUEST_ENABLE_GPS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否允许蓝牙4.0权限
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isBLEEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            final BluetoothAdapter adapter = bluetoothManager.getAdapter();
            return adapter != null && adapter.isEnabled();
        } else {
            return false;
        }
    }

    /**
     * 请求蓝牙权限，去启动蓝牙
     */
    public static void requestBlueTooth(Activity activity) {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (activity != null) {
            activity.startActivityForResult(enableIntent, Config.REQUEST_ENABLE_BLUETOOTH);
        }
    }

    /**
     * 获取当前时间是否与要比较时间为同一天
     *
     * @param time 要比较的时间戳1
     * @return true:是,false:否
     */
    public static boolean isToday(long time) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_YEAR);
        c.setTimeInMillis(time);
        return c.get(Calendar.DAY_OF_YEAR) == day;
    }

//    /**
//     * 同步微信步数
//     *
//     * @param context        上下文
//     * @param ignorePreCache 数据请求是否忽略之前的缓存结果
//     * @return 如果已请求后台服务器同步微信步数返回 300001,否则返回0
//     */
//    public static int syncWeChatStep(Context context, boolean ignorePreCache) {
//        int result = 0;
//        String unionId = PrefsHelper.with(context, AccessTokenKeeper.WECHAT_OAUTH_NAME)
//                .read(AccessTokenKeeper.KEY_UNION_ID, "");
//        String openId = PrefsHelper.with(context, AccessTokenKeeper.WECHAT_OAUTH_NAME)
//                .read(AccessTokenKeeper.KEY_OPENID, "");
////        if (TextUtils.isEmpty(unionId) || TextUtils.isEmpty(openId)) {
////            return false;
////        }
//
//        long lastWriteTime = PrefsHelper.with(context, Config.PREFS_SPORT).readLong(Config.SP_KEY_TODAY_STEPS_TIME);
//        if (!FitmixUtil.isToday(lastWriteTime)) {//防止上传不是当天的数据
//            return result;
//        }
//
//        int todayStep = PrefsHelper.with(context, com.fitmix.sdk.Config.PREFS_SPORT).readInt(com.fitmix.sdk.Config.SP_KEY_TODAY_STEPS, 0);
//        Logger.i(Logger.DEBUG_TAG, "syncWeChatStep-->unionId:" + unionId + ",openId:" + openId + ",todayStep:" + todayStep);
//        if (todayStep > 0 && todayStep < 120000) {//每日步数处于(1,120000)之间
//            result = SportDataManager.getInstance().setWeChatTodaySteps(unionId, openId, todayStep, ignorePreCache);
//        }
//        return result;
//    }

//    /**
//     * 同步步数到QQ健康
//     */
//    public static void syncToQQHealth(Context context) {
//        SharedPreferences sp = MixApp.getContext().getSharedPreferences(AccessTokenKeeper.QQ_OAUTH_NAME, Context.MODE_PRIVATE);
//        String openid = sp.getString(AccessTokenKeeper.KEY_OPENID, "");
//        String tokenId = sp.getString(AccessTokenKeeper.KEY_ACCESS_TOKEN, "");
//        if (!TextUtils.isEmpty(openid) && !TextUtils.isEmpty(tokenId)) {
//            RunLogInfo runLogInfo = new RunLogInfo();
//
//            int dWeight = SettingsHelper.getInt(Config.SETTING_USER_WEIGHT, Config.USER_DEFAULT_WEIGHT);
//            int dHeight = SettingsHelper.getInt(Config.SETTING_USER_HEIGHT, Config.USER_DEFAULT_HEIGHT);
//            int age = SettingsHelper.getInt(Config.SETTING_USER_AGE, Config.USER_DEFAULT_AGE);
//            boolean bFemale = SettingsHelper.getInt(Config.SETTING_USER_GENDER, -1) == Config.GENDER_FEMALE;
//
//            int todayStep = PrefsHelper.with(context, com.fitmix.sdk.Config.PREFS_SPORT).readInt(com.fitmix.sdk.Config.SP_KEY_TODAY_STEPS, 0);
//            int distance = (int) (todayStep * BpmManager.getStepRatioByBpm(100, bFemale) * dHeight / 100);
//            Long endTime = System.currentTimeMillis();
//            long runTIme = (long) (todayStep / 120.0f * 60000);//bpm按120算
//            int calorie = (int) (calculateCalorie(bFemale, age, dWeight, dHeight, runTIme, 120, todayStep));
//
//            runLogInfo.setStep(todayStep);
//            runLogInfo.setDistance(distance);
//            runLogInfo.setEndTime(endTime);
//            runLogInfo.setRunTime(runTIme);
//            runLogInfo.setCalorie(calorie);
//
//            SportDataManager.getInstance().syncToQQSport(tokenId, openid, runLogInfo);
//        }
//    }

    /**
     * 计算能量消耗卡路里 //http://www.freedieting.com/tools/calories_burned.htm
     *
     * @param bFemale 是否为女性,true:是,false:否
     * @param age     年龄
     * @param weight  体重,单位为千克
     * @param height  身高,单位为厘米
     * @param lTime   运动时长,单位为毫秒
     * @param bpm     步频
     * @param step    步数
     */
    public static double calculateCalorie(boolean bFemale, int age, double weight,
                                          double height, long lTime, int bpm, int step) {
        double result;
        //判断年龄,身高,体重,BPM
        if (age <= 0) {
            age = Config.USER_DEFAULT_AGE;
        }

        if (weight <= 0) {
            weight = Config.USER_DEFAULT_WEIGHT;
        }

        if (height <= 0) {
            height = Config.USER_DEFAULT_HEIGHT;
        }

        if (bpm <= 0 && lTime > 60000) { //运动bpm小于等于0(原因有:运动时间过长而步数过小)
            bpm = 120;//以120算
            if (step >= 0) {//根据步数重新更改运动时长,避免卡路里过大 //FIXME >=
                lTime = (long) (step * 1.0f / bpm * 60000);//单位为毫秒
            }
        }

        if (lTime <= 0) {
            return 0;
        }

        if (bFemale) {
            result = 655.1 + (9.563 * weight) + (1.850 * height)
                    - (4.676 * age);
        } else {
            result = 66.5 + (13.75 * weight) + (5.003 * height) - (6.775 * age);
        }
        double dRatio = getRatioCalorieByBpm(bpm);
        result = result * dRatio * lTime / 60000 / 1440;
        return result;
    }

    /**
     * 根据步频获取卡路里计算因子
     *
     * @param bpm 步频,每分钟步数
     */
    public static double getRatioCalorieByBpm(int bpm) {
        final int BPM1 = 60;
        final int BPM2 = 100;
        final int BPM3 = 120;
        final int BPM4 = 140;
        final int BPM5 = 149;
        final int BPM6 = 155;
        final int BPM7 = 159;
        final int BPM8 = 171;
        final int BPM9 = 181;

        //final float RATIO_1 = 2.0f;
        //final float RATIO_2 = 3.0f;
        //final float RATIO_3 = 3.8f;
        //final float RATIO_4 = 4.5f;
        //final float RATIO_5 = 6.0f;
        //final float RATIO_6 = 8.3f;
        //final float RATIO_7 = 9.0f;
        //final float RATIO_8 = 9.8f;
        //final float RATIO_9 = 10.5f;

        double ratio;
        if (bpm < BPM1) {
            //ratio = RATIO_1 * bpm / BPM1;
            ratio = bpm / 30.0;
        } else if (bpm < BPM2) {
            //ratio = RATIO_1 + (RATIO_2 - RATIO_1) * (bpm - BPM1) / (BPM2 - BPM1);
            ratio = 0.5 + bpm * 0.025;
        } else if (bpm < BPM3) {
            //ratio = RATIO_2 + (RATIO_3 - RATIO_2) * (bpm - BPM2) / (BPM3 - BPM2);
            ratio = 0.04 * bpm - 1;
        } else if (bpm < BPM4) {
            //ratio = RATIO_3 + (RATIO_4 - RATIO_3) * (bpm - BPM3) / (BPM4 - BPM3);
            ratio = 0.035 * bpm - 0.4;
        } else if (bpm < BPM5) {
            //ratio = RATIO_4 + (RATIO_5 - RATIO_4) * (bpm - BPM4) / (BPM5 - BPM4);
            ratio = 0.167 * bpm - 18.88;
        } else if (bpm < BPM6) {
            //ratio = RATIO_5 + (RATIO_6 - RATIO_5) * (bpm - BPM5) / (BPM6 - BPM5);
            ratio = 0.283 * bpm - 36.167;
        } else if (bpm < BPM7) {
            //ratio = RATIO_6 + (RATIO_7 - RATIO_6) * (bpm - BPM6) / (BPM7 - BPM6);
            ratio = 0.175 * bpm - 18.825;
        } else if (bpm < BPM8) {
            //ratio = RATIO_7 + (RATIO_8 - RATIO_7) * (bpm - BPM7) / (BPM8 - BPM7);
            ratio = 0.067 * bpm - 1.653;
        } else if (bpm < BPM9) {
            //ratio = RATIO_8 + (RATIO_9 - RATIO_8) * (bpm - BPM8) / (BPM9 - BPM8);
            ratio = 0.07 * bpm - 2.17;
        } else {
            //ratio = RATIO_9 + 0.07 * (bpm - BPM9);
            ratio = 0.07 * bpm - 2.17;
        }
        return ratio;
    }

    /**
     * 计算跳绳消耗卡路里 //http://www.freedieting.com/tools/calories_burned.htm
     *
     * @param bFemale 是否为女性,true:是,false:否
     * @param age     年龄
     * @param weight  体重,单位为千克
     * @param height  身高,单位为厘米
     * @param lTime   运动时长,单位为毫秒
     * @param bpm     跳频
     */
    public static double calculateSkipCalorie(boolean bFemale, int age, double weight,
                                              double height, long lTime, int bpm) {
        double result;
        if (bFemale) {
            result = 655.1 + (9.563 * weight) + (1.850 * height)
                    - (4.676 * age);
        } else {
            result = 66.5 + (13.75 * weight) + (5.003 * height) - (6.775 * age);
        }
        double dRatio = getRatioCalorieBySkipBpm(bpm);
        result = result * dRatio * lTime / 60000 / 1440;
//        Logger.i(Logger.DEBUG_TAG, "SkipService --- > result : " + result);
        return result;
    }

    public static double getRatioCalorieBySkipBpm(int bpm) {
        double ratio;
        if (bpm < 20) {
            ratio = 0;
        } else if (bpm < 100) {
            ratio = 8.8;
        } else if (bpm < 120) {
            ratio = 11.8;
        } else if (bpm < 160) {
            ratio = 12.3;
        } else if (bpm < 200) {
            ratio = 12.5;
        } else {
            ratio = 12.8;
        }
//        if (bpm < 100) {
//            ratio = 0.088 * bpm;
//        } else {
//            ratio = 0.017 * bpm + 9.93;
//        }
        return ratio;
    }
    //endregion ========================================== 运动相关 ==========================================

    /**
     * 获取手机当前的语言是否为中文
     *
     * @return true:是,false:否
     */
//    public static boolean phoneLanguageIsChinese() {
//        String sLanguage = ApiUtils.getLanguage();
//        return sLanguage != null && sLanguage.endsWith("zh");
//    }

    /**
     * 获取手机Cpu核数
     *
     * @return 手机cpu核数
     */
    public static int getPhoneCpuCoreNum() {
        if (Build.VERSION.SDK_INT >= 17) {
            int num = Runtime.getRuntime().availableProcessors();
            if (num <= 0) {
                return 2;
            }
            return num;
        } else {
            return 2;
        }
    }

    /**
     * 写空字符
     *
     * @param sFile
     * @param iStart
     * @param length
     */
    public static void clearFileData(String sFile, int iStart, int length) {
        if (sFile == null || sFile.isEmpty())
            return;

        final int iBufSize = 1024;
        RandomAccessFile rf = null;
        File f;
        byte buffer[];
        try {
            f = new File(sFile);
            if (!f.exists())
                return;
            if ((iStart < 0) || (length <= 0))
                return;
            rf = new RandomAccessFile(sFile, "rw");
            rf.seek(iStart);
            buffer = new byte[iBufSize];
            int iLoop = length / iBufSize;
            for (int i = 0; i < iLoop; i++) {
                rf.write(buffer);
            }
            int iLeft = length % iBufSize;
            if (iLeft > 0) {
                rf.write(buffer, 0, iLeft);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rf != null)
                    rf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //region ================================ 文件夹或文件操作 ================================

    /**
     * 根据远程url,资源id,资源类型,获取本地文件绝对路径
     *
     * @param path 远程url
     * @param id   资源id
     * @param type 资源类型,参考{@link Config DOWNLOAD_FORMAT_MUSIC}
     * @return 本地文件绝对路径
     */
    public static String getLocalFilePath(String path, long id, int type) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        String sExt;
        String sPrefix="";
        String sOther = "";
        switch (type) {
            case Config.DOWNLOAD_FORMAT_MUSIC:
//                sPrefix = getMusicPath();
                int index = path.lastIndexOf(".");
                /** java.lang.RuntimeException: Unable to start activity ComponentInfo{com.fitmix.sdk/com.fitmix.sdk.view.activity.PlayMusicActivity}:
                 * java.lang.StringIndexOutOfBoundsException: length=49; regionStart=-1; regionLength=50*/
                if (index == -1) {
                    sExt = ".m4a";
                    break;
                }
                sExt = path.substring(index, path.length());//因为后缀不一定是固定的.mp3后缀,可能.m4a
                break;
            case Config.DOWNLOAD_FORMAT_PICTURE:
//                sPrefix = getPicturePath();
                sExt = ".jpg";
                break;
            case Config.DOWNLOAD_FORMAT_CLUB_COVER:
//                sPrefix = getPicturePath();
                sPrefix += "club";
                sExt = ".jpg";
                break;
            case Config.DOWNLOAD_FORMAT_ALBUM_COVER:
//                sPrefix = getPicturePath();
                sPrefix += "album";
                sExt = ".jpg";
                break;
            case Config.DOWNLOAD_FORMAT_AVATAR:
//                sPrefix = getPicturePath();
                sPrefix += "avatar";
                sExt = ".jpg";
                break;
            case Config.DOWNLOAD_LOCAL_COVER:
//                sPrefix = getCoverPath();
                sExt = ".jpg";
                break;
            case Config.DOWNLOAD_FORMAT_PICTURE2:
                sOther = "_big";
//                sPrefix = getPicturePath();
                sExt = ".jpg";
                if ((path != null) && (!path.isEmpty()) && (path.length() > 4))
                    sExt = path.substring(path.length() - 4);
                break;
            case Config.DOWNLOAD_FORMAT_TRAIL:
                sPrefix = getTrailPath();
                sExt = ".json";
                break;
            case Config.DOWNLOAD_FORMAT_STEP:
                sPrefix = getStepPath();
                sExt = ".step";
                break;
            default:
                return null;
        }

//        if ((path != null) && (!path.isEmpty())) {
//            path.lastIndexOf('/');
//        }

        return sPrefix + id + sOther + sExt;
    }

    /**
     * 获取乐享动根文件夹路径
     */
    public static String getDataPath() {
        return createDirByPath(Config.PATH_APP_STORAGE);
    }

    /**
     * 获取乐享动音乐专辑封面,banner封面,电台封面下载文件夹路径
     */
//    public static String getCoverPath() {
//        return createDirByPath(Config.PATH_LOCAL_COVER);
////    }

    /**
     * 获取乐享动运动直播声效下载文件夹路径
     */
//    public static String getVoicePath() {
//        return createDirByPath(Config.PATH_RUN_VOICE);
////    }

    /**
     * 获取乐享动音乐封面下载文件夹路径
     */
//    public static String getPicturePath() {
//        return createDirByPath(Config.PATH_DOWN_PICTURE);
//    }

    /**
     * 获取乐享动用户头像,运动赛事,运动直播拍照下载文件夹路径
     */
//    public static String getPhotoPath() {
//        return createDirByPath(Config.PATH_RUN_PHOTO);
//    }

    /**
     * 获取乐享动音乐下载文件夹路径
     */
//    public static String getMusicPath() {
//        return createDirByPath(Config.PATH_DOWN_MUSIC);
//    }

    /**
     * 获取乐享动运动轨迹下载文件夹路径
     */
    public static String getTrailPath() {
        return createDirByPath(Config.PATH_DOWN_TRAIL);
    }

    /**
     * 获取乐享动运动步数下载文件夹路径
     */
    public static String getStepPath() {
        return createDirByPath(Config.PATH_DOWN_STEP);
    }

    /**
     * 获取乐享动临时文件(比如分享等)文件夹路径
     */
    public static String getTempPath() {
        return createDirByPath(Config.PATH_LOCAL_TEMP);
    }

    /**
     * 获取乐享动跳绳文件夹路径
     */
//    public static String getSkipPath() {
//        return createDirByPath(Config.PATH_DOWN_SKIP);
//    }

    /**
     * 获取乐享动话题文件夹路径
     */
//    public static String getTopicPath() {
//        return createDirByPath(Config.PATH_TOPIC);
//    }

//    /**
//     * 根据音乐信息,获取音乐临时文件的绝对路径
//     * example：.../32.m4a.tmp
//     *
//     * @param music 音乐信息
//     * @return 音乐信息对应的临时文件的绝对路径
//     */
//    public static String getTempMusicPath(Music music) {
//        String sPrefix = getMusicPath();
//        String sExt = ".tmp";
//
//        String s = music.getUrl();
//        int index = s.lastIndexOf(".");
//        /**
//         * Bug:Caused by: java.lang.StringIndexOutOfBoundsException: length=47; regionStart=-1; regionLength=48
//         * at java.lang.String.startEndAndLength(String.java:504)
//         * */
//        if (index == -1) {
//            return null;
//        }
//        String engName = s.substring(index, s.length());
//        return sPrefix + music.getId() + engName + sExt;
//    }

    /**
     * 获取手表文件夹路径
     */
    public static String getWatchPath() {
        return createDirByPath(Config.PATH_WATCH);
    }

    /**
     * 根据文件夹路径,在外部存储器中创建文件夹
     *
     * @param path 文件夹绝对路径
     * @return 文件夹绝对路径
     */
    private static String createDirByPath(String path) {
        FileUtils.makeFolders(path);
        return path;
    }

    /**
     * 获取正在下载的音乐片段绝对路径名
     */
//    public static String getDownloadingPropName() {
//        return getMusicPath() + "downloading.prop";
//    }

    /**
     * 根据远程url,资源id,资源类型,获取本地文件绝对路径,并判断指定的文件是否存在
     *
     * @param path 远程url
     * @param id   资源id
     * @param type 资源类型,参考{@link Config DOWNLOAD_FORMAT_MUSIC}
     * @return true:存在,false:不存在
     */
    public static boolean isExistCacheFile(String path, long id, int type) {
        String sFilename = getLocalFilePath(path, id, type);
        if (sFilename == null)
            return false;
        File f = new File(sFilename);
        return f.exists();
    }

    /**
     * 根据文件夹绝对路径,在外部存储器创建文件夹
     *
     * @param dirPath 文件夹绝对路径
     */
    public static String createDirByAbsolutePath(String dirPath) {
        if (dirPath == null || dirPath.isEmpty())
            return dirPath;
        File f = new File(dirPath);
        if (!f.exists())
            f.mkdir();
        return dirPath;
    }

    /**
     * 从URI中获取文件的全路径
     *
     * @param context    上下文
     * @param contentUri 源URI
     * @return contentUri对应文件的全路径, 注意空值判断
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        if (contentUri == null) {
            return null;
        }
        //判断Url scheme
        if ("file".equalsIgnoreCase(contentUri.getScheme())) {//本身就是文件
            return contentUri.getPath();
        } else if ("content".equalsIgnoreCase(contentUri.getScheme())) {//contentProvider
            Cursor cursor = null;
            try {
                String[] projection = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(columnIndex);
                } else {
                    return null;
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return null;
    }

    //endregion ================================ 文件夹或文件操作 ================================

    //region ================================= 音乐文件操作 =================================

    /**
     *
     * */
    public static boolean parseDownloadInfo(String sInfo,
                                            HashMap<String, Integer> hashmap) {
        if (sInfo == null || sInfo.isEmpty())
            return false;
        if (hashmap == null)
            return false;

        final int HEAD_INDEX = 0;
        final int TAIL_INDEX = 1;
        final int BODY_INDEX = 2;

        int iIndex = sInfo.indexOf('_');
        if (iIndex == -1)
            return false;
        /**
         * 解决bug
         * java.lang.NumberFormatException: Invalid int: "
         * at java.lang.Integer.parseInt(Integer.java:334)
         * at com.fitmix.sdk.base.FitmixUtil.parseDownloadInfo(FitmixUtil.java:589)
         * */
        int id;
        try {
            id = Integer.parseInt(sInfo.substring(0, iIndex));
        } catch (NumberFormatException e) {
            return false;
        }
        sInfo = sInfo.substring(iIndex + 1);
        String sSeg[] = sInfo.split("_");
        if (sSeg == null || sSeg.length != 3)
            return false;

        iIndex = sSeg[HEAD_INDEX].indexOf('.');
        if (iIndex == -1)
            return false;
        String sHeadCurrent = sSeg[HEAD_INDEX].substring(0, iIndex);
        if (sHeadCurrent == null || sHeadCurrent.isEmpty())
            return false;
        String sHeadTotal = sSeg[HEAD_INDEX].substring(iIndex + 1);
        if (sHeadTotal == null || sHeadTotal.isEmpty())
            return false;

        iIndex = sSeg[BODY_INDEX].indexOf('.');
        if (iIndex == -1)
            return false;
        String sBodyCurrent = sSeg[BODY_INDEX].substring(0, iIndex);
        if (sBodyCurrent == null || sBodyCurrent.isEmpty())
            return false;
        String sBodyTotal = sSeg[BODY_INDEX].substring(iIndex + 1);
        if (sBodyTotal == null || sBodyTotal.isEmpty())
            return false;

        iIndex = sSeg[TAIL_INDEX].indexOf('.');
        if (iIndex == -1)
            return false;
        String sTailCurrent = sSeg[TAIL_INDEX].substring(0, iIndex);
        if (sTailCurrent == null || sTailCurrent.isEmpty())
            return false;
        String sTailTotal = sSeg[TAIL_INDEX].substring(iIndex + 1);
        if (sTailTotal == null || sTailTotal.isEmpty())
            return false;

        int headCurrent = Integer.parseInt(sHeadCurrent);
        int headTotal = Integer.parseInt(sHeadTotal);
        int bodyCurrent = Integer.parseInt(sBodyCurrent);
        int bodyTotal = Integer.parseInt(sBodyTotal);
        int tailCurrent = Integer.parseInt(sTailCurrent);
        int tailTotal = Integer.parseInt(sTailTotal);

        if ((headCurrent > headTotal) || (bodyCurrent > bodyTotal)
                || (tailCurrent > tailTotal))
            return false;
        hashmap.put("id", id);
        hashmap.put("headCurrent", headCurrent);
        hashmap.put("headTotal", headTotal);
        hashmap.put("bodyCurrent", bodyCurrent);
        hashmap.put("bodyTotal", bodyTotal);
        hashmap.put("tailCurrent", tailCurrent);
        hashmap.put("tailTotal", tailTotal);

        return true;
    }

//    /**
//     * 判断音乐文件是否能播放
//     *
//     * @param sPath      文件绝对路径名
//     * @param bCheckPlay
//     * @return true:音乐文件可以播放,false:音乐文件不能播放
//     */
//    public static boolean checkMusicDownloadParamValid(String sPath, boolean bCheckPlay) {
//        if (sPath == null || sPath.isEmpty())
//            return false;
//        String sInfo;
//        HashMap<String, Integer> map = new HashMap<>();
//        PropUtil propUtil = PropUtil.getInstance();
//        if ((FitmixUtil.getDownloadingPropName() != null)
//                && (!FitmixUtil.getDownloadingPropName().equals(propUtil.getFilename()))) {
//            propUtil.setLocaleFilename(FitmixUtil.getDownloadingPropName());
//        }
//
//        int iIndex = sPath.lastIndexOf('/');
//        if (iIndex == -1)
//            return false;
//        String sTag = sPath.substring(iIndex + 1, sPath.length() - 4);
//        sInfo = propUtil.getStringValue(sTag);
//        if (sInfo == null || sInfo.isEmpty())
//            return false;
//        if (!parseDownloadInfo(sInfo, map))
//            return false;
//
//        int headCurrent = map.get("headCurrent");
//        int headTotal = map.get("headTotal");
//        int bodyCurrent = map.get("bodyCurrent");
//        int bodyTotal = map.get("bodyTotal");
//        int tailCurrent = map.get("tailCurrent");
//        int tailTotal = map.get("tailTotal");
//        int iTotalSize = headTotal + bodyTotal + tailTotal;
//
//        File f = new File(sPath);
//
//        if ((headCurrent > headTotal) || (bodyCurrent > bodyTotal)
//                || (tailCurrent > tailTotal))
//            return false;
//        if ((headTotal <= 0) || (tailTotal <= 0))
//            return false;
//
//        if (iTotalSize != f.length())
//            return false;
//        if (bCheckPlay) {
//            if ((headCurrent != headTotal) || (tailCurrent != tailTotal))
//                return false;
//        }
//        map.clear();
//        return true;
//    }

    //endregion ================================= 音乐文件操作 =================================

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static long getSDAvailableSize() {
        //获得SD卡空间的信息
        File path = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSize();
//        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();

        //计算SD卡的空间大小
//        long totalSize = blockSize * totalBlocks;
        return availableBlocks * blockSize;
    }

    /**
     * 获取网络文件大小
     */
    public static long getFileSizeByUrl(String filePath) {
        HttpURLConnection urlcon = null;
        long fileSize = 0L;
        //format double
        try {
            URL url = new URL(filePath);
            urlcon = (HttpURLConnection) url.openConnection();
            fileSize = urlcon.getContentLength();
            //format output
//            size=fnum.format(fileSize/1024);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close connect
            if (urlcon != null) {
                urlcon.disconnect();
            }
        }
        return fileSize;
//        try {
//            URL url = new URL(urlPath);
//            URLConnection urlCon = url.openConnection();
//            return urlCon.getContentLength();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    //region ================================= 心率相关 =================================

//    /**
//     * 根据心率值、静息心率获取当前处于的阶段
//     *
//     * @param heartRate     当前心率
//     * @param restHeartRate 静息心率
//     * @return 5、4、3、2、1、0分别代表最大（极限强度）、无氧（强化机能）、有氧（强化心肺）、燃脂（燃烧脂肪）、热身、放松
//     * 另外 6代表自定义模式、7代表自由模式
//     */
//    public static int getHeartRateLevel(int heartRate, int restHeartRate) {
//        int age = SettingsHelper.getInt(Config.SETTING_USER_AGE, Config.USER_DEFAULT_AGE);
//        age = age > 0 ? age : Config.USER_DEFAULT_AGE;//新用户可能存在获取到的SettingsHelper.getInt(Config.SETTING_USER_AGE, 24)为0的情况
//        int max = SettingsHelper.getInt(Config.HEART_RATE_MAX, FitmixUtil.getMaxHeartRate(age));
//
//        if ((heartRate > restHeartRate + 0.9 * (max - restHeartRate))/* && heartRate <= max */) {
//            return 5;
//        }
//        if ((heartRate > restHeartRate + 0.8 * (max - restHeartRate)) && heartRate <= restHeartRate + 0.9 * (max - restHeartRate)) {
//            return 4;
//        }
//        if ((heartRate > restHeartRate + 0.7 * (max - restHeartRate)) && heartRate <= restHeartRate + 0.8 * (max - restHeartRate)) {
//            return 3;
//        }
//        if ((heartRate > restHeartRate + 0.6 * (max - restHeartRate)) && heartRate <= restHeartRate + 0.7 * (max - restHeartRate)) {
//            return 2;
//        }
//        if ((heartRate > restHeartRate + 0.5 * (max - restHeartRate)) && heartRate <= restHeartRate + 0.6 * (max - restHeartRate)) {
//            return 1;
//        }
//        if (/*(heartRate > restHeartRate - 15) && */ heartRate <= restHeartRate + 0.5 * (max - restHeartRate)) {
//            return 0;
//        }
//        return 0;
//    }

    /**
     * 根据心率值、静息心率获取当前处于的阶段即两个特定的值（仅仅用在心率语音播报上）
     *
     * @param heartRate     当前心率值
     * @param restHeartRate 静息心率值
     * @return 6、5、4、3、2、1、0分别代表达到最大限、5最大(极限)、4无氧(强化机能)、3有氧(强化心肺)、2燃脂、1热身、0燃脂与热身的交界处,-1代表放松状态
     */
//    public static int getHeartRateLevelAndSpecialNum(int heartRate, int restHeartRate) {
//        int age = SettingsHelper.getInt(Config.SETTING_USER_AGE, Config.USER_DEFAULT_AGE);
//        age = age > 0 ? age : Config.USER_DEFAULT_AGE;//新用户可能存在获取到的SettingsHelper.getInt(Config.SETTING_USER_AGE, 24)为0的情况
//        int max = SettingsHelper.getInt(Config.HEART_RATE_MAX, getMaxHeartRate(age));
//        double hr_01 = restHeartRate + 0.5 * (max - restHeartRate);//放松与热身之间的边界值
//        double hr_21 = restHeartRate + 0.6 * (max - restHeartRate);//燃脂与热身之间的边界值
//        double hr_32 = restHeartRate + 0.7 * (max - restHeartRate);//燃脂与强化心肺之间的边界值
//        double hr_43 = restHeartRate + 0.8 * (max - restHeartRate);//强化心肺与强化机能之间的边界值
//        double hr_54 = restHeartRate + 0.9 * (max - restHeartRate);//极限与强化机能之间的边界值
//
//        if (heartRate >= max) {
//            return 6;
//        } else if ((heartRate >= hr_54) && heartRate < max) {
//            return 5;
//        } else if ((heartRate >= hr_43) && heartRate < hr_54) {
//            return 4;
//        } else if ((heartRate >= hr_32) && heartRate < hr_43) {
//            return 3;
//        } else if ((heartRate > hr_21) && heartRate < hr_32) {
//            return 2;
//        } else if (heartRate == (int) hr_21) {//心率刚好达到2、1交界处。心率触达燃脂心率临界点,FIXME 这个条件很难达到
//            return 0;
//        } else if ((heartRate >= hr_01) && heartRate < hr_21) {
//            return 1;
//        } else if (heartRate > 0 && heartRate < hr_01) {
//            return 7;
//        }
//        return -1;
//    }

    /**
     * 获取最大心率
     * 普通人群：HRmax=208-0.7*年龄
     * 肥胖人群：HRmax=200-0.5*年龄
     *
     * @param age 年龄
     * @return 理论最大心率值
     */
//    public static int getMaxHeartRate(int age) {
//        int max;
//        if (ifFat(age)) {
//            max = (int) (200 - 0.5 * age);
//        } else {
//            max = (int) (208 - 0.7 * age);
//        }
//        return max;
//    }

    /**
     * 获取心率区间中任一区间的中间心率值
     * 普通人群：HRmax=208-0.7*年龄
     * 肥胖人群：HRmax=200-0.5*年龄
     *
     * @param restHeartRate
     * @param level
     * @return
     */
//    public static int getLevelMiddleNum(int restHeartRate, int level) {
//        int middleNum = -1;
//        int age = SettingsHelper.getInt(Config.SETTING_USER_AGE, Config.USER_DEFAULT_AGE);
//        age = age > 0 ? age : Config.USER_DEFAULT_AGE;//新用户可能存在获取到的SettingsHelper.getInt(Config.SETTING_USER_AGE, 24)为0的情况
//        int max = SettingsHelper.getInt(Config.HEART_RATE_MAX, getMaxHeartRate(age));
//
//        switch (level) {
//            case 1:
//                middleNum = (int) (restHeartRate + (0.5 * (max - restHeartRate) + 0.6 * (max - restHeartRate)) / 2);
//                break;
//            case 2:
//                middleNum = (int) (restHeartRate + (0.6 * (max - restHeartRate) + 0.7 * (max - restHeartRate)) / 2);
//                break;
//            case 3:
//                middleNum = (int) (restHeartRate + (0.7 * (max - restHeartRate) + 0.8 * (max - restHeartRate)) / 2);
//                break;
//            case 4:
//                middleNum = (int) (restHeartRate + (0.8 * (max - restHeartRate) + 0.9 * (max - restHeartRate)) / 2);
//                break;
//            case 5:
//                middleNum = (int) (restHeartRate + (0.9 * (max - restHeartRate) + max) / 2);
//                break;
//        }
//        return middleNum;
//
//    }

    /**
     * 根据卡路里换算脂肪公式得出脂肪燃烧克数
     * 根据1磅脂肪=3600卡路里
     * 同时六大心率区间（从低到高）脂肪燃烧同时要根据比重85%、85%、55%、36%、19%、14%换算
     * 1磅(lb)=0.4535924千克(kg)
     *
     * @param cal   运动消耗卡路里
     * @param level 运动心率区间,5、4、3、2、1、0分别代表最大、无氧、有氧、燃脂、热身、放松
     * @return 脂肪消耗克数
     */
    public static float getFatBurnFromCal(float cal, int level) {
        if (cal <= 0)
            return 0;
        float fatBurn;
        switch (level) {
            case 0:
                fatBurn = (cal * 453.0f / 3600 * 85 / 100);
                break;
            case 1:
                fatBurn = (cal * 453.0f / 3600 * 85 / 100);
                break;
            case 2:
                fatBurn = (cal * 453.0f / 3600 * 55 / 100);
                break;
            case 3:
                fatBurn = (cal * 453.0f / 3600 * 36 / 100);
                break;
            case 4:
                fatBurn = (cal * 453.0f / 3600 * 19 / 100);
                break;
            case 5:
                fatBurn = (cal * 453.0f / 3600 * 14 / 100);
                break;
            default:
                fatBurn = 0;
        }
        return fatBurn;
    }

    /**
     * 获取体质指数（BMI）
     * BMI=体重（kg）÷ 身高^2（m）
     *
     * @return
     */
//    public static double getBMI() {
//        int weight = SettingsHelper.getInt(Config.SETTING_USER_WEIGHT, Config.USER_DEFAULT_WEIGHT);
//        int height = SettingsHelper.getInt(Config.SETTING_USER_HEIGHT, Config.USER_DEFAULT_HEIGHT);
//        if (height == 0) {
//            height = Config.USER_DEFAULT_HEIGHT;
//        }
//        return weight / (Math.pow(height / 100.0f, 2));
//    }

    /**
     * 身体脂肪率（ BFR ）
     * BFR = [ 1.2 × BMI + 0.23 × Age - 5.4 - 10.8 × Gender ( Male = 1，Female = 0 ) ] × 100%
     *
     * @param gender 性别,1:男,2:女
     * @param age    年龄
     */
//    public static double getBFR(int gender, int age) {
////        int age = SettingsHelper.getInt(Config.SETTING_USER_AGE, Config.USER_DEFAULT_AGE);
//
//        return (1.2 * getBMI() + 0.23 * age - 5.4 - ((gender == 1) ? (10.8) : (0))) / 100;
//    }

    /**
     * 是否肥胖
     *
     * @param age 年龄
     * @return true:肥胖,false:正常
     */
//    public static boolean ifFat(int age) {
//        int gender = SettingsHelper.getInt(Config.SETTING_USER_GENDER, Config.GENDER_MALE);
//        double bfr = getBFR(gender, age);
//        switch (gender) {
//            case Config.GENDER_MALE:
//                return bfr >= 0.26;
//            case Config.GENDER_FEMALE:
//                return bfr >= 0.32;
//        }
//        return false;
//    }

    /**
     * 获取正常人的最大摄氧量
     *
     * @param gender 用户性别,int型,1:男,2:女
     * @param age    年龄
     * @return
     */
    public static float getAvgVoMax(int gender, int age) {
        float voMaxValue = 0f;
        boolean isFemale = gender != 1;
        if (age <= 29.5) {
            voMaxValue = isFemale ? (38.9f + 31) / 2.0f : (43.9f + 34) / 2.0f;
        } else if (age <= 39.5 && age > 29.5) {
            voMaxValue = isFemale ? (36.9f + 28) / 2.0f : (41.9f + 31) / 2.0f;
        } else if (age <= 49.5 && age > 39.5) {
            voMaxValue = isFemale ? (34.9f + 25) / 2.0f : (38.9f + 27) / 2.0f;
        } else if (age <= 59.5 && age > 49.5) {
            voMaxValue = isFemale ? (33.9f + 22) / 2.0f : (37.9f + 25) / 2.0f;
        } else if (age <= 69.5 && age > 59.5) {
            voMaxValue = isFemale ? (32.9f + 21) / 2.0f : (35.9f + 23) / 2.0f;
        } else if (age > 69.5) {
            voMaxValue = isFemale ? (30.9f + 20) / 2.0f : (32.9f + 21) / 2.0f;
        }
        return voMaxValue;
    }

    /**
     * 根据性别、年龄获取静息心率区间
     *
     * @param gender 性别,1:男,2:女
     * @param age    年龄
     */
    public static int[] getRestingHRArea(int gender, int age) {
        int[] area = new int[]{50, 100};
        if (age <= 0) {
            return area;
        }
        boolean isFemale = gender != 1;
        if (age > 0 && age <= 3) {
            area[0] = isFemale ? 88 : 85;
            area[1] = isFemale ? 125 : 124;
            return area;
        } else if (age > 0 && age <= 5) {
            area[0] = isFemale ? 76 : 74;
            area[1] = isFemale ? 117 : 112;
            return area;
        } else if (age > 0 && age <= 8) {
            area[0] = isFemale ? 69 : 66;
            area[1] = isFemale ? 106 : 105;
            return area;
        } else if (age > 0 && age <= 11) {
            area[0] = isFemale ? 66 : 61;
            area[1] = isFemale ? 103 : 97;
            return area;
        } else if (age > 0 && age <= 15) {
            area[0] = isFemale ? 60 : 57;
            area[1] = isFemale ? 99 : 97;
            return area;
        } else if (age > 0 && age <= 19) {
            area[0] = isFemale ? 58 : 52;
            area[1] = isFemale ? 99 : 92;
            return area;
        } else if (age > 0 && age <= 39) {
            area[0] = isFemale ? 57 : 52;
            area[1] = isFemale ? 95 : 89;
            return area;
        } else if (age > 0 && age <= 59) {
            area[0] = isFemale ? 56 : 52;
            area[1] = isFemale ? 92 : 90;
            return area;
        } else if (age > 0 && age <= 79) {
            area[0] = isFemale ? 56 : 50;
            area[1] = isFemale ? 92 : 91;
            return area;
        } else if (age >= 80) {
            area[0] = isFemale ? 56 : 51;
            area[1] = isFemale ? 93 : 94;
        }
        return area;
    }


    //endregion ================================= 心率相关 =================================
}
