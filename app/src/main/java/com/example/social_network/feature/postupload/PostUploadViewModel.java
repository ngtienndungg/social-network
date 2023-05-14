package com.example.social_network.feature.postupload;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.social_network.data.Repository;
import com.example.social_network.model.GeneralResponse;

import okhttp3.MultipartBody;

public class PostUploadViewModel extends ViewModel {
    private Repository repository;

    public PostUploadViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage) {
        return this.repository.uploadPost(multipartBody, isCoverOrProfileImage);
    }
}
