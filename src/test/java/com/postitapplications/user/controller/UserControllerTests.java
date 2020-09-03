package com.postitapplications.user.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.postitapplications.user.document.User;
import com.postitapplications.user.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void saveUserShouldReturnExpectedErrorMessageWhenUserUsernameIsNull() throws Exception {
        User userToSave = new User(null, null, "password");

        mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userToSave))
                                     .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's username cannot be null or empty")));
    }

    @Test
    public void saveUserShouldReturnExpectedErrorMessageWhenUserUsernameIsEmpty() throws Exception {
        User userToSave = new User(null, "", "password");

        mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userToSave))
                                     .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's username cannot be null or empty")));
    }

    @Test
    public void saveUserShouldReturnExpectedErrorMessageWhenUserPasswordIsNull() throws Exception {
        User userToSave = new User(null, "johnSmith123", null);

        mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userToSave))
                                     .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's password cannot be null or empty")));
    }

    @Test
    public void saveUserShouldReturnExpectedErrorMessageWhenUserUsernameAlreadyExists()
        throws Exception {
        User userToSave = new User(null, "johnSmith123", "password");
        User savedUser = new User(UUID.randomUUID(), "johnSmith123", "password");

        when(userRepository.findByUsername("johnSmith123")).thenReturn(savedUser);

        mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userToSave))
                                     .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isConflict()).andExpect(
            content().string(containsString("Cannot save user as johnSmith123 is already taken")));
    }

    @Test
    public void saveUserShouldReturnExpectedErrorMessageWhenUserPasswordIsEmpty() throws Exception {
        User userToSave = new User(null, "johnSmith123", "");

        mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userToSave))
                                     .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's password cannot be null or empty")));
    }

    @Test
    public void getUserByIdShouldReturnExpectedErrorMessageWhenUserIsNotFound() throws Exception {
        UUID nonExistingUserId = UUID.randomUUID();

        when(userRepository.findById(nonExistingUserId)).thenReturn(null);

        mockMvc.perform(get("/user/" + nonExistingUserId).contentType(MediaType.APPLICATION_JSON)
                                                         .accept(MediaType.APPLICATION_JSON))
               .andDo(print()).andExpect(status().isNotFound()).andExpect(content()
            .string(containsString("User with id: " + nonExistingUserId + " was not found")));
    }

    @Test
    public void getUserByUsernameShouldReturnExpectedErrorMessageWhenUserIsNotFound()
        throws Exception {
        String nonExistingUserUsername = "johnSmith123";

        when(userRepository.findByUsername(nonExistingUserUsername)).thenReturn(null);

        mockMvc.perform(
            get("/user/username/" + nonExistingUserUsername).contentType(MediaType.APPLICATION_JSON)
                                                            .accept(MediaType.APPLICATION_JSON))
               .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
            containsString("User with username: " + nonExistingUserUsername + " was not found")));
    }

    @Test
    public void updateUserShouldReturnExpectedErrorMessageWhenUserIsNotFound() throws Exception {
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        User userToUpdate = new User(UUID.randomUUID(), "johnSmith123", "password");

        when(updateResult.getMatchedCount()).thenReturn((long) 0);
        when(userRepository.update(Mockito.any())).thenReturn(updateResult);

        mockMvc.perform(put("/user").contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(userToUpdate))
                                    .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isNotFound()).andExpect(content()
            .string(containsString("User with id: " + userToUpdate.getId() + " was not found")));
    }

    @Test
    public void updateUserShouldReturnExpectedErrorMessageWhenUserUsernameIsNull()
        throws Exception {
        User userToUpdate = new User(UUID.randomUUID(), null, "password");

        mockMvc.perform(put("/user").contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(userToUpdate))
                                    .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's username cannot be null or empty")));
    }

    @Test
    public void updateUserShouldReturnExpectedErrorMessageWhenUserUsernameIsEmpty()
        throws Exception {
        User userToUpdate = new User(UUID.randomUUID(), "", "password");

        mockMvc.perform(put("/user").contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(userToUpdate))
                                    .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's username cannot be null or empty")));
    }

    @Test
    public void updateUserShouldReturnExpectedErrorMessageWhenUserPasswordIsNull()
        throws Exception {
        User userToUpdate = new User(UUID.randomUUID(), "johnSmith123", null);

        mockMvc.perform(put("/user").contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(userToUpdate))
                                    .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's password cannot be null or empty")));
    }

    @Test
    public void updateUserShouldReturnExpectedErrorMessageWhenUserPasswordIsEmpty()
        throws Exception {
        User userToUpdate = new User(UUID.randomUUID(), "johnSmith123", "");

        mockMvc.perform(put("/user").contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(userToUpdate))
                                    .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's password cannot be null or empty")));
    }

    @Test
    public void updateUserShouldReturnExpectedErrorMessageWhenUserIdIsNull() throws Exception {
        User userToUpdate = new User(null, "johnSmith123", "password");

        mockMvc.perform(put("/user").contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(userToUpdate))
                                    .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().string(containsString("Id cannot be null")));
    }

    @Test
    public void deleteUserByIdShouldReturnExpectedErrorMessageWhenUserIsNotFound()
        throws Exception {
        DeleteResult deleteResult = Mockito.mock(DeleteResult.class);
        UUID nonExistingUserId = UUID.randomUUID();

        when(deleteResult.getDeletedCount()).thenReturn((long) 0);
        when(userRepository.removeById(nonExistingUserId)).thenReturn(deleteResult);

        mockMvc.perform(delete("/user/" + nonExistingUserId).contentType(MediaType.APPLICATION_JSON)
                                                            .accept(MediaType.APPLICATION_JSON))
               .andDo(print()).andExpect(status().isNotFound()).andExpect(content()
            .string(containsString("User with id: " + nonExistingUserId + " was not found")));
    }
}
