package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;


    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should send notification successfully when everything is OK")
    void sendNotification() {
        // Arrange
        String email = "email@email.com";
        String message = "Transaction completed successfully!";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setFirstname("Test User");
        user.setLastname("Last Name");
        user.setBalance(new BigDecimal("100.00"));
        user.setDocument("123456789");
        user.setUserType(UserType.COMMON);

        // Act
        when(restTemplate.postForEntity("https://util.devi.tools/api/v1/notify",
                new NotificationDTO(email, message), String.class))
                .thenReturn(new ResponseEntity<>("Notification sent", HttpStatus.OK));

        // Assert
        assertDoesNotThrow(() -> notificationService.sendNotification(user, message));

    }
}