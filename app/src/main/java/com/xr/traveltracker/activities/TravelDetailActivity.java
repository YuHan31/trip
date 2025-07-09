package com.xr.traveltracker.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xr.traveltracker.R;
import com.xr.traveltracker.models.TravelRecord;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TravelDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);

        TravelRecord record = getIntent().getParcelableExtra("travel_record");
        if (record == null) {
            finish();
            return;
        }

        TextView tvDestination = findViewById(R.id.tv_detail_destination);
        TextView tvDateRange = findViewById(R.id.tv_detail_date_range);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        TextView tvBudget = findViewById(R.id.tv_detail_budget);

        tvDestination.setText(record.getDestination());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = record.getStartDate() != null ?
                dateFormat.format(record.getStartDate()) : "未设置";
        String endDate = record.getEndDate() != null ?
                dateFormat.format(record.getEndDate()) : "未设置";
        tvDateRange.setText(startDate + " 至 " + endDate);

        tvDescription.setText(record.getDescription());
        tvBudget.setText(String.format(Locale.getDefault(), "预算: ¥%.2f", record.getBudget()));
    }
}