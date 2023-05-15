package com.example.social_network.feature.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.social_network.R;
import com.example.social_network.feature.homepage.friends.FriendAdapter;
import com.example.social_network.feature.homepage.friends.FriendsFragment;
import com.example.social_network.feature.homepage.friends.RequestAdapter;
import com.example.social_network.feature.homepage.newsfeed.NewsFeedFragment;
import com.example.social_network.feature.postupload.PostUploadActivity;
import com.example.social_network.feature.profile.ProfileActivity;
import com.example.social_network.feature.profile.ProfileViewModel;
import com.example.social_network.feature.search.SearchActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.friend.FriendResponse;
import com.example.social_network.utils.ViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity implements RequestAdapter.IPerformAction {

    private BottomNavigationView bottomNavigationView;
    private FriendsFragment friendsFragment;
    private NewsFeedFragment newsFeedFragment;
    private FloatingActionButton fabCreate;
    private ImageView searchIcon;
    private ProgressBar progressBar;
    private DashboardViewModel viewModel;
    private BottomNavigationView.OnItemSelectedListener selectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.newsFeedFragment:
                    setFragment(newsFeedFragment);
                    return true;

                case R.id.friendsFragment:
                    setFragment(friendsFragment);
                    return true;

                case R.id.profileActivity:
                    startActivity(new Intent(DashboardActivity.this, ProfileActivity.class).putExtra("uid", FirebaseAuth.getInstance().getUid()));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(DashboardViewModel.class);

        bottomNavigationView = findViewById(R.id.bnvNavigation);
        progressBar = findViewById(R.id.progressBar);

        fabCreate = findViewById(R.id.fabCreate);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, PostUploadActivity.class));
            }
        });

        searchIcon = findViewById(R.id.ivSearch);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SearchActivity.class));
            }
        });
        friendsFragment = new FriendsFragment();
        newsFeedFragment = new NewsFeedFragment();
        setFragment(newsFeedFragment);

        bottomNavigationView.setOnItemSelectedListener(selectedListener);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment).commit();
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void performAction(int position, String profileId, int operationType) {
        showProgressBar();
        viewModel.performAction(new ProfileActivity.PerformAction(
                operationType + "",
                FirebaseAuth.getInstance().getUid(),
                profileId)).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                hideProgressBar();
                Toast.makeText(DashboardActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus() == 200) {
                    FriendResponse response = viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).getValue();
                    response.getResult().getRequests().remove(position);
                    viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).setValue(response);
                }
            }
        });
    }
}