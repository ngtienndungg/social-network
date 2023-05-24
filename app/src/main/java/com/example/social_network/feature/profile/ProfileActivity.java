package com.example.social_network.feature.profile;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.social_network.R;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.feature.auth.LoginActivity;
import com.example.social_network.feature.fullimage.FullImageActivity;
import com.example.social_network.feature.postupload.PostUploadActivity;
import com.example.social_network.feature.search.SearchActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.post.Post;
import com.example.social_network.model.post.PostResponse;
import com.example.social_network.model.profile.ProfileResponse;
import com.example.social_network.model.reaction.ReactResponse;
import com.example.social_network.utils.Util;
import com.example.social_network.utils.ViewModelFactory;
import com.example.social_network.utils.adapter.PostAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener, SwipeRefreshLayout.OnRefreshListener, PostAdapter.IUpdateUserReaction {

    private String uid = "", avatarUrl = "", coverUrl = "";
    private int current_state = 0;
    // 1: loading, 2: fiend/unfriend, 3: received friend request, 4: no friend, 5: own profile

    private Button btProfileOption;
    private ImageView ivAvatar, ivCover;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView rvProfile;
    private ProgressBar progressBar;
    private ProfileViewModel viewModel;
    private SwipeRefreshLayout srlPost;

    private Boolean isCoverImage = false;
    private ProgressDialog progressDialog;

    private PostAdapter postAdapter;

    private List<Post> posts = new ArrayList<>();
    private Boolean isFirstLoading = true;
    private int limit = 5;
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ProfileViewModel.class);

        btProfileOption = findViewById(R.id.acbProfile);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivCover = findViewById(R.id.ivCover);
        rvProfile = findViewById(R.id.rvProfile);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        progressBar = findViewById(R.id.progressBar);
        srlPost = findViewById(R.id.srlPost);

        srlPost.setOnRefreshListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvProfile.setLayoutManager(linearLayoutManager);

        rvProfile.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemReached()) {
                    offset+=limit;
                    getProfilePosts();
                }
            }
        });

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });

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
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getUid());
        if (current_state == 5) {
            params.put("current_state", current_state + "");
        }
        else {
            params.put("profileId", uid);
        }

        viewModel.fetchProfileInfo(params).observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(ProfileResponse profileResponse) {
                progressDialog.hide();
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
                        Glide.with(ProfileActivity.this).load(avatarUrl).placeholder(R.drawable.default_profile_placeholder).into(ivAvatar);
                    }
                    else {
                        avatarUrl = R.drawable.default_profile_placeholder + "";
                    }

                    if (!coverUrl.isEmpty()) {
                        Uri coverUri = Uri.parse(coverUrl);
                        if (coverUri.getAuthority() == null) {
                            coverUrl = ApiClient.BASE_URL + coverUrl;
                        }
                        Glide.with(ProfileActivity.this).load(coverUrl).placeholder(R.drawable.cover_default_placeholder).into(ivCover);
                    }
                    else {
                        coverUrl = R.drawable.cover_default_placeholder + "";
                    }
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
                    loadBtProfileOption();
                    getProfilePosts();
                } else {
                    Toast.makeText(ProfileActivity.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getProfilePosts() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("limit", limit + "");
        params.put("offset", offset + "");
        params.put("current_state", current_state + "");

        progressBar.setVisibility(View.VISIBLE);

        viewModel.getProfilePosts(params).observe(this, new Observer<PostResponse>() {
            @Override
            public void onChanged(PostResponse postResponse) {
                progressBar.setVisibility(View.GONE);
                if (postResponse.getStatus() == 200) {

                    if (srlPost.isRefreshing()) {
                        posts.clear();
                        postAdapter.notifyDataSetChanged();
                        srlPost.setRefreshing(false);
                    }
                    posts.addAll(postResponse.getPosts());

                    if (isFirstLoading) {
                        postAdapter = new PostAdapter(ProfileActivity.this, posts);
                        rvProfile.setAdapter(postAdapter);
                    }
                    else {
                        postAdapter.notifyItemRangeInserted(posts.size(), postResponse.getPosts().size());
                    }
                    if (postResponse.getPosts().size()==0) {
                        offset-=limit;
                    }
                    isFirstLoading = false;
                }
                else {
                    if (srlPost.isRefreshing()) srlPost.setRefreshing(false);
                    Toast.makeText(ProfileActivity.this, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadBtProfileOption() {
        btProfileOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btProfileOption.setEnabled(false);

                if (current_state == 5) {
                    CharSequence[] options = new CharSequence[]{
                            getResources().getString(R.string.option_change_cover),
                            getResources().getString(R.string.option_profile_image),
                            getResources().getString(R.string.option_view_cover),
                            getResources().getString(R.string.option_view_avatar),
                            "Sign out"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle(R.string.choose_options);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                isCoverImage = true;
                                selectImage();
                            } else if (which == 1) {
                                isCoverImage = false;
                                selectImage();
                            } else if (which == 2) {
                                viewFullImage(ivCover, coverUrl);
                            } else if (which == 3) {
                                viewFullImage(ivAvatar, avatarUrl);
                            }
                            else {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 4) {
                    CharSequence[] options = new CharSequence[]{
                            getResources().getString(R.string.add_friend)
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle(R.string.choose_options);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                isCoverImage = true;
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 3) {
                    CharSequence[] options = new CharSequence[]{
                            getResources().getString(R.string.accept_request)
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle(R.string.choose_options);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                isCoverImage = true;
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 2) {
                    CharSequence[] options = new CharSequence[]{
                            getResources().getString(R.string.cancel_request)
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle(R.string.choose_options);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                isCoverImage = true;
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                }
                else if (current_state == 1) {
                    CharSequence[] options = new CharSequence[]{
                            getResources().getString(R.string.unfriend)
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle(R.string.choose_options);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                isCoverImage = true;
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                }
            }
        });
    }

    private boolean isLastItemReached() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvProfile.getLayoutManager();
        int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int numberOfItems = postAdapter.getItemCount();
        return (position >= numberOfItems - 1);
    }

    private void performAction() {
        progressDialog.show();
        viewModel.performAction(new PerformAction(current_state + "", FirebaseAuth.getInstance().getUid(), uid)).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus() == 200) {
                    btProfileOption.setEnabled(true);
                    if (current_state == 4) {
                        current_state = 2;
                        btProfileOption.setText(R.string.cancel_request);
                    } else if (current_state == 3) {
                        current_state = 1;
                        btProfileOption.setText(R.string.you_are_friends);
                    } else if (current_state == 2) {
                        current_state = 4;
                        btProfileOption.setText(R.string.add_friend);
                    }
                    else if (current_state == 1) {
                        current_state = 4;
                        btProfileOption.setText(R.string.add_friend);
                    }
                }
                else {
                    btProfileOption.setEnabled(false);
                    btProfileOption.setText(R.string.error);
                }
            }
        });
    }

    private void viewFullImage(ImageView imageView, String imageUrl) {
        Intent intent = new Intent(ProfileActivity.this, FullImageActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(imageView, imageUrl);
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pairs);
            startActivity(intent, activityOptions.toBundle());
        }
        else {
            startActivity(intent);
        }
    }

    private void selectImage() {
        ImagePicker.create(this).single().folderMode(true).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);
            try {
                File compressedImageFile = new Compressor(this).setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));
                uploadImage(compressedImageFile);
            } catch (IOException e) {
                Toast.makeText(this, R.string.image_picker_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(File compressedImageFile) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("uid", FirebaseAuth.getInstance().getUid() + "");
        builder.addFormDataPart("isCoverImage", isCoverImage + "");
        builder.addFormDataPart("file", compressedImageFile.getName(),
                RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));
        progressDialog.show();

        viewModel.uploadPost(builder.build(), true).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus() == 200) {
                    if (isCoverImage) {
                        Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL + generalResponse.getExtra()).placeholder(R.drawable.cover_default_placeholder).into(ivCover);
                    }
                    else {
                        Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL + generalResponse.getExtra()).placeholder(R.drawable.default_profile_placeholder).into(ivAvatar);
                    }
                }
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        btProfileOption.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        offset = 0;
        posts.clear();
        isFirstLoading = true;
    }

    @Override
    public void onRefresh() {
        offset = 0;
        isFirstLoading = true;
        getProfilePosts();
    }

    @Override
    public void updateUserReaction(String uid, int postId, String postOwnerId, String previousReactionType, String newReactionType, int adapterPosition) {
        viewModel.performReaction(new Util.PerformReaction(uid, postId + "", postOwnerId, previousReactionType, newReactionType)).observe(this, new Observer<ReactResponse>() {
            @Override
            public void onChanged(ReactResponse reactResponse) {
                if (reactResponse.getStatus() == 200) {
                    postAdapter.updatePostAfterReaction(adapterPosition, reactResponse.getReaction());
                } 
                else {
                    Toast.makeText(ProfileActivity.this, reactResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class PerformAction {
        String operationType, uid, profileId;

        public PerformAction(String operationType, String uid, String profileId) {
            this.operationType = operationType;
            this.uid = uid;
            this.profileId = profileId;
        }
    }
}

