package com.xr.traveltracker.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.xr.traveltracker.R;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.MediaResponse;
import com.xr.traveltracker.models.TravelRequest;
import com.xr.traveltracker.models.TravelResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFragment extends Fragment {
    private TextInputEditText etDestination, etStartDate, etEndDate, etDescription, etBudget;
    private Button btnSubmit;
    private ImageButton btnAddPhoto;
    private String token;
    private String userId;
    private List<Uri> selectedImages = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        Bundle args = getArguments();
        if (args != null) {
            token = args.getString("token");
            userId = args.getString("userId");
        }

        initViews(view);
        setupDatePickers();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        etDestination = view.findViewById(R.id.et_destination);
        etStartDate = view.findViewById(R.id.et_start_date);
        etEndDate = view.findViewById(R.id.et_end_date);
        etDescription = view.findViewById(R.id.et_description);
        etBudget = view.findViewById(R.id.et_budget);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnAddPhoto = view.findViewById(R.id.btn_add_photo);
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                    editText.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void setupListeners() {
        btnAddPhoto.setOnClickListener(v -> openImagePicker());

        btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                createTravelRecord();
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (etDestination.getText().toString().isEmpty()) {
            etDestination.setError("请输入目的地");
            isValid = false;
        }

        if (etStartDate.getText().toString().isEmpty()) {
            etStartDate.setError("请选择开始日期");
            isValid = false;
        }

        if (etEndDate.getText().toString().isEmpty()) {
            etEndDate.setError("请选择结束日期");
            isValid = false;
        }

        return isValid;
    }

    private void createTravelRecord() {
        String destination = etDestination.getText().toString();
        String startDate = etStartDate.getText().toString();
        String endDate = etEndDate.getText().toString();
        String description = etDescription.getText().toString();
        double budget = etBudget.getText().toString().isEmpty() ? 0 : Double.parseDouble(etBudget.getText().toString());

        TravelRequest request = new TravelRequest(destination, startDate, endDate, description, budget);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.createTravelRecord("Bearer " + token, request)
                .enqueue(new Callback<TravelResponse>() {
                    @Override
                    public void onResponse(Call<TravelResponse> call, Response<TravelResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TravelResponse travelResponse = response.body();

                            if (!selectedImages.isEmpty()) {
                                uploadMedia(travelResponse.getTravelId(), () -> {
                                    navigateToSuccessPage(travelResponse.getTravelId());
                                });
                            } else {
                                navigateToSuccessPage(travelResponse.getTravelId());
                            }
                        } else {
                            Toast.makeText(getContext(), "创建旅行记录失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TravelResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToSuccessPage(int travelId) {
        SuccessFragment fragment = new SuccessFragment();
        Bundle args = new Bundle();
        args.putString("token", token);
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "无法获取用户ID", Toast.LENGTH_SHORT).show();
            return;
        }
        args.putString("userId", userId);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void uploadMedia(int travelId, Runnable onComplete) {
        AtomicInteger uploadCount = new AtomicInteger(0);
        int totalImages = selectedImages.size();

        for (Uri imageUri : selectedImages) {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                File file = new File(requireContext().getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("media", file.getName(), requestFile);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);

                apiService.uploadMedia("Bearer " + token, travelId, body)
                        .enqueue(new Callback<MediaResponse>() {
                            @Override
                            public void onResponse(Call<MediaResponse> call, Response<MediaResponse> response) {
                                if (uploadCount.incrementAndGet() == totalImages) {
                                    onComplete.run();
                                }
                            }

                            @Override
                            public void onFailure(Call<MediaResponse> call, Throwable t) {
                                if (uploadCount.incrementAndGet() == totalImages) {
                                    onComplete.run();
                                }
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
                if (uploadCount.incrementAndGet() == totalImages) {
                    onComplete.run();
                }
            }
        }

        if (totalImages == 0) {
            onComplete.run();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImages.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                selectedImages.add(imageUri);
            }
            Toast.makeText(getContext(), "已选择 " + selectedImages.size() + " 张图片", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etDestination.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
        etDescription.setText("");
        etBudget.setText("");
        selectedImages.clear();
    }
}