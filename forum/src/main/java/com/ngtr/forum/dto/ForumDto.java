package com.ngtr.forum.dto;

import java.util.List;

import com.ngtr.forum.model.Post;

public class ForumDto {
	private Long id;
	private String name;
	private String description;
	private Integer numberOfPosts;
	private List<Post> posts;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getNumberOfPosts() {
		return numberOfPosts;
	}
	public void setNumberOfPosts(Integer numberOfPosts) {
		this.numberOfPosts = numberOfPosts;
	}
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
}
