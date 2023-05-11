package com.example.social_network.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.social_network.data.remote.ApiError;
import com.example.social_network.data.remote.ApiService;
import com.example.social_network.feature.auth.LoginActivity;
import com.example.social_network.model.auth.AuthResponse;
import com.example.social_network.model.profile.ProfileResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static Repository instance = null;
    private final ApiService apiService;

    private Repository(ApiService apiService) {
        this.apiService = apiService;
    }

    public static Repository getRepository(ApiService apiService) {
        if (instance == null) {
            instance = new Repository(apiService);
        }
        return instance;
    }

    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo) {
        MutableLiveData<AuthResponse> auth = new MutableLiveData<>();
        Call<AuthResponse> call = apiService.login(userInfo);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    auth.postValue(response.body());
                } else {
                    Gson gson = new Gson();
                    AuthResponse authResponse = null;
                    try {
                        authResponse = gson.fromJson(response.errorBody().string(), AuthResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        authResponse = new AuthResponse(errorMessage.message, errorMessage.status);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorMessageFromThrowable(t);
                AuthResponse authResponse = new AuthResponse(errorMessage.message, errorMessage.status);
                auth.postValue(authResponse);
            }
        });
        return auth;
    }

    public LiveData<ProfileResponse> fetchProfileInfo(Map<String, String> params) {
        MutableLiveData<ProfileResponse> userInfo = new MutableLiveData<>();
        Call<ProfileResponse> call = apiService.fetchProfileInfo(params);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    userInfo.postValue(response.body());
                } else {
                    Gson gson = new Gson();
                    ProfileResponse profileResponse = null;
                    try {
                        profileResponse = gson.fromJson(response.errorBody().string(), ProfileResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        profileResponse = new ProfileResponse(errorMessage.message, errorMessage.status);
                    }
                    userInfo.postValue(profileResponse);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorMessageFromThrowable(t);
                ProfileResponse profileResponse = new ProfileResponse(errorMessage.message, errorMessage.status);
                userInfo.postValue(profileResponse);
            }
        });
        return userInfo;
    }
}
