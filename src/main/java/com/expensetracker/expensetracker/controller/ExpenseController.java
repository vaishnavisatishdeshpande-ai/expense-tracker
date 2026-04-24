package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.CreateExpenseRequest;
import com.expensetracker.expensetracker.dto.ExpenseResponse;
import com.expensetracker.expensetracker.entity.Expense;
import com.expensetracker.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(
            @RequestBody @Valid CreateExpenseRequest request,
            @RequestHeader("Idempotency-Key") String key) {

        return ResponseEntity.ok(
                expenseService.createExpense(request, key)
        );
    }

    @GetMapping
    public ResponseEntity<ExpenseResponse> getExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort) {

        return ResponseEntity.ok(
                expenseService.getExpenses(category, sort)
        );

    }
}