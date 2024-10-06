package com.example.personal_finance_management.services;

import com.example.personal_finance_management.dto.BudgetDTO;
import com.example.personal_finance_management.entities.Budget;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.exceptions.ResourceNotFoundException;
import com.example.personal_finance_management.repositories.BudgetRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserService userService;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository,
                         UserService userService) {
        this.budgetRepository = budgetRepository;
        this.userService = userService;
    }


    public List<BudgetDTO> getAllBudgets(String userEmail) {
        User user = userService.getCurrentUser(userEmail);
        List<Budget> budgets = budgetRepository.findByUser(user);
        return budgets.stream().map(this::convertToDTO).toList();
    }



    public BudgetDTO createBudget(BudgetDTO budgetDTO, String userEmail) {
        User user = userService.getCurrentUser(userEmail);
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(budgetDTO.getCategory());
        budget.setTargetAmount(budgetDTO.getTargetAmount());
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());

        Budget savedBudget = budgetRepository.save(budget);
        return convertToDTO(savedBudget);
    }

    public Map<String, Object> getBudgetStatus(String userEmail, String category, LocalDate date) {
        User user = userService.getCurrentUser(userEmail);

        log.info("Fetching budget for category: {}, date: {}", category, date);

        Budget activeBudget = budgetRepository
                .findByUserAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        user, category, date, date)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No active budget found for category: " + category));

        Map<String, Object> status = new HashMap<>();
        status.put("budget", convertToDTO(activeBudget));
        status.put("targetAmount", activeBudget.getTargetAmount());

        return status;
    }


    public BudgetDTO updateBudget(Long budgetId, BudgetDTO budgetDTO, String userEmail) throws AccessDeniedException {
        User user = userService.getCurrentUser(userEmail);
        log.info("Updating budget with id: {} for user: {}", budgetId, userEmail);
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to update this budget");
        }

        budget.setCategory(budgetDTO.getCategory());
        budget.setTargetAmount(budgetDTO.getTargetAmount());
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());

        Budget updatedBudget = budgetRepository.save(budget);
        return convertToDTO(updatedBudget);
    }

    public void deleteBudget(Long budgetId, String userEmail) throws AccessDeniedException {
        User user = userService.getCurrentUser(userEmail);
        log.info("Deleting budget with id: {} for user: {}", budgetId, userEmail);
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this budget");
        }

        budgetRepository.delete(budget);
    }

    private BudgetDTO convertToDTO(Budget budget) {
        BudgetDTO dto = new BudgetDTO();
        dto.setId(budget.getId());
        dto.setCategory(budget.getCategory());
        dto.setTargetAmount(budget.getTargetAmount());
        dto.setStartDate(budget.getStartDate());
        dto.setEndDate(budget.getEndDate());
        return dto;
    }
}