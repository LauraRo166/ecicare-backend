package edu.escuelaing.ecicare.challenges.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.models.entity.RedeemableId;
import edu.escuelaing.ecicare.challenges.models.dto.ChallengeDTO;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleWithChallengesDTO;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.models.entity.Module;
import edu.escuelaing.ecicare.challenges.services.ChallengeService;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

        private Set<Redeemable> createTestRedeemable(String name) {
                Award award = Award.builder()
                                .name("Gold Medal")
                                .description("Special award")
                                .build();

                // Crear un RedeemableId
                RedeemableId redeemableId = new RedeemableId(name, award.getAwardId());

                Redeemable redeemable = Redeemable.builder()
                                .id(redeemableId)
                                .award(award)
                                .limitDays(30)
                                .build();

                Set<Redeemable> redeemables = Set.of(redeemable);
                return redeemables;
        }

        @Test
        @DisplayName("Should create a challenge successfully")
        void shouldCreateChallenge() throws Exception {
                ChallengeDTO challengeDto = ChallengeDTO.builder()
                                .name("Challenge1")
                                .description("Description1")
                                .imageUrl("imageUrl1")
                                .duration(LocalDateTime.now().plusDays(5))
                                .build();

                when(challengeService.createChallenge(any(ChallengeDTO.class)))
                                .thenReturn(Challenge.builder()
                                                .name("Challenge1")
                                                .description("Description1")
                                                .imageUrl("imageUrl1")
                                                .duration(LocalDateTime.now().plusDays(5))
                                                .build());

                mockMvc.perform(post("/challenges/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(challengeDto)))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        Challenge response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(), Challenge.class);
                                        assert response.getName().equals(challengeDto.getName());
                                        assert response.getImageUrl().equals(challengeDto.getImageUrl());
                                        assert response.getDescription().equals(challengeDto.getDescription());
                                });
        }

        @Test
        @DisplayName("Should get all challenges")
        void shouldGetAllChallenges() throws Exception {
                List<Challenge> challenges = Arrays.asList(
                                Challenge.builder().name("Challenge1").description("Desc1").build(),
                                Challenge.builder().name("Challenge2").description("Desc2").build());

                when(challengeService.getAllChallenges()).thenReturn(challenges);

                mockMvc.perform(get("/challenges/"))
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

                mockMvc.perform(get("/challenges/Challenge1"))
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
                                Challenge.builder().name("Challenge1").duration(duration).build());

                when(challengeService.getChallengeByDuration(duration)).thenReturn(challenges);

                mockMvc.perform(get("/challenges/duration/" + duration))
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
                                Challenge.builder().name("Challenge1").build());

                when(challengeService.getChallengesByUserEmail(user.getEmail())).thenReturn(challenges);

                mockMvc.perform(get("/challenges/user/" + user.getEmail())
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
                ChallengeDTO updated = ChallengeDTO.builder()
                                .name("Challenge1")
                                .description("Updated Desc")
                                .imageUrl("imageUrl1")
                                .build();
                Challenge challenge = Challenge.builder()
                                .name("Challenge1")
                                .description("Updated Desc")
                                .imageUrl("imageUrl1")
                                .build();

                when(challengeService.updateChallenge(updated)).thenReturn(challenge);

                mockMvc.perform(put("/challenges/Challenge1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updated)))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        Challenge response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(), Challenge.class);
                                        assert response.getDescription().equals("Updated Desc");
                                        assert response.getImageUrl().equals("imageUrl1");
                                });
        }

        @Test
        @DisplayName("Should delete challenge successfully")
        void shouldDeleteChallenge() throws Exception {
                doNothing().when(challengeService).deleteChallenge("Challenge1");

                mockMvc.perform(delete("/challenges/Challenge1"))
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
                mockMvc.perform(put("/challenges/users/{userEmail}/challenges/{challengeName}", userEmail,
                                challengeName))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should search challenges grouped by modules successfully")
        void shouldSearchChallengesGroupedByModules() throws Exception {
                // Crear módulos de prueba
                Module hydrationModule = Module.builder()
                                .name("Hidratación")
                                .description("Módulo sobre hidratación")
                                .imageUrl("hydration.jpg")
                                .build();

                Module exerciseModule = Module.builder()
                                .name("Ejercicio")
                                .description("Módulo de actividad física")
                                .imageUrl("exercise.jpg")
                                .build();

                // Crear challenges de prueba
                Challenge waterChallenge = Challenge.builder()
                                .name("Reto Agua Diaria")
                                .description("Tomar 8 vasos de agua al día")
                                .module(hydrationModule)
                                .build();

                Challenge lemonWaterChallenge = Challenge.builder()
                                .name("Agua con Limón")
                                .description("Beber agua con limón en ayunas")
                                .module(hydrationModule)
                                .build();

                Challenge walkingChallenge = Challenge.builder()
                                .name("Caminar 10mil pasos")
                                .description("Caminar 10,000 pasos diarios")
                                .module(exerciseModule)
                                .build();

                // Crear DTOs agrupados
                List<ModuleWithChallengesDTO> groupedResults = Arrays.asList(
                                ModuleWithChallengesDTO.builder()
                                                .module(hydrationModule)
                                                .challenges(Arrays.asList(waterChallenge, lemonWaterChallenge))
                                                .totalChallenges(2)
                                                .build(),
                                ModuleWithChallengesDTO.builder()
                                                .module(exerciseModule)
                                                .challenges(Arrays.asList(walkingChallenge))
                                                .totalChallenges(1)
                                                .build());

                when(challengeService.searchChallengesGroupedByModule("agua")).thenReturn(groupedResults);

                mockMvc.perform(get("/challenges/search")
                                .param("q", "agua"))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        ModuleWithChallengesDTO[] response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        ModuleWithChallengesDTO[].class);
                                        assert response.length == 2;
                                        assert response[0].getModule().getName().equals("Hidratación");
                                        assert response[0].getTotalChallenges() == 2;
                                        assert response[1].getModule().getName().equals("Ejercicio");
                                        assert response[1].getTotalChallenges() == 1;
                                });
        }

        @Test
        @DisplayName("Should return empty list when search query is null")
        void shouldReturnEmptyListWhenSearchQueryIsNull() throws Exception {
                mockMvc.perform(get("/challenges/search"))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        List<?> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(), List.class);
                                        assert response.isEmpty();
                                });
        }

        @Test
        @DisplayName("Should return empty list when search query is empty")
        void shouldReturnEmptyListWhenSearchQueryIsEmpty() throws Exception {
                mockMvc.perform(get("/challenges/search")
                                .param("q", ""))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        List<?> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(), List.class);
                                        assert response.isEmpty();
                                });
        }

        @Test
        @DisplayName("Should return empty list when search query is only whitespace")
        void shouldReturnEmptyListWhenSearchQueryIsWhitespace() throws Exception {
                mockMvc.perform(get("/challenges/search")
                                .param("q", "   "))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        List<?> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(), List.class);
                                        assert response.isEmpty();
                                });
        }

        @Test
        @DisplayName("Should return empty list when no challenges match search criteria")
        void shouldReturnEmptyListWhenNoChallengesMatch() throws Exception {
                when(challengeService.searchChallengesGroupedByModule("nonexistent"))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/challenges/search")
                                .param("q", "nonexistent"))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        List<?> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(), List.class);
                                        assert response.isEmpty();
                                });
        }

        @Test
        @DisplayName("Should search challenges with single module result")
        void shouldSearchChallengesWithSingleModuleResult() throws Exception {
                Module hydrationModule = Module.builder()
                                .name("Hidratación")
                                .description("Módulo sobre hidratación")
                                .imageUrl("hydration.jpg")
                                .build();

                Challenge waterChallenge = Challenge.builder()
                                .name("Reto Agua Diaria")
                                .description("Tomar 8 vasos de agua al día")
                                .module(hydrationModule)
                                .build();

                List<ModuleWithChallengesDTO> groupedResults = Arrays.asList(
                                ModuleWithChallengesDTO.builder()
                                                .module(hydrationModule)
                                                .challenges(Arrays.asList(waterChallenge))
                                                .totalChallenges(1)
                                                .build());

                when(challengeService.searchChallengesGroupedByModule("agua")).thenReturn(groupedResults);

                mockMvc.perform(get("/challenges/search")
                                .param("q", "agua"))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        ModuleWithChallengesDTO[] response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        ModuleWithChallengesDTO[].class);
                                        assert response.length == 1;
                                        assert response[0].getModule().getName().equals("Hidratación");
                                        assert response[0].getTotalChallenges() == 1;
                                        assert response[0].getChallenges().size() == 1;
                                        assert response[0].getChallenges().get(0).getName().equals("Reto Agua Diaria");
                                });
        }

        @Test
        @DisplayName("Should handle case-insensitive search")
        void shouldHandleCaseInsensitiveSearch() throws Exception {
                Module exerciseModule = Module.builder()
                                .name("Ejercicio")
                                .description("Módulo de actividad física")
                                .imageUrl("exercise.jpg")
                                .build();

                Challenge walkingChallenge = Challenge.builder()
                                .name("Caminar Diario")
                                .description("Caminar todos los días")
                                .module(exerciseModule)
                                .build();

                List<ModuleWithChallengesDTO> groupedResults = Arrays.asList(
                                ModuleWithChallengesDTO.builder()
                                                .module(exerciseModule)
                                                .challenges(Arrays.asList(walkingChallenge))
                                                .totalChallenges(1)
                                                .build());

                when(challengeService.searchChallengesGroupedByModule("CAMINAR")).thenReturn(groupedResults);

                mockMvc.perform(get("/challenges/search")
                                .param("q", "CAMINAR"))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        ModuleWithChallengesDTO[] response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        ModuleWithChallengesDTO[].class);
                                        assert response.length == 1;
                                        assert response[0].getChallenges().get(0).getName().equals("Caminar Diario");
                                });
        }
    @Test
    @DisplayName("Should get confirmed challenges by user email")
    void shouldGetConfirmedChallengesByUserEmail() throws Exception {
        String userEmail = "test@eci.edu.co";
        List<Challenge> confirmedChallenges = Arrays.asList(
                Challenge.builder().name("Challenge1").description("Completed Desc").build()
        );

        when(challengeService.getChallengesCompletedByUserEmail(userEmail))
                .thenReturn(confirmedChallenges);

        mockMvc.perform(get("/challenges/confirmed/" + userEmail))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Challenge[] response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            Challenge[].class
                    );
                    assert response.length == 1;
                    assert response[0].getName().equals("Challenge1");
                });
    }

}
