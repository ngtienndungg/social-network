package com.example.social_network.feature.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.social_network.data.Repository;
import com.example.social_network.model.profile.ProfileResponse;

import java.util.Map;

public class ProfileViewModel extends ViewModel {
    private Repository repository;

    public ProfileViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<ProfileResponse> fetchProfileInfo(Map<String, String> params) {
        return repository.fetchProfileInfo(params);
    }
}
