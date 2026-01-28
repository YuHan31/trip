// adapters/AttractionAdapter.java
package com.xr.traveltracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.xr.traveltracker.R;
import com.xr.traveltracker.models.Attraction;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    private List<Attraction> attractionList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Attraction attraction);
    }

    public AttractionAdapter(List<Attraction> attractionList, OnItemClickListener listener) {
        this.attractionList = attractionList;
        this.listener = listener;
    }

    public void updateData(List<Attraction> newList) {
        this.attractionList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);

        // 设置数据
        holder.tvName.setText(attraction.getName());
        holder.tvType.setText(attraction.getType());
        holder.tvAddress.setText(attraction.getAddress());

        // 加载图片
        if (attraction.getImageUrl() != null && !attraction.getImageUrl().isEmpty()) {
            // 使用服务器返回的完整图片URL
            String imageUrl = attraction.getImageUrl();
            if (!imageUrl.startsWith("http")) {
                // 如果是相对路径，添加基础URL
                imageUrl = "http://你的服务器IP:3000/" + imageUrl;
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_image)
                    .error(R.drawable.ic_default_image)
                    .into(holder.ivAttraction);
        } else {
            holder.ivAttraction.setImageResource(R.drawable.ic_default_image);
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(attraction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionList != null ? attractionList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAttraction;
        TextView tvName, tvType, tvAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAttraction = itemView.findViewById(R.id.iv_attraction);
            tvName = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }
    }
}