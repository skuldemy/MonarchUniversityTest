package com.MonarchUniversity.MonarchUniversity.Payload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortalManagementRequestDto {
	@NotBlank(message = "Portal Name is required")
	private String portalName;
	@NotBlank(message = "Description is required")
	private String description;
	@NotBlank(message = "Academic year is required")
	private String academicYear; // 2024/2025
	@NotBlank(message = "Semester is required")
	private String semester;
	@NotNull(message = "Opening date is required")
	private LocalDateTime openingDate;

	@NotNull(message = "Closing date is required")
	private LocalDateTime closingDate;

	@NotEmpty(message = "At least one allowed action is required")
	private List<Long> allowedActions = new ArrayList<>();

}
