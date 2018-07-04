package com.fish.muturalcoo.entity;

/**
 * 上传计步文件中心率arraylist的bean
 */
public class HeartRateJsonInfo {
    private long time;//时间戳
    private int heartrate;//心率值

    public HeartRateJsonInfo() {
    }

    public HeartRateJsonInfo(long time, int heartrate) {
        this.time = time;
        this.heartrate = heartrate;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(int heartrate) {
        this.heartrate = heartrate;
    }
}
