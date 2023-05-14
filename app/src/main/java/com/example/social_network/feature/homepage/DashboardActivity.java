package com.example.social_network.feature.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.social_network.R;
import com.example.social_network.feature.homepage.friends.FriendsFragment;
import com.example.social_network.feature.homepage.newsfeed.NewsFeedFragment;
import com.example.social_network.feature.postupload.PostUploadActivity;
import com.example.social_network.feature.profile.ProfileActivity;
import com.example.social_network.feature.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FriendsFragment friendsFragment;
    private NewsFeedFragment newsFeedFragment;
    private FloatingActionButton fabCreate;
    private ImageView searchIcon;
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

        bottomNavigationView = findViewById(R.id.bnvNavigation);

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
}