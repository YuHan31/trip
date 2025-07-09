package com.xr.traveltracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.xr.traveltracker.R;
import com.xr.traveltracker.activities.MainActivity;

public class SuccessFragment extends Fragment {
    private Button btnViewRecords;
    private Button btnBackHome;
    private String token;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);

        // 设置带返回键的Toolbar
        setupBackToolbar();

        Bundle args = getArguments();
        if (args != null) {
            token = args.getString("token");
            userId = args.getString("userId");
        }

        btnViewRecords = view.findViewById(R.id.btn_view_records);
        btnBackHome = view.findViewById(R.id.btn_back_home);

        btnViewRecords.setOnClickListener(v -> navigateToTravelRecords());
        btnBackHome.setOnClickListener(v -> navigateToHome());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 恢复默认Toolbar
        restoreMainToolbar();
    }

    private void setupBackToolbar() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBackToolbar();
        }
    }

    private void restoreMainToolbar() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).restoreMainToolbar();
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

    private void navigateToHome() {
        // 创建HomeFragment实例并传递参数
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("userId", userId);
        homeFragment.setArguments(args);

        // 导航到HomeFragment并清除回退栈
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();

        // 可选：选中底部导航栏的首页项
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomNavItem(R.id.nav_home);
        }
    }
}