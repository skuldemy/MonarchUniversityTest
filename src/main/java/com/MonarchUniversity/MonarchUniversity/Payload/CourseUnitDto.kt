package com.MonarchUniversity.MonarchUniversity.Payload

import com.MonarchUniversity.MonarchUniversity.Model.CourseUnit

data class CourseUnitRequestDto (
    val programId : Long,
    val levelId : Long,
    val semesterName: String,
    val minUnits : Integer,
    val maxUnits : Integer
)



data class CourseUnitResponseDto (
    val programName : String,
    val levelNumber : String,
    val semesterName: String,
    val minUnits : Int,
    val maxUnits : Int,
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


