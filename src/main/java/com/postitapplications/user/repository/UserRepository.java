package com.postitapplications.user.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.postitapplications.user.document.User;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("MongoDBRepo")
public class UserRepository implements UserRepo {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User save(UUID id, User userToSave) {
        User user = new User(id, userToSave.getUsername(), userToSave.getPassword());
        return mongoTemplate.save(user);
    }

    @Override
    public User findById(UUID id) {
        return mongoTemplate.findById(id, User.class);
    }

    @Override
    public UpdateResult update(User user) {
        Update update = new Update();
        update.set("username", user.getUsername());
        update.set("password", user.getPassword());

        return mongoTemplate
            .updateFirst(new Query(Criteria.where("id").is(user.getId())), update, User.class);
    }

    @Override
    public DeleteResult removeById(UUID id) {
        return mongoTemplate.remove(new Query(Criteria.where("id").is(id)), User.class);
    }
}
