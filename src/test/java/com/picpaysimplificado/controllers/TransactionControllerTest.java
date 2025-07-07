package com.picpaysimplificado.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.services.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create a transaction successfully and return 200")
    void createTransactionSuccessfullyReturn200() throws Exception {
        //Arrange
        Long id = 1L;
        TransactionDTO request = new TransactionDTO(
                new BigDecimal(188),
                id,
                id
        );

        User sender = new User();
        sender.setId(id);
        sender.setFirstname("First Name");
        sender.setLastname("Last Name");
        sender.setEmail("email@email.com");
        sender.setBalance(new BigDecimal(12));
        sender.setDocument("123456789");
        sender.setUserType(UserType.COMMON);

        User receiver = new User();
        receiver.setId(id);
        receiver.setFirstname("First Name");
        receiver.setLastname("Last Name");
        receiver.setEmail("email@email.com");
        receiver.setBalance(new BigDecimal(2000));
        receiver.setDocument("1234567819");
        receiver.setUserType(UserType.MERCHANT);

        Transaction exceptedTransaction = new Transaction();
        exceptedTransaction.setId(id);
        exceptedTransaction.setAmount(new BigDecimal(100));
        exceptedTransaction.setSender(sender);
        exceptedTransaction.setReceiver(receiver);
        exceptedTransaction.setTimestamp(LocalDateTime.now());

        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(exceptedTransaction);

        //Act & Assert
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exceptedTransaction.getId()))
                .andExpect(jsonPath("$.amount").value(exceptedTransaction.getAmount()))
                .andExpect(jsonPath("$.sender.id").value(exceptedTransaction.getSender().getId()))
                .andExpect(jsonPath("$.receiver.id").value(exceptedTransaction.getReceiver().getId()));

        verify(transactionService, times(1)).createTransaction(any(TransactionDTO.class));
    }
}