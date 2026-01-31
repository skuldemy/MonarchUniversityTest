package com.MonarchUniversity.MonarchUniversity.Impl;

import java.util.List;
import java.util.stream.Collectors;

import com.MonarchUniversity.MonarchUniversity.Model.Department;
import com.MonarchUniversity.MonarchUniversity.Repositories.DepartmentRepository;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Program;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseForbiddenException;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.LevelDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.LevelRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.ProgramRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepo;
    private final DepartmentRepository departmentRepo;

       public LevelDto createLevel(LevelDto dto) {

        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("Department not found"));

        Level level = new Level();
        level.setDepartment(department);
        level.setLevelNumber(dto.getLevelNumber());
        level.setSemester(dto.getSemester());
        level.setCapacity(dto.getCapacity());

        Level saved = levelRepo.save(level);
        return mapToDto(saved);
    }

  
    public LevelDto editLevel(Long id, LevelDto dto) {

        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Level not found"));

        if (dto.getLevelNumber() == null || dto.getLevelNumber().isBlank()) {
            throw new ResponseForbiddenException("Level number is required");
        }
        if (dto.getSemester() == null || dto.getSemester().isBlank()) {
            throw new ResponseForbiddenException("Semester is required");
        }
        if (dto.getDepartmentId() == null) {
            throw new ResponseForbiddenException("Program ID is required");
        }

        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("Department not found"));


        level.setDepartment(department);
        level.setLevelNumber(dto.getLevelNumber());
        level.setSemester(dto.getSemester());
        level.setCapacity(dto.getCapacity());

        Level updated = levelRepo.save(level);
        return mapToDto(updated);
    }

   
    public String deleteLevel(Long id) {
        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Level not found"));

        levelRepo.delete(level);
        return "Level successfully deleted";
    }

//    @Cacheable("levels")
//    public List<LevelDto> getAllLevels() {
//        return levelRepo.findAll()
//                .stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }
//    
//    @Cacheable("levels")
    public List<LevelDto> getAllLevels() {
        return levelRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    
    private LevelDto mapToDto(Level level) {
        return new LevelDto(
                level.getId(),
                level.getDepartment() != null ? level.getDepartment().getId() : null,
                level.getDepartment().getDepartmentName(),
                level.getLevelNumber(),
                level.getSemester(),
                level.getCapacity()
        );
    }
}
