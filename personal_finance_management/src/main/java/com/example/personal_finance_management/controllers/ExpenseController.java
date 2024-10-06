package com.example.personal_finance_management.controllers;

import com.example.personal_finance_management.dto.ExpenseDTO;
import com.example.personal_finance_management.services.ExpenseService;
import com.example.personal_finance_management.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class ExpenseController {
    private final ExpenseService expenseService;
    private final JwtUtils jwtUtils;

    @Autowired
    public ExpenseController(ExpenseService expenseService, JwtUtils jwtUtils) {
        this.expenseService = expenseService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO,
                                                    @RequestHeader("Authorization") String bearerToken) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Creating expense for user: {}", userEmail);
        ExpenseDTO created = expenseService.createExpense(expenseDTO, userEmail);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<ExpenseDTO>> getMonthlyExpenses(
            @RequestParam int year,
            @RequestParam int month,
            @RequestHeader("Authorization") String bearerToken) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Fetching monthly expenses for user: {}, year: {}, month: {}", userEmail, year, month);
        List<ExpenseDTO> expenses = expenseService.getMonthlyExpenses(userEmail, year, month);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDTO expenseDTO,
            @RequestHeader("Authorization") String bearerToken) throws AccessDeniedException {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Updating expense {} for user: {}", id, userEmail);
        ExpenseDTO updated = expenseService.updateExpense(id, expenseDTO, userEmail);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            @RequestHeader("Authorization") String bearerToken) throws AccessDeniedException {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Deleting expense {} for user: {}", id, userEmail);
        expenseService.deleteExpense(id, userEmail);
        return ResponseEntity.noContent().build();
    }

}
