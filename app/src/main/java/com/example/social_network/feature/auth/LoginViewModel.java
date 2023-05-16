package com.example.social_network.feature.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.social_network.data.UserRepository;
import com.example.social_network.model.auth.AuthResponse;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;

    public LoginViewModel(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    public LiveData<AuthResponse> login(LoginActivity.UserInfo userInfo) {
        return userRepository.login(userInfo);
    }
}