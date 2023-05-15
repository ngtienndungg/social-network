package com.example.social_network.feature.homepage.friends;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_network.R;
import com.example.social_network.feature.homepage.DashboardActivity;
import com.example.social_network.feature.homepage.DashboardViewModel;
import com.example.social_network.model.friend.Friend;
import com.example.social_network.model.friend.FriendResponse;
import com.example.social_network.model.friend.Request;
import com.example.social_network.utils.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private DashboardViewModel viewModel;
    private Context context;
    private RecyclerView rvRequest, rvFriend;
    private TextView tvFriendTitle, tvRequestTitle, tvDefault;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList = new ArrayList<>();
    private RequestAdapter requestAdapter;
    private List<Request> requestList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider((FragmentActivity)context, new ViewModelFactory()).get(DashboardViewModel.class);

        rvFriend = view.findViewById(R.id.rvFriend);
        rvRequest = view.findViewById(R.id.rvRequest);
        tvDefault = view.findViewById(R.id.tvDefault);
        tvRequestTitle = view.findViewById(R.id.tvRequestTitle);
        tvFriendTitle = view.findViewById(R.id.tvFriendTitle);

        friendAdapter = new FriendAdapter(context, friendList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvFriend.setAdapter(friendAdapter);
        rvFriend.setLayoutManager(linearLayoutManager);
        loadFriends();

        requestAdapter = new RequestAdapter(context, requestList);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        rvRequest.setAdapter(requestAdapter);
        rvRequest.setLayoutManager(linearLayoutManager1);
    }

    private void loadFriends() {
        ((DashboardActivity)getActivity()).showProgressBar();
        viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).observe(getViewLifecycleOwner(), new Observer<FriendResponse>() {
            @Override
            public void onChanged(FriendResponse friendResponse) {
                loadData(friendResponse);
                ((DashboardActivity)getActivity()).hideProgressBar();
            }
        });
    }

    private void loadData(FriendResponse friendResponse) {
        if (friendResponse.getStatus() == 200) {
            friendList.clear();
            friendList.addAll(friendResponse.getResult().getFriends());
            friendAdapter.notifyDataSetChanged();

            requestList.clear();
            requestList.addAll(friendResponse.getResult().getRequests());
            requestAdapter.notifyDataSetChanged();

            if (friendResponse.getResult().getFriends().size()>0) {
                tvFriendTitle.setVisibility(View.VISIBLE);
            }
            else {
                tvFriendTitle.setVisibility(View.GONE);
            }

            if (friendResponse.getResult().getRequests().size()>0) {
                tvRequestTitle.setVisibility(View.VISIBLE);
            }
            else {
                tvRequestTitle.setVisibility(View.GONE);
            }

            if (friendResponse.getResult().getRequests().size() == 0 && friendResponse.getResult().getFriends().size() == 0) {
                tvDefault.setVisibility(View.VISIBLE);
            }
        }
        else {
            Toast.makeText(context, friendResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}