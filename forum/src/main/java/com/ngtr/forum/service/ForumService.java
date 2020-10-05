package com.ngtr.forum.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ngtr.forum.dto.ForumDto;
import com.ngtr.forum.dtoMapper.ForumDtoMapper;
import com.ngtr.forum.exception.ForumException;
import com.ngtr.forum.model.Forum;
import com.ngtr.forum.repository.ForumRepository;

@Service
public class ForumService {

	@Autowired
	private ForumRepository forumRepository;
	
	@Transactional
	public ForumDto save(ForumDto forumDto) {
		Forum savedForum = forumRepository.save(ForumDtoMapper.mapForumDto(forumDto));
		forumDto.setId(savedForum.getId());
		return forumDto;
	}

	@Transactional(readOnly = true)
	public List<ForumDto> getAll() {
		return forumRepository
				.findAll()
				.stream()
				.map(ForumDtoMapper :: mapToDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public ForumDto getForum(Long id) {
		Optional<Forum> forumOptional = forumRepository.findById(id);
		forumOptional.orElseThrow(() -> new ForumException("Cannot find forum with id: " + id));
		return ForumDtoMapper.mapToDto(forumOptional.get());
	}

	@Transactional(readOnly = true)
	public Forum getForumByName(String forumName) {
		Optional<Forum> forumOptional = forumRepository.findByName(forumName);
		forumOptional.orElseThrow(() -> new ForumException("Cannot find forum: " + forumName));
		return forumOptional.get();
	}
}
