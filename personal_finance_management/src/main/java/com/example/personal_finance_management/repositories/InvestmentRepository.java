package com.example.personal_finance_management.repositories;

import com.example.personal_finance_management.entities.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByUserEmail(String email);
}