package com.xr.traveltracker.activities;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xr.traveltracker.R;
import com.xr.traveltracker.fragments.AddFragment;
import com.xr.traveltracker.fragments.HomeFragment;
import com.xr.traveltracker.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout container;
    private Fragment currentFragment;
    private long lastClickTime = 0;
    private static final long CLICK_DELAY = 500; // 防抖延迟时间(毫秒)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        container = findViewById(R.id.container);

        // 检查Intent是否要求显示ProfileFragment
        boolean showProfile = getIntent().getBooleanExtra("showProfile", false);
        if (showProfile) {
            // 直接显示ProfileFragment并传递用户信息
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle args = new Bundle();
            args.putString("userId", getIntent().getStringExtra("userId"));
            args.putString("username", getIntent().getStringExtra("username"));
            args.putString("token", getIntent().getStringExtra("token"));
            profileFragment.setArguments(args);

            currentFragment = profileFragment;
            loadFragment(profileFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else {
            // 正常初始化
            initBottomNavigation(savedInstanceState);
        }
    }


    private void initBottomNavigation(Bundle savedInstanceState) {
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // 恢复或初始化Fragment
        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            loadFragment(currentFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            // 恢复保存的Fragment
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
            if (currentFragment == null) {
                currentFragment = new HomeFragment();
            }
            loadFragment(currentFragment);

            // 恢复选中的导航项
            int selectedItemId = savedInstanceState.getInt("selectedItem", R.id.nav_home);
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                // 防快速点击处理
                if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_DELAY) {
                    return false;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                // 获取对应的Fragment
                Fragment selectedFragment = getFragmentForMenuItem(item);

                // 如果Fragment不同则切换
                if (selectedFragment != null &&
                        (currentFragment == null || !selectedFragment.getClass().equals(currentFragment.getClass()))) {
                    currentFragment = selectedFragment;
                    loadFragment(selectedFragment);
                }
                return true;
            };

    private Fragment getFragmentForMenuItem(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            return new HomeFragment();
        } else if (itemId == R.id.nav_add) {
            return new AddFragment();
        } else if (itemId == R.id.nav_profile) {
            return new ProfileFragment();
        }
        return null;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out)
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前Fragment
        if (currentFragment != null && currentFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
        }
        // 保存选中的导航项
        outState.putInt("selectedItem", bottomNavigationView.getSelectedItemId());
    }
}