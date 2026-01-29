package com.xr.traveltracker.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xr.traveltracker.R;
import com.xr.traveltracker.models.City;
import com.xr.traveltracker.utils.CityDataProvider;
import com.xr.traveltracker.utils.CityPreferences;
import com.xr.traveltracker.utils.ToolbarHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TravelTrackActivity extends AppCompatActivity {

    private static final String TAG = "TravelTrackActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private MapView mapView;
    private AMap aMap;
    private TextView tvVisitedCount;
    private TextView tvTotalCount;
    private FloatingActionButton fabAddCity;
    private CityPreferences cityPreferences;
    private List<City> allCities;
    private List<Marker> markers = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_track);

        try {
            // 使用ToolbarHelper设置Toolbar
            ToolbarHelper.setupToolbar(this, "旅行痕迹", true, v -> {
                finish();
            });

            // 初始化视图
            mapView = findViewById(R.id.map_view);
            tvVisitedCount = findViewById(R.id.tv_visited_count);
            tvTotalCount = findViewById(R.id.tv_total_count);
            fabAddCity = findViewById(R.id.fab_add_city);

            // 初始化数据
            cityPreferences = new CityPreferences(this);
            allCities = CityDataProvider.getChinaCities();

            // 初始化地图
            if (mapView != null) {
                mapView.onCreate(savedInstanceState);
                initMap();
            } else {
                Log.e(TAG, "MapView is null");
                Toast.makeText(this, "地图初始化失败", Toast.LENGTH_SHORT).show();
            }

            // 设置添加城市按钮点击事件
            fabAddCity.setOnClickListener(v -> {
                Intent intent = new Intent(TravelTrackActivity.this, CitySelectionActivity.class);
                startActivity(intent);
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            Toast.makeText(this, "页面初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initMap() {
        try {
            if (aMap == null) {
                aMap = mapView.getMap();
            }

            if (aMap != null) {
                // 设置地图UI设置
                aMap.getUiSettings().setZoomControlsEnabled(false);
                aMap.getUiSettings().setScaleControlsEnabled(true);
                aMap.getUiSettings().setMyLocationButtonEnabled(false);

                // 设置地图初始位置（中国中心）
                LatLng centerChina = new LatLng(35.0, 105.0);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerChina, 4));

                // 延迟加载城市数据，等待地图完全初始化
                handler.postDelayed(() -> {
                    loadVisitedCities();
                }, 500);

                // 设置地图标记点击事件
                aMap.setOnMarkerClickListener(marker -> {
                    marker.showInfoWindow();
                    return true;
                });

                Log.d(TAG, "Map initialized successfully");
            } else {
                Log.e(TAG, "AMap is null after getMap()");
                Toast.makeText(this, "地图加载失败，请检查网络连接", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "initMap error: " + e.getMessage(), e);
            Toast.makeText(this, "地图初始化异常: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mapView != null) {
                mapView.onResume();
            }
            // 重新加载城市数据，以便从城市选择页面返回时更新地图
            if (aMap != null) {
                loadVisitedCities();
            }
        } catch (Exception e) {
            Log.e(TAG, "onResume error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mapView != null) {
                mapView.onPause();
            }
        } catch (Exception e) {
            Log.e(TAG, "onPause error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mapView != null) {
                mapView.onDestroy();
            }
        } catch (Exception e) {
            Log.e(TAG, "onDestroy error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (mapView != null) {
                mapView.onSaveInstanceState(outState);
            }
        } catch (Exception e) {
            Log.e(TAG, "onSaveInstanceState error: " + e.getMessage(), e);
        }
    }

    /**
     * 加载并显示已访问的城市
     */
    private void loadVisitedCities() {
        try {
            if (aMap == null) {
                Log.w(TAG, "AMap is null, cannot load cities");
                return;
            }

            // 清除现有标记
            aMap.clear();
            markers.clear();

            // 获取已访问的城市
            Set<String> visitedCityNames = cityPreferences.getVisitedCities();

            // 更新统计数据
            if (tvVisitedCount != null && tvTotalCount != null) {
                tvVisitedCount.setText(String.valueOf(visitedCityNames.size()));
                tvTotalCount.setText(String.valueOf(allCities.size()));
            }

            // 在地图上添加标记
            for (City city : allCities) {
                if (visitedCityNames.contains(city.getName())) {
                    addMarkerToMap(city);
                }
            }

            // 如果有已访问的城市，显示提示
            if (visitedCityNames.size() > 0) {
                Toast.makeText(this, "已点亮 " + visitedCityNames.size() + " 个城市", Toast.LENGTH_SHORT).show();
            }

            Log.d(TAG, "Loaded " + visitedCityNames.size() + " visited cities");
        } catch (Exception e) {
            Log.e(TAG, "loadVisitedCities error: " + e.getMessage(), e);
            Toast.makeText(this, "加载城市数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 在地图上添加城市标记
     */
    private void addMarkerToMap(City city) {
        try {
            if (aMap == null) {
                return;
            }

            LatLng latLng = new LatLng(city.getLatitude(), city.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(city.getName())
                    .snippet(city.getProvince())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            Marker marker = aMap.addMarker(markerOptions);
            if (marker != null) {
                markers.add(marker);
            }
        } catch (Exception e) {
            Log.e(TAG, "addMarkerToMap error for city " + city.getName() + ": " + e.getMessage(), e);
        }
    }
}
