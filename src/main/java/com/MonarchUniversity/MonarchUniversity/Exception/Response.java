package com.MonarchUniversity.MonarchUniversity.Exception;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
	private int status;
	private String description;
	private String message;
	private LocalDate localDate;
	
    // Constructor to accept HttpStatus
    public Response(HttpStatus status, String description, String message, LocalDate localDate) {
        this.status = status.value(); // Use the value from HttpStatus
        this.description = description;
        this.message = message;
        this.localDate = localDate;
    }
}
