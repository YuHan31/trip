package com.xr.traveltracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.xr.traveltracker.R;
import com.xr.traveltracker.models.City;
import java.util.List;

public class CitySelectionAdapter extends RecyclerView.Adapter<CitySelectionAdapter.ViewHolder> {

    private List<City> cities;
    private OnCityCheckListener listener;

    public interface OnCityCheckListener {
        void onCityChecked(City city, boolean isChecked);
    }

    public CitySelectionAdapter(List<City> cities, OnCityCheckListener listener) {
        this.cities = cities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        City city = cities.get(position);
        holder.cityName.setText(city.getName());
        holder.provinceName.setText(city.getProvince());

        // 先移除监听器，避免在设置状态时触发
        holder.checkBox.setOnCheckedChangeListener(null);
        // 设置复选框状态
        holder.checkBox.setChecked(city.isVisited());

        // 重新设置监听器
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            city.setVisited(isChecked);
            if (listener != null) {
                listener.onCityChecked(city, isChecked);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;
        TextView provinceName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.tv_city_name);
            provinceName = itemView.findViewById(R.id.tv_province_name);
            checkBox = itemView.findViewById(R.id.checkbox_city);
        }
    }
}
