// UpcomingTripAdapter.java
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
import com.xr.traveltracker.models.Trip;

import java.util.List;

public class UpcomingTripAdapter extends RecyclerView.Adapter<UpcomingTripAdapter.ViewHolder> {

    private Context context;
    private List<Trip> trips;

    public UpcomingTripAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_upcoming_trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.tripImage.setImageResource(trip.getImageRes());
        holder.tripTitle.setText(trip.getTitle());
        holder.tripDate.setText(trip.getDate());
        holder.tripLocation.setText(trip.getLocation());
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tripImage;
        TextView tripTitle;
        TextView tripDate;
        TextView tripLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tripImage = itemView.findViewById(R.id.trip_image);
            tripTitle = itemView.findViewById(R.id.trip_title);
            tripDate = itemView.findViewById(R.id.trip_date);
            tripLocation = itemView.findViewById(R.id.trip_location);
        }
    }
}

