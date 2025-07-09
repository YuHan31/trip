package com.xr.traveltracker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xr.traveltracker.R;
import com.xr.traveltracker.activities.EditProfileActivity;
import com.xr.traveltracker.activities.LoginActivity;
import com.xr.traveltracker.api.ApiService;
import com.xr.traveltracker.models.UserDetailsResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {
    private static final int EDIT_PROFILE_REQUEST_CODE = 1001;
    private String userId;
    private String username;
    private String token;

    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnEditProfile;
    private Button btnLogout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            username = getArguments().getString("username");
            token = getArguments().getString("token");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnLogout = view.findViewById(R.id.btn_logout);

        // 显示当前用户信息
        tvUsername.setText(username);

        // 编辑资料按钮点击事件
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("username", username);
            intent.putExtra("token", token);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        // 退出登录按钮点击事件
        btnLogout.setOnClickListener(v -> {
            // 清除用户信息或执行退出登录逻辑
            // 例如，清除SharedPreferences中的用户信息
            // SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            // SharedPreferences.Editor editor = preferences.edit();
            // editor.remove("token");
            // editor.remove("userId");
            // editor.remove("username");
            // editor.apply();

            // 提示用户已退出登录
            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();

            // 跳转到登录页面或其他页面
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        // 加载用户详细信息
        loadUserDetails();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 当从编辑页面返回且操作成功时，刷新数据
            loadUserDetails();
            Toast.makeText(getContext(), "资料已更新", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserDetails() {
        Log.d("ProfileFragment", "开始加载用户详情，userId: " + userId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url)) // 使用字符串资源
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<UserDetailsResponse> call = apiService.getUserDetails("Bearer " + token, userId);

        call.enqueue(new Callback<UserDetailsResponse>() {
            @Override
            public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                Log.d("ProfileFragment", "收到响应，状态码: " + response.code());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        UserDetailsResponse userDetails = response.body();
                        Log.d("ProfileFragment", "用户详情: " +
                                "username=" + userDetails.getUsername() +
                                ", email=" + userDetails.getEmail());

                        // 更新UI
                        getActivity().runOnUiThread(() -> {
                            username = userDetails.getUsername(); // 更新本地username
                            tvUsername.setText(username);
                            tvEmail.setText(userDetails.getEmail() != null ?
                                    userDetails.getEmail() : "未设置邮箱");
                        });
                    } else {
                        Log.e("ProfileFragment", "响应体为null");
                    }
                } else {
                    Log.e("ProfileFragment", "请求不成功，错误码: " + response.code());
                    try {
                        Log.e("ProfileFragment", "错误响应体: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                Log.e("ProfileFragment", "API请求失败", t);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "加载用户信息失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}