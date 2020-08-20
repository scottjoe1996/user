package com.postitapplications.user.utility;

import com.postitapplications.exception.exceptions.NullOrEmptyException;
import com.postitapplications.user.document.User;
import java.util.UUID;

public class DocumentValidator {

    public static void validateUser(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }

        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
    }

    private static void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NullOrEmptyException("Username cannot be null or empty");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new NullOrEmptyException("User's password cannot be null or empty");
        }
    }

    public static void validateUserId(UUID id) {
        if (id == null) {
            throw new NullPointerException("Id cannot be null");
        }
    }

}