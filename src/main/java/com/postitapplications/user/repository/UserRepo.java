package com.postitapplications.user.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.postitapplications.user.document.User;
import java.util.UUID;

public interface UserRepo {

    User save(UUID id, User user);

    default User save(User user) {
        UUID id = UUID.randomUUID();
        return save(id, user);
    }

    User findById(UUID id);

    User findByUsername(String username);

    UpdateResult update(User user);

    DeleteResult removeById(UUID id);
}
