package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Payload.MaterialReqDto;
import com.MonarchUniversity.MonarchUniversity.Payload.MaterialResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MaterialService {
    public MaterialResDto uploadMaterial(MaterialReqDto dto, MultipartFile file);
    public Page<MaterialResDto> getAllMaterialsAssignedToLecturer(Pageable pageable);
}
