package com.example.social_network.feature.postupload;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.social_network.data.PostRepository;
import com.example.social_network.data.UserRepository;
import com.example.social_network.model.GeneralResponse;

import okhttp3.MultipartBody;

public class PostUploadViewModel extends ViewModel {
    private PostRepository postRepository;

    public PostUploadViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage) {
        return this.postRepository.uploadPost(multipartBody, isCoverOrProfileImage);
    }
}
