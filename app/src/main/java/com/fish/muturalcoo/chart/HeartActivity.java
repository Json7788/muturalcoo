package com.fish.muturalcoo.chart;

import android.os.Bundle;

import com.fish.muturalcoo.R;
import com.fish.muturalcoo.base.BaseActivity;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HeartActivity extends BaseActivity {

    @Bind(R.id.heart_line_view)
    HeartLineView heartLineView;
    private ArrayList<Integer> chartList;

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
        for (int i = 0; i < 30; i++) {
            int temp = 60 + new Random().nextInt(70);
            chartList.add(temp);
        }
        heartLineView.setData(chartList);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }
}
