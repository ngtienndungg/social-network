package com.example.social_network.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.social_network.data.PostRepository;
import com.example.social_network.data.UserRepository;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.data.remote.ApiService;
import com.example.social_network.feature.auth.LoginViewModel;
import com.example.social_network.feature.homepage.DashboardViewModel;
import com.example.social_network.feature.postupload.PostUploadViewModel;
import com.example.social_network.feature.profile.ProfileViewModel;
import com.example.social_network.feature.search.SearchViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ViewModelFactory() {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        userRepository = UserRepository.getRepository(apiService);
        postRepository = PostRepository.getRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(userRepository, postRepository);
        } else if (modelClass.isAssignableFrom(PostUploadViewModel.class)) {
            return (T) new PostUploadViewModel(postRepository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(userRepository, postRepository);
        }
            throw new IllegalArgumentException("View Model is not founded");
    }
}
