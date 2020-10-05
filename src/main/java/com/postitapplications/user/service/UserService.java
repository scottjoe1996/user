package com.postitapplications.user.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.postitapplications.exception.exceptions.UsernameTakenException;
import com.postitapplications.user.document.User;
import com.postitapplications.user.repository.UserRepo;
import com.postitapplications.user.utility.UserValidator;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;

    @Autowired
    public UserService(@Qualifier("MongoDBRepo") UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User saveUser(User user) {
        UserValidator.validateUser(user);
        ensureUsernameNotTaken(user.getUsername());

        return userRepo.save(user);
    }

    private void ensureUsernameNotTaken(String username) {
        if (isUsernameTaken(username)) {
            throw new UsernameTakenException(
                String.format("Cannot save user as %s is already taken", username));
        }
    }

    private boolean isUsernameTaken(String username) {
        return userRepo.findByUsername(username) != null;
    }

    public User getUserById(UUID id) {
        UserValidator.validateUserId(id);
        return userRepo.findById(id);
    }

    public User getUserByUsername(String username) {
        UserValidator.validateUsername(username);
        return userRepo.findByUsername(username);
    }

    public UpdateResult updateUser(User user) {
        UserValidator.validateUser(user);
        UserValidator.validateUserId(user.getId());
        return userRepo.update(user);
    }

    public DeleteResult deleteUserById(UUID id) {
        UserValidator.validateUserId(id);
        return userRepo.removeById(id);
    }
}
