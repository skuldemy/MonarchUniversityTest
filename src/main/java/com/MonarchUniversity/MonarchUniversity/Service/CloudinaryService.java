package com.MonarchUniversity.MonarchUniversity.Service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    public String uploadFile(MultipartFile file, String folder);
}
