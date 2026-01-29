package com.xr.traveltracker;

import android.app.Application;
import android.util.Log;
import com.amap.api.maps.MapsInitializer;

public class TravelTrackerApplication extends Application {

    private static final String TAG = "TravelTrackerApp";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // 高德地图隐私合规设置（必须在使用地图前调用）
            // 设置已经向用户展示隐私政策
            MapsInitializer.updatePrivacyShow(this, true, true);
            // 设置用户已经同意隐私政策
            MapsInitializer.updatePrivacyAgree(this, true);

            Log.d(TAG, "高德地图隐私合规设置完成");
        } catch (Exception e) {
            Log.e(TAG, "高德地图隐私合规设置失败: " + e.getMessage(), e);
        }
    }
}
