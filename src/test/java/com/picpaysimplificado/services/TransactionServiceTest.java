package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepositry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepositry repository;

    @Mock
    private AuthorizationService authService;

    @Mock
    private NotificationService notificationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should create transaction successfully when everything is OK")
    void createTransactionWithSuccess() throws Exception {
        //Arrange
        User sender = new User(1L, "Maria", "Souza", "99999999901", "maria@email.com", "12345", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Joao", "Souza", "99999999902", "joao@email.com", "12345", new BigDecimal(10), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);

        //Act
        transactionService.createTransaction(request);

        //Assert
        verify(repository, times(1)).save(any());

        sender.setBalance(new BigDecimal(0));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(20));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transação realizada com sucesso!");
        verify(notificationService, times(1)).sendNotification(receiver, "Transação recebida com sucesso!");
    }

    @Test
    @DisplayName("Should throw exception when transaction is not allowed")
    void createTransactionIsNotAllowed() throws Exception {
        //Arrange
        User sender = new User(1L, "Maria", "Souza", "99999999901", "maria@email.com", "12345", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Joao", "Souza", "99999999902", "joao@email.com", "12345", new BigDecimal(10), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
            //Act
            transactionService.createTransaction(request);
        });

        //Assert
        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());

    }
}