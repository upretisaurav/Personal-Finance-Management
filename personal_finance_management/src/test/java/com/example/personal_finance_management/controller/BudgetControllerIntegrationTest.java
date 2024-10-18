package com.example.personal_finance_management.controller;

import com.example.personal_finance_management.dto.BudgetDTO;
import com.example.personal_finance_management.entities.Budget;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.repositories.BudgetRepository;
import com.example.personal_finance_management.repositories.UserRepository;
import com.example.personal_finance_management.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class BudgetControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String authToken;
    private User testUser;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        budgetRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(new BCryptPasswordEncoder().encode("password"));
        testUser = userRepository.save(testUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(),
                "password"
        );
        authToken = "Bearer " + tokenProvider.generateToken(authentication);

        testBudget = new Budget();
        testBudget.setUser(testUser);
        testBudget.setCategory("Food");
        testBudget.setTargetAmount(500.0);
        testBudget.setStartDate(LocalDate.now());
        testBudget.setEndDate(LocalDate.now().plusMonths(1));
        testBudget = budgetRepository.save(testBudget);
    }


//    @Test
//    @DisplayName("Should create budget through API")
//    void shouldCreateBudget() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        BudgetDTO budgetDTO = new BudgetDTO();
//        budgetDTO.setCategory("Entertainment");
//        budgetDTO.setTargetAmount(300.0);
//        budgetDTO.setStartDate(LocalDate.now());
//        budgetDTO.setEndDate(LocalDate.now().plusMonths(1));
//
//        mockMvc.perform(post("/api/budgets")
//                        .header("Authorization", authToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(budgetDTO)))
//                .andExpect(status().isCreated())
//                .andExpect((ResultMatcher) jsonPath("$.category").value("Entertainment"))
//                .andExpect((ResultMatcher) jsonPath("$.targetAmount").value(300.0));
//    }
//
//    @Test
//    @DisplayName("Should get budget status through API")
//    void shouldGetBudgetStatus() throws Exception {
//        mockMvc.perform(get("/api/budgets/status")
//                        .param("category", "Food")
//                        .header("Authorization", authToken))
//                .andExpect(status().isOk())
//                .andExpect((ResultMatcher) jsonPath("$.budget.category").value("Food"))
//                .andExpect((ResultMatcher) jsonPath("$.targetAmount").value(500.0));
//    }
//
//    @Test
//    @DisplayName("Should update budget through API")
//    void shouldUpdateBudget() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        BudgetDTO updateDTO = new BudgetDTO();
//        updateDTO.setCategory("Food");
//        updateDTO.setTargetAmount(600.0);
//        updateDTO.setStartDate(LocalDate.now());
//        updateDTO.setEndDate(LocalDate.now().plusMonths(1));
//
//        mockMvc.perform(put("/api/budgets/" + testBudget.getId())
//                        .header("Authorization", authToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect((ResultMatcher) jsonPath("$.targetAmount").value(600.0));
//    }
//
//    @Test
//    @DisplayName("Should delete budget through API")
//    void shouldDeleteBudget() throws Exception {
//        mockMvc.perform(delete("/api/budgets/" + testBudget.getId())
//                        .header("Authorization", authToken))
//                .andExpect(status().isNoContent());
//
//        assertFalse(budgetRepository.findById(testBudget.getId()).isPresent());
//    }

    @Test
    @DisplayName("Should fail when creating invalid budget")
    void shouldFailWithInvalidBudget() throws Exception {
        BudgetDTO invalidBudget = new BudgetDTO();

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidBudget)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail when accessing with invalid token")
    void shouldFailWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/budgets/status")
                        .param("category", "Food")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}