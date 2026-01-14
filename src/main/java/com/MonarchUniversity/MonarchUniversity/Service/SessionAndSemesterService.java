package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Semester;
import com.MonarchUniversity.MonarchUniversity.Entity.Session;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class SessionAndSemesterService {
    private final SessionRepo sessionRepo;
    private final SemesterRepo semesterRepo;

    @Transactional
    public SessionResponseDto createSession(SessionRequestDto dto){
        String sessionName = dto.getSessionName().trim();
        Session session = new Session();
        if(sessionRepo.existsBySessionName(sessionName)){
            throw new ResponseNotFoundException("Session " + session.getSessionName() + "already exists in the system");
        }
        sessionRepo.deactivateAllSessions();


        session.setSessionName(sessionName);
        session.setRemarks(dto.getRemarks());
        session.setActive(true);

        Session savedSession = sessionRepo.save(session);

        return new SessionResponseDto(savedSession.getId(), savedSession.getSessionName(),
                savedSession.getRemarks()
                );
    }



    @Transactional
    public SemesterResponseDto createSemester(SemesterRequestDto dto){
        Semester semester = new Semester();
        Session session = sessionRepo.findById(dto.getSessionId()).orElseThrow(()->
                new ResponseNotFoundException("No such session"));
        boolean semesterExists = semesterRepo.existsBySessionIdAndSemesterName(dto.getSessionId(), dto.getSemesterName());
        if(semesterExists){
            throw new ResponseNotFoundException("Session and semester already exists");
        }
        if (!session.isActive()) {
            throw new ResponseNotFoundException("Cannot create semester for an inactive session, contact the" +
                    " IT team to update inactive session to active");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        if (dto.getRegistrationEndDate().isBefore(dto.getRegistrationStartDate())) {
            throw new IllegalArgumentException("Registration end date cannot be before registration start date");
        }

        semesterRepo.deactivateAllBySessionId(session.getId());
        semester.setSession(session);
        semester.setStartDate(dto.getStartDate());
        semester.setEndDate(dto.getEndDate());
        semester.setRegistrationStartDate(dto.getRegistrationStartDate());
        semester.setRegistrationEndDate(dto.getRegistrationEndDate());
        semester.setSemesterName(dto.getSemesterName());
        semester.setActive(true);

        Semester savedSemester = semesterRepo.save(semester);

        return new SemesterResponseDto(savedSemester.getId(), savedSemester.getSession().getSessionName(),
                savedSemester.getStartDate(), savedSemester.getEndDate(),
                savedSemester.getRegistrationStartDate(), savedSemester.getRegistrationEndDate(),
                savedSemester.getSemesterName()
                );
    }

    public List<SessionResponseDto> getAllSession() {

        return sessionRepo.findAll()
                .stream()
                .map(session -> new SessionResponseDto(
                        session.getId(),
                        session.getSessionName(),
                        session.getRemarks()
                ))
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
                        d.getRegistrationStartDate(),
                        d.getRegistrationEndDate(),
                        d.getSemesterName()
                )).toList();
    }

    public SemesterResponseDto getCurrentSemesterAndSession(){
        LocalDate today = LocalDate.now();
        Semester currentSemester = semesterRepo.findAll()
                .stream()
                .filter(s ->
                        !today.isBefore(s.getStartDate()) &&   // today >= session.start
                                !today.isAfter(s.getEndDate())         // today <= session.end
                )
                .findFirst()
                .orElseThrow(() -> new ResponseNotFoundException(
                        "No academic session found for the current date"
                ));

        return new SemesterResponseDto(currentSemester.getId(),
                currentSemester.getSession().getSessionName(),
                currentSemester.getStartDate(),
                currentSemester.getEndDate(),
                currentSemester.getRegistrationStartDate(),
                currentSemester.getRegistrationEndDate(),
                currentSemester.getSemesterName()
                );
    }
}
