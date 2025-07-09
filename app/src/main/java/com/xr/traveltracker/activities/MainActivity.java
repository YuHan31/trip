package com.xr.traveltracker.activities;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xr.traveltracker.R;
import com.xr.traveltracker.database.DatabaseHelper;
import com.xr.traveltracker.fragments.AddFragment;
import com.xr.traveltracker.fragments.HomeFragment;
import com.xr.traveltracker.fragments.ProfileFragment;

import android.media.MediaPlayer;
import android.animation.ObjectAnimator;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout container;
    private Fragment currentFragment;
    private long lastClickTime = 0;
    private boolean isInitialProfileLoad = false;
    private static final long CLICK_DELAY = 500; // 防抖延迟时间(毫秒)
    private DatabaseHelper dbHelper;
    private MediaPlayer mediaPlayer;
    private ImageButton musicControlButton;
    private ObjectAnimator animator;
    private EditText etDestination, etStartDate, etEndDate, etDescription, etBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        container = findViewById(R.id.container);
        musicControlButton = findViewById(R.id.btn_music_control); // 获取播放按钮
        dbHelper = new DatabaseHelper(this); // 初始化数据库帮助类
        dbHelper.insertTravel("Paris", "2024-07-26", "Visited the Eiffel Tower");

        // 初始化音乐播放器
        initializeMediaPlayer();

        // 初始化旋转动画
        animator = ObjectAnimator.ofFloat(musicControlButton, "rotation", 0f, 360f);
        animator.setDuration(1000); // 旋转一圈的时间
        animator.setRepeatCount(ObjectAnimator.INFINITE); // 无限循环
        animator.setRepeatMode(ObjectAnimator.RESTART);

        // 设置播放按钮的点击事件
        musicControlButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                animator.pause(); // 暂停动画
            } else {
                mediaPlayer.start();
                animator.resume(); // 恢复动画
            }
        });

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
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("selectedItem", R.id.nav_home));
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                // 防快速点击处理
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
            HomeFragment fragment=new HomeFragment();
            Bundle args = new Bundle();
            args.putString("userId", getIntent().getStringExtra("userId"));
            args.putString("username", getIntent().getStringExtra("username"));
            args.putString("token", getIntent().getStringExtra("token"));
            fragment.setArguments(args);
            return fragment;
        } else if (itemId == R.id.nav_add) {
            AddFragment fragment = new AddFragment();
            Bundle args = new Bundle();
            args.putString("userId", getIntent().getStringExtra("userId"));
            args.putString("username", getIntent().getStringExtra("username"));
            args.putString("token", getIntent().getStringExtra("token"));
            fragment.setArguments(args);
            return fragment;
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

    private void initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

}