package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelDto {
	private Long id;
	private String program;
	private String levelNumber; 
	private String semester;
	private Integer capacity;
}
