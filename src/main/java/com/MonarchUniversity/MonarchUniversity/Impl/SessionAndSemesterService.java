package com.MonarchUniversity.MonarchUniversity.Impl;

import com.MonarchUniversity.MonarchUniversity.Model.Semester;
import com.MonarchUniversity.MonarchUniversity.Model.Session;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Payload.SemesterRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.SemesterResponseDto;
import com.MonarchUniversity.MonarchUniversity.Payload.SessionRequestDto;
import com.MonarchUniversity.MonarchUniversity.Payload.SessionResponseDto;
import com.MonarchUniversity.MonarchUniversity.Repositories.SemesterRepo;
import com.MonarchUniversity.MonarchUniversity.Repositories.SessionRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class SessionAndSemesterService {
    private final SessionRepo sessionRepo;
    private final SemesterRepo semesterRepo;

    @Transactional
    public SessionResponseDto createSession(SessionRequestDto dto){
        String sessionName = dto.getSessionName().trim();
        Session session = new Session();
        if(sessionRepo.count()>1){
            throw new ResponseNotFoundException("It can't create more than one session, consider editing");
        }
        if(sessionRepo.existsBySessionName(sessionName)){
            throw new ResponseNotFoundException("Session " + session.getSessionName() + "already exists in the system");
        }


        session.setSessionName(sessionName);
        session.setRemarks(dto.getRemarks());

        Session savedSession = sessionRepo.save(session);
        String status = "";
        if(savedSession.isActive()){
            status = "Active";
        }
        else{
           status = "Not_Active";
        }

        return new SessionResponseDto(savedSession.getId(), savedSession.getSessionName(),
                savedSession.getRemarks(),
                status
                );
    }



    @Transactional
    public SemesterResponseDto createSemester(SemesterRequestDto dto){
        Semester semester = new Semester();
        Session session = sessionRepo.findById(dto.getSessionId()).orElseThrow(()->
                new ResponseNotFoundException("No such session"));
        if(semesterRepo.count()>3){
            throw new ResponseNotFoundException("It can't more than 3 semsesters, consider editing");
        }
        boolean semesterExists = semesterRepo.existsBySemesterName(dto.getSemesterName());
        if(semesterExists){
            throw new ResponseNotFoundException("Session and semester already exists");
        }

        List<String> allowedSemesters = List.of(
                "First Semester", "Second Semester", "Supplementary"
        );

        if(!allowedSemesters.contains(dto.getSemesterName())){
            throw new ResponseNotFoundException("Invalid semester name. Allowed values: First Semester, Second Semester, Supplementary");
        }



        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new ResponseNotFoundException("End date cannot be before start date");
        }

        boolean overlap = semesterRepo.existsOverlappingSemester(
                dto.getSessionId(),
                dto.getStartDate(),
                dto.getEndDate()
        );

        if (overlap) {
            throw new ResponseNotFoundException(
                    "Semester dates overlap with an existing semester in this session"
            );
        }


        semester.setSession(session);
        semester.setStartDate(dto.getStartDate());
        semester.setEndDate(dto.getEndDate());
        semester.setSemesterName(dto.getSemesterName());

        Semester savedSemester = semesterRepo.save(semester);

        return new SemesterResponseDto(savedSemester.getId(), savedSemester.getSession().getSessionName(),
                savedSemester.getStartDate(), savedSemester.getEndDate(),
                savedSemester.getSemesterName()
                );
    }

    public List<SessionResponseDto> getAllSession() {

        return sessionRepo.findAll()
                .stream()
                .map(session -> {

                    String status;
                    if (session.isActive()) {
                        status = "Active";
                    } else {
                        status = "Not Active";
                    }

                    return new SessionResponseDto(
                            session.getId(),
                            session.getSessionName(),
                            session.getRemarks(),
                            status
                    );
                })
                .toList();
    }


    public List<SemesterResponseDto> getAllSemester(){
        return semesterRepo.findAll()
                .stream()
                .map(d -> new SemesterResponseDto(
                        d.getId(),
                        d.getSession().getSessionName(),
                        d.getStartDate(),
                        d.getEndDate(),
                        d.getSemesterName()
                )).toList();
    }

    public SemesterResponseDto getCurrentSemesterAndSession() {

        LocalDate today = LocalDate.now();

        Semester semester = semesterRepo.findCurrentSemester(today)
                .orElseThrow(() ->
                        new ResponseNotFoundException("No active semester today")
                );

        return new SemesterResponseDto(
                semester.getId(),
                semester.getSession().getSessionName(),
                semester.getStartDate(),
                semester.getEndDate(),
                semester.getSemesterName()
        );
    }

    public SemesterResponseDto getSemesterById(@PathVariable Long id){
        Semester currentSemester = semesterRepo.findById(id)
                .orElseThrow(()-> new ResponseNotFoundException("No such semester"));

        return new SemesterResponseDto(currentSemester.getId(),
                currentSemester.getSession().getSessionName(),
                currentSemester.getStartDate(),
                currentSemester.getEndDate(),
                currentSemester.getSemesterName()
        );
    }

    public SessionResponseDto getSessionById(@PathVariable Long id){
        Session session = sessionRepo.findById(id)
                .orElseThrow(()-> new ResponseNotFoundException("No such session"));

        String status = "";
        if(session.isActive()){
            status = "Active";
        }
        else{
            status = "Not_Active";
        }


        return new SessionResponseDto(
                session.getId(),
                session.getSessionName(),
                session.getRemarks(),
                status
        );
    }

    @Transactional
    public SessionResponseDto updateSession(Long id, SessionRequestDto dto) {

        Session existingSession = sessionRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such session"));

        String newSessionName = dto.getSessionName().trim();

        // check for duplicate session name (excluding itself)
        boolean nameExists = sessionRepo.existsBySessionNameAndIdNot(newSessionName, id);
        if (nameExists) {
            throw new IllegalArgumentException("Another session with this name already exists");
        }

        // If this session is being set to active, deactivate others
        if (dto.isActive()) {
            sessionRepo.deactivateAllSessions();
            existingSession.setActive(true);
        }

        existingSession.setSessionName(newSessionName);
        existingSession.setRemarks(dto.getRemarks());

        Session updated = sessionRepo.save(existingSession);

        String status = "";
        if(updated.isActive()){
            status = "Active";
        }
        else{
            status = "Not_Active";
        }


        return new SessionResponseDto(
                updated.getId(),
                updated.getSessionName(),
                updated.getRemarks(),
                status
        );
    }

    @Transactional
    public SemesterResponseDto updateSemester(Long id, SemesterRequestDto dto) {

        Semester existingSemester = semesterRepo.findById(id)
                .orElseThrow(() -> new ResponseNotFoundException("No such semester"));

        Session session = sessionRepo.findById(dto.getSessionId())
                .orElseThrow(() -> new ResponseNotFoundException("No such session"));

        // Date validations
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new ResponseNotFoundException("End date cannot be before start date");
        }

        boolean overlap = semesterRepo.existsOverlappingSemesterForUpdate(
                dto.getSessionId(),
                dto.getStartDate(),
                dto.getEndDate(),
                id
        );

        if (overlap) {
            throw new ResponseNotFoundException(
                    "Updated dates overlap with another semester in this session"
            );
        }


        // Check duplicate semester name inside session (excluding itself)
        boolean exists = semesterRepo.existsBySemesterNameAndIdNot(
                dto.getSemesterName(),
                id
        );

        if (exists) {
            throw new IllegalArgumentException("Semester already exists ");
        }

        List<String> allowedSemesters = List.of(
                "First Semester", "Second Semester", "Supplementary"
        );

        if(!allowedSemesters.contains(dto.getSemesterName())){
            throw new ResponseNotFoundException("Invalid semester name. Allowed values: First Semester, Second Semester, Supplementary");
        }


        existingSemester.setSession(session);
        existingSemester.setSemesterName(dto.getSemesterName());
        existingSemester.setStartDate(dto.getStartDate());
        existingSemester.setEndDate(dto.getEndDate());

        Semester updated = semesterRepo.save(existingSemester);

        return new SemesterResponseDto(
                updated.getId(),
                updated.getSession().getSessionName(),
                updated.getStartDate(),
                updated.getEndDate(),
                updated.getSemesterName()
        );
    }

}
