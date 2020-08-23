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
import org.springframework.http.HttpStatus;
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
    public void saveUserShouldReturnUnsupportedMediaTypeStatusCodeWhenUserIsNull() {
        ResponseEntity<User> responseEntity = restTemplate
            .postForEntity("/user", null, User.class);
        HttpStatus responseStatusCode = responseEntity.getStatusCode();

        assertThat(responseStatusCode).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
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
        ResponseEntity<User> responseEntity = restTemplate
            .getForEntity("/user/123456", User.class);
        HttpStatus responseEntityStatusCode = responseEntity.getStatusCode();

        assertThat(responseEntityStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
