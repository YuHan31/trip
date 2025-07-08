package com.xr.traveltracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.xr.traveltracker.R;

public class SuccessFragment extends Fragment {
    private Button btnViewRecords;
    private String token;
    private String  userId; // 保持为int类型

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);

        Bundle args = getArguments();
        if (args != null) {
            token = args.getString("token");
            userId = args.getString("userId"); // 直接获取int类型的userId
        }

        btnViewRecords = view.findViewById(R.id.btn_view_records);
        btnViewRecords.setOnClickListener(v -> navigateToTravelRecords());

        return view;
    }

    private void navigateToTravelRecords() {
        TravelRecordsFragment fragment = new TravelRecordsFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("userId", userId); // 传递int类型的userId
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}