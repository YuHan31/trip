package com.xr.traveltracker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xr.traveltracker.R;
import com.xr.traveltracker.activities.TravelDetailActivity;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.TravelRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TravelRecordAdapter extends RecyclerView.Adapter<TravelRecordAdapter.ViewHolder> {
    private static final String TAG = "TravelRecordAdapter";
    private final Context context;
    private final List<TravelRecord> travelRecords;
    private final String token;
    private final DeleteListener deleteListener;

    public interface DeleteListener {
        void onDeleteSuccess(int position);
        void onDeleteFailure(String errorMessage);
    }

    public TravelRecordAdapter(Context context, List<TravelRecord> travelRecords,
                               String token, DeleteListener deleteListener) {
        this.context = context;
        this.travelRecords = travelRecords;
        this.token = token;
        this.deleteListener = deleteListener;
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
        holder.bind(record);

        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(record.getTravelId(), position);
        });

        holder.btnViewDetail.setOnClickListener(v -> {
            openDetailActivity(record.getTravelId());
        });
    }

    private void openDetailActivity(int travelId) {
        try {
            Intent intent = new Intent(context, TravelDetailActivity.class);
            intent.putExtra("travel_id", travelId);
            intent.putExtra("token", token);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "打开详情页失败", e);
            Toast.makeText(context, "打开详情页失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(int travelId, int position) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteTravelRecord(travelId, position);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteTravelRecord(int travelId, int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.deleteTravelRecord("Bearer " + token, travelId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            travelRecords.remove(position);
                            notifyItemRemoved(position);
                            deleteListener.onDeleteSuccess(position);
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = "删除失败: " + response.code();
                            deleteListener.onDeleteFailure(errorMessage);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        String errorMessage = "网络错误: " + t.getMessage();
                        deleteListener.onDeleteFailure(errorMessage);
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return travelRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDestination, tvDate, tvDescription;
        ImageButton btnDelete, btnViewDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDestination = itemView.findViewById(R.id.tv_destination);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
        }

        public void bind(TravelRecord record) {
            tvDestination.setText(record.getDestination());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String startDateStr = record.getStartDate() != null ?
                    dateFormat.format(record.getStartDate()) : "未设置";
            String endDateStr = record.getEndDate() != null ?
                    dateFormat.format(record.getEndDate()) : "未设置";

            tvDate.setText(startDateStr + " 至 " + endDateStr);
            tvDescription.setText(record.getDescription());
        }
    }
}