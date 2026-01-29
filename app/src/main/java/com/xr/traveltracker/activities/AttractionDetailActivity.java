package com.xr.traveltracker.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.xr.traveltracker.R;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.Attraction;
import com.xr.traveltracker.models.AttractionDetailResponse;
import com.xr.traveltracker.utils.ToolbarHelper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttractionDetailActivity extends AppCompatActivity {

    private static final String TAG = "AttractionDetail";

    private ImageView ivDetailImage;
    private TextView tvDetailName, tvDetailType, tvDetailCity, tvDetailAddress;
    private TextView tvDetailTicketPrice, tvDetailOpeningHours, tvDetailDescription;

    private ApiService apiService;
    private int attractionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        // 获取景点ID
        attractionId = getIntent().getIntExtra("attractionId", -1);
        if (attractionId == -1) {
            Toast.makeText(this, "景点ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "启动景点详情页，景点ID: " + attractionId);
        Log.d(TAG, "Base URL配置: " + getString(R.string.base_url));

        // 初始化Toolbar
        ToolbarHelper.setupToolbar(this, "景点详情", true, v -> finish());

        // 初始化Retrofit
        initRetrofit();

        // 初始化视图
        initViews();

        // 加载景点详情
        loadAttractionDetail();
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void initViews() {
        ivDetailImage = findViewById(R.id.iv_detail_image);
        tvDetailName = findViewById(R.id.tv_detail_name);
        tvDetailType = findViewById(R.id.tv_detail_type);
        tvDetailCity = findViewById(R.id.tv_detail_city);
        tvDetailAddress = findViewById(R.id.tv_detail_address);
        tvDetailTicketPrice = findViewById(R.id.tv_detail_ticket_price);
        tvDetailOpeningHours = findViewById(R.id.tv_detail_opening_hours);
        tvDetailDescription = findViewById(R.id.tv_detail_description);
    }

    private void loadAttractionDetail() {
        Log.d(TAG, "开始请求景点详情，ID: " + attractionId);
        Call<AttractionDetailResponse> call = apiService.getAttractionDetail(attractionId);

        call.enqueue(new Callback<AttractionDetailResponse>() {
            @Override
            public void onResponse(Call<AttractionDetailResponse> call, Response<AttractionDetailResponse> response) {
                Log.d(TAG, "收到API响应，状态码: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API调用成功");

                    if (response.body().isSuccess()) {
                        Attraction attraction = response.body().getData();
                        if (attraction != null) {
                            // 打印API返回的JSON数据
                            try {
                                String rawResponse = new Gson().toJson(response.body());
                                Log.d(TAG, "API返回的完整数据: " + rawResponse);
                            } catch (Exception e) {
                                Log.e(TAG, "解析响应失败", e);
                            }

                            Log.d(TAG, "成功获取景点数据");
                            Log.d(TAG, "景点名称: " + attraction.getName());
                            Log.d(TAG, "图片URL: " + attraction.getImageUrl());
                            displayAttractionDetail(attraction);
                        } else {
                            Log.e(TAG, "景点数据为空");
                            Toast.makeText(AttractionDetailActivity.this, "景点数据为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = response.body().getMessage() != null ?
                                response.body().getMessage() : "加载失败";
                        Log.e(TAG, "API返回失败: " + errorMsg);
                        Toast.makeText(AttractionDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e(TAG, "HTTP错误: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "无错误详情";
                        Log.e(TAG, "错误响应体: " + errorBody);
                        Toast.makeText(AttractionDetailActivity.this,
                                "加载失败: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "读取错误响应失败", e);
                        Toast.makeText(AttractionDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AttractionDetailResponse> call, Throwable t) {
                Log.e(TAG, "网络请求失败", t);
                Toast.makeText(AttractionDetailActivity.this,
                        "网络连接失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAttractionDetail(Attraction attraction) {
        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(attraction.getName());
        }

        // 设置数据
        tvDetailName.setText(attraction.getName());
        tvDetailType.setText(attraction.getType());
        tvDetailCity.setText(attraction.getCity());
        tvDetailAddress.setText(attraction.getAddress());
        tvDetailTicketPrice.setText(String.format("¥%.2f", attraction.getTicketPrice()));
        tvDetailOpeningHours.setText(attraction.getOpeningHours());
        tvDetailDescription.setText(attraction.getDescription());

        // 加载图片
        String imageUrl = attraction.getImageUrl();
        Log.d(TAG, "=========================================");
        Log.d(TAG, "图片加载调试开始");
        Log.d(TAG, "景点名称: " + attraction.getName());
        Log.d(TAG, "原始图片URL: " + imageUrl);
        Log.d(TAG, "图片URL是否为null: " + (imageUrl == null));
        Log.d(TAG, "图片URL是否为空: " + (imageUrl != null && imageUrl.trim().isEmpty()));
        Log.d(TAG, "图片URL是否为'null': " + (imageUrl != null && "null".equals(imageUrl)));

        if (imageUrl != null && !imageUrl.trim().isEmpty() && !"null".equals(imageUrl)) {
            // 记录原始URL
            String processedUrl = imageUrl;

            // 如果已经是完整URL，直接使用
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                Log.d(TAG, "已经是完整URL，直接使用");
            } else if (imageUrl.startsWith("//")) {
                processedUrl = "http:" + imageUrl;
                Log.d(TAG, "处理 // 前缀 -> " + processedUrl);
            } else if (imageUrl.startsWith("/")) {
                // 相对路径处理
                String baseUrl = getString(R.string.base_url);
                Log.d(TAG, "Base URL配置: " + baseUrl);

                // 处理baseUrl - 移除/api部分
                if (baseUrl.endsWith("/api/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 5);
                } else if (baseUrl.endsWith("/api")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 4);
                }

                // 确保baseUrl不以/结尾
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }

                processedUrl = baseUrl + imageUrl;
                Log.d(TAG, "处理相对路径 -> " + processedUrl);
            } else {
                // 不是完整URL，也不是相对路径
                Log.d(TAG, "URL格式异常: " + imageUrl);
                processedUrl = null;
            }

            if (processedUrl != null) {
                Log.d(TAG, "最终处理的图片URL: " + processedUrl);

                // 使用Glide加载图片
                Glide.with(this)
                        .load(processedUrl)
                        .placeholder(R.drawable.ic_default_image)
                        .error(R.drawable.ic_default_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)  // 调试时禁用缓存
                        .skipMemoryCache(true)  // 调试时禁用内存缓存
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "图片加载失败: " + model, e);
                                if (e != null) {
                                    Log.e(TAG, "加载失败原因: " + e.getMessage());
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                Log.d(TAG, "图片加载成功: " + model);
                                return false;
                            }
                        })
                        .into(ivDetailImage);
            } else {
                Log.d(TAG, "图片URL处理失败，显示默认图片");
                ivDetailImage.setImageResource(R.drawable.ic_default_image);
            }
        } else {
            Log.d(TAG, "图片URL无效，显示默认图片");
            ivDetailImage.setImageResource(R.drawable.ic_default_image);
        }
        Log.d(TAG, "图片加载调试结束");
        Log.d(TAG, "=========================================");
    }
}