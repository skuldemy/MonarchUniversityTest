package com.MonarchUniversity.MonarchUniversity.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
	@ManyToMany
	@JoinTable(
	        name = "portal_allowed_actions",
	        joinColumns = @JoinColumn(name="portal_id"),
	        inverseJoinColumns = @JoinColumn(name = "action_id")
	)
	private List<PortalAction> allowedActions = new ArrayList<>();
	private boolean enabled;
}
