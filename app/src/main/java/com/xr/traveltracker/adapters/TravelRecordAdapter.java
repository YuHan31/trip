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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TravelRecordAdapter extends RecyclerView.Adapter<TravelRecordAdapter.ViewHolder> {
    private List<TravelRecord> travelRecords;
    private String token;
    private DeleteListener deleteListener;
    private Context context;

    public interface DeleteListener {
        void onDeleteSuccess(int position);
        void onDeleteFailure(String errorMessage);
    }

    public TravelRecordAdapter(Context context, List<TravelRecord> travelRecords, String token, DeleteListener deleteListener) {
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
            int travelId = record.getTravelId();
            showDeleteConfirmationDialog(holder.itemView.getContext(), travelId, position);
        });

        holder.btnViewDetail.setOnClickListener(v -> {
            // 直接使用当前列表中的记录对象，避免再次网络请求
            openDetailActivity(record);
        });
    }

    private void openDetailActivity(TravelRecord record) {
        if (record == null) {
            Toast.makeText(context, "无法查看详情：记录为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(context, TravelDetailActivity.class);
            // 直接传递Parcelable对象
            intent.putExtra("travel_record", record);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("TravelRecordAdapter", "打开详情页失败", e);
            Toast.makeText(context, "打开详情页失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(Context context, int travelId, int position) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteTravelRecord(context, travelId, position);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteTravelRecord(Context context, int travelId, int position) {
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
                        } else {
                            String errorMessage = "删除失败: " + response.code();
                            if (response.errorBody() != null) {
                                try {
                                    errorMessage = response.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            deleteListener.onDeleteFailure(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        deleteListener.onDeleteFailure("网络错误: " + t.getMessage());
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

            tvDate.setText(startDateStr + " 至 " + endDateStr);
            tvDescription.setText(record.getDescription());
        }
    }
}