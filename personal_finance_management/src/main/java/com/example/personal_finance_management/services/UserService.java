package com.example.personal_finance_management.services;

import com.example.personal_finance_management.dto.UserLoginDTO;
import com.example.personal_finance_management.dto.UserRegistrationDTO;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.repositories.UserRepository;
import com.example.personal_finance_management.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}