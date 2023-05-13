package com.example.social_network.data.remote;

import com.example.social_network.feature.auth.LoginActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.auth.AuthResponse;
import com.example.social_network.model.profile.ProfileResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);

    @POST("uploadpost")
    Call<GeneralResponse> uploadpost(@Body MultipartBody body);

    @GET("loadprofileinfo")
    Call<ProfileResponse> fetchProfileInfo(@QueryMap Map<String, String> params);
}
