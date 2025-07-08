// TripMemoryAdapter.java
package com.xr.traveltracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xr.traveltracker.R;
import com.xr.traveltracker.models.TripMemory;

import java.util.List;

public class TripMemoryAdapter extends RecyclerView.Adapter<TripMemoryAdapter.ViewHolder> {

    private Context context;
    private List<TripMemory> memories;

    public TripMemoryAdapter(Context context, List<TripMemory> memories) {
        this.context = context;
        this.memories = memories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_memory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TripMemory memory = memories.get(position);
        holder.memoryImage.setImageResource(memory.getImageRes());
        holder.memoryTitle.setText(memory.getTitle());
        holder.memoryDate.setText(memory.getDate() + " · " + memory.getDurationDays() + "天");
        holder.memoryDescription.setText(memory.getDescription());
    }

    @Override
    public int getItemCount() {
        return memories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView memoryImage;
        TextView memoryTitle;
        TextView memoryDate;
        TextView memoryDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memoryImage = itemView.findViewById(R.id.memory_image);
            memoryTitle = itemView.findViewById(R.id.memory_title);
            memoryDate = itemView.findViewById(R.id.memory_date);
            memoryDescription = itemView.findViewById(R.id.memory_description);
        }
    }
}