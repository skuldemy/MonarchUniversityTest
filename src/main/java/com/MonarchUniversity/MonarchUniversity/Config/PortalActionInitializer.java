package com.MonarchUniversity.MonarchUniversity.Config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.MonarchUniversity.MonarchUniversity.Model.PortalAction;
import com.MonarchUniversity.MonarchUniversity.Repositories.PortalActionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PortalActionInitializer implements CommandLineRunner {

    private final PortalActionRepository actionRepo;

    @Override
    public void run(String... args) {
        List<String> actions = List.of(
            "Register Courses",
            "Drop Courses",
            "View Registration",
            "Upload Results",
            "View Results",
            "Edit Results",
            "Make Payment",
            "View Invoice",
            "Print Receipt",
            "Vote",
            "View Candidates",
            "Submit Assignment",
            "View Submissions",
            "Download Materials"
        );

        for (String name : actions) {
            if (actionRepo.findByActionName(name).isEmpty()) {
                actionRepo.save(new PortalAction(null, name));
            }
        }
    }
}
