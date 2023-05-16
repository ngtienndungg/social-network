package com.example.social_network.data.remote;

import com.example.social_network.feature.auth.LoginActivity;
import com.example.social_network.feature.profile.ProfileActivity;
import com.example.social_network.model.GeneralResponse;
import com.example.social_network.model.auth.AuthResponse;
import com.example.social_network.model.friend.FriendResponse;
import com.example.social_network.model.post.PostResponse;
import com.example.social_network.model.profile.ProfileResponse;
import com.example.social_network.model.reaction.ReactResponse;
import com.example.social_network.model.search.SearchResponse;
import com.example.social_network.utils.Util;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);

    @POST("performreaction")
    Call<ReactResponse> performReaction(@Body Util.PerformReaction performReaction);

    @POST("uploadpost")
    Call<GeneralResponse> uploadPost(@Body MultipartBody body);

    @POST("uploadimage")
    Call<GeneralResponse> uploadImage(@Body MultipartBody body);

    @GET("getnewsfeed")
    Call<PostResponse> getNewsfeed(@QueryMap Map<String, String> params);

    @GET("loadprofileinfo")
    Call<ProfileResponse> fetchProfileInfo(@QueryMap Map<String, String> params);

    @GET("loadprofileposts")
    Call<PostResponse> loadProfilePosts(@QueryMap Map<String, String> params);

    @GET("search")
    Call<SearchResponse> search(@QueryMap Map<String, String> params);

    @GET("loadfriends")
    Call<FriendResponse> loadFriends(@Query("uid") String uid);

    @POST("performaction")
    Call<GeneralResponse> performAction(@Body ProfileActivity.PerformAction performAction);
}
