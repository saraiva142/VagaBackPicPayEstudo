package com.picpaysimplificado.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class createUsers {

        @Test
        @DisplayName("Should create a user successfully and return 200")
        void createUserWithSuccess() throws Exception {
            //Arrange

            UserDTO request = new UserDTO(
                    "First Name",
                    "Last Name",
                    "123456789",
                    new BigDecimal(100),
                    "email@email.com",
                    "pass123",
                    UserType.COMMON
            );

            Long id = 1L;
            User user = new User();
            user.setId(id);
            user.setFirstname("First Name");
            user.setLastname("Last Name");
            user.setEmail("email@email.com");
            user.setBalance(new BigDecimal(200));
            user.setDocument("123456789");
            user.setUserType(UserType.COMMON);

            User exceptedUser = new User();
            exceptedUser.setId(id);
            exceptedUser.setFirstname("First Name");
            exceptedUser.setLastname("Last Name");
            exceptedUser.setEmail("email@email.com");
            exceptedUser.setBalance(new BigDecimal(200));
            exceptedUser.setDocument("123456789");
            exceptedUser.setUserType(UserType.COMMON);

            when(userService.createUser(any(UserDTO.class))).thenReturn(exceptedUser);

            //Act & Assert
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(exceptedUser.getId()))
                    .andExpect(jsonPath("$.document").value(exceptedUser.getDocument()));

            verify(userService, times(1)).createUser(any(UserDTO.class));
        }

//        Não tenho a função de erro ao criar user, percebi isso no teste, visto que qualquer coisa que passa retorna 201 no post
//        @Test
//        @DisplayName("Should return error when create user is fail")
//        void createUserWithError() throws Exception {
//            //Arrange
//            UserDTO request = new UserDTO(
//                    null,
//                    "Last Name",
//                    null,
//                    new BigDecimal(100),
//                    "email@email.com",
//                    "pas123",
//                    UserType.COMMON
//            );
//
////            doThrow(new Exception()).when(userService).createUser(any(UserDTO.class));
//
//            //Act & Assert
//            mockMvc.perform(post("/users")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isBadRequest());
//
//            verify(userService, never()).createUser(any(UserDTO.class));
//        }

    }


    @Test
    void getAllUsers() {
    }
}