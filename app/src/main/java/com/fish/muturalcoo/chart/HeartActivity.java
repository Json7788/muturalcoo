package com.fish.muturalcoo.chart;

import android.os.Bundle;

import com.fish.muturalcoo.R;
import com.fish.muturalcoo.base.BaseActivity;
import com.fish.muturalcoo.entity.HeartRatePoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HeartActivity extends BaseActivity {

    @Bind(R.id.heart_line_view)
    HeartLineView heartLineView;
    private ArrayList<Integer> chartList;
    private List<HeartRatePoint> heartRatePointsMax = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    @Override
    public void initData() {
        ArrayList<Integer> chartList = new ArrayList<>();
//初始化数据
//        for (int i = 0; i < 30; i++) {
//            int temp = 60 + new Random().nextInt(70);
//            chartList.add(temp);
//        }

        for (int i = 0; i < 30; i++) {
            int temp = 60 + new Random().nextInt(70);
//            chartList.add(temp);
            HeartRatePoint heartRatePoint=new HeartRatePoint(i,temp);
            if (i%3==0) {
                heartRatePoint=new HeartRatePoint(i,0);
            }
            heartRatePointsMax.add(heartRatePoint);
        }

        heartLineView.setData(heartRatePointsMax);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }
}
