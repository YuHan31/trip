package com.xr.traveltracker.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xr.traveltracker.R;
import com.xr.traveltracker.adapters.CitySelectionAdapter;
import com.xr.traveltracker.models.City;
import com.xr.traveltracker.utils.CityDataProvider;
import com.xr.traveltracker.utils.CityPreferences;
import com.xr.traveltracker.utils.ToolbarHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CitySelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private CitySelectionAdapter adapter;
    private List<City> allCities;
    private List<City> filteredCities;
    private CityPreferences cityPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        ToolbarHelper.setupToolbar(this, "选择去过的城市", true, v -> {
            finish();
        });

        cityPreferences = new CityPreferences(this);

        searchEditText = findViewById(R.id.et_search_city);
        recyclerView = findViewById(R.id.rv_cities);

        // 初始化城市列表
        allCities = CityDataProvider.getChinaCities();
        filteredCities = new ArrayList<>(allCities);

        // 加载已访问的城市
        Set<String> visitedCities = cityPreferences.getVisitedCities();
        for (City city : allCities) {
            city.setVisited(visitedCities.contains(city.getName()));
        }

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CitySelectionAdapter(filteredCities, (city, isChecked) -> {
            if (isChecked) {
                cityPreferences.addVisitedCity(city.getName());
            } else {
                cityPreferences.removeVisitedCity(city.getName());
            }
        });
        recyclerView.setAdapter(adapter);

        // 搜索功能
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterCities(String query) {
        filteredCities.clear();
        if (query.isEmpty()) {
            filteredCities.addAll(allCities);
        } else {
            String lowerQuery = query.toLowerCase();
            for (City city : allCities) {
                if (city.getName().toLowerCase().contains(lowerQuery) ||
                    city.getProvince().toLowerCase().contains(lowerQuery)) {
                    filteredCities.add(city);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
