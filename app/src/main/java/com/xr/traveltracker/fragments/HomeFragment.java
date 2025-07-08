package com.xr.traveltracker.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.xr.traveltracker.R;
import com.xr.traveltracker.adapters.MyPagerAdapter;
import com.xr.traveltracker.adapters.MyRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private Button musicControlButton;
    private List<Integer> images = Arrays.asList(
            R.drawable.ic_foreground1,
            R.drawable.ic_foreground2,
            R.drawable.ic_foreground3
    );
    private List<String> dynamicContent = Arrays.asList("Content 1", "Content 2", "Content 3");
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        recyclerView = view.findViewById(R.id.recyclerView);
        musicControlButton = view.findViewById(R.id.musicControlButton);

        // 初始化音乐播放器
        initializeMediaPlayer();

        // 设置音乐控制按钮的点击事件
        musicControlButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                musicControlButton.setText("Play");
            } else {
                mediaPlayer.start();
                musicControlButton.setText("Pause");
            }
        });

        // 复制图片列表以实现无缝循环
        List<Integer> duplicatedImages = new ArrayList<>();
        duplicatedImages.addAll(images);
        duplicatedImages.addAll(images);
        duplicatedImages.addAll(images);

        // 设置ViewPager2和RecyclerView的适配器
        viewPager.setAdapter(new MyPagerAdapter(duplicatedImages));
        recyclerView.setAdapter(new MyRecyclerViewAdapter(dynamicContent));

        return view;
    }

    private void initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.music);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true); // 设置为循环播放
            mediaPlayer.start(); // 开始播放音乐
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}