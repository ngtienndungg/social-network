package com.example.social_network.feature.homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_network.data.Repository;
import com.example.social_network.feature.profile.ProfileActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.friend.FriendResponse;
import com.example.social_network.model.post.PostResponse;

import java.util.Map;

public class DashboardViewModel extends ViewModel {
    private final Repository repository;

    public DashboardViewModel(Repository repository) {
        this.repository = repository;
    }

    private MutableLiveData<FriendResponse> friends = null;

    public MutableLiveData<FriendResponse> loadFriends(String uid) {
        if (friends == null) {
            friends = repository.loadFriends(uid);
        }
        return repository.loadFriends(uid);
    }

    public LiveData<GeneralResponse> performAction(ProfileActivity.PerformAction performAction) {
        return repository.performOperation(performAction);
    }

    public LiveData<PostResponse> getNewsfeed(Map<String, String> params) {
        return repository.getNewsfeed(params);
    }
}
