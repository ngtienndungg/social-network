package com.example.social_network.model.auth;

import com.google.gson.annotations.SerializedName;

public class Auth {

    @SerializedName("profileUrl")
    private String profileUrl;

    @SerializedName("coverUrl")
    private String coverUrl;

    @SerializedName("uid")
    private String uid;

    @SerializedName("userToken")
    private String userToken;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}