package com.example.social_network.feature.auth;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.social_network.data.Repository;
import com.example.social_network.data.remote.ApiService;
import com.example.social_network.model.auth.AuthResponse;

public class LoginViewModel extends ViewModel {
    private final Repository repository;

    public LoginViewModel(Repository repository) {
        super();
        this.repository = repository;
    }

    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo) {
        return repository.login(userInfo);
    }
}