package edu.escuelaing.ecicare.challenges.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.models.entity.RedeemableId;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleDTO;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.models.entity.Module;
import edu.escuelaing.ecicare.challenges.services.ModuleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModuleController.class)
class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleService moduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should save module when creating a new one")
    void shouldCreateModule() throws Exception {
        ModuleDTO moduleDto = ModuleDTO.builder()
                .name("Module1")
                .description("Description1")
                .imageUrl("ImageUrl1")
                .build();

        when(moduleService.createModule(any(ModuleDTO.class)))
                .thenReturn(Module.builder()
                        .name("Module1")
                        .description("Description1")
                        .imageUrl("ImageUrl1")
                        .challenges(Collections.emptyList())
                        .build());


        mockMvc.perform(post("/modules/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moduleDto)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Module response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            Module.class);
                    assert response.getName().equals(moduleDto.getName());
                    assert response.getDescription().equals(moduleDto.getDescription());
                    assert response.getImageUrl().equals(moduleDto.getImageUrl());
                });
    }

    @Test
    @DisplayName("Should return all modules when they exist")
    void shouldGetAllModules() throws Exception {
        List<Module> modules = Arrays.asList(
                Module.builder().name("Module1").description("Desc1").build(),
                Module.builder().name("Module2").description("Desc2").build()
        );

        when(moduleService.getAllModules()).thenReturn(modules);

        mockMvc.perform(get("/modules/"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Module[] response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            Module[].class);
                    assert response.length == modules.size();
                    assert response[0].getName().equals(modules.get(0).getName());
                    assert response[0].getDescription().equals(modules.get(0).getDescription());
                    assert response[1].getName().equals(modules.get(1).getName());
                    assert response[1].getDescription().equals(modules.get(1).getDescription());
                });
    }

    @Test
    @DisplayName("Should return all challenges from any module")
    void shouldGetModuleChallenges() throws Exception {
        Module module = Module.builder()
                .name("Module1")
                .description("Desc1")
                .build();

        Award award = Award.builder()
                .name("Gold Medal")
                .description("Special award")
                .build();

        // Crear un RedeemableId
        RedeemableId redeemableId = new RedeemableId("Challenge1", award.getAwardId());

        Redeemable redeemable = Redeemable.builder()
                .id(redeemableId)
                .award(award)
                .limitDays(30)
                .build();

        Set<Redeemable> redeemables = Set.of(redeemable);

        List<Challenge> challenges = Arrays.asList(
                Challenge.builder()
                        .name("Challenge1")
                        .description("Desc1")
                        .phrase("Keep going!")
                        .duration(LocalDateTime.now().plusDays(5))
                        .goals(List.of("Goal1", "Goal2"))
                        .redeemables(redeemables)
                        .module(module)
                        .build(),
                Challenge.builder()
                        .name("Challenge2")
                        .description("Desc2")
                        .phrase("Never give up!")
                        .duration(LocalDateTime.now().plusDays(10))
                        .goals(List.of("GoalA"))
                        .redeemables(redeemables)
                        .module(module)
                        .build()
        );

        when(moduleService.getChallengesByModule("Module1")).thenReturn(challenges);

        mockMvc.perform(get("/modules/challenge/Module1"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Challenge[] response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            Challenge[].class);
                    assert response.length == challenges.size();
                    assert response[0].getName().equals(challenges.get(0).getName());
                    assert response[0].getDescription().equals(challenges.get(0).getDescription());
                    assert response[0].getModule().getName().equals("Module1");
                    assert response[1].getName().equals(challenges.get(1).getName());
                    assert response[1].getDescription().equals(challenges.get(1).getDescription());
                    assert response[1].getModule().getName().equals("Module1");
                });
    }

    @Test
    @DisplayName("Should update module description when module exists")
    void shouldUpdateModuleDescription() throws Exception {
        ModuleDTO moduleDto = ModuleDTO.builder()
                .name("Module1")
                .description("New Description")
                .imageUrl("ImageUrl1")
                .build();

        Module module = Module.builder()
                .name("Module1")
                .description("New Description")
                .imageUrl("ImageUrl1")
                .build();

        when(moduleService.updateModuleByName(moduleDto))
                .thenReturn(module);

        mockMvc.perform(put("/modules/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moduleDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should call deleteModule when deleting a challenge")
    void shouldDeleteModule() throws Exception {
        doNothing().when(moduleService).deleteModule("Module1");

        mockMvc.perform(delete("/modules/Module1"))
                .andExpect(status().isOk());
    }
}
