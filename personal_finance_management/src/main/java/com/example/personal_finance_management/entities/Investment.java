package com.example.personal_finance_management.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments")
@Data
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime closedAt;

    @Column
    private Double profitLoss;

    @Column(nullable = false)
    private Boolean isActive = true;
}