package com.fish.muturalcoo.map;

import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.fish.muturalcoo.R;
import com.fish.muturalcoo.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapActivity extends BaseActivity {


    @Bind(R.id.tv_position)
    TextView tvPosition;
    private LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        initData();
        initView();
        initListener();
    }

    @Override
    public void initData() {
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
    }

    @Override
    public void initView() {
        requestLocation();

    }

    /**
     * 请求位置信息
     */
    private void requestLocation() {
        locationClient.start();
    }

    @Override
    public void initListener() {

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
            currentPosition.append("定位方式：");
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            } else if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");

            }
            tvPosition.setText(currentPosition);
        }


    }
}
