package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.CreateExpenseRequest;
import com.expensetracker.expensetracker.dto.ExpenseResponse;
import com.expensetracker.expensetracker.entity.Expense;

public interface ExpenseService {

    Expense createExpense(CreateExpenseRequest request, String idempotencyKey);

    ExpenseResponse getExpenses(String category, String sort);
}