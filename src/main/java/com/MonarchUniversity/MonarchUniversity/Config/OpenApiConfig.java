package com.MonarchUniversity.MonarchUniversity.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	            .tags(List.of(
	                new Tag().name("Super Admin").description("Manage faculties")
	               	            ));
	}


}

