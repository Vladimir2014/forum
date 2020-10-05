package com.ngtr.forum.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngtr.forum.dto.PostDto;
import com.ngtr.forum.dtoMapper.PostDtoMapper;
import com.ngtr.forum.exception.ForumException;
import com.ngtr.forum.model.Forum;
import com.ngtr.forum.model.Post;
import com.ngtr.forum.model.User;
import com.ngtr.forum.model.Vote;
import com.ngtr.forum.model.VoteType;
import com.ngtr.forum.repository.ForumRepository;
import com.ngtr.forum.repository.PostRepository;
import com.ngtr.forum.repository.UserRepository;
import com.ngtr.forum.repository.VoteRepository;

@Service
public class PostService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ForumRepository forumRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private ForumService forumService;
	
	@Autowired
	private VoteRepository voteRepository;
	
	@Autowired
	private PostDtoMapper postDtoMapper;
	
	@Transactional
	public PostDto save(PostDto postDto) {
		Post post = postDtoMapper.toPost(postDto);
		post.setCreatedOn(Instant.now());
		post.setUser(authService.getCurrentUser());
		post.setForum(forumService.getForumByName(postDto.getForumName()));
		Post savedPost = postRepository.save(post);
		postDto.setPostId(savedPost.getPostId());
		return postDto;
	}

	@Transactional(readOnly = true)
	public PostDto getPost(Long id) {
		Optional<Post> post = postRepository.findById(id);
		post.orElseThrow(() -> new ForumException("Cannot find forum for id: " + id));
		return postDtoMapper.toPostDto(post.get());
	}
	
	@Transactional(readOnly = true)
	public Post findById(Long id) {
		return postRepository.findById(id)
				.orElseThrow(() -> new ForumException("Cannot find forum for id: " + id));
	}

	@Transactional(readOnly = true)
	public List<PostDto> getAllPosts() {
		return postRepository
				.findAll()
				.stream()
				.map(postDtoMapper :: toPostDto)
				.collect(Collectors.toList())
				;
	}

	@Transactional(readOnly = true)
	public List<PostDto> getPostsForForum(Long id) {
		Forum forum = forumRepository
				.findById(id)
				.orElseThrow(() -> new ForumException("Cannot find forum for id: " + id));
		
		return postRepository
				.findByForum(forum)
				.stream()
				.map(postDtoMapper :: toPostDto)
				.collect(Collectors.toList())
				;
	}

	@Transactional(readOnly = true)
	public List<PostDto> getPostsForUser(String name) {
		User user = userRepository
				.findByUsername(name)
				.orElseThrow(() -> new ForumException("Cannot find user: " + name));
		
		return postRepository
				.findByUser(user)
				.stream()
				.map(postDtoMapper :: toPostDto)
				.collect(Collectors.toList())
				;
	}
	
	public boolean checkVoteType(Post post, VoteType downvote) {
		if (authService.isLoggedIn()) {
			Optional<Vote> voteForPostByUser = voteRepository.findTopByPostAndUserOrderByIdDesc(post, authService.getCurrentUser());
			return voteForPostByUser.filter(vote -> vote.getVoteType().equals(downvote)).isPresent();
			
		}
		return false;
	}

}
