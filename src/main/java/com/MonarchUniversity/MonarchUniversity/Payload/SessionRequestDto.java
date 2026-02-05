package com.MonarchUniversity.MonarchUniversity.Payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRequestDto{
    @NotBlank(message = "Session name is required")
    private String sessionName;
    private String remarks;
    private boolean active;
}