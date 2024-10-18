package com.example.personal_finance_management.services;

import com.example.personal_finance_management.dto.InvestmentDTO;
import com.example.personal_finance_management.dto.CreateInvestmentDTO;
import com.example.personal_finance_management.dto.UpdateInvestmentDTO;
import com.example.personal_finance_management.dto.CloseInvestmentDTO;
import com.example.personal_finance_management.entities.Investment;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.exceptions.ResourceNotFoundException;
import com.example.personal_finance_management.repositories.InvestmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final UserService userService;

    public InvestmentService(InvestmentRepository investmentRepository, UserService userService) {
        this.investmentRepository = investmentRepository;
        this.userService = userService;
    }

    private InvestmentDTO convertToDTO(Investment investment) {
        InvestmentDTO dto = new InvestmentDTO();
        dto.setId(investment.getId());
        dto.setName(investment.getName());
        dto.setAmount(investment.getAmount());
        dto.setCreatedAt(investment.getCreatedAt());
        dto.setClosedAt(investment.getClosedAt());
        dto.setProfitLoss(investment.getProfitLoss());
        dto.setIsActive(investment.getIsActive());
        return dto;
    }

    @Transactional
    public InvestmentDTO createInvestment(String userEmail, CreateInvestmentDTO createDTO) {
        log.info("Creating investment for user: {}, name: {}, amount: {}", userEmail, createDTO.getName(), createDTO.getAmount());
        User user = userService.getCurrentUser(userEmail);
        userService.subtractBalance(userEmail, createDTO.getAmount());

        Investment investment = new Investment();
        investment.setUser(user);
        investment.setName(createDTO.getName());
        investment.setAmount(createDTO.getAmount());
        investment.setCreatedAt(LocalDateTime.now());
        investment.setIsActive(true);

        return convertToDTO(investmentRepository.save(investment));
    }

    public List<InvestmentDTO> getUserInvestments(String userEmail) {
        log.info("Fetching investments for user: {}", userEmail);
        return investmentRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InvestmentDTO getInvestmentById(Long id) {
        log.info("Fetching investment with id: {}", id);
        return investmentRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
    }

    @Transactional
    public InvestmentDTO updateInvestment(Long id, UpdateInvestmentDTO updateDTO) {
        log.info("Updating investment with id: {}", id);
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
        investment.setName(updateDTO.getName());
        investment.setAmount(updateDTO.getAmount());
        return convertToDTO(investmentRepository.save(investment));
    }

    @Transactional
    public InvestmentDTO closeInvestment(Long id, CloseInvestmentDTO closeDTO) {
        log.info("Closing investment with id: {}, profit/loss: {}", id, closeDTO.getProfitLoss());
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
        if (!investment.getIsActive()) {
            throw new IllegalStateException("Investment is already closed");
        }

        investment.setIsActive(false);
        investment.setClosedAt(LocalDateTime.now());
        investment.setProfitLoss(closeDTO.getProfitLoss());

        Double totalReturn = investment.getAmount() + closeDTO.getProfitLoss();
        userService.addBalance(investment.getUser().getEmail(), totalReturn, com.example.personal_finance_management.enums.BalanceSource.INVESTMENT);

        return convertToDTO(investmentRepository.save(investment));
    }

    @Transactional
    public void deleteInvestment(Long id) {
        log.info("Deleting investment with id: {}", id);
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
        if (investment.getIsActive()) {
            userService.addBalance(investment.getUser().getEmail(), investment.getAmount(), com.example.personal_finance_management.enums.BalanceSource.INVESTMENT);
        }
        investmentRepository.delete(investment);
    }
}






