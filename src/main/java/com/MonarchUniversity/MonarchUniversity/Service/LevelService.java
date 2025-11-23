package com.MonarchUniversity.MonarchUniversity.Service;

import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Level;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.LevelDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.LevelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LevelService {
	
	private final LevelRepository levelRepo;
	
	public LevelDto createLevel(LevelDto dto) {
		Level level = new Level();
		level.setCapacity(dto.getCapacity());
		level.setLevelNumber(dto.getLevelNumber());
		level.setProgram(dto.getProgram());
		level.setSemester(dto.getSemester());
	
		levelRepo.save(level);
		
		return dto;
	}
	
	public LevelDto editLevel(Long id, LevelDto dto) {
		Level level = levelRepo.findById(id).orElseThrow(()-> new ResponseNotFoundException("No such Id"));
		level.setCapacity(dto.getCapacity());
		level.setLevelNumber(dto.getLevelNumber());
		level.setSemester(dto.getSemester());
		level.setProgram(dto.getProgram());
		
		Level savedLevel = levelRepo.save(level);
		return new LevelDto(savedLevel.getId(), savedLevel.getProgram(),
				savedLevel.getLevelNumber(), savedLevel.getSemester(),
				savedLevel.getCapacity());
	}
	
	public String deleteByLevelId(Long id) {
		Level level = levelRepo.findById(id).orElseThrow(()-> new ResponseNotFoundException("No such Id"));
		levelRepo.delete(level);
		return "Level successfully deleted";
	}
}
