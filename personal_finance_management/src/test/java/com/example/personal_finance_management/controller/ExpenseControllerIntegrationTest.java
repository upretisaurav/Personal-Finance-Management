package com.example.personal_finance_management.controller;

import com.example.personal_finance_management.dto.ExpenseDTO;
import com.example.personal_finance_management.entities.Expense;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.repositories.ExpenseRepository;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ExpenseControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String authToken;
    private User testUser;
    private Expense testExpense;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        userRepository.deleteAll();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(new BCryptPasswordEncoder().encode("password"));
        testUser = userRepository.save(testUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(),
                "password"
        );
        authToken = "Bearer " + tokenProvider.generateToken(authentication);

        testExpense = new Expense();
        testExpense.setUser(testUser);
        testExpense.setAmount(100.0);
        testExpense.setCategory("Food");
        testExpense.setExpenseDate(LocalDate.now());
        testExpense.setDescription("Test expense");
        testExpense = expenseRepository.save(testExpense);
    }

    @Test
    @DisplayName("Should create expense through API")
    void shouldCreateExpense() throws Exception {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(150.0);
        expenseDTO.setCategory("Entertainment");
        expenseDTO.setExpenseDate(LocalDate.now());
        expenseDTO.setDescription("Movie night");

        mockMvc.perform(post("/api/expenses")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("Entertainment"))
                .andExpect(jsonPath("$.amount").value(150.0));
    }

    @Test
    @DisplayName("Should get monthly expenses through API")
    void shouldGetMonthlyExpenses() throws Exception {
        mockMvc.perform(get("/api/expenses/monthly")
                        .param("year", String.valueOf(LocalDate.now().getYear()))
                        .param("month", String.valueOf(LocalDate.now().getMonthValue()))
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Food"))
                .andExpect(jsonPath("$[0].amount").value(100.0));
    }

    @Test
    @DisplayName("Should update expense through API")
    void shouldUpdateExpense() throws Exception {
        ExpenseDTO updateDTO = new ExpenseDTO();
        updateDTO.setAmount(200.0);
        updateDTO.setCategory("Food");
        updateDTO.setExpenseDate(LocalDate.now());
        updateDTO.setDescription("Updated expense");

        mockMvc.perform(put("/api/expenses/" + testExpense.getId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.0))
                .andExpect(jsonPath("$.description").value("Updated expense"));
    }

    @Test
    @DisplayName("Should delete expense through API")
    void shouldDeleteExpense() throws Exception {
        mockMvc.perform(delete("/api/expenses/" + testExpense.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        assertFalse(expenseRepository.findById(testExpense.getId()).isPresent());
    }

    @Test
    @DisplayName("Should fail when creating invalid expense")
    void shouldFailWithInvalidExpense() throws Exception {
        ExpenseDTO invalidExpense = new ExpenseDTO();

        mockMvc.perform(post("/api/expenses")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidExpense)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail when accessing with invalid token")
    void shouldFailWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/expenses/monthly")
                        .param("year", String.valueOf(LocalDate.now().getYear()))
                        .param("month", String.valueOf(LocalDate.now().getMonthValue()))
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}