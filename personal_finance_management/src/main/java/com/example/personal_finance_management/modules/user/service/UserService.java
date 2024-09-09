package com.example.personal_finance_management.modules.user.service;

import com.example.personal_finance_management.modules.user.dto.UserLoginDTO;
import com.example.personal_finance_management.modules.user.dto.UserRegistrationDTO;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final InMemoryUserDetailsManager inMemoryUserDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserService(InMemoryUserDetailsManager inMemoryUserDetailsManager, PasswordEncoder passwordEncoder) {
        this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserRegistrationDTO registrationDTO) {
        if (inMemoryUserDetailsManager.userExists(registrationDTO.getEmail())) {
            throw new IllegalStateException("Email already exists!");
        }

        inMemoryUserDetailsManager.createUser(
                User.builder()
                        .username(registrationDTO.getEmail())
                        .password(passwordEncoder.encode(registrationDTO.getPassword()))
                        .roles("USER")
                        .build()
        );
    }

    public boolean loginUser(UserLoginDTO userLoginDTO) {
        UserDetails userDetails = inMemoryUserDetailsManager.loadUserByUsername(userLoginDTO.getEmail());
        return passwordEncoder.matches(userLoginDTO.getPassword(), userDetails.getPassword());
    }
}
