package com.example.social_network.feature.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.social_network.R;
import com.example.social_network.data.remote.ApiClient;
import com.example.social_network.feature.postupload.PostUploadActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.profile.ProfileResponse;
import com.example.social_network.utils.ViewModelFactory;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    private Boolean isCoverImage = false;
    private ProgressDialog progressDialog;

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
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setCancelable(false);

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
                            coverUrl = ApiClient.BASE_URL + coverUrl;
                        }
                        Glide.with(ProfileActivity.this).load(coverUrl).into(ivCover);
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
                } else {
                    Toast.makeText(ProfileActivity.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                    CharSequence[] options = new CharSequence[] {getResources().getString(R.string.option_change_cover), getResources().getString(R.string.option_profile_image)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle(R.string.choose_options);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which==0) {
                                isCoverImage = true;
                                selectImage();
                            }
                            else if (which==1) {
                                isCoverImage = false;
                                selectImage();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    private void selectImage() {
        ImagePicker.create(this).single().start();
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
                        Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL + generalResponse.getExtra()).into(ivCover);
                    }
                    else {
                        Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL + generalResponse.getExtra()).into(ivAvatar);
                    }
                }
            }
        });
    }
}