package com.example.social_network.model.auth;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("auth")
    private Auth auth;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public AuthResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}