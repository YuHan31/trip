package com.xr.traveltracker.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.xr.traveltracker.R;
import com.xr.traveltracker.adapters.AttractionAdapter;
import com.xr.traveltracker.adapters.FilterAdapter;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.Attraction;
import com.xr.traveltracker.models.AttractionListResponse;
import com.xr.traveltracker.models.FilterOptionsResponse;
import com.xr.traveltracker.utils.ToolbarHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttractionsListActivity extends AppCompatActivity {

    private EditText etSearch;
    private TextView tvType, tvCity;
    private View layoutType, layoutCity;
    private RecyclerView recyclerView;

    private AttractionAdapter adapter;
    private List<Attraction> attractionList = new ArrayList<>();
    private ApiService apiService;

    // 筛选数据
    private List<String> attractionTypes = new ArrayList<>();
    private List<String> cities = new ArrayList<>();

    // 当前选中的筛选条件
    private String selectedType = "全部类型";
    private String selectedCity = "全部城市";
    private String searchKeyword = "";
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

    // 搜索防抖
    private static final long DEBOUNCE_DELAY = 500; // 500ms
    private android.os.Handler handler = new android.os.Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions_list);

        // 初始化Toolbar
        ToolbarHelper.setupToolbar(this, "热门景点", true, v -> finish());

        // 初始化Retrofit
        initRetrofit();

        // 初始化视图
        initViews();

        // 设置监听器
        setupListeners();

        // 加载筛选选项
        loadFilterOptions();

        // 加载景点数据
        loadAttractions();
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url)) // 使用字符串资源
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        tvType = findViewById(R.id.tv_type);
        tvCity = findViewById(R.id.tv_city);
        layoutType = findViewById(R.id.layout_type);
        layoutCity = findViewById(R.id.layout_city);

        // 设置默认文本
        tvType.setText(selectedType);
        tvCity.setText(selectedCity);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAttractions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化适配器
        adapter = new AttractionAdapter(attractionList, attraction -> {
            // 跳转到详情页面
            openAttractionDetail(attraction.getId());
        });
        recyclerView.setAdapter(adapter);

        // 添加滚动监听实现分页加载
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && currentPage < totalPages) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreAttractions();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        // 搜索框监听
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // 防抖处理
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        searchKeyword = s.toString().trim();
                        currentPage = 1;
                        loadAttractions();
                    }
                };
                handler.postDelayed(searchRunnable, DEBOUNCE_DELAY);
            }
        });

        // 景区类型筛选
        layoutType.setOnClickListener(v -> showTypeFilterDialog());

        // 城市筛选
        layoutCity.setOnClickListener(v -> showCityFilterDialog());
    }

    // 加载筛选选项
    private void loadFilterOptions() {
        Call<FilterOptionsResponse> call = apiService.getFilterOptions();
        call.enqueue(new Callback<FilterOptionsResponse>() {
            @Override
            public void onResponse(Call<FilterOptionsResponse> call, Response<FilterOptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    FilterOptionsResponse.FilterData data = response.body().getData();
                    if (data != null) {
                        attractionTypes = data.getTypes();
                        cities = data.getCities();
                    }
                } else {
                    // 如果API失败，使用默认数据
                    setDefaultFilterOptions();
                    Toast.makeText(AttractionsListActivity.this, "使用本地筛选数据", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FilterOptionsResponse> call, Throwable t) {
                Toast.makeText(AttractionsListActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                setDefaultFilterOptions();
            }
        });
    }

    // 设置默认筛选选项
    private void setDefaultFilterOptions() {
        attractionTypes = Arrays.asList(
                "全部类型", "自然景观", "古迹遗址", "人文景观", "宗教圣地",
                "博物馆", "主题公园", "动物园/植物园", "温泉度假", "海滨浴场",
                "登山徒步", "美食街区", "城市地标"
        );
        cities = Arrays.asList(
                "全部城市", "北京市", "上海市", "广州市", "深圳市", "成都市",
                "重庆市", "西安市", "杭州市", "南京市", "苏州市", "武汉市",
                "长沙市", "天津市", "青岛市", "厦门市", "三亚市", "桂林市",
                "昆明市", "大连市", "哈尔滨市"
        );
    }

    // 加载景点数据
    private void loadAttractions() {
        isLoading = true;

        // 构建查询条件
        String type = selectedType.equals("全部类型") ? "" : selectedType;
        String city = selectedCity.equals("全部城市") ? "" : selectedCity;
        String search = searchKeyword;

        Call<AttractionListResponse> call = apiService.getAllAttractions(
                currentPage, 10, type, city, search
        );

        call.enqueue(new Callback<AttractionListResponse>() {
            @Override
            public void onResponse(Call<AttractionListResponse> call, Response<AttractionListResponse> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Attraction> newAttractions = response.body().getData();

                        if (currentPage == 1) {
                            // 第一页，清空列表
                            attractionList.clear();
                        }

                        if (newAttractions != null && !newAttractions.isEmpty()) {
                            attractionList.addAll(newAttractions);
                            adapter.updateData(attractionList);
                        } else {
                            Toast.makeText(AttractionsListActivity.this, "没有更多景点了", Toast.LENGTH_SHORT).show();
                        }

                        // 更新分页信息
                        if (response.body().getPagination() != null) {
                            totalPages = response.body().getPagination().getPages();
                        }

                    } else {
                        // 服务器返回success=false
                        String errorMsg = response.body().getMessage() != null ?
                                response.body().getMessage() : "加载失败";
                        Toast.makeText(AttractionsListActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // HTTP状态码非200
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "未知错误";
                        Toast.makeText(AttractionsListActivity.this,
                                "加载失败: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(AttractionsListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AttractionListResponse> call, Throwable t) {
                isLoading = false;
                Toast.makeText(AttractionsListActivity.this, "网络连接失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 加载更多景点
    private void loadMoreAttractions() {
        if (currentPage < totalPages) {
            currentPage++;
            loadAttractions();
        }
    }

    // 显示景区类型筛选对话框
    private void showTypeFilterDialog() {
        if (attractionTypes.isEmpty()) {
            Toast.makeText(this, "正在加载筛选选项...", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        EditText etSearchFilter = view.findViewById(R.id.et_search_filter);
        RecyclerView recyclerFilter = view.findViewById(R.id.recycler_filter);

        tvTitle.setText("选择景区类型");

        FilterAdapter adapter = new FilterAdapter(attractionTypes, selectedType, item -> {
            selectedType = item;
            tvType.setText(item);
            dialog.dismiss();

            // 重置分页并重新加载
            currentPage = 1;
            loadAttractions();
        });

        recyclerFilter.setLayoutManager(new LinearLayoutManager(this));
        recyclerFilter.setAdapter(adapter);

        etSearchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    // 显示城市筛选对话框
    private void showCityFilterDialog() {
        if (cities.isEmpty()) {
            Toast.makeText(this, "正在加载筛选选项...", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        EditText etSearchFilter = view.findViewById(R.id.et_search_filter);
        RecyclerView recyclerFilter = view.findViewById(R.id.recycler_filter);

        tvTitle.setText("选择城市");

        FilterAdapter adapter = new FilterAdapter(cities, selectedCity, item -> {
            selectedCity = item;
            tvCity.setText(item);
            dialog.dismiss();

            // 重置分页并重新加载
            currentPage = 1;
            loadAttractions();
        });

        recyclerFilter.setLayoutManager(new LinearLayoutManager(this));
        recyclerFilter.setAdapter(adapter);

        etSearchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    // 打开景点详情页面
    private void openAttractionDetail(int attractionId) {
        android.content.Intent intent = new android.content.Intent(this, AttractionDetailActivity.class);
        intent.putExtra("attractionId", attractionId);
        startActivity(intent);
    }
}