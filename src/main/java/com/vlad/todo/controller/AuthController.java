package com.vlad.todo.controller;


import com.vlad.todo.dto.*;
import com.vlad.todo.model.User;
import com.vlad.todo.repository.UserRepository;
import com.vlad.todo.security.JwtProvider;
import com.vlad.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> handle(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }


    @PostMapping("/register")
    public UserDtoResponse register(@RequestBody UserDtoRequest userDtoRequest) {
        return userService.save(userDtoRequest);
    }


    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}