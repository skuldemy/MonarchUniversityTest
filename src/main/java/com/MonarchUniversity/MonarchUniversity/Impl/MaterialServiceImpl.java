package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Model.*;
import com.MonarchUniversity.MonarchUniversity.Payload.MaterialReqDto;
import com.MonarchUniversity.MonarchUniversity.Payload.MaterialResDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import com.MonarchUniversity.MonarchUniversity.Service.CloudinaryService;
import com.MonarchUniversity.MonarchUniversity.Service.MaterialService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MaterialServiceImpl implements MaterialService {
    private final DepartmentRepository departmentRepository;
    private final LevelRepository levelRepository;
    private final MaterialRepo materialRepo;
    private final SemesterCourseRepo semesterCourseRepo;
    private final UserRepository userRepository;
    private final LecturerProfileRepo lecturerProfileRepo;
    private final CloudinaryService cloudinaryService;

    private LecturerProfile getLoggedInLecturerProfile() {
        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User userEntity = userRepository.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return lecturerProfileRepo.findByUser(userEntity)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));
    }

    @Override
    public MaterialResDto uploadMaterial(MaterialReqDto dto, MultipartFile file) {
        LecturerProfile lecturerProfile = getLoggedInLecturerProfile();

        List<Course> courseList = lecturerProfile.getCourses();

        SemesterCourse semesterCourse = semesterCourseRepo
                .findById(dto.getSemCourseId()).orElseThrow(()->
                        new ResponseNotFoundException("No such semester course"));

        if(!courseList.contains(semesterCourse.getCourse())){
            throw new ResponseNotFoundException("You do not teach " + semesterCourse.getCourse().getCourseTitle());
        }

        String fileUrl = cloudinaryService.uploadFile(file, "materials");

        Material material = new Material();
        material.setSemesterCourse(semesterCourse);
        material.setMaterialType(dto.getMaterialType());
        material.setWeek(dto.getWeek());
        material.setFileUrl(fileUrl);
        material.setLecturerProfile(lecturerProfile);
        material.setStatus(Material.MATERIAL_STATUS.HIDDEN);

       Material savedMaterial = materialRepo.save(material);

        MaterialResDto materialResDto = MapToDto(savedMaterial);

        return materialResDto;
    }

    @Override
    public Page<MaterialResDto> getAllMaterialsAssignedToLecturer(Pageable pageable) {
        LecturerProfile lecturerProfile = getLoggedInLecturerProfile();
        Page<Material> materialsViaLecturer = materialRepo.findByLecturerWithDetails(lecturerProfile, pageable);

        return materialsViaLecturer.map(this::MapToDto);
    }


    private MaterialResDto MapToDto(Material material){
        return new MaterialResDto(
                material.getId(),
                material.getSemesterCourse().getCourse().getLevel().getLevelNumber(),
                material.getSemesterCourse().getCourse().getDepartment().getDepartmentName(),
                material.getSemesterCourse().getCourse().getCourseTitle(),
                material.getSemesterCourse().getCourse().getCourseCode(),
                material.getMaterialType(),
                material.status.name(),
                material.getWeek(),
                material.getFileUrl(),
                material.getLecturerProfile().getFullName()
        );
    }
}
