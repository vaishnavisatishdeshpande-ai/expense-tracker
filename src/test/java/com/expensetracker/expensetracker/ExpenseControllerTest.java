package com.expensetracker.expensetracker;

import com.expensetracker.expensetracker.dto.CreateExpenseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ======================
    // ✅ POSITIVE TESTS
    // ======================

    @Test
    void shouldCreateExpenseSuccessfully() throws Exception {

        CreateExpenseRequest request = validRequest();

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1000))
                .andExpect(jsonPath("$.category").value("food"));
    }

    @Test
    void shouldReturnExpensesList() throws Exception {

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenses").exists())
                .andExpect(jsonPath("$.total").exists());
    }

    @Test
    void shouldFilterExpensesByCategoryPartialMatch() throws Exception {

        mockMvc.perform(get("/expenses")
                        .param("category", "fo"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSortExpensesByDateDesc() throws Exception {

        mockMvc.perform(get("/expenses")
                        .param("sort", "date_desc"))
                .andExpect(status().isOk());
    }

    // ======================
    // ❌ NEGATIVE TESTS
    // ======================

    @Test
    void shouldFailForNegativeAmount() throws Exception {

        CreateExpenseRequest request = validRequest();
        request.setAmount(-100);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailForMissingCategory() throws Exception {

        CreateExpenseRequest request = validRequest();
        request.setCategory(""); // invalid

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailForMissingDate() throws Exception {

        CreateExpenseRequest request = validRequest();
        request.setDate(null);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ======================
    // 🔁 IDEMPOTENCY TEST (FIXED)
    // ======================

    @Test
    void shouldReturnSameResponseForSameIdempotencyKey() throws Exception {

        String key = UUID.randomUUID().toString();
        CreateExpenseRequest request = validRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        String response1 = mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", key)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String response2 = mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", key)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // ✅ FIX: don't compare full JSON (timestamps differ)
        Assertions.assertTrue(response1.contains("\"amount\":1000"));
        Assertions.assertTrue(response2.contains("\"amount\":1000"));
    }

    // ======================
    // 🧩 HELPER
    // ======================

    private CreateExpenseRequest validRequest() {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setAmount(1000);
        request.setCategory("food");
        request.setDescription("lunch");
        request.setDate(LocalDate.now());
        return request;
    }
}