package com.xr.traveltracker.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xr.traveltracker.R;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.UpdateProfileRequest;
import com.xr.traveltracker.models.UpdateProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSave;
    private Button btnCancel;

    private String userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 获取传递的参数
        userId = getIntent().getStringExtra("userId");
        token = getIntent().getStringExtra("token");
        String username = getIntent().getStringExtra("username");

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // 加载当前用户信息
        loadCurrentUserInfo();

        btnSave.setOnClickListener(v -> updateProfile());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 直接关闭当前Activity，返回ProfileFragment
                finish();
            }
        });
    }

    private void loadCurrentUserInfo() {
        // 这里可以添加加载当前用户信息的逻辑
        // 可以使用与ProfileFragment中相同的API调用
    }

    private void updateProfile() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url)) // 使用字符串资源
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        UpdateProfileRequest request = new UpdateProfileRequest(userId, email, password);

        Call<UpdateProfileResponse> call = apiService.updateProfile("Bearer " + token, request);
        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "资料更新成功", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK); // 设置成功结果
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "未知错误";
                        Toast.makeText(EditProfileActivity.this,
                                "更新失败: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(EditProfileActivity.this,
                                "更新失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}