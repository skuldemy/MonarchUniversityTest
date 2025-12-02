package com.MonarchUniversity.MonarchUniversity.Payload;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Nationality is required")
    private String nationality;

    @NotBlank(message = "State of origin is required")
    private String stateOfOrigin;

    @NotBlank(message = "LGA is required")
    private String lga;

    @NotNull(message = "Program ID is required")
    private Long programId;

    @NotNull(message = "Faculty ID is required")
    private Long facultyId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Level ID is required")
    private Long levelId;

    @NotNull(message = "Admission year is required")
    private LocalDate admissionYear;

    // Matric number can be provided or auto-generated
    private String matricNumber;

    // NEW: flag to indicate auto-generation of matric number
    private boolean autoGenerateMatric;

    @NotBlank(message = "Mode of entry is required")
    private String modeOfEntry;

    @NotBlank(message = "Email address is required")
    private String emailAddress;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Home address is required")
    private String homeAddress;
}
