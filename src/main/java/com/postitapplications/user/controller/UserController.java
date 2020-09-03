package com.postitapplications.user.controller;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.postitapplications.exception.exceptions.UserNotFoundException;
import com.postitapplications.user.document.User;
import com.postitapplications.user.service.UserService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") UUID id) {
        User foundUser = userService.getUserById(id);

        if (foundUser == null) {
            throw new UserNotFoundException(
                String.format("User with id: %s was not found", id));
        }

        return foundUser;
    }

    @GetMapping("username/{username}")
    public User getUserByUsername(@PathVariable("username") String username) {
        User foundUser = userService.getUserByUsername(username);

        if (foundUser == null) {
            throw new UserNotFoundException(
                String.format("User with username: %s was not found", username));
        }

        return foundUser;
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        UpdateResult updateResult = userService.updateUser(user);

        if (updateResult.getMatchedCount() == 0) {
            throw new UserNotFoundException(
                String.format("User with id: %s was not found", user.getId()));
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<UUID> deleteUserById(@PathVariable("id") UUID id) {
        DeleteResult deleteResult = userService.deleteUserById(id);

        if (deleteResult.getDeletedCount() == 0) {
            throw new UserNotFoundException(
                String.format("User with id: %s was not found", id));
        }

        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
