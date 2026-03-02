package com.MonarchUniversity.MonarchUniversity.Payload

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor


data class MaterialReqDto(
    val semCourseId: Long = 0,
    val materialType: String = "",
    val week: String = ""
)

class MaterialResDto(
    val id : Long,
    val levelName : String,
    val deptName : String,
    val courseName : String,
    val courseCode : String,
    val materialType : String,
    val status : String,
    val week : String,
    val fileUrl : String,
    val lecturerName : String
)