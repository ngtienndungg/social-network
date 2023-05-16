package com.example.social_network.feature.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.social_network.data.UserRepository;
import com.example.social_network.model.search.SearchResponse;

import java.util.Map;

public class SearchViewModel extends ViewModel {
    private UserRepository userRepository;

    public SearchViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<SearchResponse> search(Map<String, String> params) {
        return userRepository.search(params);
    }
}
