package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.Expense;

import java.util.List;

public class ExpenseResponse {

    private List<Expense> expenses;
    private long total;

    public ExpenseResponse(List<Expense> expenses, long total) {
        this.expenses = expenses;
        this.total = total;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public long getTotal() {
        return total;
    }
}