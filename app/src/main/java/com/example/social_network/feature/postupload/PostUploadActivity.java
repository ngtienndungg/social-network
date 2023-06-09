package com.example.social_network.feature.postupload;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.social_network.R;
import com.example.social_network.feature.homepage.DashboardActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.utils.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostUploadActivity extends AppCompatActivity {

    private AppCompatSpinner spinner;
    private PostUploadViewModel viewModel;
    private TextView tvPost;
    private TextInputEditText tiePostContent;
    private int privacyLevel = 0;
    private ProgressDialog progressDialog;
    private Boolean isImageSelected = false;
    private ImageView ivAddImage, ivPreview;
    private File compressedImageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(PostUploadViewModel.class);
        tvPost = findViewById(R.id.btPost);
        tiePostContent = findViewById(R.id.tiePostContent);

        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.uploading_post));

        ivAddImage = findViewById(R.id.ivAddImage);
        ivPreview = findViewById(R.id.ivPreview);

        ivAddImage.setOnClickListener(v -> selectImage());

        ivPreview.setOnClickListener(v -> selectImage());
        spinner = findViewById(R.id.spinPrivacy);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedTextView = (TextView) view;
                if (selectedTextView!=null) {
                    selectedTextView.setTextColor(Color.WHITE);
                    selectedTextView.setTypeface(null, Typeface.BOLD);
                }
                privacyLevel = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                privacyLevel = 0;
            }
        });

        tvPost.setOnClickListener(v -> {
            String status = tiePostContent.getText().toString();
            String userId = FirebaseAuth.getInstance().getUid();

            if (status.trim().length()>0 || isImageSelected) {
                progressDialog.show();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("post", status);
                builder.addFormDataPart("postUserId", userId);
                builder.addFormDataPart("privacy", privacyLevel + "");

                if (isImageSelected) {
                    builder.addFormDataPart("file", compressedImageFile.getName(), RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));
                }
                MultipartBody multipartBody = builder.build();

                viewModel.uploadPost(multipartBody, false).observe(PostUploadActivity.this, new Observer<GeneralResponse>() {
                    @Override
                    public void onChanged(GeneralResponse generalResponse) {
                        progressDialog.hide();
                        Toast.makeText(PostUploadActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (generalResponse.getStatus()==200) {
                            onBackPressed();
                        }
                    }
                });
            }
            else {
                Toast.makeText(PostUploadActivity.this, R.string.write_your_status, Toast.LENGTH_SHORT).show();
            }
        });
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
                compressedImageFile = new Compressor(this).setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));
                isImageSelected = true;
                ivAddImage.setVisibility(View.GONE);
                ivPreview.setVisibility(View.VISIBLE);

                Glide.with(PostUploadActivity.this)
                        .load(selectedImage.getPath())
                        .error(R.drawable.ic_launcher_background)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivPreview);
            } catch (IOException e) {
                ivPreview.setVisibility(View.GONE);
                ivAddImage.setVisibility(View.VISIBLE);
            }
        }
    }
}