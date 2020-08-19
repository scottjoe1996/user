package com.postitapplications.user.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.postitapplications.user.document.User;
import com.postitapplications.user.repository.UserRepo;
import com.postitapplications.user.utility.DocumentValidator;
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
        DocumentValidator.validateUser(user);
        return userRepo.save(user);
    }

    public User getUserById(UUID id) {
        DocumentValidator.validateUserId(id);
        return userRepo.findById(id);
    }

    public UpdateResult updateUser(User user) {
        DocumentValidator.validateUser(user);
        DocumentValidator.validateUserId(user.getId());
        return userRepo.update(user);
    }

    public DeleteResult deleteUserById(UUID id) {
        DocumentValidator.validateUserId(id);
        return userRepo.removeById(id);
    }
}
