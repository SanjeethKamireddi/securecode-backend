package com.securecode.backend.controller;

import com.securecode.backend.entity.User;
import com.securecode.backend.repository.UserRepository;
import com.securecode.backend.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody User loginRequest) {
        // 1. Find the user by username
        Optional<User> userOptional =
                userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isEmpty()) {
            return "Login failed: user not found";
        }

        User user = userOptional.get();

        // 2. Check the submitted password against the stored hash
        boolean passwordMatches =
                passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if (!passwordMatches) {
            return "Login failed: wrong password";
        }

        // 3. Password correct → generate and return a JWT
        return jwtUtil.generateToken(user.getUsername());
    }
}