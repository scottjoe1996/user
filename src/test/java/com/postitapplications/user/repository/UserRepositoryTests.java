package com.postitapplications.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mongodb.client.ListIndexesIterable;
import com.postitapplications.user.document.User;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        mongoTemplate.save(new User(UUID.randomUUID(), "johnSmith123", "password"));
        userRepository = new UserRepository(mongoTemplate);
    }

    @AfterEach
    public void tearDown() {
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    public void findByIdShouldReturnExpectedUserWithCorrectId() {
        UUID savedUserId = mongoTemplate.findAll(User.class).get(0).getId();
        User userFound = userRepository.findById(savedUserId);

        assertThat(userFound.getUsername()).isEqualTo("johnSmith123");
        assertThat(userFound.getPassword()).isEqualTo("password");
    }

    @Test
    public void findByIdShouldReturnNullWithInvalidId() {
        assertThat(userRepository.findById(UUID.randomUUID())).isEqualTo(null);
    }

    @Test
    public void findByIdShouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userRepository.findById(null);
        });

        assertThat(exception.getMessage()).contains("Id must not be null!");
    }

    @Test
    public void findByUsernameShouldReturnExpectedUserWithCorrectUsername() {
        String savedUserUsername = mongoTemplate.findAll(User.class).get(0).getUsername();
        User userFound = userRepository.findByUsername(savedUserUsername);

        assertThat(userFound.getUsername()).isEqualTo("johnSmith123");
        assertThat(userFound.getPassword()).isEqualTo("password");
    }

    @Test
    public void findByUsernameShouldReturnNullWithInvalidUsername() {
        assertThat(userRepository.findByUsername("notSavedUsername")).isEqualTo(null);
    }

    @Test
    public void removeByIdShouldRemoveSavedUser() {
        UUID savedUserId = mongoTemplate.findAll(User.class).get(0).getId();

        assertThat(mongoTemplate.findAll(User.class).size()).isEqualTo(1);
        userRepository.removeById(savedUserId);

        assertThat(mongoTemplate.findAll(User.class).size()).isEqualTo(0);
    }

    @Test
    public void removeByIdShouldReturnDeletedCount0WithInvalidId() {
        assertThat(userRepository.removeById(UUID.randomUUID()).getDeletedCount()).isEqualTo(0);
    }

    @Test
    public void updateShouldUpdateUserWithNewFields() {
        UUID savedUserId = mongoTemplate.findAll(User.class).get(0).getId();
        User updatedUser = new User(savedUserId, "joanneSmith123", "password");

        userRepository.update(updatedUser);
        User userFound = mongoTemplate.findById(savedUserId, User.class);

        assertThat(userFound.getId()).isEqualTo(savedUserId);
        assertThat(userFound.getUsername()).isEqualTo("joanneSmith123");
        assertThat(userFound.getPassword()).isEqualTo("password");
    }

    @Test
    public void updateShouldReturnMatchedCount0WithInvalidId() {
        User nonExistingUser = new User(UUID.randomUUID(), "JoanneSmith123", "password");

        assertThat(userRepository.update(nonExistingUser).getMatchedCount()).isEqualTo(0);
    }

    @Test
    public void saveShouldAddAUserToTheUserDatabase() {
        userRepository.save(new User(null, "johnSmith123", "password"));

        assertThat(mongoTemplate.findAll(User.class).size()).isEqualTo(2);
    }

    @Test
    public void saveShouldAddAUserToTheUserDatabaseWithAGeneratedUUID() {
        mongoTemplate.dropCollection(User.class);

        userRepository.save(new User(null, "johnSmith123", "password"));

        assertThat(mongoTemplate.findAll(User.class).get(0).getId()).isNotNull();
    }

    @Test
    public void saveShouldAddAUserToTheUserDatabaseWithTheExpectedFields() {
        mongoTemplate.dropCollection(User.class);

        userRepository.save(new User(null, "johnSmith123", "password"));
        User savedUser = mongoTemplate.findAll(User.class).get(0);

        assertThat(savedUser.getUsername()).isEqualTo("johnSmith123");
        assertThat(savedUser.getPassword()).isEqualTo("password");
    }

    @Test
    public void saveShouldAddAUserToTheUserDatabaseWithTheExpectedUUIDWhenSpecified() {
        mongoTemplate.dropCollection(User.class);
        UUID specifiedUUID = UUID.randomUUID();

        userRepository.save(specifiedUUID, new User(null, "johnSmith123", "password"));
        User savedUser = mongoTemplate.findAll(User.class).get(0);

        assertThat(savedUser.getId()).isEqualTo(specifiedUUID);
    }

    @Test
    public void saveShouldThrowExceptionWhenUsingNullId() {
        Exception exception = assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            userRepository.save(null, new User(null, "johnSmith123", "password"));
        });

        assertThat(exception.getMessage()).contains("Cannot autogenerate id");
    }

    @Test
    public void saveShouldThrowExceptionWhenSavingWithAPreExistingUsername() {
        assertThrows(DuplicateKeyException.class, () -> {
            userRepository.save(new User(null, "johnSmith123", "password"));
        });
    }
}
