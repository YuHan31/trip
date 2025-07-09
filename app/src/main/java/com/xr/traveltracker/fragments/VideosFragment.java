package com.xr.traveltracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xr.traveltracker.R;
import com.xr.traveltracker.adapters.VideoAdapter;
import com.xr.traveltracker.models.VideoItem;
import com.xr.traveltracker.video.VideoPlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment {

    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private String token;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        setupToolbarWithBackButton();

        Bundle args = getArguments();
        if (args != null) {
            token = args.getString("token");
            userId = args.getString("userId");
        }

        videoRecyclerView = view.findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<VideoItem> videoItems = getSampleVideos();

        videoAdapter = new VideoAdapter(getContext(), videoItems, item -> {
            playVideo(item);
        });

        videoRecyclerView.setAdapter(videoAdapter);

        return view;
    }

    private void setupToolbarWithBackButton() {
        // 确保Activity是我们的MainActivity
        if (getActivity() instanceof com.xr.traveltracker.activities.MainActivity) {
            ((com.xr.traveltracker.activities.MainActivity) getActivity()).showBackToolbar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 恢复默认Toolbar
        if (getActivity() instanceof com.xr.traveltracker.activities.MainActivity) {
            ((com.xr.traveltracker.activities.MainActivity) getActivity()).restoreMainToolbar();
        }
    }

    private List<VideoItem> getSampleVideos() {
        List<VideoItem> videos = new ArrayList<>();
        videos.add(new VideoItem(
                "西藏自驾游",
                "00:43",
                "https://img.freepik.com/free-photo/beautiful-scenery-summit-mount-everest-covered-with-snow-white-clouds_181624-21317.jpg",
                "https://gossv-vcg.cfp.cn/videos/mts_videos/medium/VCG2219130950.mp4"));

        videos.add(new VideoItem(
                "云南大理风光",
                "4:38",
                "https://img.freepik.com/free-photo/beautiful-shot-crystal-clear-lake-snowy-mountain-base-during-sunny-day_181624-5459.jpg",
                "https://gossv-vcg.cfp.cn/videos/mts_videos/medium/VCG2236257507.mp4"));


        videos.add(new VideoItem(
                "天津城市夜景",
                "00:12",
                "https://vcg00.cfp.cn/creative/vcg/800/new/VCG211365271792.jpg",
                "https://gossv-vcg.cfp.cn/videos/mts_videos/medium/VCG2232794141.mp4"));
        return videos;
    }

    private void playVideo(VideoItem videoItem) {
        try {
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            intent.putExtra("VIDEO_URL", videoItem.getVideoUrl());
            intent.putExtra("VIDEO_TITLE", videoItem.getTitle());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "无法播放视频: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}