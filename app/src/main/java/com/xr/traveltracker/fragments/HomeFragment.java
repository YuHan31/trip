package com.xr.traveltracker.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private List<Integer> images = Arrays.asList(
            R.drawable.ic_foreground1,
            R.drawable.ic_foreground2,
            R.drawable.ic_foreground3
    );
    private List<String> dynamicContent = Arrays.asList("Content 1", "Content 2", "Content 3");
    private MediaPlayer mediaPlayer;
    private boolean isUserPlaying = false;

    // 自动播放相关变量
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private static final int AUTO_PLAY_DELAY = 3000; // 自动播放间隔时间（毫秒）

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        recyclerView = view.findViewById(R.id.recyclerView);

        // 初始化音乐播放器
        initializeMediaPlayer();

        // 复制图片列表以实现无缝循环
        List<Integer> duplicatedImages = new ArrayList<>();
        duplicatedImages.addAll(images);
        duplicatedImages.addAll(images);
        duplicatedImages.addAll(images);

        // 设置ViewPager2和RecyclerView的适配器
        viewPager.setAdapter(new MyPagerAdapter(duplicatedImages));
        recyclerView.setAdapter(new MyRecyclerViewAdapter(dynamicContent));

        // 启动自动播放
        startAutoPlay();

        return view;
    }

    private void initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.music);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
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
        // 停止自动播放
        stopAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        // 暂停自动播放
        stopAutoPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null && isUserPlaying) { // 只有用户之前播放了音乐，才会自动播放
            mediaPlayer.start();
        }
        // 恢复自动播放
        startAutoPlay();
    }

    private void startAutoPlay() {
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (viewPager != null) {
                        int currentItem = viewPager.getCurrentItem();
                        int totalItems = viewPager.getAdapter().getItemCount();
                        int nextItem = (currentItem + 1) % totalItems;
                        viewPager.setCurrentItem(nextItem, true);
                    }
                    handler.postDelayed(this, AUTO_PLAY_DELAY);
                }
            };
            handler.postDelayed(runnable, AUTO_PLAY_DELAY);
        }
    }

    private void stopAutoPlay() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
    }
}