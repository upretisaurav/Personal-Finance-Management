package com.example.personal_finance_management.modules.user.controller;

import com.example.personal_finance_management.modules.user.dto.UserLoginDTO;
import com.example.personal_finance_management.modules.user.dto.UserRegistrationDTO;
import com.example.personal_finance_management.modules.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDTO registrationDTO) {
        userService.registerUser(registrationDTO);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        boolean isAuthenticated = userService.loginUser(userLoginDTO);
        if (isAuthenticated) {
            return ResponseEntity.ok("User logged in successfully!");
        }
        return ResponseEntity.status(401).body("Invalid credentials for login");
    }
}
