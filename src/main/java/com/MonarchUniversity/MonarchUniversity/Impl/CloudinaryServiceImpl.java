package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Service.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    private final static List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/jpg",
            "application/pdf"
    );

    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public void validateFile(MultipartFile file){
        if(file.isEmpty()){
            throw new ResponseNotFoundException("File cannot be empty!");
        }
        if(!ALLOWED_TYPES.contains(file.getContentType())){
            throw new ResponseNotFoundException("Only JPG, PNG images and PDF files are allowed");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        try {

            String resourceType = file.getContentType().equals("application/pdf")
                    ? "raw"
                    : "image";

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", resourceType
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }

    }
}
