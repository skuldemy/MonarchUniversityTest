package com.MonarchUniversity.MonarchUniversity.Config;

import com.MonarchUniversity.MonarchUniversity.Entity.FeeType;
import com.MonarchUniversity.MonarchUniversity.Entity.PortalAction;
import com.MonarchUniversity.MonarchUniversity.Repositories.FeeTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeeTypeInitializer implements CommandLineRunner {

    private final FeeTypeRepo feeTypeRepo;

    @Override
    public void run(String... args) throws Exception {
        List<String> actions = List.of(
               "Tuition",
                "Acceptance",
                "Medical",
                "Departmental",
                "Ict",
                "Development"
        );
        for (String name : actions) {
            if (feeTypeRepo.findByName(name).isEmpty()) {
                feeTypeRepo.save(new FeeType(null, name));
            }
        }
    }
}
