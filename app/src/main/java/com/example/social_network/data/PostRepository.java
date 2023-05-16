package com.example.social_network.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.social_network.data.remote.ApiError;
import com.example.social_network.data.remote.ApiService;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.post.PostResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {
    private static PostRepository instance = null;

    private final ApiService apiService;

    private PostRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public static PostRepository getRepository(ApiService apiService) {
        if (instance == null) {
            instance = new PostRepository(apiService);
        }
        return instance;
    }

    public LiveData<GeneralResponse> uploadPost(MultipartBody multipartBody, Boolean isCoverOrProfileImage) {
        MutableLiveData<GeneralResponse> postUpload = new MutableLiveData<>();
        Call<GeneralResponse> call = null;
        if (isCoverOrProfileImage) {
            call = apiService.uploadImage(multipartBody);
        }
        else {
            call = apiService.uploadPost(multipartBody);
        }
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    postUpload.postValue(response.body());
                }
                else {
                    Gson gson = new Gson();
                    GeneralResponse generalResponse = null;
                    try {
                        generalResponse = gson.fromJson(response.errorBody().string(), GeneralResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        generalResponse = new GeneralResponse(errorMessage.message, errorMessage.status);
                    }
                    postUpload.postValue(generalResponse);
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorMessageFromThrowable(t);
                GeneralResponse generalResponse= new GeneralResponse(errorMessage.message, errorMessage.status);
                postUpload.postValue(generalResponse);
            }
        });
        return postUpload;
    }

    public LiveData<PostResponse> getNewsfeed(Map<String, String> params) {
        MutableLiveData<com.example.social_network.model.post.PostResponse> posts = new MutableLiveData<>();
        Call<com.example.social_network.model.post.PostResponse> call = apiService.getNewsfeed(params);
        call.enqueue(new Callback<com.example.social_network.model.post.PostResponse>() {
            @Override
            public void onResponse(Call<com.example.social_network.model.post.PostResponse> call, Response<com.example.social_network.model.post.PostResponse> response) {
                if (response.isSuccessful()) {
                    posts.postValue(response.body());
                } else {
                    Gson gson = new Gson();
                    com.example.social_network.model.post.PostResponse postResponse = null;
                    try {
                        postResponse = gson.fromJson(response.errorBody().string(), com.example.social_network.model.post.PostResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        postResponse = new com.example.social_network.model.post.PostResponse(errorMessage.message, errorMessage.status);
                    }
                    posts.postValue(postResponse);
                }
            }

            @Override
            public void onFailure(Call<com.example.social_network.model.post.PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorMessageFromThrowable(t);
                com.example.social_network.model.post.PostResponse postResponse = new com.example.social_network.model.post.PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(postResponse);
            }
        });
        return posts;
    }

    public androidx.lifecycle.LiveData<PostResponse> getProfilePosts(Map<String, String> params) {
        MutableLiveData<PostResponse> posts = new MutableLiveData<>();
        Call<PostResponse> call = apiService.loadProfilePosts(params);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    posts.postValue(response.body());
                } else {
                    Gson gson = new Gson();
                    PostResponse postResponse = null;
                    try {
                        postResponse = gson.fromJson(response.errorBody().string(), PostResponse.class);
                    } catch (IOException e) {
                        ApiError.ErrorMessage errorMessage = ApiError.getErrorFromException(e);
                        postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                    }
                    posts.postValue(postResponse);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                ApiError.ErrorMessage errorMessage = ApiError.getErrorMessageFromThrowable(t);
                PostResponse postResponse = new PostResponse(errorMessage.message, errorMessage.status);
                posts.postValue(postResponse);
            }
        });
        return posts;
    }
}
