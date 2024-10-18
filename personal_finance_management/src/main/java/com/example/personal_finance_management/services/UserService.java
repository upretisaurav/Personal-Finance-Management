package com.example.personal_finance_management.services;

import com.example.personal_finance_management.dto.UserLoginDTO;
import com.example.personal_finance_management.dto.UserRegistrationDTO;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.enums.BalanceSource;
import com.example.personal_finance_management.exceptions.ResourceNotFoundException;
import com.example.personal_finance_management.repositories.UserRepository;
import com.example.personal_finance_management.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public void registerUser(UserRegistrationDTO userRegistrationDTO) {
        log.info("Attempting to register user with email: {}", userRegistrationDTO.getEmail());
        if (userRepository.findByEmail(userRegistrationDTO.getEmail()).isPresent()) {
            log.warn("User with email {} already exists", userRegistrationDTO.getEmail());
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setBalance(0.0);
        userRepository.save(user);
        log.info("User registered successfully with email: {}", user.getEmail());
    }

    public String loginUser(UserLoginDTO userLoginDTO) {
        log.info("Attempting to login user with email: {}", userLoginDTO.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginDTO.getEmail(),
                            userLoginDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);
            log.info("User logged in successfully with email: {}", userLoginDTO.getEmail());
            return token;
        } catch (Exception e) {
            log.error("Failed to authenticate user with email: {}. Error: {}", userLoginDTO.getEmail(), e.getMessage());
            throw e;
        }
    }

    public User getCurrentUser(String email) {
        log.info("Fetching current user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
    }

    @Transactional
    public void addBalance(String userEmail, Double amount, BalanceSource source) {
        log.info("Adding balance {} to user with email: {} from source: {}", amount, userEmail, source);
        User user = getCurrentUser(userEmail);
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
        log.info("Balance updated successfully for user with email: {}. New balance: {}", userEmail, user.getBalance());
    }

    @Transactional
    public void subtractBalance(String userEmail, Double amount) {
        log.info("Subtracting balance {} from user with email: {}", amount, userEmail);
        User user = getCurrentUser(userEmail);
        if (user.getBalance() < amount) {
            log.error("Insufficient balance for user with email: {}", userEmail);
            throw new IllegalArgumentException("Insufficient balance");
        }
        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);
        log.info("Balance updated successfully for user with email: {}. New balance: {}", userEmail, user.getBalance());
    }

    public Double getBalance(String userEmail) {
        log.info("Fetching balance for user with email: {}", userEmail);
        User user = getCurrentUser(userEmail);
        log.info("Balance for user with email {}: {}", userEmail, user.getBalance());
        return user.getBalance();
    }

    @Transactional
    public void updateUser(User user) {
        log.info("Updating user with email: {}", user.getEmail());
        userRepository.save(user);
        log.info("User updated successfully with email: {}", user.getEmail());
    }
}