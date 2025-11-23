package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyDto {
    private Long id;
    private String facultyName;
    private String facultyCode;
    private String facultyDescription;
    private String facultyEmail;
    private String facultyAddress;
    private Integer establishedYear;
    private String facultyMotto;
}
