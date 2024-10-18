package com.example.personal_finance_management.controllers;

import com.example.personal_finance_management.dto.InvestmentDTO;
import com.example.personal_finance_management.dto.CreateInvestmentDTO;
import com.example.personal_finance_management.dto.UpdateInvestmentDTO;
import com.example.personal_finance_management.dto.CloseInvestmentDTO;
import com.example.personal_finance_management.services.InvestmentService;
import com.example.personal_finance_management.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
@Slf4j
public class InvestmentController {

    private final InvestmentService investmentService;
    private final JwtUtils jwtUtils;

    public InvestmentController(InvestmentService investmentService, JwtUtils jwtUtils) {
        this.investmentService = investmentService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<InvestmentDTO> createInvestment(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody CreateInvestmentDTO createDTO) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Create investment request received for user: {}, name: {}, amount: {}", userEmail, createDTO.getName(), createDTO.getAmount());
        InvestmentDTO investment = investmentService.createInvestment(userEmail, createDTO);
        return ResponseEntity.ok(investment);
    }

    @GetMapping
    public ResponseEntity<List<InvestmentDTO>> getUserInvestments(@RequestHeader("Authorization") String bearerToken) {
        String userEmail = jwtUtils.extractEmail(bearerToken);
        log.info("Get investments request received for user: {}", userEmail);
        List<InvestmentDTO> investments = investmentService.getUserInvestments(userEmail);
        return ResponseEntity.ok(investments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentDTO> getInvestment(@PathVariable Long id) {
        log.info("Get investment request received for id: {}", id);
        InvestmentDTO investment = investmentService.getInvestmentById(id);
        return ResponseEntity.ok(investment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestmentDTO> updateInvestment(
            @PathVariable Long id,
            @RequestBody UpdateInvestmentDTO updateDTO) {
        log.info("Update investment request received for id: {}, name: {}, amount: {}", id, updateDTO.getName(), updateDTO.getAmount());
        InvestmentDTO investment = investmentService.updateInvestment(id, updateDTO);
        return ResponseEntity.ok(investment);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<InvestmentDTO> closeInvestment(
            @PathVariable Long id,
            @RequestBody CloseInvestmentDTO closeDTO) {
        log.info("Close investment request received for id: {}, profit/loss: {}", id, closeDTO.getProfitLoss());
        InvestmentDTO investment = investmentService.closeInvestment(id, closeDTO);
        return ResponseEntity.ok(investment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        log.info("Delete investment request received for id: {}", id);
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }
}









