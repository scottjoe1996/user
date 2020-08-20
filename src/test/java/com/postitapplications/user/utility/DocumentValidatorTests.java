package com.postitapplications.user.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.postitapplications.exception.exceptions.NullOrEmptyException;
import com.postitapplications.user.document.User;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentValidatorTests {

    @Test
    public void validateUserShouldThrowNullPointerExceptionWhenUserIsNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            DocumentValidator.validateUser(null);
        });

        assertThat(exception.getMessage()).isEqualTo("User cannot be null");
    }

    @Test
    public void validateUserShouldThrowNullOrEmptyExceptionWhenUserUsernameIsNull() {
        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            DocumentValidator.validateUser(new User(null, null, "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("Username cannot be null or empty");
    }

    @Test
    public void validateUserShouldThrowNullOrEmptyExceptionWhenUserUsernameIsEmpty() {
        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            DocumentValidator.validateUser(new User(null, "", "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("Username cannot be null or empty");
    }

    @Test
    public void validateUserShouldThrowNullOrEmptyExceptionWhenUserPasswordIsNull() {
        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            DocumentValidator.validateUser(new User(null, "johnSmith123", null));
        });

        assertThat(exception.getMessage()).isEqualTo("User's password cannot be null or empty");
    }

    @Test
    public void validateUserShouldThrowNullOrEmptyExceptionWhenUserPasswordIsEmpty() {
        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            DocumentValidator.validateUser(new User(null, "johnSmith123", ""));
        });

        assertThat(exception.getMessage()).isEqualTo("User's password cannot be null or empty");
    }

    @Test
    public void validateUserShouldNotThrowAnExceptionWithValidUser() {
        assertDoesNotThrow(
            () -> DocumentValidator.validateUser(new User(null, "johnSmith123", "password")));
    }

    @Test
    public void validateUserIdShouldThrowNullPointerExceptionWhenIdIsNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            DocumentValidator.validateUserId(null);
        });

        assertThat(exception.getMessage()).isEqualTo("Id cannot be null");
    }

    @Test
    public void validateUserIdShouldNotThrowAnExceptionWithValidId() {
        assertDoesNotThrow(() -> DocumentValidator.validateUserId(UUID.randomUUID()));
    }
}
