package com.example.personal_finance_management.services;

import com.example.personal_finance_management.dto.BudgetDTO;
import com.example.personal_finance_management.entities.Budget;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.exceptions.ResourceNotFoundException;
import com.example.personal_finance_management.repositories.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {
    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BudgetService budgetService;

    private User testUser;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testBudget = new Budget();
        testBudget.setId(1L);
        testBudget.setUser(testUser);
        testBudget.setCategory("Food");
        testBudget.setTargetAmount(500.0);
        testBudget.setStartDate(LocalDate.now().minusDays(5));
        testBudget.setEndDate(LocalDate.now().plusDays(25));
    }

    @Test
    @DisplayName("Should create budget successfully")
    void shouldCreateBudget() {
        BudgetDTO budgetDTO = new BudgetDTO();
        budgetDTO.setCategory("Food");
        budgetDTO.setTargetAmount(500.0);
        budgetDTO.setStartDate(LocalDate.now());
        budgetDTO.setEndDate(LocalDate.now().plusMonths(1));

        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);

        BudgetDTO result = budgetService.createBudget(budgetDTO, "test@example.com");

        assertNotNull(result);
        assertEquals("Food", result.getCategory());
        assertEquals(500.0, result.getTargetAmount());
        Mockito.verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    @DisplayName("Should get budget status successfully")
    void shouldGetBudgetStatus() {
        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(budgetRepository.findByUserAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                any(User.class), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.of(testBudget));


        Map<String, Object> result = budgetService.getBudgetStatus("test@example.com", "Food", LocalDate.now());

        assertNotNull(result);
        assertEquals(500.0, ((BudgetDTO) result.get("budget")).getTargetAmount());
    }

    @Test
    @DisplayName("Should update budget successfully")
    void shouldUpdateBudget() throws AccessDeniedException {
        BudgetDTO updateDTO = new BudgetDTO();
        updateDTO.setCategory("Food");
        updateDTO.setTargetAmount(600.0);

        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(budgetRepository.findById(anyLong())).thenReturn(Optional.of(testBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);

        BudgetDTO result = budgetService.updateBudget(1L, updateDTO, "test@example.com");

        assertNotNull(result);
        Mockito.verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    @DisplayName("Should delete budget successfully")
    void shouldDeleteBudget() throws AccessDeniedException {
        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(budgetRepository.findById(anyLong())).thenReturn(Optional.of(testBudget));

        budgetService.deleteBudget(1L, "test@example.com");

        Mockito.verify(budgetRepository).delete(any(Budget.class));

    }

    @Test
    @DisplayName("Should fail when budget not found")
    void shouldFailWhenBudgetNotFound() {
        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(budgetRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            budgetService.deleteBudget(1L, "test@example.com");
        });

        assertEquals("Budget not found with id: 1", exception.getMessage());
    }


    @Test
    @DisplayName("Should fail when user not authorized")
    void shouldFailWhenUserNotAuthorized() {
        User differentUser = new User();
        differentUser.setId(2L);

        when(userService.getCurrentUser(anyString())).thenReturn(differentUser);
        when(budgetRepository.findById(anyLong())).thenReturn(Optional.of(testBudget));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            budgetService.updateBudget(1L, new BudgetDTO(), "other@example.com");
        });

        assertEquals("You are not authorized to update this budget.", exception.getMessage());
    }

}