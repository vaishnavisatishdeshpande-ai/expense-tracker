package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<Idempotency, String> {
}