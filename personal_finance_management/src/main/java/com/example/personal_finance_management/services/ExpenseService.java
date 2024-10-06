package com.example.personal_finance_management.services;

import com.example.personal_finance_management.dto.ExpenseDTO;
import com.example.personal_finance_management.entities.Expense;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.exceptions.ResourceNotFoundException;
import com.example.personal_finance_management.repositories.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    public ExpenseDTO createExpense(ExpenseDTO expenseDTO, String userEmail) {
        User user = userService.getCurrentUser(userEmail);
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(expenseDTO.getCategory());
        expense.setAmount(expenseDTO.getAmount());
        expense.setExpenseDate(expenseDTO.getExpenseDate());
        expense.setDescription(expenseDTO.getDescription());

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDTO(savedExpense);
    }

    public List<ExpenseDTO> getMonthlyExpenses(String userEmail, int year, int month) {
        User user = userService.getCurrentUser(userEmail);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpenseDTO updateExpense(Long expenseId, ExpenseDTO expenseDTO, String userEmail) throws AccessDeniedException {
        User user = userService.getCurrentUser(userEmail);
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to update this expense");
        }

        expense.setCategory(expenseDTO.getCategory());
        expense.setAmount(expenseDTO.getAmount());
        expense.setExpenseDate(expenseDTO.getExpenseDate());
        expense.setDescription(expenseDTO.getDescription());

        Expense updatedExpense = expenseRepository.save(expense);
        return convertToDTO(updatedExpense);
    }

    public void deleteExpense(Long expenseId, String userEmail) throws AccessDeniedException {
        User user = userService.getCurrentUser(userEmail);
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this expense");
        }

        expenseRepository.delete(expense);
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setCategory(expense.getCategory());
        dto.setAmount(expense.getAmount());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setDescription(expense.getDescription());
        return dto;
    }
}