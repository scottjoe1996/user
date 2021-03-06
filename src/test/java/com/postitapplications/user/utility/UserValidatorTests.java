package com.postitapplications.user.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.postitapplications.exception.exceptions.NullOrEmptyException;
import com.postitapplications.exception.exceptions.ValidationException;
import com.postitapplications.user.document.User;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserValidatorTests {

    @Test
    public void validateUserShouldThrowValidationExceptionWhenUserIsNull() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            UserValidator.validateUser(null);
        });

        assertThat(exception.getMessage()).isEqualTo("User cannot be null");
    }

    @Test
    public void validateUserShouldThrowNullOrEmptyExceptionWhenUserUsernameIsNull() {
        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            UserValidator.validateUser(new User(null, null, "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("User's username cannot be null or empty");
    }

    @Test
    public void validateUserShouldThrowNullOrEmptyExceptionWhenUserUsernameIsEmpty() {
        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            UserValidator.validateUser(new User(null, "", "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("User's username cannot be null or empty");
    }

    @Test
    public void validateUserShouldThrowNullOrEmptyExceptionWhenUserPasswordIsNull() {
        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            UserValidator.validateUser(new User(null, "johnSmith123", null));
        });

        assertThat(exception.getMessage()).isEqualTo("User's password cannot be null or empty");
    }

    @Test
    public void validateUserShouldThrowNullOrEmptyExceptionWhenUserPasswordIsEmpty() {
        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            UserValidator.validateUser(new User(null, "johnSmith123", ""));
        });

        assertThat(exception.getMessage()).isEqualTo("User's password cannot be null or empty");
    }

    @Test
    public void validateUserShouldNotThrowAnExceptionWithValidUser() {
        assertDoesNotThrow(
            () -> UserValidator.validateUser(new User(null, "johnSmith123", "password")));
    }

    @Test
    public void validateUserIdShouldThrowValidationExceptionWhenIdIsNull() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            UserValidator.validateUserId(null);
        });

        assertThat(exception.getMessage()).isEqualTo("Id cannot be null");
    }

    @Test
    public void validateUserIdShouldNotThrowAnExceptionWithValidId() {
        assertDoesNotThrow(() -> UserValidator.validateUserId(UUID.randomUUID()));
    }
}
