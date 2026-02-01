package com.MonarchUniversity.MonarchUniversity.Impl;

import java.util.List;
import java.util.stream.Collectors;

import com.MonarchUniversity.MonarchUniversity.Payload.FacultyResponseDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Model.Faculty;
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
    
    public FacultyResponseDto createFaculty(FacultyDto dto) {
        Faculty faculty = new Faculty();

        if (facultyRepo.existsByFacultyNameIgnoreCase(dto.getFacultyName())){
            throw new ResponseNotFoundException("Faculty name exists already");
        }

        faculty.setFacultyName(dto.getFacultyName());
        if (facultyRepo.existsByFacultyCodeIgnoreCase(dto.getFacultyCode())){
            throw new ResponseNotFoundException("Faculty code exists already");
        }

        faculty.setFacultyCode(dto.getFacultyCode());
        faculty.setFacultyDescription(dto.getFacultyDescription());
        if (facultyRepo.existsByFacultyEmailIgnoreCase(dto.getFacultyEmail())){
            throw new ResponseNotFoundException("Faculty email exists already");
        }
        faculty.setFacultyEmail(dto.getFacultyEmail());
        faculty.setFacultyAddress(dto.getFacultyAddress());
        faculty.setEstablishedYear(dto.getEstablishedYear());
        faculty.setFacultyMotto(dto.getFacultyMotto());

        Faculty savedFaculty = facultyRepo.save(faculty);

        return mapToDto(savedFaculty);
    }

    public FacultyResponseDto getFacultyById(Long id){
        Faculty faculty = facultyRepo.findById(id)
                .orElseThrow(()-> new ResponseNotFoundException("No such faculty"));

        return mapToDto(faculty);
    }

    public FacultyResponseDto editFaculty(Long id, FacultyDto dto) {
        Faculty faculty = facultyRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such Faculty Id"));

//        if (dto.getFacultyName() == null || dto.getFacultyName().isBlank()) {
//            throw new ResponseForbiddenException("Faculty name is required");
//        }
//        if (dto.getFacultyCode() == null || dto.getFacultyCode().isBlank()) {
//            throw new ResponseForbiddenException("Faculty code is required");
//        }

        if (facultyRepo.existsByFacultyNameIgnoreCaseAndIdNot(dto.getFacultyName(),id)){
            throw new ResponseNotFoundException("Faculty name exists already");
        }
        faculty.setFacultyName(dto.getFacultyName());
        if (facultyRepo.existsByFacultyCodeIgnoreCaseAndIdNot(dto.getFacultyCode(), id)){
            throw new ResponseNotFoundException("Faculty code exists already");
        }
        faculty.setFacultyCode(dto.getFacultyCode());
        faculty.setFacultyDescription(dto.getFacultyDescription());
        if (facultyRepo.existsByFacultyEmailIgnoreCaseAndIdNot(dto.getFacultyEmail(), id)){
            throw new ResponseNotFoundException("Faculty email exists already");
        }

        faculty.setFacultyEmail(dto.getFacultyEmail());
        faculty.setFacultyAddress(dto.getFacultyAddress());
        faculty.setEstablishedYear(dto.getEstablishedYear());
        faculty.setFacultyMotto(dto.getFacultyMotto());

        Faculty saved = facultyRepo.save(faculty);

        return mapToDto(saved);
    }

    
    public String deleteFaculty(Long id) {
        Faculty faculty = facultyRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such Faculty Id"));

        facultyRepo.delete(faculty);
        return "Faculty successfully deleted";
    }

    public List<FacultyResponseDto> getAllFaculties(){
    	return facultyRepo.findAll().stream().map(r-> mapToDto(r)
        ).collect(Collectors.toList());
    }

    public FacultyResponseDto mapToDto(Faculty saved){
        return new FacultyResponseDto(
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
}

