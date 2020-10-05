package com.ngtr.forum.dto;

public class PostDto {
	private Long postId;
	private String forumName;
	private String postName;
	private String url;
	private String description;
	
	private String userName;
	private Integer voteCount;
	private Integer commnetCount;
	private String duration;
	
	private boolean isPostUpVoted;
	private boolean isPostDownVoted;
	
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public String getForumName() {
		return forumName;
	}
	public void setForumName(String forumName) {
		this.forumName = forumName;
	}
	public String getPostName() {
		return postName;
	}
	public void setPostName(String postName) {
		this.postName = postName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getVoteCount() {
		return voteCount;
	}
	public void setVoteCount(Integer voteCount) {
		this.voteCount = voteCount;
	}
	public Integer getCommnetCount() {
		return commnetCount;
	}
	public void setCommnetCount(Integer commnetCount) {
		this.commnetCount = commnetCount;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public boolean isPostUpVoted() {
		return isPostUpVoted;
	}
	public void setPostUpVoted(boolean isPostUpVoted) {
		this.isPostUpVoted = isPostUpVoted;
	}
	public boolean isPostDownVoted() {
		return isPostDownVoted;
	}
	public void setPostDownVoted(boolean isPostDownVoted) {
		this.isPostDownVoted = isPostDownVoted;
	}
	
}
