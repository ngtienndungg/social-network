package com.example.social_network.data.remote;

import com.example.social_network.feature.auth.LoginActivity;
import com.example.social_network.model.auth.AuthResponse;
import com.example.social_network.model.profile.ProfileResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);

    @GET("loadprofileinfo")
    Call<ProfileResponse> fetchProfileInfo(@QueryMap Map<String, String> params);
}
