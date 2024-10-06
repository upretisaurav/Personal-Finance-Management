package com.example.personal_finance_management.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.personal_finance_management.dto.ExpenseDTO;
import com.example.personal_finance_management.entities.Expense;
import com.example.personal_finance_management.entities.User;
import com.example.personal_finance_management.exceptions.ResourceNotFoundException;
import com.example.personal_finance_management.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private Expense testExpense;
    private ExpenseDTO testExpenseDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setUser(testUser);
        testExpense.setAmount(100.0);
        testExpense.setCategory("Food");
        testExpense.setExpenseDate(LocalDate.now());
        testExpense.setDescription("Test expense");

        testExpenseDTO = new ExpenseDTO();
        testExpenseDTO.setAmount(100.0);
        testExpenseDTO.setCategory("Food");
        testExpenseDTO.setExpenseDate(LocalDate.now());
        testExpenseDTO.setDescription("Test expense");
    }

    @Test
    @DisplayName("Should create expense successfully")
    void shouldCreateExpense() {
        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        ExpenseDTO result = expenseService.createExpense(testExpenseDTO, "test@example.com");

        assertNotNull(result);
        assertEquals(testExpenseDTO.getAmount(), result.getAmount());
        assertEquals(testExpenseDTO.getCategory(), result.getCategory());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should get monthly expenses successfully")
    void shouldGetMonthlyExpenses() {
        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(expenseRepository.findByUserAndExpenseDateBetween(any(), any(), any()))
                .thenReturn(Arrays.asList(testExpense));

        List<ExpenseDTO> results = expenseService.getMonthlyExpenses(
                "test@example.com",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue()
        );

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Should update expense successfully")
    void shouldUpdateExpense() throws AccessDeniedException {
        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        ExpenseDTO result = expenseService.updateExpense(1L, testExpenseDTO, "test@example.com");

        assertNotNull(result);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should delete expense successfully")
    void shouldDeleteExpense() throws AccessDeniedException {
        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));

        expenseService.deleteExpense(1L, "test@example.com");

        verify(expenseRepository).delete(any(Expense.class));
    }

    @Test
    @DisplayName("Should fail when expense not found")
    void shouldFailWhenExpenseNotFound() {
        when(userService.getCurrentUser(anyString())).thenReturn(testUser);
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                expenseService.deleteExpense(1L, "test@example.com"));

        assertEquals("Expense not found with id: 1", exception.getMessage());
    }


    @Test
    @DisplayName("Should fail when user not authorized")
    void shouldFailWhenUserNotAuthorized() {
        User differentUser = new User();
        differentUser.setId(2L);

        when(userService.getCurrentUser(anyString())).thenReturn(differentUser);
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                expenseService.updateExpense(1L, testExpenseDTO, "other@example.com"));

        assertEquals("You are not authorized to update this expense.", exception.getMessage());
    }

}

