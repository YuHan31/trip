package com.xr.traveltracker.api;


import com.xr.traveltracker.models.LoginRequest;
import com.xr.traveltracker.models.LoginResponse;
import com.xr.traveltracker.models.RegisterRequest;
import com.xr.traveltracker.models.RegisterResponse;
import com.xr.traveltracker.models.UpdateProfileRequest;
import com.xr.traveltracker.models.UpdateProfileResponse;
import com.xr.traveltracker.models.UserDetailsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/users/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @GET("api/users/{userId}")
    Call<UserDetailsResponse> getUserDetails(@Header("Authorization") String token, @Path("userId") String userId);

    @PUT("api/users/update")
    Call<UpdateProfileResponse> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);
}