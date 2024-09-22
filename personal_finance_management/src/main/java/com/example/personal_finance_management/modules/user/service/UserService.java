package com.example.personal_finance_management.modules.user.service;

import com.example.personal_finance_management.modules.user.dto.UserLoginDTO;
import com.example.personal_finance_management.modules.user.dto.UserRegistrationDTO;
import com.example.personal_finance_management.modules.user.entity.User;
import com.example.personal_finance_management.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserRegistrationDTO userRegistrationDTO) {
        log.info("User registration going on!!!");
        if (userRepository.findByEmail(userRegistrationDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        userRepository.save(user);
    }

    public boolean loginUser(UserLoginDTO userLoginDTO) {
        log.debug("User login going on!!!");
        return userRepository.findByEmail(userLoginDTO.getEmail())
                .map(user -> passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword()))
                .orElse(false);
    }
}
