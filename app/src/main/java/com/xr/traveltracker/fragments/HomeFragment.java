package com.xr.traveltracker.fragments;

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

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private final int delay = 3000; // 轮播间隔时间（毫秒）

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        recyclerView = view.findViewById(R.id.recyclerView);

        // 复制图片列表以实现无缝循环
        List<Integer> duplicatedImages = new ArrayList<>();
        duplicatedImages.addAll(images);
        duplicatedImages.addAll(images);
        duplicatedImages.addAll(images);

        // Setup ViewPager2 with images
        viewPager.setAdapter(new MyPagerAdapter(duplicatedImages));
        viewPager.setOffscreenPageLimit(3); // 设置缓存页面数量
        viewPager.setUserInputEnabled(true); // 启用手动滑动

        // 设置页面切换监听器
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // 当滑动到最后一张图片时，自动跳转到第一张
                if (position == duplicatedImages.size() - 1) {
                    viewPager.setCurrentItem(0, false);
                }
            }
        });

        // 启动自动轮播
        startAutoSlide();

        // Setup RecyclerView with dynamic content
        recyclerView.setAdapter(new MyRecyclerViewAdapter(dynamicContent));
    }

    private void startAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int totalItems = viewPager.getAdapter().getItemCount();
                int nextPage = (currentItem + 1) % totalItems;

                // 如果当前页是最后一张，跳转到第一张
                if (nextPage == 0) {
                    viewPager.setCurrentItem(nextPage, false);
                } else {
                    viewPager.setCurrentItem(nextPage, true);
                }

                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // 停止自动轮播
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay); // 恢复自动轮播
    }
}