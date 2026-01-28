package com.xr.traveltracker.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.xr.traveltracker.R;
import com.xr.traveltracker.utils.ToolbarHelper;

public class WeatherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 使用ToolbarHelper设置Toolbar
        ToolbarHelper.setupToolbar(this, "天气查询", true, v -> {
            finish();
        });
        // 初始化你的天气页面
    }
}