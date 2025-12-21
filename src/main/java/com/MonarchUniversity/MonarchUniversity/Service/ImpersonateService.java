package com.MonarchUniversity.MonarchUniversity.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.User;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Jwt.JwtService;
import com.MonarchUniversity.MonarchUniversity.Payload.AuthenticationToken;
import com.MonarchUniversity.MonarchUniversity.Payload.LoginResponse;
import com.MonarchUniversity.MonarchUniversity.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ImpersonateService {

    private final UserRepository userRepo;
    private final JwtService jwtService;

    private User getLoggedInUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseNotFoundException("Logged-in user not found"));
    }

    public LoginResponse impersonateUser(Long userId) {

        User superAdmin = getLoggedInUser();

        boolean isSuperAdmin = superAdmin.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals("SUPER_ADMIN"));

        if (!isSuperAdmin) {
            throw new ResponseNotFoundException("Only Super admin can impersonate users");
        }

        User targetUser = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseNotFoundException("No such user"));

        boolean isAnotherSuperAdmin = targetUser.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals("SUPER_ADMIN"));

        if (isAnotherSuperAdmin) {
            throw new ResponseNotFoundException("You can't impersonate another super admin");
        }


//        String role = targetUser.getRoles()
//                .stream()
//                .map(r -> r.getName())
//                .findFirst()
//                .orElse("UNKNOWN");

        String role = targetUser.getRoles()
                .stream()
                .map(r-> r.getName())
                .findFirst().orElse("UNKNOWN");
        // 🔐 Generate impersonation token
        String token = jwtService.generateImpersonationToken(
                targetUser,
                superAdmin
        );

        // ✅ SAME RESPONSE AS LOGIN
        return new LoginResponse(token, role);
    }
}
