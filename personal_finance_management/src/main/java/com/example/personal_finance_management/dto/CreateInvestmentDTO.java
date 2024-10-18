package com.example.personal_finance_management.dto;

import lombok.Data;

@Data
public class CreateInvestmentDTO {
    private String name;
    private Double amount;
}