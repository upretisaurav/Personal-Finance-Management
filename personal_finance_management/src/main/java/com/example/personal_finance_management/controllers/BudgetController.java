package com.example.personal_finance_management.controllers;

import com.example.personal_finance_management.dto.BudgetDTO;
import com.example.personal_finance_management.services.BudgetService;
import com.example.personal_finance_management.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
@Slf4j
public class BudgetController {
    private final BudgetService budgetService;
    private final JwtUtils jwtUtils;

    @Autowired
    public BudgetController(BudgetService budgetService, JwtUtils jwtUtils) {
        this.budgetService = budgetService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<List<BudgetDTO>> getAllBudgets(@RequestHeader("Authorization") String bearerToken) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Fetching all budgets for user: {}", userEmail);
        List<BudgetDTO> budgets = budgetService.getAllBudgets(userEmail);
        return ResponseEntity.ok(budgets);
    }


    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody BudgetDTO budgetDTO,
                                                  @RequestHeader("Authorization") String bearerToken) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Creating budget for user: {}", userEmail);
        BudgetDTO created = budgetService.createBudget(budgetDTO, userEmail);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetDTO> updateBudget(
            @PathVariable Long budgetId,
            @RequestBody BudgetDTO budgetDTO,
            @RequestHeader("Authorization") String token) throws AccessDeniedException {
        String userEmail = jwtUtils.extractEmail(token);
        BudgetDTO updatedBudget = budgetService.updateBudget(budgetId, budgetDTO, userEmail);
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long budgetId,
            @RequestHeader("Authorization") String token) throws AccessDeniedException {
        String userEmail = jwtUtils.extractEmail(token);
        budgetService.deleteBudget(budgetId, userEmail);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBudgetStatus(
            @RequestParam String category,
            @RequestHeader("Authorization") String bearerToken) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Fetching budget status for user: {}, category: {}", userEmail, category);
        Map<String, Object> status = budgetService.getBudgetStatus(userEmail, category, LocalDate.now());
        return ResponseEntity.ok(status);
    }
}