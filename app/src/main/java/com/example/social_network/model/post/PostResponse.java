package com.example.social_network.model.post;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PostResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("posts")
	private List<Post> posts;

	@SerializedName("status")
	private int status;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setPosts(List<Post> posts){
		this.posts = posts;
	}

	public List<Post> getPosts(){
		return posts;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	@SerializedName("loveCount")
	private int loveCount;

	@SerializedName("careCount")
	private int careCount;

	@SerializedName("wowCount")
	private int wowCount;

	@SerializedName("reactionType")
	private String reactionType;

	@SerializedName("sadCount")
	private int sadCount;

	@SerializedName("angryCount")
	private int angryCount;

	@SerializedName("likeCount")
	private int likeCount;

	@SerializedName("hahaCount")
	private int hahaCount;

	public void setLoveCount(int loveCount){
		this.loveCount = loveCount;
	}

	public int getLoveCount(){
		return loveCount;
	}

	public void setCareCount(int careCount){
		this.careCount = careCount;
	}

	public int getCareCount(){
		return careCount;
	}

	public void setWowCount(int wowCount){
		this.wowCount = wowCount;
	}

	public int getWowCount(){
		return wowCount;
	}

	public void setReactionType(String reactionType){
		this.reactionType = reactionType;
	}

	public String getReactionType(){
		return reactionType;
	}

	public void setSadCount(int sadCount){
		this.sadCount = sadCount;
	}

	public int getSadCount(){
		return sadCount;
	}

	public void setAngryCount(int angryCount){
		this.angryCount = angryCount;
	}

	public int getAngryCount(){
		return angryCount;
	}

	public void setLikeCount(int likeCount){
		this.likeCount = likeCount;
	}

	public int getLikeCount(){
		return likeCount;
	}

	public void setHahaCount(int hahaCount){
		this.hahaCount = hahaCount;
	}

	public int getHahaCount(){
		return hahaCount;
	}

	public PostResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}
}