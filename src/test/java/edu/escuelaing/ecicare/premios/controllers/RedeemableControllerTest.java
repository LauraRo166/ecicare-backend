package edu.escuelaing.ecicare.premios.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.ecicare.premios.models.dto.RedeemableDto;
import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import edu.escuelaing.ecicare.premios.models.entity.RedeemableId;
import edu.escuelaing.ecicare.premios.services.RedeemableService;
import edu.escuelaing.ecicare.retos.models.entity.Challenge;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.utils.exceptions.notfound.RedeemableNotFoundException;
import edu.escuelaing.ecicare.utils.models.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redeemable Controller Tests")
class RedeemableControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RedeemableService redeemableService;

    @InjectMocks
    private RedeemableController redeemableController;

    private ObjectMapper objectMapper;

    private Redeemable testRedeemable;
    private RedeemableDto testRedeemableDto;
    private Challenge testChallenge;
    private Award testAward;
    private UserEcicare testUser;
    private RedeemableId testRedeemableId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(redeemableController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        LocalDateTime testDateTime = LocalDateTime.now();
        
        testUser = UserEcicare.builder()
                .idEci(1L)
                .name("Test User")
                .email("test@escuelaing.edu.co")
                .password("password123")
                .role(Role.ADMINISTRATION)
                .registrationDate(testDateTime)
                .hasMedicalApprove(true)
                .build();

        testChallenge = Challenge.builder()
                .name("Test Challenge")
                .description("Test Challenge Description")
                .imageUrl("/images/test-challenge.png")
                .phrase("Test Phrase")
                .build();

        testAward = Award.builder()
                .awardId(1L)
                .name("Test Award")
                .description("Test Award Description")
                .inStock(10)
                .imageUrl("/images/test-award.png")
                .creationDate(testDateTime)
                .updateDate(testDateTime)
                .createdBy(testUser)
                .updatedBy(testUser)
                .build();

        testRedeemableId = RedeemableId.builder()
                .challengeName("Test Challenge")
                .awardId(1L)
                .build();

        testRedeemable = Redeemable.builder()
                .id(testRedeemableId)
                .challenge(testChallenge)
                .award(testAward)
                .limitDays(30)
                .build();

        testRedeemableDto = RedeemableDto.builder()
                .awardId(1L)
                .limitDays(45)
                .build();
    }

    @Test
    @DisplayName("GET /redeemables - Should return all redeemables")
    void shouldReturnAllRedeemables() throws Exception {
        // Given
        List<Redeemable> redeemables = Arrays.asList(testRedeemable, createAnotherRedeemable());
        when(redeemableService.getAllRedeemables()).thenReturn(redeemables);

        // When & Then
        mockMvc.perform(get("/redeemables"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id.challengeName", is("Test Challenge")))
                .andExpect(jsonPath("$[0].id.awardId", is(1)))
                .andExpect(jsonPath("$[0].limitDays", is(30)));

        verify(redeemableService, times(1)).getAllRedeemables();
    }

    @Test
    @DisplayName("GET /redeemables - Should handle empty redeemables list")
    void shouldHandleEmptyRedeemablesList() throws Exception {
        // Given
        when(redeemableService.getAllRedeemables()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/redeemables"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(redeemableService, times(1)).getAllRedeemables();
    }

    @Test
    @DisplayName("GET /redeemables/{challengeName}/{awardId} - Should return redeemable by composite key")
    void shouldReturnRedeemableByCompositeKey() throws Exception {
        // Given
        when(redeemableService.getRedeemableById("Test Challenge", 1L)).thenReturn(testRedeemable);

        // When & Then
        mockMvc.perform(get("/redeemables/Test Challenge/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id.challengeName", is("Test Challenge")))
                .andExpect(jsonPath("$.id.awardId", is(1)))
                .andExpect(jsonPath("$.limitDays", is(30)))
                .andExpect(jsonPath("$.challenge.name", is("Test Challenge")));
                // Note: award field is not included due to @JsonBackReference annotation

        verify(redeemableService, times(1)).getRedeemableById("Test Challenge", 1L);
    }

    @Test
    @DisplayName("GET /redeemables/{challengeName}/{awardId} - Should return 404 when redeemable not found")
    void shouldReturn404WhenRedeemableNotFound() throws Exception {
        // Given
        when(redeemableService.getRedeemableById("Nonexistent Challenge", 999L))
                .thenThrow(new RedeemableNotFoundException("Nonexistent Challenge", 999L));

        // When & Then
        mockMvc.perform(get("/redeemables/Nonexistent Challenge/999"))
                .andExpect(status().isNotFound());

        verify(redeemableService, times(1)).getRedeemableById("Nonexistent Challenge", 999L);
    }

    @Test
    @DisplayName("GET /redeemables/{challengeName}/{awardId} - Should handle special characters in challenge name")
    void shouldHandleSpecialCharactersInChallengeName() throws Exception {
        // Given
        String challengeNameWithSpaces = "Challenge With Spaces";
        Redeemable specialRedeemable = Redeemable.builder()
                .id(RedeemableId.builder()
                        .challengeName(challengeNameWithSpaces)
                        .awardId(1L)
                        .build())
                .challenge(Challenge.builder()
                        .name(challengeNameWithSpaces)
                        .description("Special Challenge")
                        .build())
                .award(testAward)
                .limitDays(15)
                .build();

        when(redeemableService.getRedeemableById(challengeNameWithSpaces, 1L)).thenReturn(specialRedeemable);

        // When & Then
        mockMvc.perform(get("/redeemables/{challengeName}/{awardId}", challengeNameWithSpaces, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id.challengeName", is(challengeNameWithSpaces)))
                .andExpect(jsonPath("$.limitDays", is(15)));

        verify(redeemableService, times(1)).getRedeemableById(challengeNameWithSpaces, 1L);
    }

    @Test
    @DisplayName("PUT /redeemables/{challengeName}/{awardId} - Should update existing redeemable")
    void shouldUpdateExistingRedeemable() throws Exception {
        // Given
        Redeemable updatedRedeemable = Redeemable.builder()
                .id(testRedeemableId)
                .challenge(testChallenge)
                .award(testAward)
                .limitDays(60)
                .build();

        when(redeemableService.updateRedeemable(eq("Test Challenge"), eq(1L), any(RedeemableDto.class)))
                .thenReturn(updatedRedeemable);

        // When & Then
        mockMvc.perform(put("/redeemables/Test Challenge/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRedeemableDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id.challengeName", is("Test Challenge")))
                .andExpect(jsonPath("$.id.awardId", is(1)))
                .andExpect(jsonPath("$.limitDays", is(60)));

        verify(redeemableService, times(1)).updateRedeemable(eq("Test Challenge"), eq(1L), any(RedeemableDto.class));
    }

    @Test
    @DisplayName("PUT /redeemables/{challengeName}/{awardId} - Should return 404 when updating non-existent redeemable")
    void shouldReturn404WhenUpdatingNonExistentRedeemable() throws Exception {
        // Given
        when(redeemableService.updateRedeemable(eq("Nonexistent Challenge"), eq(999L), any(RedeemableDto.class)))
                .thenThrow(new RedeemableNotFoundException("Nonexistent Challenge", 999L));

        // When & Then
        mockMvc.perform(put("/redeemables/Nonexistent Challenge/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRedeemableDto)))
                .andExpect(status().isNotFound());

        verify(redeemableService, times(1)).updateRedeemable(eq("Nonexistent Challenge"), eq(999L), any(RedeemableDto.class));
    }

    @Test
    @DisplayName("PUT /redeemables/{challengeName}/{awardId} - Should handle invalid JSON")
    void shouldHandleInvalidJsonForUpdate() throws Exception {
        // When & Then
        mockMvc.perform(put("/redeemables/Test Challenge/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(redeemableService, never()).updateRedeemable(any(), any(), any(RedeemableDto.class));
    }

    @Test
    @DisplayName("DELETE /redeemables/{challengeName}/{awardId} - Should delete redeemable successfully")
    void shouldDeleteRedeemableSuccessfully() throws Exception {
        // Given
        doNothing().when(redeemableService).deleteRedeemable("Test Challenge", 1L);

        // When & Then
        mockMvc.perform(delete("/redeemables/Test Challenge/1"))
                .andExpect(status().isNoContent());

        verify(redeemableService, times(1)).deleteRedeemable("Test Challenge", 1L);
    }

    @Test
    @DisplayName("DELETE /redeemables/{challengeName}/{awardId} - Should handle deletion of non-existent redeemable")
    void shouldHandleDeletionOfNonExistentRedeemable() throws Exception {
        // Given
        doThrow(new RedeemableNotFoundException("Nonexistent Challenge", 999L))
                .when(redeemableService).deleteRedeemable("Nonexistent Challenge", 999L);

        // When & Then
        mockMvc.perform(delete("/redeemables/Nonexistent Challenge/999"))
                .andExpect(status().isNotFound());

        verify(redeemableService, times(1)).deleteRedeemable("Nonexistent Challenge", 999L);
    }

    @Test
    @DisplayName("PUT /redeemables/{challengeName}/{awardId} - Should update with minimal DTO")
    void shouldUpdateWithMinimalDto() throws Exception {
        // Given
        RedeemableDto minimalDto = RedeemableDto.builder()
                .limitDays(7)
                .build();

        Redeemable updatedRedeemable = Redeemable.builder()
                .id(testRedeemableId)
                .challenge(testChallenge)
                .award(testAward)
                .limitDays(7)
                .build();

        when(redeemableService.updateRedeemable(eq("Test Challenge"), eq(1L), any(RedeemableDto.class)))
                .thenReturn(updatedRedeemable);

        // When & Then
        mockMvc.perform(put("/redeemables/Test Challenge/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.limitDays", is(7)));

        verify(redeemableService, times(1)).updateRedeemable(eq("Test Challenge"), eq(1L), any(RedeemableDto.class));
    }

    @Test
    @DisplayName("PUT /redeemables/{challengeName}/{awardId} - Should handle zero limit days")
    void shouldHandleZeroLimitDays() throws Exception {
        // Given
        RedeemableDto zeroDto = RedeemableDto.builder()
                .awardId(1L)
                .limitDays(0)
                .build();

        Redeemable updatedRedeemable = Redeemable.builder()
                .id(testRedeemableId)
                .challenge(testChallenge)
                .award(testAward)
                .limitDays(0)
                .build();

        when(redeemableService.updateRedeemable(eq("Test Challenge"), eq(1L), any(RedeemableDto.class)))
                .thenReturn(updatedRedeemable);

        // When & Then
        mockMvc.perform(put("/redeemables/Test Challenge/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zeroDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.limitDays", is(0)));

        verify(redeemableService, times(1)).updateRedeemable(eq("Test Challenge"), eq(1L), any(RedeemableDto.class));
    }

    @Test
    @DisplayName("Should handle very large limit days")
    void shouldHandleVeryLargeLimitDays() throws Exception {
        // Given
        RedeemableDto largeDto = RedeemableDto.builder()
                .awardId(1L)
                .limitDays(365)
                .build();

        Redeemable updatedRedeemable = Redeemable.builder()
                .id(testRedeemableId)
                .challenge(testChallenge)
                .award(testAward)
                .limitDays(365)
                .build();

        when(redeemableService.updateRedeemable(eq("Test Challenge"), eq(1L), any(RedeemableDto.class)))
                .thenReturn(updatedRedeemable);

        // When & Then
        mockMvc.perform(put("/redeemables/Test Challenge/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.limitDays", is(365)));

        verify(redeemableService, times(1)).updateRedeemable(eq("Test Challenge"), eq(1L), any(RedeemableDto.class));
    }

    @Test
    @DisplayName("Should handle URL encoding in challenge names")
    void shouldHandleUrlEncodingInChallengeNames() throws Exception {
        // Given
        String challengeWithSpecialChars = "Challenge-With_Special.Chars";
        when(redeemableService.getRedeemableById(challengeWithSpecialChars, 1L)).thenReturn(testRedeemable);

        // When & Then
        mockMvc.perform(get("/redeemables/{challengeName}/{awardId}", challengeWithSpecialChars, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(redeemableService, times(1)).getRedeemableById(challengeWithSpecialChars, 1L);
    }

    private Redeemable createAnotherRedeemable() {
        Challenge anotherChallenge = Challenge.builder()
                .name("Another Challenge")
                .description("Another Description")
                .build();

        Award anotherAward = Award.builder()
                .awardId(2L)
                .name("Another Award")
                .description("Another Description")
                .build();

        RedeemableId anotherId = RedeemableId.builder()
                .challengeName("Another Challenge")
                .awardId(2L)
                .build();

        return Redeemable.builder()
                .id(anotherId)
                .challenge(anotherChallenge)
                .award(anotherAward)
                .limitDays(60)
                .build();
    }
}
