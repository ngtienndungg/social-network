package com.example.social_network.data.remote;

import com.example.social_network.feature.auth.LoginActivity;
import com.example.social_network.model.auth.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);
}
