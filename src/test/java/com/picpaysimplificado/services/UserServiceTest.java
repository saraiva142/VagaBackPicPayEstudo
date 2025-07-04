package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {}

    @Nested
    class validateTransactionUser {
        @Test
        @DisplayName("Should validate transaction about user with success")
        void validateTransactionWithSuccess() {
            //Arrange
            User sender = new User();
            sender.setId(1L);
            sender.setFirstname("First Name");
            sender.setLastname("Last Name");
            sender.setEmail("email@email.com");
            sender.setBalance(new BigDecimal("100"));
            sender.setDocument("123456789");
            sender.setUserType(UserType.COMMON);

            BigDecimal value = new BigDecimal(50);

            //Act
            assertDoesNotThrow(() -> userService.validateTransaction(sender, value));

            //Assert
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Should return error if sender is merchant type")
        void validateTransactionIfSenderIsMechant() {
            //Arrange
            User sender = new User();
            sender.setId(1L);
            sender.setFirstname("First Name");
            sender.setLastname("Last Name");
            sender.setEmail("email@email.com");
            sender.setBalance(new BigDecimal("100"));
            sender.setDocument("123456789");
            sender.setUserType(UserType.MERCHANT);

            BigDecimal value = new BigDecimal(50);

            //Act & Assert
            Exception thrown = assertThrows(Exception.class, () -> {
                userService.validateTransaction(sender, value);
            });

            assertEquals("Usuário do tipo lojista não está autorizado a realizar transação", thrown.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Should return error if the amount value is not enough")
        void validateTransactionIfAmountIsNotEnough() {
            //Arrange
            Long id = 1L;
            User sender = new User();
            sender.setId(1L);
            sender.setFirstname("First Name");
            sender.setLastname("Last Name");
            sender.setEmail("email@email.com");
            sender.setBalance(new BigDecimal("20"));
            sender.setDocument("123456789");
            sender.setUserType(UserType.COMMON);

            BigDecimal value = new BigDecimal(50);

            //Act && Assert
            Exception thrown = assertThrows(Exception.class, () -> {
                userService.validateTransaction(sender, value);
            });
            assertEquals("Saldo insuficiente", thrown.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Should return exception if balance is zero and the transaction is positive")
        void validateTransactionIfBalanceIsZeroAndTransactionIsPositive() {
            //Arrange
            Long id = 1L;
            User sender = new User();
            sender.setId(1L);
            sender.setFirstname("First Name");
            sender.setLastname("Last Name");
            sender.setEmail("email@email.com");
            sender.setBalance(BigDecimal.ZERO);
            sender.setDocument("123456789");
            sender.setUserType(UserType.COMMON);

            BigDecimal amount = new BigDecimal(20); // Positive Transaction

            //Act & Assert
            Exception thrown = assertThrows(Exception.class, () -> {
                userService.validateTransaction(sender, amount);
            });
            assertEquals("Saldo insuficiente", thrown.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("Should validate transaction when the balance is the same of the amount value")
        void validateTransactionWhenBalanceIsSameAmountValue() {
            //Arrange
            Long id = 1L;
            User sender = new User();
            sender.setId(1L);
            sender.setFirstname("First Name");
            sender.setLastname("Last Name");
            sender.setEmail("email@email.com");
            sender.setBalance(new BigDecimal(20));
            sender.setDocument("123456789");
            sender.setUserType(UserType.COMMON);

            BigDecimal amount = new BigDecimal(20); // The same of balance

            //Act
            assertDoesNotThrow(() -> userService.validateTransaction(sender, amount));

            //Assert
            verifyNoInteractions(userRepository);
        }


    }


    @Test
    void findUserById() {
    }

    @Test
    void createUser() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void saveUser() {
    }
}