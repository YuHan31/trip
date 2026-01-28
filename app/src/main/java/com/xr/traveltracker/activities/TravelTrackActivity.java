package com.xr.traveltracker.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.xr.traveltracker.R;
import com.xr.traveltracker.utils.ToolbarHelper;

public class TravelTrackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_track);

        // 使用ToolbarHelper设置Toolbar
        ToolbarHelper.setupToolbar(this, "旅行痕迹", true, v -> {
            finish();
        });
        // 初始化你的旅行痕迹页面
    }
}