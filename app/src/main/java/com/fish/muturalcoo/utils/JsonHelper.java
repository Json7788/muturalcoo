package com.fish.muturalcoo.utils;

import android.text.TextUtils;

import com.fish.muturalcoo.entity.HeartRateJsonInfo;
import com.fish.muturalcoo.entity.RunStepInfo;
import com.fish.muturalcoo.entity.TrailInfo;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 利用google Gson对json字符串和对应的实体类进行相互转换
 */
public class JsonHelper {

    /**
     * 获取实体的json字符串
     *
     * @param value 实体
     * @return 实体的json字符串
     */
    public static String createJsonString(Object value) {
        Gson gson = new Gson();
        return gson.toJson(value);
    }

    /**
     * 根据json字符串,实体类型获取实例,用法:
     * <p>Login login = JsonHelper.getObject(jsonString,Login.class);</p>
     *
     * @param jsonString json字符串
     * @param cls        要转换的实体类型
     * @return 与json字符串对应的实例, 注意null值判断
     */
    public static <T> T getObject(String jsonString, Class<T> cls) {
        T t = null;
        try {
            if (isValidJsonStr(jsonString)) {
                Gson gson = new Gson();
                t = gson.fromJson(jsonString, cls);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 根据json字符串,实体类型获取实例集合,用法:
     * <p>List&lt; Login> list = JsonHelper.getList(jsonString, new TypeToken&lt;List&lt;Login>>(){}.getType() );</p>
     *
     * @param jsonString json字符串
     * @param type       要转换的实体类型集合Type
     * @return 与json字符串对应的实例集合
     */
    public static <T> List<T> getList(String jsonString, Type type) {
        List<T> list = new ArrayList<>();
        try {
            if (isValidJsonStr(jsonString)) {
                Gson gson = new Gson();
                list = gson.fromJson(jsonString, type);
            }
        } catch (Exception e) {
            Logger.e(Logger.DEBUG_TAG, "JsonHelper-->getList error:" + e.getMessage());
        }
        return list;
    }


    /**
     * 判断json字符串是否有效
     *
     * @param json 要判断的json字符串
     * @return true:有效,false:无效
     */
    public static boolean isValidJsonStr(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        try {
            new JsonParser().parse(json);
        } catch (JsonParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 以流的方式解析跑步轨迹文件
     *
     * @param fileName 轨迹文件绝对路径名
     * @return 轨迹点集合
     */
    public static List<TrailInfo> readRunTrailFile(String fileName) {
        Logger.i(Logger.DEBUG_TAG, "readRunTrailFile fileName:" + fileName);
        List<TrailInfo> trailInfoList = new ArrayList<>();
        if (TextUtils.isEmpty(fileName)) {
            return trailInfoList;
        }
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return trailInfoList;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream, "UTF-8"));
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name != null && name.equals("array")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        TrailInfo trailInfo = new TrailInfo();
                        trailInfo.setUsed(true);//默认所有的点都显示在地图上,兼容ios
                        while (reader.hasNext()) {
                            String fieldName = reader.nextName();
                            if (fieldName != null) {
                                if (fieldName.equalsIgnoreCase("accurace") || fieldName.equalsIgnoreCase("accuracy")) {//精度
                                    trailInfo.setAccuracy(reader.nextDouble());
                                } else if (fieldName.equalsIgnoreCase("time")) {//时间戳
                                    trailInfo.setTime(reader.nextLong());
                                } else if (fieldName.equalsIgnoreCase("sportState")) {//运动类型,0:运动中,1:运动暂停
                                    try {
                                        trailInfo.setSportState(reader.nextInt());
                                    } catch (Exception e) {
                                        trailInfo.setSportState(reader.nextBoolean() ? 1 : 0);//V1.0老版本
                                    }
                                } else if (fieldName.equalsIgnoreCase("speed")) {//速度,单位米/秒
                                    try {
                                        trailInfo.setSpeed(Float.parseFloat(reader.nextString()));
                                    } catch (Exception e) {
                                        trailInfo.setSpeed(reader.nextInt());//手表
                                        e.printStackTrace();
                                    }
                                } else if (fieldName.equalsIgnoreCase("altitude")) {//海拔,单位米
                                    trailInfo.setAltitude(reader.nextDouble());
                                } else if (fieldName.equalsIgnoreCase("bearing")) {//角度
                                    try {
                                        trailInfo.setBearing(Float.parseFloat(reader.nextString()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (fieldName.equalsIgnoreCase("lng")) {//经度
                                    trailInfo.setLng(reader.nextDouble());
                                } else if (fieldName.equalsIgnoreCase("type")) {//定位类型,0:GPS,1:LBS
                                    trailInfo.setType(reader.nextInt());
                                } else if (fieldName.equalsIgnoreCase("used")) {//是否使用(显示在地图上)V2.0.4版本开始废弃,true:是,false:否
                                    try {
                                        trailInfo.setUsed(reader.nextBoolean());
                                    } catch (Exception e) {
                                        trailInfo.setUsed(reader.nextInt() == 1);
                                    }
                                } else if (fieldName.equalsIgnoreCase("lat")) {//纬度
                                    trailInfo.setLat(reader.nextDouble());
                                } else if (fieldName.equalsIgnoreCase("color")) {//颜色
//                                    trailInfo.setColor(reader.nextInt());//不处理颜色
                                    reader.nextInt();
                                }
                            }
                        }
                        reader.endObject();
                        trailInfoList.add(trailInfo);
                    }//array
                    reader.endArray();
                }
            }//object
            reader.endObject();
            reader.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage();
            if (!TextUtils.isEmpty(error)) {
                Logger.i(Logger.DEBUG_TAG, "readRunTrailFile error:" + error);
            }
        }
        return trailInfoList;
    }

    /**
     * 从计步文件名获取对应的开始运动时间戳
     *
     * @param fileName 计步文件绝对路径名
     */
    public static long getStartTimeStampFromFileName(String fileName) {
        long startTimeStamp = 0;//开始时间戳,用于IOS时间转android时间戳
        if (TextUtils.isEmpty(fileName))
            return startTimeStamp;

        int start = fileName.indexOf("_");
        int end = fileName.indexOf(".");
        if (start != -1 && end != -1 && (start + 1) < end) {
            String startTimeStampStr = fileName.substring(start + 1, end);
            startTimeStamp = Long.parseLong(startTimeStampStr);
        }
        return startTimeStamp;
    }


    /**
     * 以流的方式解析跑步计步文件
     *
     * @param fileName   计步文件绝对路径名
     * @param dataSource 数据来源,0:表示乐享动app产生(android或ios),1:手表
     * @return 计步信息集合
     */
    public static List<RunStepInfo> readRunStepFile(String fileName, int dataSource) {
        Logger.i(Logger.DEBUG_TAG, "readRunStepFile fileName:" + fileName);
        List<RunStepInfo> runStepInfoList = new ArrayList<>();
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return runStepInfoList;
            }
            long startTimeStamp = getStartTimeStampFromFileName(fileName);//开始时间戳,用于IOS时间转android时间戳
            FileInputStream fileInputStream = new FileInputStream(file);
            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream, "UTF-8"));
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name != null && name.equals("array")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        RunStepInfo runStepInfo = new RunStepInfo();
                        while (reader.hasNext()) {
                            String fieldName = reader.nextName();
                            if (fieldName != null) {
                                if (fieldName.equalsIgnoreCase("time")) {//时间戳
                                    try {
                                        long time = reader.nextLong();
                                        if (dataSource == 1) {//手表产生的时间是时间戳
                                            //time = startTimeStamp + time;
                                        } else {
                                            if (time < 86400) {//ios时间不是时间戳,而是秒数
                                                time = startTimeStamp + time * 1000;
                                            }
                                        }
                                        runStepInfo.setTime(time);
                                    } catch (Exception e) {
                                        try {
                                            long time = Long.parseLong(reader.nextString());
                                            runStepInfo.setTime(time);//
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else if (fieldName.equalsIgnoreCase("step")) {//步数
                                    try {
                                        runStepInfo.setStep(reader.nextInt());
                                    } catch (Exception e) {//兼容ios
                                        try {
                                            int step = Integer.parseInt(reader.nextString());
                                            runStepInfo.setStep(step);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else if (fieldName.equalsIgnoreCase("distance")) {//距离,单位为米
                                    try {
                                        runStepInfo.setDistance(reader.nextInt());
                                    } catch (Exception e) {//兼容ios,ios单位为公里
                                        try {
                                            String distanceStr = reader.nextString();
                                            if (distanceStr != null && distanceStr.contains(".")) {
                                                long distance = (long) (Float.parseFloat(distanceStr) * 1000);//单位为米
                                                runStepInfo.setDistance(distance);
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else if (fieldName.equalsIgnoreCase("speed")) {//速度,ios特有,不需要用
                                    try {
                                        reader.nextString();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else if (fieldName.equalsIgnoreCase("bpm")) {//步频,ios特有,不需要用
                                    try {
                                        reader.nextString();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else {//未知字段
                                    try {
                                        reader.nextString();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        reader.endObject();
                        runStepInfoList.add(runStepInfo);
                    }//array
                    reader.endArray();
                } else if (name != null && name.equals("HeartRateArray")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String fieldName = reader.nextName();
                            if (fieldName != null) {
                                if (fieldName.equalsIgnoreCase("time")) {//时间戳
                                    try {
                                        reader.nextLong();
                                    } catch (Exception e) {
                                        try {
                                            reader.nextString();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else if (fieldName.equalsIgnoreCase("heartrate")) {
                                    try {
                                        reader.nextInt();
                                    } catch (Exception e) {//兼容ios
                                        try {
                                            reader.nextString();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        reader.endObject();
                    }//array
                    reader.endArray();
                }
            }//object
            reader.endObject();
            reader.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return runStepInfoList;
    }


    /**
     * 以流的方式解析心率文件
     *
     * @param fileName   心率文件绝对路径名
     * @param dataSource 数据来源,0:表示乐享动app产生(android或ios),1:手表
     */
    public static List<HeartRateJsonInfo> readHeartRateFile(String fileName, int dataSource) {
        List<HeartRateJsonInfo> heartRateJsonInfoList = new ArrayList<>();
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return heartRateJsonInfoList;
            }
            long startTimeStamp = getStartTimeStampFromFileName(fileName);//开始时间戳,用于IOS时间转android时间戳
            FileInputStream fileInputStream = new FileInputStream(file);
            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream, "UTF-8"));
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name != null && name.equals("HeartRateArray")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        HeartRateJsonInfo heartRateJsonInfo = new HeartRateJsonInfo();
                        while (reader.hasNext()) {
                            String fieldName = reader.nextName();
                            if (fieldName != null) {
                                if (fieldName.equalsIgnoreCase("time")) {//时间戳
                                    long time;
                                    try {
                                        time = reader.nextLong();
                                        if (dataSource == 1) {//手表产生的时间是时间戳
                                            //time = startTimeStamp + time;
                                        } else {
                                            if (time < 86400) {//ios时间不是时间戳,而是秒数
                                                time = startTimeStamp + time * 1000;
                                            }
                                        }
                                        heartRateJsonInfo.setTime(time);
                                    } catch (Exception e) {

                                        try {
                                            time = Long.parseLong(reader.nextString());
                                            heartRateJsonInfo.setTime(time);//
                                        } catch (Exception ex) {
                                            try {
                                                float aFloat = Float.parseFloat(reader.nextString());
                                                time = (long) (startTimeStamp + aFloat * 1000);
                                                heartRateJsonInfo.setTime(time);//
                                            } catch (Exception ex1) {
                                                ex1.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (fieldName.equalsIgnoreCase("heartrate")) {//心率
                                    try {
                                        heartRateJsonInfo.setHeartrate(reader.nextInt());
                                    } catch (Exception e) {//兼容ios
                                        try {
                                            int heartRate = Integer.parseInt(reader.nextString());
                                            heartRateJsonInfo.setHeartrate(heartRate);
                                        } catch (Exception ex) {
                                            try {
                                                float aFloat = Float.parseFloat(reader.nextString());
                                                heartRateJsonInfo.setHeartrate((int) aFloat);//
                                            } catch (Exception ex1) {
                                                ex1.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        reader.endObject();
                        heartRateJsonInfoList.add(heartRateJsonInfo);
                    }//array
                    reader.endArray();
                } else if (name != null && name.equals("array")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String fieldName = reader.nextName();
                            if (fieldName != null) {
                                if (fieldName.equalsIgnoreCase("time")) {//时间戳
                                    try {
                                        reader.nextLong();
                                    } catch (Exception e) {
                                        try {
                                            reader.nextString();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else if (fieldName.equalsIgnoreCase("step")) {//步数
                                    try {
                                        reader.nextInt();
                                    } catch (Exception e) {//兼容ios
                                        try {
                                            reader.nextString();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else if (fieldName.equalsIgnoreCase("distance")) {//距离,单位为米
                                    try {
                                        reader.nextInt();
                                    } catch (Exception e) {//兼容ios
                                        try {
                                            reader.nextString();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else if (fieldName.equalsIgnoreCase("speed")) {//速度,ios特有,不需要用
                                    try {
                                        reader.nextString();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else if (fieldName.equalsIgnoreCase("bpm")) {//步频,ios特有,不需要用
                                    try {
                                        reader.nextString();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else {//未知字段
                                    try {
                                        reader.nextString();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        reader.endObject();
                    }//array
                    reader.endArray();
                }
            }//object
            reader.endObject();
            reader.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return heartRateJsonInfoList;
    }


//    /**
//     * 以流的方式写跑步计步文件（不含心率）
//     *
//     * @param fileName        计步文件名绝对路径
//     * @param runStepInfoList 要写入计步文件的运动步数信息集合
//     * @return true:写文件成功,false:写文件失败
//     */
//    public static boolean writeRunStepFile(String fileName, List<RunStepInfo> runStepInfoList) {
//        if (TextUtils.isEmpty(fileName) || runStepInfoList == null) {
//            Logger.e(Logger.DEBUG_TAG, "writeRunStepFile fileName is null:" + (fileName == null) + " runStepInfoList is null:" + (runStepInfoList == null));
//            return false;
//        }
//        try {
//            FitmixUtil.getStepPath();//确保计步文件夹已创建
//            File file = new File(fileName);
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
//            writer.beginObject();
//            writer.name("array");
//            writer.beginArray();
//            Gson gson = new Gson();
//            for (RunStepInfo runStepInfo : runStepInfoList) {
//                gson.toJson(runStepInfo, RunStepInfo.class, writer);
//            }
//            writer.endArray();
//            writer.endObject();
//            writer.close();
//            fileOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (!TextUtils.isEmpty(e.getMessage())) {
//                Logger.e(Logger.DEBUG_TAG, "writeRunStepFile fail:" + e.getMessage());
//            }
//            return false;
//        }
//        Logger.i(Logger.DEBUG_TAG, "writeRunStepFile success thread:" + Thread.currentThread().getId());
//        return true;
//    }


//    /**
//     * 以流的方式写跑步计步文件
//     *
//     * @param fileName        计步文件名绝对路径
//     * @param runStepInfoList 要写入计步文件的运动步数信息集合
//     * @param heartRateList   要写入计步文件的心率信息集合
//     * @return true:写文件成功,false:写文件失败
//     */
//    public static boolean writeRunStepFile(String fileName, List<RunStepInfo> runStepInfoList, List<HeartRateJsonInfo> heartRateList) {
//        if (TextUtils.isEmpty(fileName) || runStepInfoList == null) {
//            Logger.e(Logger.DEBUG_TAG, "writeRunStepFile fileName is null:" + (fileName == null) + " runStepInfoList is null:" + (runStepInfoList == null));
//            return false;
//        }
//        try {
//            FitmixUtil.getStepPath();//确保计步文件夹已创建
//            File file = new File(fileName);
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
//            writer.beginObject();
//
//            writer.name("array");
//
//            writer.beginArray();
//            Gson gson = new Gson();
//            for (RunStepInfo runStepInfo : runStepInfoList) {
//                gson.toJson(runStepInfo, RunStepInfo.class, writer);
//            }
//            writer.endArray();
//
//            writer.name("HeartRateArray");
//            writer.beginArray();
//            for (HeartRateJsonInfo heart_rate_json_info : heartRateList) {
//                gson.toJson(heart_rate_json_info, HeartRateJsonInfo.class, writer);
//            }
//            writer.endArray();
//
//            writer.endObject();
//            writer.close();
//            fileOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (!TextUtils.isEmpty(e.getMessage())) {
//                Logger.e(Logger.DEBUG_TAG, "writeRunStepFile fail:" + e.getMessage());
//            }
//            return false;
//        }
//        Logger.i(Logger.DEBUG_TAG, "writeRunStepFile success thread:" + Thread.currentThread().getId());
//        return true;
//    }

    /**
     * 以流的方式写跑步轨迹文件
     *
     * @param fileName      轨迹文件名绝对路径
     * @param trailInfoList 要写入轨迹文件的运动轨迹信息集合
     * @return true:写文件成功,false:写文件失败
     */
    public static boolean writeRunTrailFile(String fileName, List<TrailInfo> trailInfoList) {
        if (TextUtils.isEmpty(fileName) || trailInfoList == null)
            return false;
        try {
            FitmixUtil.getTrailPath();//确保轨迹文件夹已创建
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
            writer.beginObject();
            writer.name("array");

            writer.beginArray();
            Gson gson = new Gson();
            for (TrailInfo trailInfo : trailInfoList) {
                gson.toJson(trailInfo, TrailInfo.class, writer);
            }
            writer.endArray();
            writer.endObject();
            writer.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    /**
//     * 以流的方式写跳绳计个文件
//     *
//     * @param fileName           计数文件名绝对路径
//     * @param skipNumberInfoList 要写入计数文件的跳绳信息集合
//     * @return true:写文件成功,false:写文件失败
//     */
//    public static boolean writeSkipNumberFile(String fileName, List<SkipNumberInfo> skipNumberInfoList) {
//        if (TextUtils.isEmpty(fileName) || skipNumberInfoList == null) {
//            return false;
//        }
//        try {
//            FitmixUtil.getSkipPath();//确保计步文件夹已创建
//            File file = new File(fileName);
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
//            writer.beginObject();
//            writer.name("array");
//            writer.beginArray();
//            Gson gson = new Gson();
//            List<HeartRateJsonInfo> heart_rate_json_list = new ArrayList<>();
//
//            for (SkipNumberInfo skipNumberInfo : skipNumberInfoList) {
//                heart_rate_json_list.add(new HeartRateJsonInfo(skipNumberInfo.getTime(), skipNumberInfo.getHeartRate()));
//                gson.toJson(skipNumberInfo, SkipNumberInfo.class, writer);
//            }
//
//            writer.endArray();
//
//            writer.name("HeartRateArray");
//            writer.beginArray();
//            for (HeartRateJsonInfo heart_rate_json_info : heart_rate_json_list) {
//                gson.toJson(heart_rate_json_info, HeartRateJsonInfo.class, writer);
//            }
//
//            writer.endArray();
//            writer.endObject();
//            writer.close();
//            fileOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.e(Logger.DEBUG_TAG, "writeSkipNumberFile fail:" + e.getMessage());
//            return false;
//        }
//        Logger.i(Logger.DEBUG_TAG, "writeSkipNumberFile success thread:" + Thread.currentThread().getId());
//        return true;
//    }

//    /**
//     * 以流的方式解析跳绳计数文件
//     *
//     * @param fileName 计数文件绝对路径名
//     * @return 计数信息集合
//     */
//    public static List<SkipNumberInfo> readSkipNumberFile(String fileName) {
//        List<SkipNumberInfo> skipNumberInfoList = new ArrayList<>();
//        try {
//            File file = new File(fileName);
//            if (!file.exists()) {
//                return skipNumberInfoList;
//            }
//            FileInputStream fileInputStream = new FileInputStream(file);
//            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream, "UTF-8"));
//            reader.beginObject();
//            while (reader.hasNext()) {
//                String name = reader.nextName();
//                if (name != null && name.equals("array")) {
//                    reader.beginArray();
//                    while (reader.hasNext()) {
//                        reader.beginObject();
//                        SkipNumberInfo skipNumberInfo = new SkipNumberInfo();
//                        while (reader.hasNext()) {
//                            String fieldName = reader.nextName();
//                            if (fieldName != null) {
//                                if (fieldName.equalsIgnoreCase("count")) {//个数
//                                    try {
//                                        skipNumberInfo.setCount(reader.nextInt());
//                                    } catch (Exception e) {//兼容ios
//                                        try {
//                                            int step = Integer.parseInt(reader.nextString());
//                                            skipNumberInfo.setCount(step);
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                } else if (fieldName.equalsIgnoreCase("time")) {//时间戳
//                                    try {
//                                        skipNumberInfo.setTime(reader.nextLong());
//                                    } catch (Exception e) {
//                                        try {
//                                            long time = Long.parseLong(reader.nextString());
//                                            skipNumberInfo.setTime(time);//
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                } else if (fieldName.equalsIgnoreCase("bpm")) {//跳频
//                                    try {
//                                        skipNumberInfo.setBpm(reader.nextInt());
//                                    } catch (Exception e) {
//                                        try {
//                                            int bpm = Integer.parseInt(reader.nextString());
//                                            skipNumberInfo.setBpm(bpm);//
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                } else if (fieldName.equalsIgnoreCase("heartRate")) {//心率
//                                    try {
//                                        skipNumberInfo.setHeartRate(reader.nextInt());
//                                    } catch (Exception e) {
//                                        try {
//                                            int heartRate = Integer.parseInt(reader.nextString());
//                                            skipNumberInfo.setHeartRate(heartRate);//
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        reader.endObject();
//                        skipNumberInfoList.add(skipNumberInfo);
//                    }//array
//                    reader.endArray();
//                }
//            }//object
//            reader.endObject();
//            reader.close();
//            fileInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return skipNumberInfoList;
//    }


//    /**
//     * 以流的方式解析手表气压文件
//     *
//     * @param fileName 手表气压文件绝对路径名
//     * @return 手表运动记录气压信息集合
//     */
//    public static List<WatchPressureLog> readWatchPressureFile(String fileName) {
//
//        List<WatchPressureLog> watchPressureLogs = new ArrayList<>();
//        try {
//            File file = new File(fileName);
//            if (!file.exists()) {
//                return watchPressureLogs;
//            }
//            FileInputStream fileInputStream = new FileInputStream(file);
//            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream, "UTF-8"));
//            reader.beginObject();
//            while (reader.hasNext()) {
//                String name = reader.nextName();
//                if (name != null && name.equals("array")) {
//                    reader.beginArray();
//                    while (reader.hasNext()) {
//                        reader.beginObject();
//                        WatchPressureLog watchPressureLog = new WatchPressureLog();
//                        while (reader.hasNext()) {
//                            String fieldName = reader.nextName();
//                            if (fieldName != null) {
//                                if (fieldName.equalsIgnoreCase("time")) {//时间戳
//                                    try {
//                                        watchPressureLog.setTime(reader.nextLong());
//                                    } catch (Exception e) {
//                                        try {
//                                            long time = Long.parseLong(reader.nextString());
//                                            watchPressureLog.setTime(time);//
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                } else if (fieldName.equalsIgnoreCase("pressure")) {//气压
//                                    try {
//                                        watchPressureLog.setPressure(reader.nextInt());
//                                    } catch (Exception e) {
//                                        try {
//                                            int heartRate = Integer.parseInt(reader.nextString());
//                                            watchPressureLog.setPressure(heartRate);//
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        reader.endObject();
//                        watchPressureLogs.add(watchPressureLog);
//                    }//array
//                    reader.endArray();
//                }
//            }//object
//            reader.endObject();
//            reader.close();
//            fileInputStream.close();
//        } catch (Exception e) {
//            Logger.e(Logger.DEBUG_TAG, "JsonHelper-->readWatchPressureFile fileName:" + fileName + ",error:" + e.getMessage());
//            e.printStackTrace();
//        }
//        return watchPressureLogs;
//    }

//    /**
//     * 以流的方式解析手表气温文件
//     *
//     * @param fileName 手表气温文件绝对路径名
//     * @return 手表运动记录气温信息集合
//     */
//    public static List<WatchTemperatureLog> readWatchTempFile(String fileName) {
//
//        List<WatchTemperatureLog> watchTemperatureLogs = new ArrayList<>();
//        try {
//            File file = new File(fileName);
//            if (!file.exists()) {
//                return watchTemperatureLogs;
//            }
//            FileInputStream fileInputStream = new FileInputStream(file);
//            JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream, "UTF-8"));
//            reader.beginObject();
//            while (reader.hasNext()) {
//                String name = reader.nextName();
//                if (name != null && name.equals("array")) {
//                    reader.beginArray();
//                    while (reader.hasNext()) {
//                        reader.beginObject();
//                        WatchTemperatureLog watchTemperatureLog = new WatchTemperatureLog();
//                        while (reader.hasNext()) {
//                            String fieldName = reader.nextName();
//                            if (fieldName != null) {
//                                if (fieldName.equalsIgnoreCase("time")) {//时间戳
//                                    try {
//                                        watchTemperatureLog.setTime(reader.nextLong());
//                                    } catch (Exception e) {
//                                        try {
//                                            long time = Long.parseLong(reader.nextString());
//                                            watchTemperatureLog.setTime(time);//
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                } else if (fieldName.equalsIgnoreCase("temperature")) {//气温
//                                    try {
//                                        watchTemperatureLog.setTemperature(reader.nextInt());
//                                    } catch (Exception e) {
//                                        try {
//                                            int heartRate = Integer.parseInt(reader.nextString());
//                                            watchTemperatureLog.setTemperature(heartRate);//
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        reader.endObject();
//                        watchTemperatureLogs.add(watchTemperatureLog);
//                    }//array
//                    reader.endArray();
//                }
//            }//object
//            reader.endObject();
//            reader.close();
//            fileInputStream.close();
//        } catch (Exception e) {
//            Logger.e(Logger.DEBUG_TAG, "JsonHelper-->readWatchTempFile fileName:" + fileName + ",error:" + e.getMessage());
//            e.printStackTrace();
//        }
//        return watchTemperatureLogs;
//    }

}
