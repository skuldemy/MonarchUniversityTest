package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Payload.MaterialReqDto;
import org.springframework.web.multipart.MultipartFile;

public interface MaterialService {
    public String uploadMaterial(MaterialReqDto dto, MultipartFile file);
}
