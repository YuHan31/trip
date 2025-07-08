package com.xr.traveltracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xr.traveltracker.R;
import com.xr.traveltracker.adapters.TravelRecordAdapter;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.TravelRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TravelRecordsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TravelRecordAdapter adapter;
    private List<TravelRecord> travelRecords = new ArrayList<>();
    private String token;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_records, container, false);

        // 获取传递的参数
        Bundle args = getArguments();
        if (args != null) {
            token = args.getString("token");
            userId = args.getString("userId");
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TravelRecordAdapter(travelRecords);
        recyclerView.setAdapter(adapter);

        loadTravelRecords();

        return view;
    }

    private void loadTravelRecords() {
        showLoading(true);

        // 打印传递的参数
        Log.d("TravelRecords", "Token: " + token);
        Log.d("TravelRecords", "UserID from args: " + userId);
        Log.d("TravelRecords", "Request URL: " + getString(R.string.base_url) + "api/travel/user/" + userId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

//        // 打印即将发起的请求详情
//        Log.d("TravelRecords", "Preparing request with: ");
//        Log.d("TravelRecords", "Authorization: Bearer " + token);
//        Log.d("TravelRecords", "Path userId: " + userId);

        apiService.getUserTravelRecords("Bearer " + token, userId)
                .enqueue(new Callback<List<TravelRecord>>() {
                    @Override
                    public void onResponse(Call<List<TravelRecord>> call, Response<List<TravelRecord>> response) {
                        showLoading(false);

                        // 打印响应信息
                        Log.d("TravelRecords", "Response code: " + response.code());
                        Log.d("TravelRecords", "Response message: " + response.message());

                        try {
                            if (response.errorBody() != null) {
                                Log.e("TravelRecords", "Error response body: " + response.errorBody().string());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (response.isSuccessful()) {
                            Log.d("TravelRecords", "Response contains " + (response.body() != null ? response.body().size() : 0) + " records");
                            if (response.body() != null && !response.body().isEmpty()) {
                                travelRecords.clear();
                                travelRecords.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            } else {
                                showEmptyView();
                            }
                        } else if (response.code() == 403) {
                            Log.e("TravelRecords", "Access denied - userId mismatch");
                            Toast.makeText(getContext(), "错误：用户ID不匹配，无法访问记录", Toast.LENGTH_SHORT).show();
                            showErrorView();
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e("TravelRecords", "Error response: " + errorBody);
                                Toast.makeText(getContext(), "获取失败: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Log.e("TravelRecords", "Error parsing error body", e);
                                Toast.makeText(getContext(), "获取旅行记录失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TravelRecord>> call, Throwable t) {
                        showLoading(false);
                        Log.e("TravelRecords", "Network error", t);
                        Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        showErrorView();
                    }
                });
    }
    private void showLoading(boolean show) {
        // 实现显示/隐藏加载进度条的逻辑
        // 例如使用ProgressBar或SwipeRefreshLayout
    }

    private void showEmptyView() {
        // 显示"没有旅行记录"的提示
    }

    private void showErrorView() {
        // 显示错误提示和重试按钮
    }
}
