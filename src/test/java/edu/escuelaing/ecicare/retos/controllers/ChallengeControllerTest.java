package edu.escuelaing.ecicare.retos.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.ecicare.retos.models.Challenge;
import edu.escuelaing.ecicare.retos.services.ChallengeService;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChallengeController.class)
public class ChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChallengeService challengeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create a challenge successfully")
    void shouldCreateChallenge() throws Exception {
        Challenge challenge = Challenge.builder()
                .name("Challenge1")
                .description("Description1")
                .duration(LocalDateTime.now().plusDays(5))
                .reward("Medal")
                .build();

        doNothing().when(challengeService).createChallenge(any(Challenge.class));

        mockMvc.perform(post("/challenges/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(challenge)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Challenge response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), Challenge.class);
                    assert response.getName().equals(challenge.getName());
                    assert response.getDescription().equals(challenge.getDescription());
                });
    }

    @Test
    @DisplayName("Should get all challenges")
    void shouldGetAllChallenges() throws Exception {
        List<Challenge> challenges = Arrays.asList(
                Challenge.builder().name("Challenge1").description("Desc1").build(),
                Challenge.builder().name("Challenge2").description("Desc2").build()
        );

        when(challengeService.getAllChallenges()).thenReturn(challenges);

        mockMvc.perform(get("/challenges/all"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Challenge[] response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), Challenge[].class);
                    assert response.length == challenges.size();
                    assert response[0].getName().equals(challenges.get(0).getName());
                    assert response[1].getName().equals(challenges.get(1).getName());
                });
    }

    @Test
    @DisplayName("Should get challenge by name")
    void shouldGetChallengeByName() throws Exception {
        Challenge challenge = Challenge.builder().name("Challenge1").description("Desc1").build();

        when(challengeService.getChallengeByName("Challenge1")).thenReturn(challenge);

        mockMvc.perform(get("/challenges/find/name/Challenge1"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Challenge response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), Challenge.class);
                    assert response.getName().equals("Challenge1");
                });
    }

    @Test
    @DisplayName("Should get challenges by duration")
    void shouldGetChallengesByDuration() throws Exception {
        LocalDateTime duration = LocalDateTime.now().plusDays(7);
        List<Challenge> challenges = List.of(
                Challenge.builder().name("Challenge1").duration(duration).build()
        );

        when(challengeService.getChallengeByDuration(duration)).thenReturn(challenges);

        mockMvc.perform(get("/challenges/find/duration/" + duration))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Challenge[] response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), Challenge[].class);
                    assert response.length == 1;
                    assert response[0].getName().equals("Challenge1");
                });
    }

    @Test
    @DisplayName("Should get challenges by user")
    void shouldGetChallengesByUser() throws Exception {
        UserEcicare user = new UserEcicare();
        user.setEmail("test@eci.edu.co");

        List<Challenge> challenges = List.of(
                Challenge.builder().name("Challenge1").build()
        );

        when(challengeService.getChallengesByUserEmail(user.getEmail())).thenReturn(challenges);

        mockMvc.perform(get("/challenges/find/user/challenge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Challenge[] response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), Challenge[].class);
                    assert response.length == 1;
                    assert response[0].getName().equals("Challenge1");
                });
    }

    @Test
    @DisplayName("Should update challenge successfully")
    void shouldUpdateChallenge() throws Exception {
        Challenge updated = Challenge.builder()
                .name("Challenge1")
                .description("Updated Desc")
                .build();

        when(challengeService.updateChallenge("Challenge1", updated)).thenReturn(updated);

        mockMvc.perform(put("/challenges/update/Challenge1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Challenge response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), Challenge.class);
                    assert response.getDescription().equals("Updated Desc");
                });
    }

    @Test
    @DisplayName("Should delete challenge successfully")
    void shouldDeleteChallenge() throws Exception {
        doNothing().when(challengeService).deleteChallenge("Challenge1");

        mockMvc.perform(delete("/challenges/delete/Challenge1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should add user to challenge")
    void shouldAddUserToChallenge() throws Exception {
        String userEmail = "user@test.com";
        String challengeName = "Challenge1";

        // Mock service
        Challenge mockChallenge = new Challenge();
        mockChallenge.setName(challengeName);
        mockChallenge.setRegistered(new ArrayList<>());

        when(challengeService.addUserByEmail(userEmail, challengeName)).thenReturn(mockChallenge);

        // Perform request
        mockMvc.perform(put("/challenges/update/user/{userEmail}/challenge/{name}", userEmail, challengeName))
                .andExpect(status().isOk());
    }
}
