package com.MonarchUniversity.MonarchUniversity.Controller;

import com.MonarchUniversity.MonarchUniversity.Impl.CourseUnitServiceImpl;
import com.MonarchUniversity.MonarchUniversity.Impl.HodService;
import com.MonarchUniversity.MonarchUniversity.Payload.MaterialReqDto;
import com.MonarchUniversity.MonarchUniversity.Payload.MaterialResDto;
import com.MonarchUniversity.MonarchUniversity.Service.CourseUnitService;
import com.MonarchUniversity.MonarchUniversity.Service.MaterialService;
import com.MonarchUniversity.MonarchUniversity.Service.SemesterCourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/hod")
public class HodController {
    public final SemesterCourseService semesterCourseService;
    private final HodService hodService;
    private final CourseUnitService courseUnitService;
    private final MaterialService materialService;

    @GetMapping("/get-departments-of-hod")
    public ResponseEntity<?> getDepartmentsByHod(){
        return ResponseEntity.ok(hodService.getdepartmentsViaHod());
    }

    @GetMapping("/get-levels-via-depeartment/{departmentId}")
    public ResponseEntity<?> getLevelsViaDepartmentId(@PathVariable Long departmentId){
        return ResponseEntity.ok(hodService.getLevelsViaDepartment(departmentId));
    }


    @GetMapping("/get-courses-via-levels-department/{levelId}/{departmentId}")
    public ResponseEntity<?> getCoursesByLevelAndDepartment(@PathVariable Long levelId, @PathVariable Long departmentId){
        return ResponseEntity.ok(hodService.getCoursesByLevelAndDepartment(levelId,departmentId));
    }

    @GetMapping("/semesters")
    public ResponseEntity<?> getAllSemesters(){
        return ResponseEntity.ok(hodService.getAllSemester());
    }

    @PostMapping("/semester-course/{levelId}/{departmentId}/{semesterId}")
    public ResponseEntity<?> RegisterCourseToSemester(@PathVariable Long levelId, @PathVariable Long departmentId, @RequestBody List<Long> courseId, @PathVariable Long semesterId){
        return ResponseEntity.ok(semesterCourseService.addCoursesToSemester(levelId,departmentId,courseId,semesterId));
    }
    @GetMapping("/semester-course/{levelId}/{departmentId}/{semesterId}")
    public ResponseEntity<?> getCoursesViaSemester(
          @PathVariable  Long levelId,
            @PathVariable Long departmentId,
            @PathVariable Long semesterId
    ){
        return ResponseEntity.ok(semesterCourseService.getSemesterCourses(levelId,departmentId,semesterId));
    }

    // new
    @GetMapping("/courses-assigned-to-lecturer")
    public ResponseEntity<?> getCoursesAssignedToLecteurer(){
        return ResponseEntity.ok(hodService.getCoursesViaLecturer());
    }

    @GetMapping("/current-courseunit-assigned/{levelId}/{departmentId}/{semesterId}")
    public ResponseEntity<?> getCurrentCourseUnitAssigened(
            @PathVariable  Long levelId,
            @PathVariable Long departmentId,
            @PathVariable Long semesterId
    ){
        return ResponseEntity.ok(semesterCourseService.totalSemesterCourse(levelId, departmentId
        , semesterId
        ));
    }

    @GetMapping("/course-unit/{levelId}/{departmentId}/{semesterId}")
    public ResponseEntity<?> getCourseUnit(
            @PathVariable Long levelId,
            @PathVariable Long departmentId,
            @PathVariable Long semesterId
    ){
        return ResponseEntity.ok(courseUnitService.getCourseUnitResponse(departmentId,
                levelId, semesterId));
    }


    @GetMapping("/students-offering-course/{semesterCourseId}")
    public ResponseEntity<?> getStudentsAssignedToCourse(@PathVariable Long semesterCourseId){
        return ResponseEntity.ok(hodService.getStudentsOfferingCourse(semesterCourseId));
    }


    // new
    @PostMapping(value = "/upload-material",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaterialResDto> uploadMaterial(
            @RequestPart("data") String data,
            @RequestPart("file") MultipartFile file
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        MaterialReqDto dto = mapper.readValue(data, MaterialReqDto.class);

        MaterialResDto response = materialService.uploadMaterial(dto, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/upload-material")
    public ResponseEntity<Page<MaterialResDto>> getAllMaterialsAssignedToLecturer(
            @PageableDefault(size = 20, direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        Page<MaterialResDto> response =
                materialService.getAllMaterialsAssignedToLecturer(pageable);

        return ResponseEntity.ok(response);
    }

    // new
    @GetMapping("/students-in-level-dept/{departmentId}/{levelId}")
    public ResponseEntity<?> getStudentsInLevelAndDepartment(
            @PathVariable Long departmentId,
            @PathVariable Long levelId,
            @RequestParam(defaultValue = "0") int page,       // page number, default 0
            @RequestParam(defaultValue = "10") int size       // page size, default 10
    ) {
        return ResponseEntity.ok(
                hodService.getStudentsInDeptAndLevel(departmentId, levelId, page, size)
        );
    }
}
