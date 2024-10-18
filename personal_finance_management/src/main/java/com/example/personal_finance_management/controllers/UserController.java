package com.example.personal_finance_management.controllers;

import com.example.personal_finance_management.dto.AuthResponseDTO;
import com.example.personal_finance_management.dto.UserLoginDTO;
import com.example.personal_finance_management.dto.UserRegistrationDTO;
import com.example.personal_finance_management.enums.BalanceSource;
import com.example.personal_finance_management.services.UserService;
import com.example.personal_finance_management.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDTO registrationDTO) {
        log.info("User registration request received for email: {}", registrationDTO.getEmail());
        userService.registerUser(registrationDTO);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("User login request received for email: {}", userLoginDTO.getEmail());
        String jwt = userService.loginUser(userLoginDTO);
        return new ResponseEntity<>(new AuthResponseDTO(jwt), HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(@RequestHeader("Authorization") String bearerToken) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Balance request received for user: {}", userEmail);
        Double balance = userService.getBalance(userEmail);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/balance/add")
    public ResponseEntity<String> addBalance(@RequestHeader("Authorization") String bearerToken,
                                             @RequestParam Double amount,
                                             @RequestParam BalanceSource source) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Add balance request received for user: {}, amount: {}, source: {}", userEmail, amount, source);
        userService.addBalance(userEmail, amount, source);
        return ResponseEntity.ok("Balance added successfully");
    }

    @GetMapping("/balance/sources")
    public ResponseEntity<BalanceSource[]> getBalanceSources() {
        return ResponseEntity.ok(BalanceSource.values());
    }
}