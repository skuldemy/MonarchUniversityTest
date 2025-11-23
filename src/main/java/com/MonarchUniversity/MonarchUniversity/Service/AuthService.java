package com.MonarchUniversity.MonarchUniversity.Service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.MonarchUniversity.MonarchUniversity.Entity.Role;
import com.MonarchUniversity.MonarchUniversity.Entity.User;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseNotFoundException;
import com.MonarchUniversity.MonarchUniversity.Exception.ResponseServerException;
import com.MonarchUniversity.MonarchUniversity.Jwt.JwtService;
import com.MonarchUniversity.MonarchUniversity.Payload.AuthenticationToken;
import com.MonarchUniversity.MonarchUniversity.Repositories.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthenticationToken login(AuthRequest request, HttpServletResponse response) {

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            User user = userRepository
                    .findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResponseNotFoundException("User not found"));

            String token = jwtService.generateToken(user);

            return AuthenticationToken.builder()
                    .token(token)
                    .roles(user.getRoles().stream().map(Role::getName).toList())
                    .build();
        }
        catch (BadCredentialsException e) {
            throw new ResponseNotFoundException("Invalid username or password.");
        }
        catch (DisabledException e) {
            throw new ResponseNotFoundException("Account disabled.");
        }
        catch (Exception e) {
            throw new ResponseServerException("Unexpected error, try again later.");
        }
    }
}
