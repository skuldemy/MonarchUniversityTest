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
        if(levelRepo.existsByLevelNumberAndDepartment(dto.getLevelNumber(), department)){
            throw new ResponseNotFoundException("Level exists for this department");
        }
        level.setLevelNumber(dto.getLevelNumber());
        level.setCapacity(dto.getCapacity());

        Level saved = levelRepo.save(level);
        return mapToDto(saved);
    }

    public List<LevelDto>  getLevelByDepartment(Long departmentId){
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(()-> new ResponseNotFoundException("No such department"));
        List<Level> levels = levelRepo.findByDepartment(department);
        if(levels==null){
            return null;
        }
        return levels.stream()
                .map(l -> mapToDto(l)).collect(Collectors.toList());
    }
  
    public LevelDto editLevel(Long id, LevelDto dto) {

        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Level not found"));

        if (dto.getLevelNumber() == null ) {
            throw new ResponseForbiddenException("Level number is required");
        }

        if (dto.getDepartmentId() == null) {
            throw new ResponseForbiddenException("Department is required");
        }

        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResponseNotFoundException("Department not found"));

        if(levelRepo.existsByLevelNumberAndDepartmentAndIdNot(dto.getLevelNumber(), department,id)){
            throw new ResponseNotFoundException("Level exists for this department");
        }
        level.setDepartment(department);
        level.setLevelNumber(dto.getLevelNumber());
        level.setCapacity(dto.getCapacity());

        Level updated = levelRepo.save(level);
        return mapToDto(updated);
    }


    public LevelDto getLevelById(Long id){
           Level level = levelRepo.findById(id)
                   .orElseThrow(()-> new ResponseNotFoundException("No such level"));
           return mapToDto(level);
    }

    public String deleteLevel(Long id) {
        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Level not found"));

        levelRepo.delete(level);
        return "Level successfully deleted";
    }

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
                level.getCapacity()
        );
    }
}
