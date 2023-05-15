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

	public PostResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}
}