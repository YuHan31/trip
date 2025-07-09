package com.xr.traveltracker.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xr.traveltracker.R;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.TravelMedia;
import com.xr.traveltracker.models.TravelRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
            Log.e(TAG, "未接收到必要的参数");
            finish();
            return;
        }

        // 初始化视图
        TextView tvDestination = findViewById(R.id.tv_detail_destination);
        TextView tvDateRange = findViewById(R.id.tv_detail_date_range);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        TextView tvBudget = findViewById(R.id.tv_detail_budget);
        LinearLayout mediaContainer = findViewById(R.id.media_container);
        mediaContainer.setVisibility(View.VISIBLE);

        // 加载旅行记录详情
        loadTravelRecordDetails(tvDestination, tvDateRange, tvDescription, tvBudget, mediaContainer);
    }

    private void loadTravelRecordDetails(TextView tvDestination, TextView tvDateRange,
                                         TextView tvDescription, TextView tvBudget,
                                         LinearLayout mediaContainer) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getTravelRecordDetails("Bearer " + token, travelId)
                .enqueue(new Callback<TravelRecord>() {
                    @Override
                    public void onResponse(Call<TravelRecord> call, Response<TravelRecord> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TravelRecord record = response.body();
                            displayRecordDetails(record, tvDestination, tvDateRange,
                                    tvDescription, tvBudget, mediaContainer);
                        } else {
                            Log.e(TAG, "获取详情失败: " + response.code());
                            Toast.makeText(TravelDetailActivity.this,
                                    "获取详情失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TravelRecord> call, Throwable t) {
                        Log.e(TAG, "网络请求失败", t);
                        Toast.makeText(TravelDetailActivity.this,
                                "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayRecordDetails(TravelRecord record, TextView tvDestination,
                                      TextView tvDateRange, TextView tvDescription,
                                      TextView tvBudget, LinearLayout mediaContainer) {
        // 设置基本信息
        tvDestination.setText(record.getDestination());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = record.getStartDate() != null ?
                dateFormat.format(record.getStartDate()) : "未设置";
        String endDate = record.getEndDate() != null ?
                dateFormat.format(record.getEndDate()) : "未设置";
        tvDateRange.setText(startDate + " 至 " + endDate);

        tvDescription.setText(record.getDescription());
        tvBudget.setText(String.format(Locale.getDefault(), "预算: ¥%.2f", record.getBudget()));

        // 加载并显示媒体
        List<TravelMedia> mediaList = record.getMedia();
        if (mediaList == null || mediaList.isEmpty()) {
            Log.d(TAG, "没有媒体数据");
            return;
        }

        Log.d(TAG, "共有" + mediaList.size() + "个媒体文件");
        mediaContainer.removeAllViews(); // 清除现有视图

        for (TravelMedia media : mediaList) {
            Log.d(TAG, "准备加载媒体: " + media.getMediaUrl());

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.media_height));
            params.setMargins(0, 0, 0, 16);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // 使用Glide加载图片
            Glide.with(this)
                    .load(media.getMediaUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "图片加载失败: " + media.getMediaUrl(), e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            Log.d(TAG, "图片加载成功: " + media.getMediaUrl());
                            return false;
                        }
                    })
                    .into(imageView);

            mediaContainer.addView(imageView);
        }
    }
}