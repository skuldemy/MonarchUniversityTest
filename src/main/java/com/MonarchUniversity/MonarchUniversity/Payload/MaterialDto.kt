package com.MonarchUniversity.MonarchUniversity.Payload

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

class MaterialReqDto(
    val semCourseId : Long,
    val materialType : String,
    val week : String,
    val fileUrl : String
)

@AllArgsConstructor
@NoArgsConstructor
class MaterialResDto(
    val id : Long,
    val levelName : String,
    val deptName : String,
    val courseName : String,
    val courseCode : String,
    val materialType : String,
    val week : String,
    val fileUrl : String
)