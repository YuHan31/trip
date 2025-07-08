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
    private static final long CLICK_DELAY = 500;
    private boolean isInitialProfileLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        container = findViewById(R.id.container);

        // 检查是否需要初始显示ProfileFragment
        boolean showProfile = getIntent().getBooleanExtra("showProfile", false);
        if (showProfile) {
            isInitialProfileLoad = true;
            loadInitialProfileFragment();
        } else {
            initBottomNavigation(savedInstanceState);
        }
    }

    private void loadInitialProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId", getIntent().getStringExtra("userId"));
        args.putString("username", getIntent().getStringExtra("username"));
        args.putString("token", getIntent().getStringExtra("token"));
        profileFragment.setArguments(args);

        currentFragment = profileFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, profileFragment)
                .commit();

        // 初始化导航栏（但不触发切换事件）
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        isInitialProfileLoad = false;
    }

    private void initBottomNavigation(Bundle savedInstanceState) {
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            loadFragment(currentFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
            if (currentFragment == null) {
                currentFragment = new HomeFragment();
            }
            loadFragment(currentFragment);
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("selectedItem", R.id.nav_home));
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_DELAY) {
                    return false;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                // 如果是初始加载ProfileFragment，不处理第一次导航点击
                if (isInitialProfileLoad) {
                    return true;
                }

                Fragment selectedFragment = getFragmentForMenuItem(item);
                if (selectedFragment != null && !isSameFragment(currentFragment, selectedFragment)) {
                    currentFragment = selectedFragment;
                    loadFragment(selectedFragment);
                }
                return true;
            };

    private boolean isSameFragment(Fragment current, Fragment newFragment) {
        if (current == null || newFragment == null) return false;
        return current.getClass().equals(newFragment.getClass());
    }

    private Fragment getFragmentForMenuItem(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            return new HomeFragment();
        } else if (itemId == R.id.nav_add) {
            return new AddFragment();
        } else if (itemId == R.id.nav_profile) {
            // 如果是ProfileFragment，检查是否需要传递参数
            if (getIntent().hasExtra("userId")) {
                ProfileFragment fragment = new ProfileFragment();
                Bundle args = new Bundle();
                args.putString("userId", getIntent().getStringExtra("userId"));
                args.putString("username", getIntent().getStringExtra("username"));
                args.putString("token", getIntent().getStringExtra("token"));
                fragment.setArguments(args);
                return fragment;
            }
            return new ProfileFragment();
        }
        return null;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFragment != null && currentFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
        }
        outState.putInt("selectedItem", bottomNavigationView.getSelectedItemId());
    }
}