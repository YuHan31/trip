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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.xr.traveltracker.R;
import com.xr.traveltracker.adapters.TravelRecordAdapter;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.TravelRecord;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        // 增强日志记录
        Log.d("TravelRecords", "Loading records for user: " + userId);
        Log.d("TravelRecords", "API Endpoint: " + getString(R.string.base_url) + "api/travel/user/" + userId);

        // 配置Gson日期格式（匹配数据库DATE类型）
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(Date.class, new DateDeserializer()) // 自定义日期解析器
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 打印请求头信息
        Log.d("TravelRecords", "Request Headers: Authorization: Bearer " + token);

        apiService.getUserTravelRecords("Bearer " + token, userId)
                .enqueue(new Callback<List<TravelRecord>>() {
                    @Override
                    public void onResponse(Call<List<TravelRecord>> call, Response<List<TravelRecord>> response) {
                        showLoading(false);

                        // 记录完整响应信息
                        Log.d("TravelRecords", "Response received. Code: " + response.code());

                        if (response.isSuccessful()) {
                            List<TravelRecord> records = response.body();
                            Log.d("TravelRecords", "Records count: " + (records != null ? records.size() : 0));
                            String rawResponse = new Gson().toJson(response.body());
                            Log.d("TravelRecords", "Raw response: " + rawResponse);
                            if (records != null && !records.isEmpty()) {
                                // 验证数据完整性
                                for (TravelRecord record : records) {
                                    if (record.getStartDate() == null || record.getEndDate() == null) {
                                        Log.w("TravelRecords", "Invalid date in record: " + record.getTravelId());
                                    }
                                }

                                travelRecords.clear();
                                travelRecords.addAll(records);
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d("TravelRecords", "No records found for user");
                                showEmptyView();
                            }
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TravelRecord>> call, Throwable t) {
                        showLoading(false);
                        Log.e("TravelRecords", "Network request failed", t);
                        Toast.makeText(getContext(),
                                "网络错误: " + (t.getMessage() != null ? t.getMessage() : "未知错误"),
                                Toast.LENGTH_SHORT).show();
                        showErrorView();
                    }
                });
    }

    // 提取错误处理逻辑到单独方法
    private void handleErrorResponse(Response<List<TravelRecord>> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "无错误详情";

            if (response.code() == 403) {
                Log.e("TravelRecords", "访问被拒绝: " + errorBody);
                Toast.makeText(getContext(), "错误：用户权限不足", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TravelRecords", "服务器错误: " + response.code() + " - " + errorBody);
                Toast.makeText(getContext(), "服务器错误: " + response.code(), Toast.LENGTH_SHORT).show();
            }

            // 记录原始错误响应
            Log.e("TravelRecords", "完整错误响应: " + errorBody);
        } catch (IOException e) {
            Log.e("TravelRecords", "解析错误响应时出错", e);
        }

        showErrorView();
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
    public class DateDeserializer implements JsonDeserializer<Date> {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return dateFormat.parse(json.getAsString());
            } catch (ParseException e) {
                Log.e("DateDeserializer", "Failed to parse date: " + json.getAsString(), e);
                return null;
            }
        }
    }
}
