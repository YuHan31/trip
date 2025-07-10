package com.xr.traveltracker.activities;

import android.graphics.drawable.Drawable;

import android.os.Bundle;
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

        apiService.getTravelRecordDetails("Bearer " + token, travelId)
                .enqueue(new Callback<TravelRecord>() {
                    @Override
                    public void onResponse(Call<TravelRecord> call, Response<TravelRecord> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            displayRecordDetails(response.body());
                        } else {
                            showError("获取详情失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<TravelRecord> call, Throwable t) {
                        showLoading(false);
                        showError("网络错误: " + t.getMessage());
                    }
                });
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
        List<TravelMedia> mediaList = record.getMedia();
        if (mediaList == null || mediaList.isEmpty()) {
            findViewById(R.id.media_container).setVisibility(View.GONE);
            return;
        }

        mediaContainer.removeAllViews(); // 清除现有视图

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
            Glide.with(this)
                    .load(media.getMediaUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);

            cardView.addView(imageView);
            mediaContainer.addView(cardView);
        }
    }

    private void showLoading(boolean show) {
        findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}