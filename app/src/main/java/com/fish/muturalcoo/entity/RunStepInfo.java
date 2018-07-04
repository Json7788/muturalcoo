package com.fish.muturalcoo.entity;

/**
 * 运动步数信息
 * */
public class RunStepInfo {
	private long time;//时间戳
	private int step;//当前步数
	private long distance;//由步数累计出的距离,单位米


	public RunStepInfo() {
		clear();
	}

	public void clear() {
		time = 0;
		step = 0;
		distance = 0;
	}

    /**
     * 运动步数信息
     * @param time 时间戳
     * @param step 此时运动步数
     * @param distance 由步数累计出的距离,单位米
     * */
	public RunStepInfo(long time, int step, long distance) {
		this.time = time;
		this.step = step;
		this.distance = distance;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * 获取由步数累计出的距离,单位米
	 * */
	public long getDistance() {
		return distance;
	}

    /**
     * 设置由步数累计出的距离
     *
     * @param distance 由步数累计出的距离,单位米
     * */
	public void setDistance(long distance) {
		this.distance = distance;
	}

}

