package com.example.social_network.feature.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.social_network.data.PostRepository;
import com.example.social_network.data.UserRepository;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.post.PostResponse;
import com.example.social_network.model.profile.ProfileResponse;

import java.util.Map;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {
    private UserRepository userRepository;
    private PostRepository postRepository;

    public ProfileViewModel(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public LiveData<ProfileResponse> fetchProfileInfo(Map<String, String> params) {
        return userRepository.fetchProfileInfo(params);
    }

    public LiveData<PostResponse> getProfilePosts(Map<String, String> params) {
        return postRepository.getProfilePosts(params);
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage) {
        return postRepository.uploadPost(multipartBody, isCoverOrProfileImage);
    }

    public LiveData<GeneralResponse> performAction(ProfileActivity.PerformAction performAction) {
        return userRepository.performOperation(performAction);
    }
}
