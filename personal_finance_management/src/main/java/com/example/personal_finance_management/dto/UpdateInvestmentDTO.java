package com.example.personal_finance_management.dto;

import lombok.Data;

@Data
public class UpdateInvestmentDTO {
    private String name;
    private Double amount;
}