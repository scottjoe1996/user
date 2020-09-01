package com.postitapplications.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.postitapplications.exception.exceptions.NullOrEmptyException;
import com.postitapplications.user.document.User;
import com.postitapplications.user.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class UserServiceTests {

    private UserService userService;
    @MockBean
    private UserRepository mockUserRepository;

    @Test
    public void saveUserShouldReturnSavedUserOnSuccessfulSave() {
        User expectedUser = new User(null, "johnSmith123", "password");

        when(mockUserRepository.save(expectedUser)).thenReturn(expectedUser);
        userService = new UserService(mockUserRepository);

        assertThat(userService.saveUser(expectedUser)).isEqualTo(expectedUser);
    }

    @Test
    public void saveUserShouldThrowNullPointerExceptionWhenUserIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            userService.saveUser(null);
        });

        assertThat(exception.getMessage()).isEqualTo("User cannot be null");
    }

    @Test
    public void saveUserShouldThrowNullOrEmptyExceptionWhenUserUsernameIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.saveUser(new User(null, null, "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("User's username cannot be null or empty");
    }

    @Test
    public void saveUserShouldThrowNullOrEmptyExceptionWhenUserUsernameIsEmpty() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.saveUser(new User(null, "", "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("User's username cannot be null or empty");
    }

    @Test
    public void saveUserShouldThrowNullOrEmptyExceptionWhenUserPasswordIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.saveUser(new User(null, "johnSmith123", null));
        });

        assertThat(exception.getMessage()).isEqualTo("User's password cannot be null or empty");
    }

    @Test
    public void saveUserShouldThrowNullOrEmptyExceptionWhenUserPasswordIsEmpty() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.saveUser(new User(null, "johnSmith123", ""));
        });

        assertThat(exception.getMessage()).isEqualTo("User's password cannot be null or empty");
    }

    @Test
    public void getUserByIdShouldReturnAUserWhenUserExists() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "johnSmith123", "password");

        when(mockUserRepository.findById(savedUserId)).thenReturn(savedUser);
        userService = new UserService(mockUserRepository);

        assertThat(userService.getUserById(savedUserId)).isEqualTo(savedUser);
    }

    @Test
    public void getUserByIdShouldReturnNullWhenUserDoesNotExist() {
        UUID nonExistingUserId = UUID.randomUUID();

        when(mockUserRepository.findById(nonExistingUserId)).thenReturn(null);
        userService = new UserService(mockUserRepository);

        assertThat(userService.getUserById(nonExistingUserId)).isEqualTo(null);
    }

    @Test
    public void getUserByIdShouldThrowNullPointerExceptionWhenUsingNullId() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            userService.getUserById(null);
        });

        assertThat(exception.getMessage()).isEqualTo("Id cannot be null");
    }

    @Test
    public void getUserByUsernameShouldReturnAUserWhenUserExists() {
        User savedUser = new User(UUID.randomUUID(), "johnSmith123", "password");

        when(mockUserRepository.findByUsername("johnSmith123")).thenReturn(savedUser);
        userService = new UserService(mockUserRepository);

        assertThat(userService.getUserByUsername("johnSmith123")).isEqualTo(savedUser);
    }

    @Test
    public void getUserByUsernameShouldReturnNullWhenUserDoesNotExist() {
        String invalidUsername = "nonExistingUsername";
        when(mockUserRepository.findByUsername(invalidUsername)).thenReturn(null);
        userService = new UserService(mockUserRepository);

        assertThat(userService.getUserByUsername(invalidUsername)).isEqualTo(null);
    }

    @Test
    public void getUserByUsernameShouldThrowIllegalArgumentExceptionWhenUsernameIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.getUserByUsername(null);
        });

        assertThat(exception.getMessage()).isEqualTo("User's username cannot be null or empty");
    }

    @Test
    public void getUserByUsernameShouldThrowIllegalArgumentExceptionWhenUsernameIsEmpty() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.getUserByUsername("");
        });

        assertThat(exception.getMessage()).isEqualTo("User's username cannot be null or empty");
    }

    @Test
    public void updateUserShouldReturnUpdateResultWhenUsingAValidPerson() {
        User updatedUser = new User(UUID.randomUUID(), "jeffSmith123", "password");
        UpdateResult mockUpdateResult = Mockito.mock(UpdateResult.class);

        when(mockUserRepository.update(updatedUser)).thenReturn(mockUpdateResult);
        userService = new UserService(mockUserRepository);

        assertThat(userService.updateUser(updatedUser)).isEqualTo(mockUpdateResult);
    }

    @Test
    public void updateUserShouldThrowNullPointerExceptionWhenUserIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            userService.updateUser(null);
        });

        assertThat(exception.getMessage()).isEqualTo("User cannot be null");
    }

    @Test
    public void updateUserShouldThrowNullOrEmptyExceptionWhenUserUsernameIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.updateUser(new User(UUID.randomUUID(), null, "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("User's username cannot be null or empty");
    }

    @Test
    public void updateUserShouldThrowNullOrEmptyExceptionWhenUserUsernameIsEmpty() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.updateUser(new User(UUID.randomUUID(), "", "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("User's username cannot be null or empty");
    }

    @Test
    public void updateUserShouldThrowNullOrEmptyExceptionWhenUserPasswordIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.updateUser(new User(UUID.randomUUID(), "johnSmith123", null));
        });

        assertThat(exception.getMessage()).isEqualTo("User's password cannot be null or empty");
    }

    @Test
    public void updateUserShouldThrowNullOrEmptyExceptionWhenUserPasswordIsEmpty() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullOrEmptyException.class, () -> {
            userService.updateUser(new User(UUID.randomUUID(), "johnSmith123", ""));
        });

        assertThat(exception.getMessage()).isEqualTo("User's password cannot be null or empty");
    }

    @Test
    public void updateUserShouldThrowNullPointerExceptionWhenUserIdIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            userService.updateUser(new User(null, "johnSmith123", "password"));
        });

        assertThat(exception.getMessage()).isEqualTo("Id cannot be null");
    }

    @Test
    public void deleteUserByIdShouldReturnDeleteResultWhenUsingAValidId() {
        UUID deletedUserId = UUID.randomUUID();
        DeleteResult mockDeleteResult = Mockito.mock(DeleteResult.class);

        when(mockUserRepository.removeById(deletedUserId)).thenReturn(mockDeleteResult);
        userService = new UserService(mockUserRepository);

        assertThat(userService.deleteUserById(deletedUserId)).isEqualTo(mockDeleteResult);
    }

    @Test
    public void deleteUserByIdShouldThrowNullPointerExceptionWhenIdIsNull() {
        userService = new UserService(mockUserRepository);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            userService.deleteUserById(null);
        });

        assertThat(exception.getMessage()).isEqualTo("Id cannot be null");
    }
}
