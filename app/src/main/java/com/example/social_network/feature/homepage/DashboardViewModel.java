package com.example.social_network.feature.homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.social_network.data.PostRepository;
import com.example.social_network.data.UserRepository;
import com.example.social_network.feature.profile.ProfileActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.friend.FriendResponse;
import com.example.social_network.model.post.PostResponse;

import java.util.Map;

public class DashboardViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public DashboardViewModel(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    private MutableLiveData<FriendResponse> friends = null;

    public MutableLiveData<FriendResponse> loadFriends(String uid) {
        if (friends == null) {
            friends = userRepository.loadFriends(uid);
        }
        return userRepository.loadFriends(uid);
    }

    public LiveData<GeneralResponse> performAction(ProfileActivity.PerformAction performAction) {
        return userRepository.performOperation(performAction);
    }

    public LiveData<PostResponse> getNewsfeed(Map<String, String> params) {
        return postRepository.getNewsfeed(params);
    }
}
