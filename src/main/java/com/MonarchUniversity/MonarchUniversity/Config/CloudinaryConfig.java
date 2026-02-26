package com.MonarchUniversity.MonarchUniversity.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", "dwfdbgcx4",
                        "api_key", "411441936246436",
                        "api_secret", "incT0WWct0uTCAFKphGlp-CdZZI",
                        "secure", true
                )
        );
    }
}
