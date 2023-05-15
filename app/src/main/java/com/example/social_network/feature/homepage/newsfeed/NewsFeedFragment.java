package com.example.social_network.feature.homepage.newsfeed;

import android.content.Context;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.social_network.R;
import com.example.social_network.feature.homepage.DashboardActivity;
import com.example.social_network.feature.homepage.DashboardViewModel;
import com.example.social_network.model.post.Post;
import com.example.social_network.model.post.PostResponse;
import com.example.social_network.utils.ViewModelFactory;
import com.example.social_network.utils.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFeedFragment extends Fragment {

    private RecyclerView rvNewsfeed;
    private DashboardViewModel viewModel;
    private Context context;
    private SwipeRefreshLayout srlContent;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();
    private Boolean isFirstLoading = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNewsfeed = view.findViewById(R.id.rvNewsfeed);
        srlContent = view.findViewById(R.id.srlContent);

        postAdapter = new PostAdapter(context, posts);

        viewModel = new ViewModelProvider((FragmentActivity)context, new ViewModelFactory()).get(DashboardViewModel.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvNewsfeed.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNewsfeed();
    }

    private void fetchNewsfeed() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", FirebaseAuth.getInstance().getUid());
        params.put("limit", "1");
        params.put("offset", "0");

        ((DashboardActivity) getActivity()).showProgressBar();

        viewModel.getNewsfeed(params).observe(getViewLifecycleOwner(), new Observer<PostResponse>() {
            @Override
            public void onChanged(PostResponse postResponse) {
                ((DashboardActivity) getActivity()).hideProgressBar();
                if (postResponse.getStatus() == 200) {
                    posts.addAll(postResponse.getPosts());
                    if (isFirstLoading) {
                        postAdapter = new PostAdapter(context, posts);
                        rvNewsfeed.setAdapter(postAdapter);
                    }
                    else {
                        postAdapter.notifyItemRangeInserted(posts.size(), postResponse.getPosts().size());
                    }
                    isFirstLoading = false;
                }
                else {
                    Toast.makeText(context, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        posts.clear();
        isFirstLoading = true;
    }
}