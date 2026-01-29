package com.xr.traveltracker.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.xr.traveltracker.R;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.TravelMedia;
import com.xr.traveltracker.models.TravelRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TravelDetailActivity extends AppCompatActivity {

    private static final String TAG = "TravelDetailActivity";
    private String token;
    private int travelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);

        // 获取传递的参数
        travelId = getIntent().getIntExtra("travel_id", -1);
        token = getIntent().getStringExtra("token");

        if (travelId == -1 || token == null) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadTravelRecordDetails();
    }

    private void initializeViews() {
        // 初始化返回按钮
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> supportFinishAfterTransition());

        // 设置状态栏颜色
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void loadTravelRecordDetails() {
        showLoading(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 先获取原始响应，以便调试
        Call<TravelRecord> call = apiService.getTravelRecordDetails("Bearer " + token, travelId);

        // 添加拦截器来查看原始响应
        Log.d(TAG, "正在请求旅行详情: " + travelId);

        call.enqueue(new Callback<TravelRecord>() {
            @Override
            public void onResponse(Call<TravelRecord> call, Response<TravelRecord> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    // 打印原始响应
                    try {
                        String rawResponse = new Gson().toJson(response.body());
                        Log.d(TAG, "原始响应 JSON: " + rawResponse);
                    } catch (Exception e) {
                        Log.e(TAG, "解析响应失败", e);
                    }

                    // 打印所有字段
                    TravelRecord record = response.body();
                    printAllFields(record);

                    displayRecordDetails(record);
                } else {
                    // 打印错误响应
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "无错误详情";
                        Log.e(TAG, "错误响应: " + response.code() + " - " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "读取错误响应失败", e);
                    }
                    showError("获取详情失败: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TravelRecord> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "网络请求失败: ", t);
                showError("网络错误: " + t.getMessage());
            }
        });
    }

    private void printAllFields(TravelRecord record) {
        try {
            Log.d(TAG, "=== TravelRecord 所有字段 ===");
            Class<?> clazz = record.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(record);
                if (value != null) {
                    Log.d(TAG, field.getName() + ": " + value.toString());
                } else {
                    Log.d(TAG, field.getName() + ": null");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "反射获取字段失败", e);
        }
    }

    private void displayRecordDetails(TravelRecord record) {
        TextView tvDestination = findViewById(R.id.tv_detail_destination);
        TextView tvDateRange = findViewById(R.id.tv_detail_date_range);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        TextView tvBudget = findViewById(R.id.tv_detail_budget);
        LinearLayout mediaContainer = findViewById(R.id.media_container);

        // 设置基本信息
        tvDestination.setText(record.getDestination());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        String startDate = record.getStartDate() != null ?
                dateFormat.format(record.getStartDate()) : "未设置";
        String endDate = record.getEndDate() != null ?
                dateFormat.format(record.getEndDate()) : "未设置";
        tvDateRange.setText(String.format("%s - %s", startDate, endDate));

        tvDescription.setText(record.getDescription() != null ?
                record.getDescription() : "暂无描述");
        tvBudget.setText(String.format(Locale.getDefault(), "¥%.2f", record.getBudget()));

        // 加载并显示媒体
        // 尝试多种方式获取 media
        List<TravelMedia> mediaList = extractMediaFromRecord(record);

        if (mediaList == null || mediaList.isEmpty()) {
            Log.d(TAG, "没有媒体文件");
            findViewById(R.id.media_container).setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "找到 " + mediaList.size() + " 个媒体文件");
        mediaContainer.removeAllViews(); // 清除现有视图
        mediaContainer.setVisibility(View.VISIBLE);

        for (TravelMedia media : mediaList) {
            MaterialCardView cardView = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 16);
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
            cardView.setCardElevation(getResources().getDimension(R.dimen.card_elevation));

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.media_height)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);

            // 使用Glide加载图片
            // 构建完整的图片URL
            String imageUrl = media.getMediaUrl();
            Log.d(TAG, "原始图片URL: " + imageUrl);

            if (imageUrl != null && !imageUrl.startsWith("http")) {
                // 如果是相对路径，拼接base URL
                String baseUrl = getString(R.string.base_url);
                // 确保baseUrl不以/结尾
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }
                // 确保imageUrl以/开头
                if (!imageUrl.startsWith("/")) {
                    imageUrl = "/" + imageUrl;
                }
                imageUrl = baseUrl + imageUrl;
                Log.d(TAG, "拼接后的完整URL: " + imageUrl);
            }

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "图片加载失败: " + model, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            Log.d(TAG, "图片加载成功: " + model + ", 数据源: " + dataSource);
                            return false;
                        }
                    })
                    .into(imageView);

            cardView.addView(imageView);
            mediaContainer.addView(cardView);
        }
    }

    private List<TravelMedia> extractMediaFromRecord(TravelRecord record) {
        List<TravelMedia> mediaList = new ArrayList<>();

        // 方法1: 直接获取 media 字段
        try {
            Field mediaField = TravelRecord.class.getDeclaredField("media");
            mediaField.setAccessible(true);
            Object mediaValue = mediaField.get(record);

            if (mediaValue != null) {
                Log.d(TAG, "找到 media 字段: " + mediaValue);
                if (mediaValue instanceof List) {
                    mediaList = (List<TravelMedia>) mediaValue;
                    Log.d(TAG, "成功获取 mediaList, 大小: " + mediaList.size());
                }
            } else {
                Log.d(TAG, "media 字段为 null");
            }
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "TravelRecord 没有 media 字段");
        } catch (Exception e) {
            Log.e(TAG, "获取 media 字段失败", e);
        }

        // 如果没找到，尝试查看 TravelRecord 中是否有其他可能包含 media 的字段
        if (mediaList.isEmpty()) {
            mediaList = findMediaInFields(record);
        }

        return mediaList;
    }

    private List<TravelMedia> findMediaInFields(TravelRecord record) {
        List<TravelMedia> result = new ArrayList<>();

        try {
            Class<?> clazz = record.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(record);

                if (value != null) {
                    String fieldName = field.getName();
                    String valueStr = value.toString();
                    Log.d(TAG, "检查字段: " + fieldName + " = " + valueStr);

                    // 如果字段名包含"media"，尝试解析
                    if (fieldName.toLowerCase().contains("media")) {
                        if (value instanceof List) {
                            // 尝试作为 List<TravelMedia> 处理
                            try {
                                List<?> list = (List<?>) value;
                                if (!list.isEmpty()) {
                                    Object firstItem = list.get(0);
                                    if (firstItem instanceof TravelMedia) {
                                        result = (List<TravelMedia>) value;
                                        Log.d(TAG, "找到 media 列表: " + result.size() + " 个项目");
                                        break;
                                    } else if (firstItem instanceof String) {
                                        // 可能是 URL 列表
                                        for (Object item : list) {
                                            TravelMedia media = new TravelMedia();
                                            media.setMediaUrl((String) item);
                                            result.add(media);
                                        }
                                        Log.d(TAG, "从字符串列表创建 media");
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "解析 media 列表失败", e);
                            }
                        } else if (value instanceof String) {
                            // 尝试作为 JSON 字符串解析
                            try {
                                JSONArray jsonArray = new JSONArray((String) value);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    TravelMedia media = new TravelMedia();
                                    if (jsonObject.has("mediaId")) {
                                        media.setMediaId(jsonObject.getInt("mediaId"));
                                    }
                                    if (jsonObject.has("mediaUrl")) {
                                        media.setMediaUrl(jsonObject.getString("mediaUrl"));
                                    } else if (jsonObject.has("media_url")) {
                                        media.setMediaUrl(jsonObject.getString("media_url"));
                                    }
                                    result.add(media);
                                }
                                Log.d(TAG, "从 JSON 字符串解析 media");
                                break;
                            } catch (Exception e) {
                                Log.d(TAG, "字段 " + fieldName + " 不是 JSON 字符串");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "查找 media 字段时出错", e);
        }

        return result;
    }

    private void showLoading(boolean show) {
        findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}