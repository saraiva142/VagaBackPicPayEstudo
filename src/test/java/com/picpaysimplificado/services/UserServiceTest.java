package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Nested
    class findUserById {
        @Test
        @DisplayName("Should find a user by Id successfully")
        void findUserByIdWithSuccess() throws Exception {
            //Arrange
            Long id = 1L;
            User expectedUser = new User();
            expectedUser.setId(1L);
            expectedUser.setFirstname("First Name");
            expectedUser.setLastname("Last Name");
            expectedUser.setEmail("email@email.com");
            expectedUser.setBalance(new BigDecimal(100));
            expectedUser.setDocument("123456789");
            expectedUser.setUserType(UserType.COMMON);

            when(userRepository.findUserById(id)).thenReturn(Optional.of(expectedUser));

            //Act
            User foundUser = userService.findUserById(id);

            //Assert
            assertNotNull(foundUser);
            assertEquals(expectedUser.getId(), foundUser.getId());
            assertEquals(expectedUser.getFirstname(), foundUser.getFirstname());
            verify(userRepository, times(1)).findUserById(id);
        }

        @Test
        @DisplayName("Should return error message if user not found")
        void findUserByIdWhenUserNotFound() {
            //Arrange
            Long id = 99L;

            when(userRepository.findUserById(id)).thenReturn(Optional.empty());

            //Act && Assert
            Exception thrown = assertThrows(Exception.class, () -> {
                userService.findUserById(id);
            });

            assertEquals("Usuário não encontrado", thrown.getMessage());
            verify(userRepository, times(1)).findUserById(id);
        }
    }

    @Nested
    class createUser {

        @Test
        @DisplayName("Should create some user with success")
        void createUserWithSuccess() {
            //I will do this with DTO already
            UserDTO user = new UserDTO(
                    "Ana",
                    "Teste",
                    "123456789",
                    new BigDecimal(100),
                    "ana@email.com",
                    "senha123",
                    UserType.COMMON
            );

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
                User userBeingSaved = invocation.getArgument(0);
                // Simula que o repositório atribuiu um ID (como faria um banco de dados real)
                if (userBeingSaved.getId() == null) {
                    userBeingSaved.setId(1L); // Atribui um ID de exemplo
                }
                return userBeingSaved;
            });

            //Act
            User createdUser = userService.createUser(user);

            //Assert
            User savedUser = userCaptor.getValue();

            assertNotNull(createdUser);
            assertNotNull(createdUser.getId());
            assertEquals(savedUser.getFirstname(), createdUser.getFirstname());
            assertEquals(savedUser.getLastname(), createdUser.getLastname());
            assertEquals(savedUser.getDocument(), createdUser.getDocument());
            assertEquals(savedUser.getEmail(), createdUser.getEmail());
            assertEquals(savedUser.getBalance(), createdUser.getBalance());
            assertEquals(savedUser.getPassword(), createdUser.getPassword());
            assertEquals(savedUser.getUserType(), createdUser.getUserType());
        }

    }

    @Test
    void getAllUsers() {
    }

    @Test
    void saveUser() {
    }
}