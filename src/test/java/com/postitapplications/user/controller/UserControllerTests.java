package com.postitapplications.user.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void saveUserShouldReturnExpectedErrorMessageWhenUserPasswordIsEmpty() throws Exception {
        User userToSave = new User(null, "johnSmith123", "");

        mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userToSave))
                                     .accept(MediaType.APPLICATION_JSON)).andDo(print())
               .andExpect(status().isBadRequest()).andExpect(
            content().string(containsString("User's password cannot be null or empty")));
    }

    @Test
    public void getPersonByIdShouldReturnExpectedErrorMessageWhenPersonIsNotFound()
        throws Exception {
        UUID nonExistingUserId = UUID.randomUUID();

        when(userRepository.findById(nonExistingUserId)).thenReturn(null);

        mockMvc.perform(get("/user/" + nonExistingUserId).contentType(MediaType.APPLICATION_JSON)
                                                         .accept(MediaType.APPLICATION_JSON))
               .andDo(print()).andExpect(status().isNotFound()).andExpect(content()
            .string(containsString("User with id: " + nonExistingUserId + " was not found")));
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
}
