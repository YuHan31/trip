package com.xr.traveltracker.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.xr.traveltracker.R;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.Attraction;
import com.xr.traveltracker.models.AttractionDetailResponse;
import com.xr.traveltracker.utils.ToolbarHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttractionDetailActivity extends AppCompatActivity {

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

        // 初始化Toolbar
        ToolbarHelper.setupToolbar(this, "景点详情", true, v -> finish());

        // 初始化Retrofit
        initRetrofit();

        // 初始化视图
        initViews();

        // 加载景点详情
        loadAttractionDetail();
    }

    private void setupToolbar() {
        // 设置Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("景点详情");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
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
        Call<AttractionDetailResponse> call = apiService.getAttractionDetail(attractionId);

        call.enqueue(new Callback<AttractionDetailResponse>() {
            @Override
            public void onResponse(Call<AttractionDetailResponse> call, Response<AttractionDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Attraction attraction = response.body().getData();
                        if (attraction != null) {
                            displayAttractionDetail(attraction);
                        } else {
                            Toast.makeText(AttractionDetailActivity.this, "景点数据为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 服务器返回success=false
                        String errorMsg = response.body().getMessage() != null ?
                                response.body().getMessage() : "加载失败";
                        Toast.makeText(AttractionDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // HTTP状态码非200
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "未知错误";
                        Toast.makeText(AttractionDetailActivity.this,
                                "加载失败: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(AttractionDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AttractionDetailResponse> call, Throwable t) {
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
        if (attraction.getImageUrl() != null && !attraction.getImageUrl().isEmpty()) {
            String imageUrl = attraction.getImageUrl();
            if (!imageUrl.startsWith("http")) {
                // 如果是相对路径，添加基础URL
                String baseUrl = getString(R.string.base_url).replace("/api/", "/");
                imageUrl = baseUrl + imageUrl;
            }
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_image)
                    .error(R.drawable.ic_default_image)
                    .into(ivDetailImage);
        } else {
            ivDetailImage.setImageResource(R.drawable.ic_default_image);
        }
    }
}