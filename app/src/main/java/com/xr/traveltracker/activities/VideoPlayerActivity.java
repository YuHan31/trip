package com.xr.traveltracker.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.xr.traveltracker.R;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // 获取传递的视频数据
        String videoUrl = getIntent().getStringExtra("VIDEO_URL");
        String videoTitle = getIntent().getStringExtra("VIDEO_TITLE");

        // 初始化视图
        VideoView videoView = findViewById(R.id.videoView);
        TextView titleTextView = findViewById(R.id.titleTextView);

        ImageButton btnBack = findViewById(R.id.btn_back);


        // 设置标题
        titleTextView.setText(videoTitle);


        // 设置视频播放器
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.start();

        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());



    }
}