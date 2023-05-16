package com.example.social_network.feature.fullimage;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.social_network.R;
import com.example.social_network.data.remote.ApiClient;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        String image = getIntent().getStringExtra("imageUrl");

        Uri uri = Uri.parse(image);
        PhotoView photoView = findViewById(R.id.photoView);

        if (uri.getAuthority()==null) {
            if (image.contains("../")) {
                image = ApiClient.BASE_URL + image;
                Glide.with(this).load(image).into(photoView);
            }
            else {
                Glide.with(this).load(Integer.parseInt(image)).into(photoView);
            }
        }

        Glide.with(this).load(image).into(photoView);
    }
}