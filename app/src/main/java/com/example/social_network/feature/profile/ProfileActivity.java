package com.example.social_network.feature.profile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_network.R;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.model.profile.ProfileResponse;
import com.example.social_network.utils.ViewModelFactory;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private String uid = "", avatarUrl = "", coverUrl = "";
    private int current_state = 0;
    // 1: loading, 2: fiend/unfriend, 3: received friend request, 4: no friend, 5: own profile

    private Button btProfileOption;
    private ImageView ivAvatar, ivCover;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ProfileViewModel.class);

        btProfileOption = findViewById(R.id.acbProfile);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivCover = findViewById(R.id.ivCover);
        recyclerView = findViewById(R.id.rvProfile);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        progressBar = findViewById(R.id.progressBar);


        uid = getIntent().getStringExtra("uid");

        if (uid.equals(FirebaseAuth.getInstance().getUid())) {
            current_state = 5;
            btProfileOption.setText(R.string.edit_profile);
        } else {
            btProfileOption.setText(R.string.loading);
            btProfileOption.setEnabled(false);
        }
        fetchProfileInfo();
    }

    private void fetchProfileInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getUid());
        if (current_state == 5) {
            params.put("current_state", current_state + "");
        }

        viewModel.fetchProfileInfo(params).observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(ProfileResponse profileResponse) {
                if (profileResponse.getStatus() == 200) {
                    collapsingToolbarLayout.setTitle(profileResponse.getProfile().getName());
                    avatarUrl = profileResponse.getProfile().getProfileUrl();
                    coverUrl = profileResponse.getProfile().getCoverUrl();
                    current_state = profileResponse.getProfile().getState();

                    if (!avatarUrl.isEmpty()) {
                        Uri avatarUri = Uri.parse(avatarUrl);
                        if (avatarUri.getAuthority() == null) {
                            avatarUrl = ApiClient.BASE_URL + avatarUrl;
                        }
                        Glide.with(ProfileActivity.this).load(avatarUrl).into(ivAvatar);
                    }

                    if (!coverUrl.isEmpty()) {
                        Uri coverUri = Uri.parse(coverUrl);
                        if (coverUri.getAuthority() == null) {
                            avatarUrl = ApiClient.BASE_URL + coverUrl;
                        }
                        Glide.with(ProfileActivity.this).load(coverUrl).into(ivCover);

                        if (current_state == 0) {
                            btProfileOption.setText(R.string.loading);
                            btProfileOption.setEnabled(false);
                            return;
                        } else if (current_state == 1) {
                            btProfileOption.setText(R.string.you_are_friends);
                        } else if (current_state == 2) {
                            btProfileOption.setText(R.string.cancel_request);
                        } else if (current_state == 3) {
                            btProfileOption.setText(R.string.accept_request);
                        } else if (current_state == 4) {
                            btProfileOption.setText(R.string.add_friend);
                        } else if (current_state == 5) {
                            btProfileOption.setText(R.string.edit_profile);
                        }
                        btProfileOption.setEnabled(true);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}