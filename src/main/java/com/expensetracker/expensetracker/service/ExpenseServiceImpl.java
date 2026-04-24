package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.CreateExpenseRequest;
import com.expensetracker.expensetracker.dto.ExpenseResponse;
import com.expensetracker.expensetracker.entity.Expense;
import com.expensetracker.expensetracker.entity.Idempotency;
import com.expensetracker.expensetracker.repository.ExpenseRepository;
import com.expensetracker.expensetracker.repository.IdempotencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    @Override
    public Expense createExpense(CreateExpenseRequest request, String key) {

        // 1. Check idempotency
        Optional<Idempotency> existing = idempotencyRepository.findById(key);
        if (existing.isPresent()) {
            return expenseRepository.findById(existing.get().getExpenseId())
                    .orElseThrow();
        }

        // 2. Create expense
        Expense expense = new Expense();
        expense.setId(UUID.randomUUID().toString());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());

        Expense saved = expenseRepository.save(expense);

        // 3. Save idempotency (with race handling)
        Idempotency idempotency = new Idempotency();
        idempotency.setKey(key);
        idempotency.setExpenseId(saved.getId());

        try {
            idempotencyRepository.save(idempotency);
        } catch (Exception ex) {
            Optional<Idempotency> existingRecord = idempotencyRepository.findById(key);
            if (existingRecord.isPresent()) {
                return expenseRepository.findById(existingRecord.get().getExpenseId())
                        .orElseThrow();
            }
        }

        return saved;
    }

    @Override
    public ExpenseResponse getExpenses(String category, String sort) {

        List<Expense> expenses;

        if (category != null && !category.isBlank()) {
            expenses = expenseRepository
                    .findByCategoryContainingIgnoreCaseOrderByDateDesc(category.trim());
        } else if ("date_desc".equalsIgnoreCase(sort)) {
            expenses = expenseRepository.findAll()
                    .stream()
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .toList();
        } else {
            expenses = expenseRepository.findAll();
        }

        long total = expenses.stream()
                .mapToLong(Expense::getAmount)
                .sum();

        return new ExpenseResponse(expenses, total);
    }
}