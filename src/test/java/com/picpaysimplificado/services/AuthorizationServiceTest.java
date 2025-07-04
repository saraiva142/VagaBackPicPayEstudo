package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    @InjectMocks
    private AuthorizationService authorizationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should authorize transaction successfully when response is OK")
    void authorizeTransaction() {
        //Arrange
        User sender = new User();
        sender.setId(1L);
        sender.setBalance(new BigDecimal("100.00"));

        BigDecimal value = new BigDecimal("10.00");

        Map<String, String> responseMap = Map.of("message", "Autorizado");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseMap, HttpStatus.OK);

        when(restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class))
                .thenReturn(responseEntity);

        //Act
        boolean result = authorizationService.authorizeTransaction(sender, value);

        //Assert
        // Since the method does not return a value, we can only check if it executes without throwing an exception.
        assertTrue(result);
        verify(restTemplate, times(1)).getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
    }

}