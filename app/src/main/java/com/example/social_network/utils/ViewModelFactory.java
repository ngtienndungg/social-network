package com.example.social_network.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.social_network.data.Repository;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.data.remote.ApiService;
import com.example.social_network.feature.auth.LoginViewModel;
import com.example.social_network.feature.postupload.PostUploadViewModel;
import com.example.social_network.feature.profile.ProfileViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Repository repository;

    public ViewModelFactory() {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        repository = Repository.getRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(repository);
        } else if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(repository);
        } else if (modelClass.isAssignableFrom(PostUploadViewModel.class)) {
            return (T) new PostUploadViewModel(repository);
        }
        throw new IllegalArgumentException("View Model is not founded");
    }
}
