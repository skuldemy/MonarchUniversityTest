package com.MonarchUniversity.MonarchUniversity.Payload

import com.MonarchUniversity.MonarchUniversity.Model.CourseUnit
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CourseUnitRequestDto (
    @field:NotNull(message = "Program ID is required")
    @field:Positive(message = "Program ID must be a positive number")
    val programId : Long,
    @field:NotNull(message = "Level is required")
    @field:Positive(message = "Level ID must be a positive number")
    val levelId : Long,
    @field:NotNull(message = "Semester is required")
    val semesterName: String,
    @field:NotNull(message = "Minimum units is required")
    @field:Min(value = 1, message = "Minimum units must be at least 1")

    val minUnits : Integer,
    @field:NotNull(message = "Max units is required")
    @field:Min(value = 1, message = "Max units must be at least 1")

    val maxUnits : Integer
)



data class CourseUnitResponseDto (
    val id : Long,
    val programName : String,
    val levelNumber : String,
    val semesterName: String,
    val minUnits : Int,
    val maxUnits : Int,
)

data class CourseUnitUpdate(
    @field:NotNull(message = "Minimum units is required")
    @field:Min(value = 1, message = "Minimum units must be at least 1")

    val minUnits: Int,

    @field:NotNull(message = "Max units is required")
    @field:Min(value = 1, message = "Max units must be at least 1")

    val maxUnits: Int
)

//fun CourseUnit.toResponse(): CourseUnitResponseDto {
//    return CourseUnitResponseDto(
//        programName = this.program.programName,
//        levelNumber = this.level.levelNumber,
//        semesterName = this.semesterName,
//        courseUnit = this.courseUnit,
//        minUnits = this.minUnits,
//        maxUnits = this.maxUnits
//    )
//}


