package com.MonarchUniversity.MonarchUniversity.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Level;
import com.MonarchUniversity.MonarchUniversity.Entity.Program;
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
    private final ProgramRepository programRepo;

       public LevelDto createLevel(LevelDto dto) {

        Program program = programRepo.findById(dto.getProgramId())
                .orElseThrow(() -> new ResponseNotFoundException("Program not found"));

        Level level = new Level();
        level.setProgram(program);
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
        if (dto.getProgramId() == null) {
            throw new ResponseForbiddenException("Program ID is required");
        }

        Program program = programRepo.findById(dto.getProgramId())
                .orElseThrow(() -> new ResponseNotFoundException("Program not found"));

        level.setProgram(program);
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

    @Cacheable("levels")
    public List<LevelDto> getAllLevels() {
        return levelRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    
    private LevelDto mapToDto(Level level) {
        return new LevelDto(
                level.getId(),
                level.getProgram() != null ? level.getProgram().getId() : null,
                level.getProgram().getProgramName(),
                level.getLevelNumber(),
                level.getSemester(),
                level.getCapacity()
        );
    }
}
