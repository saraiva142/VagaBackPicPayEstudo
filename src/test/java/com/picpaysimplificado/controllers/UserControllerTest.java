package com.picpaysimplificado.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        @Test
        @DisplayName("Should return message error when user already exist")
        void createUserWhenUserAlreadyExist() throws Exception {
            //Arrange
            Long id = 1L;
            UserDTO requestDTO = new UserDTO(
                    "First Name",
                    "Last Name",
                    "123456789",
                    new BigDecimal(19),
                    "email@email.com",
                    "pass123",
                    UserType.COMMON
            );

            User sameUser = new User();
            sameUser.setId(id);
            sameUser.setFirstname(requestDTO.firstName());
            sameUser.setLastname(requestDTO.lastName());
            sameUser.setDocument(requestDTO.document());
            sameUser.setBalance(requestDTO.balance());
            sameUser.setEmail(requestDTO.email());
            sameUser.setPassword(requestDTO.password());
            sameUser.setUserType(requestDTO.userType());

            doThrow(new DataIntegrityViolationException("Usuário já cadastrado"))
                    .when(userService).createUser(any(UserDTO.class));

            //Act & Assert
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sameUser)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Usuário já cadastrado"))
                    .andExpect(jsonPath("$.statusCode").value("400"));

            verify(userService, times(1)).createUser(any(UserDTO.class));
        }

    }

    @Nested
    class getAllUsers {

        @Test
        @DisplayName("Should get all users with success and return 200")
        void getAllUsersWithSuccess() throws Exception {
            //Arrange
            Long id = 1L;
            User user1 = new User();
            user1.setId(id);
            user1.setFirstname("João Victor");
            user1.setLastname("Aquino Saraiva");
            user1.setEmail("joao@email.com");
            user1.setBalance(new BigDecimal(100));
            user1.setDocument("123456789");
            user1.setUserType(UserType.COMMON);

            User user2 = new User();
            user2.setFirstname("First Name");
            user2.setLastname("Last Name");
            user2.setEmail("first@email.com");
            user2.setBalance(new BigDecimal(190));
            user2.setDocument("123456786");
            user2.setUserType(UserType.COMMON);

            List<User> userList = Arrays.asList(user1, user2);

            when(userService.getAllUsers()).thenReturn(userList);

            //Act & Assert
            mockMvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userList)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(id));
            
            assertEquals(user1.getId(), userList.get(0).getId());
            assertEquals(user2.getId(), userList.get(1).getId());
            assertEquals(user1.getDocument(), userList.get(0).getDocument());
            assertEquals(user2.getDocument(), userList.get(1).getDocument());

            verify(userService, times(1)).getAllUsers();
        }

        @Test
        @DisplayName("Should return a empty list when are no have users")
        void getAllUserReturnEmptyList() throws Exception {
            //Arrange
            List<User> userList = Arrays.asList();

            when(userService.getAllUsers()).thenReturn(userList);

            //Act & Assert
            mockMvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userList)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
            verify(userService, times(1)).getAllUsers();
        }
    }

}