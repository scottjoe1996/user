package com.postitapplications.user.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private final UUID id;
    @NotBlank
    private final String username;
    @NotBlank
    private final String password;

    public User(@JsonProperty("id") UUID id, @JsonProperty("username") String username,
        @JsonProperty("password") String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
