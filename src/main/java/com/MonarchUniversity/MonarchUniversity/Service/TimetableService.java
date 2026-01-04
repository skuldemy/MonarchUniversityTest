package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.*;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.TimetableEntryDto;
import com.MonarchUniversity.MonarchUniversity.Payload.TimetableResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.*;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import com.lowagie.text.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TimetableService {
    private final ProgramRepository programRepo;
    private final FacultyRepository facultyRepo;
    private final DepartmentRepository departmentRepo;
    private final LevelRepository levelRepo;
    private final TimetableRepository timetableRepo;
    private final TimetableEntryRepository timetableEntryRepo;


    @Transactional
    public Map<String, Object> uploadTimetableExcel(MultipartFile file) throws Exception {

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new ResponseNotFoundException("Only Excel (.xlsx) files are allowed");
        }

        int inserted = 0;
        int skipped = 0;

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Timetable timetable = null;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String facultyName = getCellValue(row.getCell(0));
            String departmentName = getCellValue(row.getCell(1));
            String programName = getCellValue(row.getCell(2));
            String levelNumber = getCellValue(row.getCell(3));
            String semester = getCellValue(row.getCell(4));
            Integer academicYear = Integer.valueOf(getCellValue(row.getCell(5)));

            String dayValue = getCellValue(row.getCell(6));
            String startTimeValue = getCellValue(row.getCell(7));
            String endTimeValue = getCellValue(row.getCell(8));
            String courseCode = getCellValue(row.getCell(9));
            String courseTitle = getCellValue(row.getCell(10));
            String venue = getCellValue(row.getCell(11));

            if (facultyName == null || programName == null || courseCode == null) {
                skipped++;
                continue;
            }

            Faculty faculty = facultyRepo.findByFacultyName(facultyName)
                    .orElseThrow(() -> new ResponseNotFoundException(
                            "Faculty not found: " + facultyName));

            Department department = departmentRepo
                    .findByDepartmentNameAndFaculty(departmentName, faculty)
                    .orElseThrow(() -> new ResponseNotFoundException(
                            "Department does not belong to faculty"));

            Program program = programRepo
                    .findByProgramNameAndDepartment(programName, department)
                    .orElseThrow(() -> new ResponseNotFoundException(
                            "Program does not belong to department"));

            Level level = levelRepo
                    .findByLevelNumberAndProgram(levelNumber, program)
                    .orElseThrow(() -> new ResponseNotFoundException(
                            "Level does not belong to program"));

            // Create or reuse timetable (once per file)
            if (timetable == null) {
                timetable = timetableRepo
                        .findByProgramAndLevelAndSemesterAndAcademicYear(
                                program, level, semester, academicYear)
                        .orElseGet(() -> {
                            Timetable t = new Timetable();
                            t.setFaculty(faculty);
                            t.setDepartment(department);
                            t.setProgram(program);
                            t.setLevel(level);
                            t.setSemester(semester);
                            t.setAcademicYear(academicYear);
                            t.setStatus(Timetable.TimetableStatus.DRAFT);
                            return timetableRepo.save(t);
                        });
            }

            TimetableEntry entry = new TimetableEntry();
            entry.setTimetable(timetable);
            entry.setDay(DayOfWeek.valueOf(dayValue.toUpperCase()));
            entry.setStartTime(LocalTime.parse(startTimeValue));
            entry.setEndTime(LocalTime.parse(endTimeValue));
            entry.setCourseCode(courseCode);
            entry.setCourseName(courseTitle);
            entry.setVenue(venue);

            timetableEntryRepo.save(entry);
            inserted++;
        }

        workbook.close();

        return Map.of(
                "inserted", inserted,
                "skipped", skipped,
                "total", inserted + skipped
        );
    }

    @Transactional(readOnly = true)
    public TimetableResponseDto getTimetable(
            String programName,
            String levelNumber,
            String semester,
            Integer academicYear
    ) {

        Program program = programRepo.findByProgramName(programName)
                .orElseThrow(() -> new ResponseNotFoundException("Program not found"));

        Level level = levelRepo.findByLevelNumberAndProgram(levelNumber, program)
                .orElseThrow(() -> new ResponseNotFoundException("Level not found"));

        Timetable timetable = timetableRepo
                .findByProgramAndLevelAndSemesterAndAcademicYear(
                        program, level, semester, academicYear
                )
                .orElseThrow(() -> new ResponseNotFoundException("Timetable not found"));

        List<TimetableEntryDto> entries = timetableEntryRepo
                .findByTimetable(timetable)
                .stream()
                .map(e -> new TimetableEntryDto(
                        e.getDay().name(),
                        e.getStartTime().toString(),
                        e.getEndTime().toString(),
                        e.getCourseCode(),
                        e.getCourseName(),
                        e.getVenue()
                ))
                .toList();

        return TimetableResponseDto.from(timetable, entries);
    }

    public byte[] generateTimetablePdf(Long timetableId) {

        Timetable timetable = timetableRepo.findById(timetableId)
                .orElseThrow(() -> new ResponseNotFoundException("Timetable not found"));

        List<TimetableEntry> entries =
                timetableEntryRepo.findByTimetableOrderByDayAscStartTimeAsc(timetable);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font cellFont   = new Font(Font.HELVETICA, 10);
            Paragraph title = new Paragraph(
                    timetable.getProgram().getProgramName() + " " +
                            timetable.getLevel().getLevelNumber() + " Level Timetable\n" +
                            timetable.getSemester() + " Semester " +
                            timetable.getAcademicYear(),
                    headerFont
            );
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Map<DayOfWeek, List<TimetableEntry>> grouped =
                    entries.stream().collect(Collectors.groupingBy(
                            TimetableEntry::getDay,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            for (DayOfWeek day : grouped.keySet()) {
                Paragraph dayHeader = new Paragraph(day.name(), headerFont);
                dayHeader.setSpacingBefore(15);
                dayHeader.setSpacingAfter(5);
                document.add(dayHeader);

                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2, 2, 4, 2});

                table.addCell("Time");
                table.addCell("Course Code");
                table.addCell("Course Title");
                table.addCell("Venue");

                for (TimetableEntry e : grouped.get(day)) {
                    table.addCell(e.getStartTime() + " - " + e.getEndTime());
                    table.addCell(e.getCourseCode());
                    table.addCell(e.getCourseName());
                    table.addCell(e.getVenue());
                }

                document.add(table);
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate timetable PDF", e);
        }
    }


    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    yield String.valueOf((long) cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

}
