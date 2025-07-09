package com.xr.traveltracker.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xr.traveltracker.R;
import com.xr.traveltracker.models.TravelRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TravelRecordAdapter extends RecyclerView.Adapter<TravelRecordAdapter.ViewHolder> {
    private List<TravelRecord> travelRecords;

    public TravelRecordAdapter(List<TravelRecord> travelRecords) {
        this.travelRecords = travelRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_travel_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelRecord record = travelRecords.get(position);
        holder.tvDestination.setText(record.getDestination());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateStr = "未设置";
        String endDateStr = "未设置";

        try {
            if (record.getStartDate() != null) {
                startDateStr = dateFormat.format(record.getStartDate());
            }
            if (record.getEndDate() != null) {
                endDateStr = dateFormat.format(record.getEndDate());
            }
        } catch (Exception e) {
            Log.e("TravelRecordAdapter", "Date formatting error", e);
        }

        holder.tvDate.setText(startDateStr + " 至 " + endDateStr);
        holder.tvDescription.setText(record.getDescription());
    }

    @Override
    public int getItemCount() {
        return travelRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDestination, tvDate, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDestination = itemView.findViewById(R.id.tv_destination);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}