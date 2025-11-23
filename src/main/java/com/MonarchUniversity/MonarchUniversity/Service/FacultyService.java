package com.MonarchUniversity.MonarchUniversity.Service;

import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Faculty;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.FacultyDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.FacultyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepo;

    public FacultyDto createFaculty(FacultyDto dto) {
        Faculty faculty = new Faculty();
        faculty.setFacultyName(dto.getFacultyName());
        faculty.setFacultyCode(dto.getFacultyCode());
        faculty.setFacultyDescription(dto.getFacultyDescription());
        faculty.setFacultyEmail(dto.getFacultyEmail());
        faculty.setFacultyAddress(dto.getFacultyAddress());
        faculty.setEstablishedYear(dto.getEstablishedYear());
        faculty.setFacultyMotto(dto.getFacultyMotto());

        facultyRepo.save(faculty);

        return dto; 
    }

    public FacultyDto editFaculty(Long id, FacultyDto dto) {
        Faculty faculty = facultyRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such Faculty Id"));

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
}

