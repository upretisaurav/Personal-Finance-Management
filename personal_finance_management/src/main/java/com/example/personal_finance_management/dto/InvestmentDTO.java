package com.example.personal_finance_management.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InvestmentDTO {
    private Long id;
    private String name;
    private Double amount;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Double profitLoss;
    private Boolean isActive;
}