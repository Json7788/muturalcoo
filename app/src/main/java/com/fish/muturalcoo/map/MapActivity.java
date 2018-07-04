package com.fish.muturalcoo.map;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ScreenUtils;
import com.fish.muturalcoo.R;
import com.fish.muturalcoo.base.BaseActivity;
import com.fish.muturalcoo.entity.TrailInfo;
import com.fish.muturalcoo.utils.Config;
import com.fish.muturalcoo.utils.JsonHelper;
import com.fish.muturalcoo.utils.ThreadManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapActivity extends BaseActivity {


    @Bind(R.id.tv_position)
    TextView tvPosition;

    @Bind(R.id.bmapView)
    MapView bmapView;
    private LocationClient locationClient;

    private boolean isFirstLocate = true;
    private BaiduMap baiduMap;
    private List<TrailInfo> mTrailInfoList;//轨迹点信息集合
    /**
     * 本地轨迹文件路径
     */
    private String trailFilename;
    private String mUid="-1";
    private String mStartTime="1526384447000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        initView();
        initListener();
    }

    @Override
    public void initData() {
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        trailFilename = Config.PATH_DOWN_TRAIL + mUid + "_" + mStartTime + ".json";
        SDKInitializer.initialize(getApplicationContext());



    }

    @Override
    public void initView() {
        baiduMap = bmapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        requestLocation();

        ThreadManager.executeOnFileThread(new Runnable() {
            @Override
            public void run() {
                setTrailData();
            }
        });
    }

    /**
     * 请求位置信息
     */
    private void requestLocation() {
        iniLocation();
        locationClient.start();
    }

    private void iniLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
//        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        locationClient.setLocOption(option);
    }

    @Override
    public void initListener() {

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation || bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                navigateTo(bdLocation);

            }

            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
            currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
            currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
            currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
            currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
            currentPosition.append("街道：").append(bdLocation.getStreet()).append("\n");
            currentPosition.append("定位方式：");
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            } else if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");

            }
            tvPosition.setText(currentPosition);
        }


    }

    private void navigateTo(BDLocation bdLocation) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }

        MyLocationData.Builder builder=new MyLocationData.Builder();
        builder.latitude(bdLocation.getLatitude());
        builder.longitude(bdLocation.getLongitude());
        MyLocationData locationData=builder.build();
        baiduMap.setMyLocationData(locationData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        bmapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bmapView.onPause();
    }

    /**
     * 设置地图轨迹数据
     */
    private void setTrailData() {
        if (false)
            return;
        if (TextUtils.isEmpty(trailFilename))
            return;

        mTrailInfoList = JsonHelper.readRunTrailFile(trailFilename);
        if (mTrailInfoList == null)
            return;
        setTrail(mTrailInfoList);
//        if (mCurrentFragment == 0) {
//            if (trackFragment != null) {
//                trackFragment.setProgressTrailVisible(false);
//                trackFragment.setMapTrail(mTrailInfoList);
//            }
//        }
    }


    /**
     * 在地图上设置轨迹
     *
     * @param trailInfoList
     */
    private void setTrail(final List<TrailInfo> trailInfoList) {
        if (trailInfoList == null)
            return;

        final int mapWidth = ScreenUtils.getScreenWidth();
//        if (defaultCamera == null) {
//            defaultCamera = new TrailCameraInfo();
//        }
//        if (!SettingsHelper.getBoolean(Config.ISABROAD,false)) {
//            if (getMapManager() == null) mapManager = new AMapHelper(mapView, false);
//            getMapManager().setTrailOfMap(trailInfoList, mapWidth, false, defaultCamera, new AMapHelper.MapCameraChangeFinish() {
//                @Override
//                public void onFinish() {
//                    if (cameraLocated)
//                        return;
//                    if (mapManager != null) {
//                        getMapManager().showTrail(true);
//                    }
//
//                    setProgressTrailVisible(false);
//                    if (mapManager != null) {
//                        mapManager.showKmFlag(true);
//                    }
//
//                    //准备好轨迹截图分享对应的镜头信息
//                    if (screenShotCamera == null) {
//                        screenShotCamera = new TrailCameraInfo();
//                    }
//                    if (defaultCamera != null) {
//                        screenShotCamera.setTrailBounds(defaultCamera.getTrailBounds());
//                        if (defaultCamera.getTrailBounds() != null) {
//                            cameraLocated = true;//确保地图范围已准备好
//                        }
//                    }
//                    if (mapView != null) {
//                        int mapWidth = mapView.getWidth();
//                        int mapHeight = mapView.getHeight();
//
//                        int padding = (int) (0.096f * mapWidth);//左边距或右边距
//                        int paddingTop = (int) (0.16f * mapWidth + (mapHeight - 0.954f * mapWidth));//(int) (padding + (mapHeight - 0.954f * mapWidth));
//                        int paddingBottom = (int) (0.346f * mapWidth);//(int) (0.368f * mapWidth);
//
//                        screenShotCamera.setPaddingLeft(padding);
//                        screenShotCamera.setPaddingRight(padding);
//                        screenShotCamera.setPaddingTop(mapHeight > mapWidth ? paddingTop : padding);
//                        screenShotCamera.setPaddingBottom(paddingBottom);
//                    }
//                }
//            });
//        } else {
//            if (getGoogleMapManager() == null)
//                googleMapManager = new GoogleMapHelper(googleMapView, false);
//            getGoogleMapManager().setGoogleMapReady(new GoogleMapHelper.GoogleMapReady() {
//                @Override
//                public void onReady() {
//                    getGoogleMapManager().setTrailOfMap(trailInfoList, true, true);
//                }
//            });
//        }

    }
}
