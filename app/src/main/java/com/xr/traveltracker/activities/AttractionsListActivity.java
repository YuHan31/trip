package com.xr.traveltracker.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.xr.traveltracker.R;
import com.xr.traveltracker.utils.ToolbarHelper;

public class AttractionsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions_list);

        // 使用ToolbarHelper设置Toolbar
        ToolbarHelper.setupToolbar(this, "热门景点", true, v -> {
            // 返回按钮点击事件
            finish();
        });

        // 初始化你的景点列表页面
        // 这里可以添加你的景点列表逻辑
    }
}