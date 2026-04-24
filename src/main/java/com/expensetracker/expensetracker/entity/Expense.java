package com.expensetracker.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;  // UUID as string; generate externally (e.g., in service layer using UUID.randomUUID().toString())

    @Column(name = "amount", nullable = false)
    private long amount;  // Stored in paise

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description")
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
