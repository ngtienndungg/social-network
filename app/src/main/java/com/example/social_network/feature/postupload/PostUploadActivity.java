package com.example.social_network.feature.postupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.social_network.R;
import com.example.social_network.feature.homepage.DashboardActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.utils.ViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import okhttp3.MultipartBody;

public class PostUploadActivity extends AppCompatActivity {

    private AppCompatSpinner spinner;
    private PostUploadViewModel viewModel;
    private TextView tvPost;
    private TextInputEditText tiePostContent;
    private int privacyLevel = 0;
    private ProgressDialog progressDialog;

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

        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = tiePostContent.getText().toString();
                String userId = FirebaseAuth.getInstance().getUid();

                if (status.trim().length()>0) {
                    progressDialog.show();
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("post", status);
                    builder.addFormDataPart("postUserId", userId);
                    builder.addFormDataPart("privacy", privacyLevel + "");

                    MultipartBody multipartBody = builder.build();

                    viewModel.uploadPost(multipartBody).observe(PostUploadActivity.this, new Observer<GeneralResponse>() {
                        @Override
                        public void onChanged(GeneralResponse generalResponse) {
                            progressDialog.hide();
                            Toast.makeText(PostUploadActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (generalResponse.getStatus()==200) {
                                Intent intent = new Intent(PostUploadActivity.this, DashboardActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(PostUploadActivity.this, R.string.write_your_status, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}