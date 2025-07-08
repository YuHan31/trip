package com.xr.traveltracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xr.traveltracker.R;
import com.xr.traveltracker.adapters.TripMemoryAdapter;
import com.xr.traveltracker.adapters.UpcomingTripAdapter;
import com.xr.traveltracker.models.Trip;
import com.xr.traveltracker.models.TripMemory;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
//    private TextView greetingText;
//    private TextView statsText;
//    private RecyclerView upcomingTripsRecyclerView;
//    private RecyclerView memoriesRecyclerView;
//    private AuthManager authManager;
//    private TripDatabase tripDatabase;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        authManager = new AuthManager(requireContext());
//        tripDatabase = TripDatabase.getInstance(requireContext());
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home_travel_log, container, false);
//
//        greetingText = view.findViewById(R.id.greeting_text);
//        statsText = view.findViewById(R.id.stats_text);
//        upcomingTripsRecyclerView = view.findViewById(R.id.upcoming_trips_recycler);
//        memoriesRecyclerView = view.findViewById(R.id.memories_recycler);
//
//        // 根据当前用户显示个性化信息
//        displayPersonalizedContent();
//
//        return view;
//    }
//
//    private void displayPersonalizedContent() {
//        int userId = authManager.getCurrentUserId();
//        String username = authManager.getCurrentUsername();
//
//        // 设置个性化欢迎语
//        greetingText.setText(String.format("欢迎回来，%s！", username));
//
//        // 从数据库获取用户特定的数据
//        new Thread(() -> {
//            // 获取统计数据
//            int tripCount = tripDatabase.tripDao().getTripCount(userId);
//            int cityCount = tripDatabase.tripDao().getVisitedCityCount(userId);
//            int totalDays = tripDatabase.tripDao().getTotalTravelDays(userId);
//
//            // 获取即将到来的旅行
//            List<Trip> upcomingTrips = tripDatabase.tripDao().getUpcomingTrips(userId, System.currentTimeMillis());
//
//            // 获取旅行回忆
//            List<TripMemory> memories = tripDatabase.tripDao().getPastTrips(userId, System.currentTimeMillis());
//
//            // 更新UI必须在主线程
//            requireActivity().runOnUiThread(() -> {
//                // 设置统计信息
//                statsText.setText(String.format(
//                        "已记录旅行: %d次\n去过城市: %d个\n旅行天数: %d天",
//                        tripCount, cityCount, totalDays
//                ));
//
//                // 设置即将到来的旅行
//                UpcomingTripAdapter upcomingAdapter = new UpcomingTripAdapter(requireContext(), upcomingTrips);
//                upcomingTripsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//                upcomingTripsRecyclerView.setAdapter(upcomingAdapter);
//
//                // 设置旅行回忆
//                TripMemoryAdapter memoryAdapter = new TripMemoryAdapter(requireContext(), memories);
//                memoriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//                memoriesRecyclerView.setAdapter(memoryAdapter);
//            });
//        }).start();
//    }
}