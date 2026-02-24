package com.MonarchUniversity.MonarchUniversity.Payload

data class CourseRegistration(
    val semesterCourseId : Int
)

data class CourseRegistrationResponse(
    val courseName : String,
    val courseUnit : Int,
    val courseCode : String
)

data class StudentOfferingCourse(
    val name : String,
    val matricNumber : String,
    val levelNumber : String,
    val departmentName : String
)