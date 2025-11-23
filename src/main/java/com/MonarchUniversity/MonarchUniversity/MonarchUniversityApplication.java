package com.MonarchUniversity.MonarchUniversity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
	    exclude = {
	        MetricsAutoConfiguration.class,
	        SystemMetricsAutoConfiguration.class
	    }
	)
public class MonarchUniversityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonarchUniversityApplication.class, args);
	}

}
