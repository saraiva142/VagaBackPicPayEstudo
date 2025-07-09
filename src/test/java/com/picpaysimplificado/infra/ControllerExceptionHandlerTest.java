package com.picpaysimplificado.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = ControllerExceptionHandlerTest.class)
class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler handler = new ControllerExceptionHandler();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void threatDuplicateEntry() {
    }

    @Test
    @DisplayName("Should return 404 not found with empty body")
    void threat404() {
        //Arrange
        EntityNotFoundException exception = new EntityNotFoundException("Usuário não encontrado para o ID fornecido");

        //Act
        ResponseEntity response = handler.threat404(exception);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        assertTrue(response.hasBody() == false);


    }
}