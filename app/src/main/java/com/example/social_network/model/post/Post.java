package com.example.social_network.model.post;

import com.google.gson.annotations.SerializedName;

public class Post {

	@SerializedName("profileUrl")
	private String profileUrl;

	@SerializedName("coverUrl")
	private String coverUrl;

	@SerializedName("postUserId")
	private String postUserId;

	@SerializedName("statusImage")
	private String statusImage;

	@SerializedName("uid")
	private String uid;

	@SerializedName("userToken")
	private String userToken;

	@SerializedName("post")
	private String post;

	@SerializedName("statusTime")
	private String statusTime;

	@SerializedName("name")
	private String name;

	@SerializedName("privacy")
	private String privacy;

	@SerializedName("postId")
	private String postId;

	@SerializedName("email")
	private String email;

	public void setProfileUrl(String profileUrl){
		this.profileUrl = profileUrl;
	}

	public String getProfileUrl(){
		return profileUrl;
	}

	public void setCoverUrl(String coverUrl){
		this.coverUrl = coverUrl;
	}

	public String getCoverUrl(){
		return coverUrl;
	}

	public void setPostUserId(String postUserId){
		this.postUserId = postUserId;
	}

	public String getPostUserId(){
		return postUserId;
	}

	public void setStatusImage(String statusImage){
		this.statusImage = statusImage;
	}

	public String getStatusImage(){
		return statusImage;
	}

	public void setUid(String uid){
		this.uid = uid;
	}

	public String getUid(){
		return uid;
	}

	public void setUserToken(String userToken){
		this.userToken = userToken;
	}

	public String getUserToken(){
		return userToken;
	}

	public void setPost(String post){
		this.post = post;
	}

	public String getPost(){
		return post;
	}

	public void setStatusTime(String statusTime){
		this.statusTime = statusTime;
	}

	public String getStatusTime(){
		return statusTime;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPrivacy(String privacy){
		this.privacy = privacy;
	}

	public String getPrivacy(){
		return privacy;
	}

	public void setPostId(String postId){
		this.postId = postId;
	}

	public String getPostId(){
		return postId;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}
}