package com.MonarchUniversity.MonarchUniversity.Payload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PortalManagementResponseDto {
	private Long id;
	private String portalName;
	private String description;
	private String academicYear; // 2024/2025
	private String semester;
	private LocalDateTime openingDate;
	private LocalDateTime closingDate;
	private List<String> allowedActions = new ArrayList<>();
	private boolean enabled;
}
