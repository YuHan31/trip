// adapters/AttractionAdapter.java
package com.xr.traveltracker.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xr.traveltracker.R;
import com.xr.traveltracker.models.Attraction;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    private static final String TAG = "AttractionAdapter";
    private List<Attraction> attractionList;
    private OnItemClickListener listener;
    private Context context;

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
        this.context = parent.getContext();
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
        String imageUrl = attraction.getImageUrl();
        Log.d(TAG, "景点[" + attraction.getName() + "] 图片URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.trim().isEmpty() && !imageUrl.equals("null")) {
            // 处理图片URL
            String processedUrl = processImageUrl(imageUrl);
            Log.d(TAG, "处理后的图片URL: " + processedUrl);

            // 使用Glide加载图片
            Glide.with(context)
                    .load(processedUrl)
                    .placeholder(R.drawable.ic_default_image)  // 加载中显示默认图
                    .error(R.drawable.ic_default_image)        // 加载失败显示默认图
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.ivAttraction);
        } else {
            // 没有图片URL，显示默认图片
            Log.d(TAG, "没有图片URL，显示默认图片");
            holder.ivAttraction.setImageResource(R.drawable.ic_default_image);
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(attraction);
            }
        });
    }

    /**
     * 处理图片URL
     */
    private String processImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        imageUrl = imageUrl.trim();

        // 如果已经是完整URL，直接返回
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        // 处理以//开头的URL
        if (imageUrl.startsWith("//")) {
            return "http:" + imageUrl;
        }

        // 处理相对路径
        if (imageUrl.startsWith("/")) {
            String baseUrl = context.getString(R.string.base_url);

            // 清理baseUrl
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }

            // 移除/api部分
            if (baseUrl.endsWith("/api")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 4);
            }

            return baseUrl + imageUrl;
        }

        // 其他情况，直接返回
        return imageUrl;
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