@Service
@AllArgsConstructor
public class SuperAdminService {

    private final UserRepository userRepo;
    private final FacultyRepository facultyRepo;
    private final DepartmentRepository departmentRepo;
    private final LecturerProfileRepo lecturerRepo;
    private final RoleRepository roleRepo;
    private final CourseRepository courseRepo;
    private final PasswordEncoder enconder;

    public List<FacultyResponseDto> findAllFaculties(){
        return facultyRepo.findAll()
                .stream()
                .map(f -> new FacultyResponseDto(f.getId(), f.getFacultyName()))
                .toList();
    }

    public List<DepartmentDto> findDepartments(Long id){
        return departmentRepo.findByFacultyId(id)
                .stream()
                .map(d -> new DepartmentDto(d.getId(), d.getDepartmentName()))
                .toList();
    }

    public List<RoleDto> getAllRoles(){
        return roleRepo.findByNameNot("STUDENT")
                .stream()
                .map(r -> new RoleDto(r.getId(), r.getName()))
                .toList();
    }

    private void validateRoles(Set<Role> roles){
        boolean hasGeneralRole = roles.stream().anyMatch(r ->
                r.getName().equals("SUPER_ADMIN")|| r.getName().equals("ADMIN"));

        boolean hasAcademicRole = roles.stream().anyMatch(r ->
                r.getName().equals("LECTURER") ||
                r.getName().equals("HOD") ||
                r.getName().equals("LEVEL_ADVISER") ||
                r.getName().equals("DEAN")
        );

        if (hasGeneralRole && hasAcademicRole) {
            throw new ResponseNotFoundException("Cannot mix global and academic roles");
        }
    }

    @Transactional
    public LecturerResponseDto createNewUser(LecturerRequestDto dto) {

        userRepo.findByUsername(dto.getEmailAddress())
                .ifPresent(u -> { throw new ResponseNotFoundException("User already exists"); });

        User user = new User();
        user.setUsername(dto.getEmailAddress());
        user.setPassword(enconder.encode(dto.getPassword()));

        Set<Role> roles = dto.getRoleId().stream()
                .map(id -> roleRepo.findById(id)
                        .orElseThrow(() -> new ResponseNotFoundException("No such role id")))
                .collect(Collectors.toSet());

        validateRoles(roles);
        user.setRoles(roles);
        userRepo.save(user);

        List<Course> courses = courseRepo.findAllById(dto.getCoursesOffering());

        LecturerProfile lecturer = new LecturerProfile();
        lecturer.setUser(user);
        lecturer.setFullName(dto.getFullName());
        lecturer.setCourses(courses);

        lecturerRepo.save(lecturer);

        return buildLecturerResponse(lecturer);
    }

    public List<LecturerResponseDto> getAllLecturers() {
        return lecturerRepo.findAll()
                .stream()
                .map(this::buildLecturerResponse)
                .toList();
    }

    public LecturerResponseDto getLecturerById(Long id) {
        LecturerProfile lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));
        return buildLecturerResponse(lecturer);
    }

    @Transactional
    public LecturerResponseDto updateLecturer(Long id, UpdateLecturerRequestDto dto) {

        LecturerProfile lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

        User user = lecturer.getUser();

        if (dto.getEmailAddress() != null)
            user.setUsername(dto.getEmailAddress());

        if (dto.getPassword() != null)
            user.setPassword(enconder.encode(dto.getPassword()));

        if (dto.getRoleId() != null) {
            Set<Role> roles = dto.getRoleId().stream()
                    .map(r -> roleRepo.findById(r)
                            .orElseThrow(() -> new ResponseNotFoundException("No role")))
                    .collect(Collectors.toSet());

            validateRoles(roles);
            user.setRoles(roles);
        }

        if (dto.getFullName() != null)
            lecturer.setFullName(dto.getFullName());

        if (dto.getCoursesOffering() != null) {
            lecturer.setCourses(courseRepo.findAllById(dto.getCoursesOffering()));
        }

        userRepo.save(user);
        lecturerRepo.save(lecturer);

        return buildLecturerResponse(lecturer);
    }

    @Transactional
    public LecturerResponseDto toggleUserStatus(Long id) {
        LecturerProfile lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

        User user = lecturer.getUser();
        user.setEnabled(!user.isEnabled());
        userRepo.save(user);

        return buildLecturerResponse(lecturer);
    }

    private LecturerResponseDto buildLecturerResponse(LecturerProfile lecturer) {

        User user = lecturer.getUser();

        LecturerResponseDto res = new LecturerResponseDto();
        res.setId(lecturer.getId());
        res.setFullName(lecturer.getFullName());
        res.setEmailAddress(user.getUsername());
        res.setStatus(user.isEnabled() ? "enabled" : "disabled");

        res.setRoleName(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );

        List<Course> courses = lecturer.getCourses();

        res.setCoursesOffering(
                courses.stream()
                        .map(Course::getCourseTitle)
                        .toList()
        );

        res.setDepartmentName(
                courses.stream()
                        .map(c -> c.getDepartment().getDepartmentName())
                        .collect(Collectors.joining(", "))
        );

        return res;
    }

    @Transactional
    public void deleteLecturer(Long id){
        LecturerProfile lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("Lecturer not found"));

        userRepo.delete(lecturer.getUser());
        lecturerRepo.delete(lecturer);
    }
}
