package com.xr.traveltracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.xr.traveltracker.R;
import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<String> originalItems;
    private List<String> filteredItems;
    private String selectedItem;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public FilterAdapter(List<String> items, String selectedItem, OnItemClickListener listener) {
        this.originalItems = items;
        this.filteredItems = new ArrayList<>(items);
        this.selectedItem = selectedItem;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = filteredItems.get(position);

        holder.tvName.setText(item);

        // 显示选中标记
        boolean isSelected = item.equals(selectedItem);
        holder.ivCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        // 设置图标
        int iconRes = R.drawable.ic_category; // 默认图标
        holder.ivIcon.setImageResource(iconRes);

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    // 搜索过滤
    public void filter(String query) {
        filteredItems.clear();
        if (query.isEmpty()) {
            filteredItems.addAll(originalItems);
        } else {
            for (String item : originalItems) {
                if (item.toLowerCase().contains(query.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon, ivCheck;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            ivCheck = itemView.findViewById(R.id.iv_check);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}