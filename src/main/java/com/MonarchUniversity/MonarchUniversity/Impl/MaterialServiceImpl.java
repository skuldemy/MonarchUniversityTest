package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Model.Department;
import com.MonarchUniversity.MonarchUniversity.Model.Level;
import com.MonarchUniversity.MonarchUniversity.Model.Material;
import com.MonarchUniversity.MonarchUniversity.Payload.MaterialReqDto;
import com.MonarchUniversity.MonarchUniversity.Payload.MaterialResDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.DepartmentRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.LevelRepository;
import com.MonarchUniversity.MonarchUniversity.Repositories.MaterialRepo;
import com.MonarchUniversity.MonarchUniversity.Service.MaterialService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
public class MaterialServiceImpl implements MaterialService {
    private final DepartmentRepository departmentRepository;
    private final LevelRepository levelRepository;
    private final MaterialRepo materialRepo;

    @Override
    public String uploadMaterial(MaterialReqDto dto, MultipartFile file) {

        return "";
    }



//    private MaterialResDto MapToDto(Material material){
//        return new MaterialResDto(
//                material.getId(),
//                material.getLevel().getLevelNumber(),
//                material.getDepartment().getDepartmentName(),
//                material.getMaterialType(),
//                material.getWeek(),
//                material.getFileUrl()
//        );
//    }
}
