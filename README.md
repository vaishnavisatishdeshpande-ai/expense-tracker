# Expense Tracker

## Overview

A full-stack expense tracking application built using Java Spring Boot and a lightweight frontend.

Users can add, view, filter, and sort expenses with proper validation and idempotency handling.

---

## Live Application

https://expense-tracker-1-9nuy.onrender.com

## Repository

https://github.com/vaishnavisatishdeshpande-ai/expense-tracker

---

## Core Features

* Add expenses with validation
* Filter by category (supports partial match, case-insensitive)
* Sort by date (ascending / descending toggle)
* View total expense amount
* Idempotent API handling (prevents duplicate requests)

---

## Tech Stack

Backend:

* Java 17
* Spring Boot
* Spring Data JPA
* H2 In-Memory Database

Frontend:

* HTML
* CSS
* Vanilla JavaScript

Deployment:

* Docker
* Render

---

## API Overview

### Create Expense

POST /expenses

Headers:
Idempotency-Key: unique-key

Body:
{
"amount": 1000,
"category": "Food",
"description": "Lunch",
"date": "2026-04-24"
}

---

### Get Expenses

GET /expenses?category=food&sort=date_desc

Response:
{
"expenses": [...],
"total": 1000
}

---

## Key Design Decisions

* Layered architecture (Controller → Service → Repository) to maintain separation of concerns
* DTO (ExpenseResponse) used to decouple API from persistence model
* Idempotency implemented using a dedicated table to prevent duplicate expense creation
* In-memory database chosen for faster development and simplicity

---

## Validation Rules

* Amount must be a positive number
* Category is required
* Date is required

---

## Trade-offs

* H2 used instead of persistent database (data resets on restart)
* Filtering and sorting done in-memory instead of optimized DB queries
* Minimal UI (focus on functionality over design complexity)

---

## How to Run Locally

1. Clone the repository
2. Run:
   mvn spring-boot:run
3. Open:
   http://localhost:8080

---

## Testing

Includes:

* Positive test cases
* Negative validation tests
* Idempotency behavior tests
* Filtering and sorting tests

---

## Future Improvements

* Add persistent database (PostgreSQL)
* Add authentication (user-specific expenses)
* Pagination support
* Better UI with charts and analytics
* API documentation (Swagger)
* Production-grade error handling

---

## Notes

* Uses H2 in-memory database (data is not persisted)
* Application is designed for clarity and correctness within time constraints
* Focus was on backend design, correctness, and reliability over UI complexity
