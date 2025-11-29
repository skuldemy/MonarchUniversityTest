package com.MonarchUniversity.MonarchUniversity.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Faculty;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseForbiddenException;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.FacultyDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.FacultyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepo;

    public long numberOfFaculties() {
    	return facultyRepo.count();
    }
    
    public FacultyDto createFaculty(FacultyDto dto) {
        Faculty faculty = new Faculty();
        faculty.setFacultyName(dto.getFacultyName());
        faculty.setFacultyCode(dto.getFacultyCode());
        faculty.setFacultyDescription(dto.getFacultyDescription());
        faculty.setFacultyEmail(dto.getFacultyEmail());
        faculty.setFacultyAddress(dto.getFacultyAddress());
        faculty.setEstablishedYear(dto.getEstablishedYear());
        faculty.setFacultyMotto(dto.getFacultyMotto());

        Faculty savedFaculty = facultyRepo.save(faculty);

        return new FacultyDto(
        	    savedFaculty.getId(),
        	    savedFaculty.getFacultyName(),
        	    savedFaculty.getFacultyCode(),            
        	    savedFaculty.getFacultyDescription(),
        	    savedFaculty.getFacultyEmail(),           
        	    savedFaculty.getFacultyAddress(),
        	    savedFaculty.getEstablishedYear(),
        	    savedFaculty.getFacultyMotto()
        	);
 
    }

    public FacultyDto editFaculty(Long id, FacultyDto dto) {
        Faculty faculty = facultyRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such Faculty Id"));

        if (dto.getFacultyName() == null || dto.getFacultyName().isBlank()) {
            throw new ResponseForbiddenException("Faculty name is required");
        }
        if (dto.getFacultyCode() == null || dto.getFacultyCode().isBlank()) {
            throw new ResponseForbiddenException("Faculty code is required");
        }

        faculty.setFacultyName(dto.getFacultyName());
        faculty.setFacultyCode(dto.getFacultyCode());
        faculty.setFacultyDescription(dto.getFacultyDescription());
        faculty.setFacultyEmail(dto.getFacultyEmail());
        faculty.setFacultyAddress(dto.getFacultyAddress());
        faculty.setEstablishedYear(dto.getEstablishedYear());
        faculty.setFacultyMotto(dto.getFacultyMotto());

        Faculty saved = facultyRepo.save(faculty);

        return new FacultyDto(
                saved.getId(),
                saved.getFacultyName(),
                saved.getFacultyCode(),
                saved.getFacultyDescription(),
                saved.getFacultyEmail(),
                saved.getFacultyAddress(),
                saved.getEstablishedYear(),
                saved.getFacultyMotto()
        );
    }

    
    public String deleteFaculty(Long id) {
        Faculty faculty = facultyRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such Faculty Id"));

        facultyRepo.delete(faculty);
        return "Faculty successfully deleted";
    }
    @Cacheable("faculties")
    public List<FacultyDto> getAllFaculties(){
    	return facultyRepo.findAll().stream().map(r-> new FacultyDto(
                r.getId(),
                r.getFacultyName(),
                r.getFacultyCode(),
                r.getFacultyDescription(),
                r.getFacultyEmail(),
                r.getFacultyAddress(),
                r.getEstablishedYear(),
                r.getFacultyMotto()
        )).collect(Collectors.toList());
    }
}

