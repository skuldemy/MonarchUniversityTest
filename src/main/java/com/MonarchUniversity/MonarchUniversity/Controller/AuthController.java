package com.MonarchUniversity.MonarchUniversity.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.MonarchUniversity.MonarchUniversity.Entity.User;
import com.MonarchUniversity.MonarchUniversity.Jwt.JwtService;
import com.MonarchUniversity.MonarchUniversity.Payload.LoginRequest;
import com.MonarchUniversity.MonarchUniversity.Payload.LoginResponse;
import com.MonarchUniversity.MonarchUniversity.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // Authenticate user (Spring Security will check password using UserDetailsService)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Fetch the full user from DB
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Extract single role (you can have multiple)
        String role = user.getRoles()
                .stream()
                .map(r -> r.getName())
                .findFirst()
                .orElse("UNKNOWN");

        // Generate JWT
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token, role));
    }
}
