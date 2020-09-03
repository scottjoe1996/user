package com.postitapplications.user.integrationTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.postitapplications.user.document.User;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
public class UserIT {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    public void tearDown() {
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    public void saveUserShouldReturnUserSavedOnSuccessfulSave() {
        User userToSave = new User(null, "johnSmith123", "password");

        ResponseEntity<User> responseEntity = restTemplate
            .postForEntity("/user", userToSave, User.class);
        User userSaved = responseEntity.getBody();

        assertThat(userSaved.getUsername()).isEqualTo(userToSave.getUsername());
    }

    @Test
    public void saveUserShouldAddUserToUserDatabase() {
        User userToSave = new User(null, "johnSmith123", "password");

        restTemplate.postForEntity("/user", userToSave, User.class);
        User userSaved = mongoTemplate.findAll(User.class).get(0);

        assertThat(userSaved.getUsername()).isEqualTo(userToSave.getUsername());
    }

    @Test
    public void saveUserShouldReturnCreatedStatusCodeOnSuccessfulSave() {
        User userToSave = new User(null, "johnSmith123", "password");

        ResponseEntity<User> responseEntity = restTemplate
            .postForEntity("/user", userToSave, User.class);
        HttpStatus responseStatusCode = responseEntity.getStatusCode();

        assertThat(responseStatusCode).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void saveUserShouldReturnBadRequestStatusCodeWhenUserIsNull() {
        ResponseEntity<User> responseEntity = restTemplate.postForEntity("/user", null, User.class);
        HttpStatus responseStatusCode = responseEntity.getStatusCode();

        assertThat(responseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveUserShouldReturnBadRequestStatusCodeWhenUserHasInvalidFields() {
        User userToSave = new User(null, "", "password");

        ResponseEntity<User> responseEntity = restTemplate
            .postForEntity("/user", userToSave, User.class);
        HttpStatus responseStatusCode = responseEntity.getStatusCode();

        assertThat(responseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveUserShouldReturnConflictStatusCodeWhenUserUsernameAlreadyExists() {
        User userToSave = new User(UUID.randomUUID(), "johnSmith123", "password");
        mongoTemplate.save(userToSave);

        ResponseEntity<User> responseEntity = restTemplate
            .postForEntity("/user", userToSave, User.class);
        HttpStatus responseStatusCode = responseEntity.getStatusCode();

        assertThat(responseStatusCode).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void getUserByIdShouldReturnFoundUser() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "John Smith", "password");
        mongoTemplate.save(savedUser);

        ResponseEntity<User> responseEntity = restTemplate
            .getForEntity("/user/" + savedUserId.toString(), User.class);
        User responseEntityBody = responseEntity.getBody();

        assertThat(responseEntityBody.getId()).isEqualTo(savedUser.getId());
        assertThat(responseEntityBody.getUsername()).isEqualTo(savedUser.getUsername());
    }

    @Test
    public void getUserByIdShouldReturnOkStatusCodeOnSuccessfulResponse() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "John Smith", "password");
        mongoTemplate.save(savedUser);

        ResponseEntity<User> responseEntity = restTemplate
            .getForEntity("/user/" + savedUserId.toString(), User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUserByIdShouldReturnNotFoundStatusCodeOnNonExistingUserId() {
        ResponseEntity<User> responseEntity = restTemplate
            .getForEntity("/user/" + UUID.randomUUID().toString(), User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserByIdShouldReturnBadRequestStatusCodeOnInvalidUserId() {
        ResponseEntity<User> responseEntity = restTemplate.getForEntity("/user/123456", User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getUserByUsernameShouldReturnFoundUser() {
        User savedUser = new User(UUID.randomUUID(), "johnSmith123", "password");
        mongoTemplate.save(savedUser);

        ResponseEntity<User> responseEntity = restTemplate
            .getForEntity("/user/username/johnSmith123", User.class);
        User responseEntityBody = responseEntity.getBody();

        assertThat(responseEntityBody.getId()).isEqualTo(savedUser.getId());
        assertThat(responseEntityBody.getUsername()).isEqualTo(savedUser.getUsername());
    }

    @Test
    public void getUserByUsernameShouldReturnOkStatusCodeOnSuccessfulResponse() {
        User savedUser = new User(UUID.randomUUID(), "johnSmith123", "password");
        mongoTemplate.save(savedUser);

        ResponseEntity<User> responseEntity = restTemplate
            .getForEntity("/user/username/johnSmith123", User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUserByUsernameShouldReturnNotFoundStatusCodeOnNonExistingUserUsername() {
        ResponseEntity<User> responseEntity = restTemplate
            .getForEntity("/user/username/randomUsername", User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserByUsernameShouldReturnBadRequestStatusCodeOnInvalidUsername() {
        ResponseEntity<User> responseEntity = restTemplate
            .getForEntity("/user/username/", User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateUserShouldReturnOKStatusCodeOnSuccessfulUpdate() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "johnSmith123", "password");
        mongoTemplate.save(savedUser);
        User updatedUser = new User(savedUserId, "johnSmith456", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> httpEntity = new HttpEntity<>(updatedUser, headers);

        ResponseEntity<User> responseEntity = restTemplate
            .exchange("/user/", HttpMethod.PUT, httpEntity, User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateUserShouldReturnUpdatedUserOnSuccessfulUpdate() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "johnSmith123", "password");
        mongoTemplate.save(savedUser);
        User updatedUser = new User(savedUserId, "johnSmith456", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> httpEntity = new HttpEntity<>(updatedUser, headers);

        ResponseEntity<User> responseEntity = restTemplate
            .exchange("/user/", HttpMethod.PUT, httpEntity, User.class);
        User responseEntityBody = responseEntity.getBody();

        assertThat(responseEntityBody.getUsername()).isEqualTo("johnSmith456");
        assertThat(responseEntityBody.getPassword()).isEqualTo("password");
    }

    @Test
    public void updateUserShouldUpdateUserOnSuccessfulUpdate() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "johnSmith123", "password");
        mongoTemplate.save(savedUser);
        User updatedUser = new User(savedUserId, "johnSmith456", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> httpEntity = new HttpEntity<>(updatedUser, headers);

        restTemplate.exchange("/user/", HttpMethod.PUT, httpEntity, User.class);
        User userToTest = mongoTemplate.findAll(User.class).get(0);

        assertThat(userToTest.getUsername()).isEqualTo("johnSmith456");
    }

    @Test
    public void updateUserShouldReturnNotFoundStatusCodeOnUserWithNonExistingUserId() {
        User savedUser = new User(UUID.randomUUID(), "johnSmith123", "password");
        mongoTemplate.save(savedUser);
        User updatedUser = new User(UUID.randomUUID(), "johnSmith456", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> httpEntity = new HttpEntity<>(updatedUser, headers);

        ResponseEntity<User> responseEntity = restTemplate
            .exchange("/user/", HttpMethod.PUT, httpEntity, User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateUserShouldReturnBadRequestStatusCodeOnUserWithInvalidFields() {
        User updatedUser = new User(UUID.randomUUID(), null, "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> httpEntity = new HttpEntity<>(updatedUser, headers);

        ResponseEntity<User> responseEntity = restTemplate
            .exchange("/user/", HttpMethod.PUT, httpEntity, User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteUserByIdShouldReturnOkStatusCodeOnSuccessfulDelete() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "johnSmith123", "password");
        mongoTemplate.save(savedUser);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<UUID> responseEntity = restTemplate
            .exchange("/user/" + savedUserId.toString(), HttpMethod.DELETE, httpEntity, UUID.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteUserByIdShouldReturnDeletedUsersIdOnSuccessfulDelete() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "johnSmith123", "password");
        mongoTemplate.save(savedUser);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<UUID> responseEntity = restTemplate
            .exchange("/user/" + savedUserId.toString(), HttpMethod.DELETE, httpEntity, UUID.class);
        UUID responseEntityBody = responseEntity.getBody();

        assertThat(responseEntityBody).isEqualTo(savedUserId);
    }

    @Test
    public void deleteUserByIdShouldDeleteUserOnSuccessfulDelete() {
        UUID savedUserId = UUID.randomUUID();
        User savedUser = new User(savedUserId, "johnSmith123", "password");
        mongoTemplate.save(savedUser);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        restTemplate
            .exchange("/user/" + savedUserId.toString(), HttpMethod.DELETE, httpEntity, UUID.class);

        assertThat(mongoTemplate.findAll(User.class).size()).isEqualTo(0);
    }

    @Test
    public void deleteUserByIdShouldReturnNotFoundStatusCodeWithNonExistingUserId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate
            .exchange("/user/" + UUID.randomUUID().toString(), HttpMethod.DELETE, httpEntity,
                String.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteUserByIdShouldReturnBadRequestStatusCodeWithInvalidUserId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate
            .exchange("/user/123456", HttpMethod.DELETE, httpEntity, String.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
