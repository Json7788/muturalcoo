package com.fish.muturalcoo.chart;

import android.os.Bundle;

import com.fish.muturalcoo.R;
import com.fish.muturalcoo.base.BaseActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChartActivity extends BaseActivity {


    @Bind(R.id.pillar_h)
    HPillarView pillarH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        initData();
        initView();
        initListener();
    }


    @Override
    public void initData() {
        ArrayList<Integer> chartList=new ArrayList<>();
//初始化数据
        chartList.add(79800);
        chartList.add(58032);
        chartList.add(36000);
        chartList.add(28800);
        chartList.add(23400);
//        chartList.add(2999);

//设置数据（一步搞定）
        pillarH.setData(chartList);

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }
}
