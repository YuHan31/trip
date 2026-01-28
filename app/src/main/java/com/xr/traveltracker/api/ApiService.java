package com.xr.traveltracker.api;


import com.xr.traveltracker.models.AttractionListResponse;
import com.xr.traveltracker.models.FilterOptionsResponse;
import com.xr.traveltracker.models.LoginRequest;
import com.xr.traveltracker.models.LoginResponse;
import com.xr.traveltracker.models.MediaResponse;
import com.xr.traveltracker.models.RegisterRequest;
import com.xr.traveltracker.models.RegisterResponse;
import com.xr.traveltracker.models.TravelRecord;
import com.xr.traveltracker.models.TravelRequest;
import com.xr.traveltracker.models.TravelResponse;
import com.xr.traveltracker.models.UpdateProfileRequest;
import com.xr.traveltracker.models.UpdateProfileResponse;
import com.xr.traveltracker.models.UserDetailsResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/users/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @GET("api/users/{userId}")
    Call<UserDetailsResponse> getUserDetails(@Header("Authorization") String token, @Path("userId") String userId);

    @PUT("api/users/update")
    Call<UpdateProfileResponse> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);

    // 添加旅行记录
    @POST("api/travel")
    Call<TravelResponse> createTravelRecord(
            @Header("Authorization") String token,
            @Body TravelRequest request
    );

    // 上传媒体文件
    @Multipart
    @POST("api/travel/{travelId}/media")
    Call<MediaResponse> uploadMedia(
            @Header("Authorization") String token,
            @Path("travelId") int travelId,
            @Part MultipartBody.Part file
    );

    @GET("api/travel/user/{userId}")
    Call<List<TravelRecord>> getUserTravelRecords(
            @Header("Authorization") String token,
            @Path("userId") String userId
    );

        @DELETE("api/travel/{travelId}")
        Call<Void> deleteTravelRecord(
                @Header("Authorization") String token,
                @Path("travelId") int travelId
        );

    @GET("api/travel/{travelId}")
    Call<TravelRecord> getTravelRecordDetails(
            @Header("Authorization") String token,
            @Path("travelId") int travelId
    );

    @GET("api/attractions/list")
    Call<AttractionListResponse> getAllAttractions(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("type") String type,
            @Query("city") String city,
            @Query("search") String search
    );

    @GET("api/attractions/hot")
    Call<AttractionListResponse> getHotAttractions();

    @GET("api/attractions/detail/{id}")
    Call<com.xr.traveltracker.models.AttractionDetailResponse> getAttractionDetail(@Path("id") int id);

    @GET("api/attractions/filters")
    Call<FilterOptionsResponse> getFilterOptions();

    @GET("api/attractions/search")
    Call<AttractionListResponse> searchAttractions(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/attractions/city/{city}")
    Call<AttractionListResponse> getAttractionsByCity(
            @Path("city") String city,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/attractions/type/{type}")
    Call<AttractionListResponse> getAttractionsByType(
            @Path("type") String type,
            @Query("page") int page,
            @Query("limit") int limit
    );
    }
