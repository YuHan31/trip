package com.xr.traveltracker.utils;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.xr.traveltracker.R;

public class ToolbarHelper {

    /**
     * 设置Toolbar
     * @param activity Activity实例
     * @param title 标题文字
     * @param showBack 是否显示返回按钮
     * @param onBackClickListener 返回按钮点击监听器
     */
    public static void setupToolbar(AppCompatActivity activity, String title,
                                    boolean showBack, View.OnClickListener onBackClickListener) {

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);

            // 设置标题
            TextView titleView = toolbar.findViewById(R.id.toolbar_title);
            if (titleView != null) {
                titleView.setText(title);
            }

            // 设置返回按钮
            View backButton = toolbar.findViewById(R.id.btn_back);
            if (backButton != null) {
                if (showBack) {
                    backButton.setVisibility(View.VISIBLE);
                    backButton.setOnClickListener(v -> {
                        if (onBackClickListener != null) {
                            onBackClickListener.onClick(v);
                        } else {
                            activity.onBackPressed();
                        }
                    });
                } else {
                    backButton.setVisibility(View.GONE);
                }
            }

            // 隐藏默认的ActionBar标题
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    /**
     * 设置右侧按钮
     */
    public static void setRightButton(AppCompatActivity activity, int iconResId,
                                      String contentDescription, View.OnClickListener listener) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ImageButton rightButton = toolbar.findViewById(R.id.btn_right);
            if (rightButton != null) {
                rightButton.setVisibility(View.VISIBLE);
                rightButton.setImageResource(iconResId);
                rightButton.setContentDescription(contentDescription);
                rightButton.setOnClickListener(listener);
            }
        }
    }

    /**
     * 更新标题
     */
    public static void updateTitle(AppCompatActivity activity, String newTitle) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            TextView titleView = toolbar.findViewById(R.id.toolbar_title);
            if (titleView != null) {
                titleView.setText(newTitle);
            }
        }
    }
}