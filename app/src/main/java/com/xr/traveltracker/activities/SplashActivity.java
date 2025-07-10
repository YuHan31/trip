package com.xr.traveltracker.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xr.traveltracker.R;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final TextView welcomeText = findViewById(R.id.welcome_text);
        final TextView subtitleText = findViewById(R.id.subtitle_text);
        final LinearLayout textContainer = findViewById(R.id.text_container);

        // 初始设置
        textContainer.setTranslationY(20f);

        // 文字浮现动画
        welcomeText.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(500)
                .start();

        subtitleText.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(1000)
                .start();

        // 浮动效果动画
        ObjectAnimator waveAnimator = ObjectAnimator.ofFloat(
                textContainer,
                "translationY",
                60f, -60f, 40f, -40f, 20f, -20f, 10f, -10f, 0f
        );
        waveAnimator.setDuration(6000);
        waveAnimator.setInterpolator(new LinearInterpolator());
        waveAnimator.start();

        // 3.5秒后跳转到登录页面
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 3500);
    }
}

