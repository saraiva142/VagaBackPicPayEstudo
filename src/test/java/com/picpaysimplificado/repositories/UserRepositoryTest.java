package com.picpaysimplificado.repositories;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.annotation.ApplicationScope;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Should get user successfully from DB")
    void findUserByDocumentSuccess() {

        //Arrange
        String document = "99999999901";
        UserDTO data = new UserDTO("Fernanda", "Teste", document, new BigDecimal(10), "test@email.com", "44444", UserType.COMMON);
        this.createUser(data);

        //Act
        Optional<User> result = this.userRepository.findUserByDocument(document);

        //Assert
        assertThat(result.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should NOT get user from DB when user not exists")
    void notFindUserByDocumentWhenUserNotExists() {

        //Arrange
        String document = "99999999901";
        //UserDTO data = new UserDTO("Fernanda", "Teste", document, new BigDecimal(10), "test@email.com", "44444", UserType.COMMON);
        //this.createUser(data);

        //Act
        Optional<User> result = this.userRepository.findUserByDocument(document);

        //Assert
        assertThat(result.isEmpty()).isTrue();

    }

    private User createUser(UserDTO data) {
        User newUser = new User(data);
        this.entityManager.persist(newUser);
        return newUser;
    }
}