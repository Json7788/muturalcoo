package com.fish.muturalcoo.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;


/**
 * @USER Administrator
 * @PACKAGE_NAME com.hesvit.sport.base
 * @DATE 2018/5/19 11:29
 * <p>
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  神兽保佑
 * 　　　　┃　　　┃  代码无bug
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 * @describe
 */

public class BaseFrameActivity extends AppCompatActivity {

    private static final String TAG="lifescycle";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除系统标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(TAG, "BaseActivity onCreate Invoke...");
        ManagerActivity.getInstance().addActivity(this);

    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "BaseActivity onStart Invoke...");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "BaseActivity onRestart Invoke...");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "BaseActivity onResume Invoke...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "BaseActivity onPause Invoke...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "BaseActivity onStop Invoke...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BaseActivity onDestroy Invoke...");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "BaseActivity onLowMemory Invoke...");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "BaseActivity onBackPressed Invoke...");
        ManagerActivity.getInstance().removeActivity(this);
    }
}
