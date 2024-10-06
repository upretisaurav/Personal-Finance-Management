package com.example.personal_finance_management.repositories;

import com.example.personal_finance_management.entities.Budget;
import com.example.personal_finance_management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserAndEndDateGreaterThanEqual(User user, LocalDate date);
    Optional<Budget> findByUserAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user, String category, LocalDate date, LocalDate sameDate);
}