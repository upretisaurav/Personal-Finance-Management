package com.example.personal_finance_management.repositories;

import com.example.personal_finance_management.entities.Expense;
import com.example.personal_finance_management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Expense> findByUserAndCategory(User user, String category);
    List<Expense> findByUserAndCategoryAndExpenseDateBetween(
            User user,
            String category,
            LocalDate startDate,
            LocalDate endDate
    );
}