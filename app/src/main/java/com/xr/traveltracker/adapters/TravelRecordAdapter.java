package com.xr.traveltracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xr.traveltracker.R;
import com.xr.traveltracker.models.TravelRecord;

import java.util.List;

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
        holder.tvDate.setText(record.getStartDate() + " 至 " + record.getEndDate());
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