package com.MonarchUniversity.MonarchUniversity.Payload;

import com.MonarchUniversity.MonarchUniversity.Model.Course
import com.MonarchUniversity.MonarchUniversity.Model.LecturerProfile
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

@NoArgsConstructor
@AllArgsConstructor
data class LecturerRequestDto(
    @field:NotBlank(message = "Full name is required")
    @field:Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    val fullName : String,
    @field:NotBlank(message = "Email address is required")
    @field:Email(message = "Invalid email format")
    val emailAddress : String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters long")
    val password : String,
    val facultyId : Long?,
   val departmentId : Long?,
    @field:NotNull(message = "Role ID cannot be null")
    val roleId : List<Long>,
    val coursesOffering : List<Long>?

)

data class UpdateLecturerRequestDto(

    @field:Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    val fullName: String? = null,

    @field:Email(message = "Invalid email format")
    val emailAddress: String? = null,

    @field:Size(min = 6, message = "Password must be at least 6 characters long")
    val password: String? = null,

    val facultyId: Long? = null,

    val departmentId: Long? = null,

    val roleId: List<
            @NotNull(message = "Role ID cannot be null")
            Long
            >? = null,

    val coursesOffering: List<Long>? = null
)

data class AssignLecturerPositionDto (
    val lecturerId : Long,
    val lecturerType : LecturerProfile.LecturerType,
    val levelId : Long ? = null,
    val deptId : Long
)

data class LecturerDto(
    val lecturerId : Long,
    val fullName : String,
    val lecturerType : LecturerProfile.LecturerType?,
    val levelId : Long ?= null
//    val levelId : Long ? = null,
//    val deptId : Long

)

data class CoursesInfo(
    val courseName : String,
    val courseCode : String,
    val levelNumber : String
)

data class LecturerDetails(
    val name : String,
    val lecturerType : LecturerProfile.LecturerType?,
    val courses : List<CoursesInfo>
)