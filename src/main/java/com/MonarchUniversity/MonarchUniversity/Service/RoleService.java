package com.MonarchUniversity.MonarchUniversity.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Payload.RoleDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final RoleRepository roleRepo;
	
	@Cacheable("roles")
	public List<RoleDto> getAllUserRoles(){
		return roleRepo.findAll().stream().map(r -> new RoleDto(r.getId(), r.getName())).collect(Collectors.toList());
	}
}
