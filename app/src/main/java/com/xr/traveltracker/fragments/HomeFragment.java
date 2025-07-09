package com.xr.traveltracker.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.xr.traveltracker.R;
import com.xr.traveltracker.adapters.MyPagerAdapter;
import com.xr.traveltracker.adapters.MyRecyclerViewAdapter;
import com.xr.traveltracker.fragments.TravelRecordsFragment;

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
    private List<String> dynamicContent = Arrays.asList("内容1", "内容2", "内容3");
    private MediaPlayer mediaPlayer;
    private boolean isUserPlaying = false;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private static final int AUTO_PLAY_DELAY = 3000;

    private MaterialCardView cardViewRecords;
    private MaterialCardView cardViewVideos;
    private String token;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        recyclerView = view.findViewById(R.id.recyclerView);
        cardViewRecords = view.findViewById(R.id.cardViewRecords);
        cardViewVideos = view.findViewById(R.id.cardViewVideos);

        initializeMediaPlayer();

        List<Integer> duplicatedImages = new ArrayList<>();
        duplicatedImages.addAll(images);
        duplicatedImages.addAll(images);
        duplicatedImages.addAll(images);

        viewPager.setAdapter(new MyPagerAdapter(duplicatedImages));
        recyclerView.setAdapter(new MyRecyclerViewAdapter(dynamicContent));

        startAutoPlay();

        Bundle args = getArguments();
        if (args != null) {
            token = args.getString("token");
            userId = args.getString("userId");
        }

        cardViewRecords.setOnClickListener(v -> {
            cardViewRecords.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                    .withEndAction(() -> cardViewRecords.animate().scaleX(1f).scaleY(1f).setDuration(100));
            navigateToTravelRecords();
        });

        cardViewVideos.setOnClickListener(v -> {
            cardViewVideos.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                    .withEndAction(() -> cardViewVideos.animate().scaleX(1f).scaleY(1f).setDuration(100));
            navigateToVideos();
        });

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
        stopAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isUserPlaying = true;
        } else {
            isUserPlaying = false;
        }
        stopAutoPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null && isUserPlaying) {
            mediaPlayer.start();
        }
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

    private void navigateToTravelRecords() {
        TravelRecordsFragment fragment = new TravelRecordsFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("userId", userId);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToVideos() {
        // 替换为你的视频Fragment
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("userId", userId);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}