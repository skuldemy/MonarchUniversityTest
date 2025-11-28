package com.MonarchUniversity.MonarchUniversity.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortalManagement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String portalName;
	private String description;
	private String academicYear; // 2024/2025
	private String semester;
	private LocalDateTime openingDate;
	private LocalDateTime closingDate;
	@OneToMany(cascade = CascadeType.ALL)
	private List<PortalAction> allowedActions = new ArrayList<>();
	private boolean enabled;
}
