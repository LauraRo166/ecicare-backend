package edu.escuelaing.ecicare.awards.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.ecicare.awards.models.dto.AwardDto;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.awards.services.AwardService;
import edu.escuelaing.ecicare.utils.exceptions.notfound.AwardNotFoundException;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Award Controller Tests")
class AwardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AwardService awardService;

    @InjectMocks
    private AwardController awardController;

    private ObjectMapper objectMapper;

    private Award testAward;
    private AwardDto testAwardDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(awardController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testAward = Award.builder()
                .awardId(1L)
                .name("Test Award")
                .description("Test Award Description")
                .inStock(10)
                .imageUrl("/images/test-award.png")
                .build();

        testAwardDto = AwardDto.builder()
                .name("Test Award DTO")
                .description("Test Award DTO Description")
                .inStock(15)
                .imageUrl("/images/test-award-dto.png")
                .build();
    }

    @Test
    @DisplayName("GET /awards/total - Should return total count of awards")
    void shouldReturnTotalCountOfAwards() throws Exception {
        // Given
        when(awardService.getAllAwardsLength()).thenReturn(5);

        // When & Then
        mockMvc.perform(get("/awards/total"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", is(5)));

        verify(awardService, times(1)).getAllAwardsLength();
    }

    @Test
    @DisplayName("GET /awards - Should return paginated awards with default parameters")
    void shouldReturnPaginatedAwardsWithDefaultParameters() throws Exception {
        // Given
        List<Award> awards = Arrays.asList(testAward, createAnotherAward());
        when(awardService.getAwardPagination(1, 10)).thenReturn(awards);

        // When & Then
        mockMvc.perform(get("/awards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].awardId", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Award")))
                .andExpect(jsonPath("$[0].description", is("Test Award Description")))
                .andExpect(jsonPath("$[0].inStock", is(10)));

        verify(awardService, times(1)).getAwardPagination(1, 10);
    }

    @Test
    @DisplayName("GET /awards - Should return paginated awards with custom parameters")
    void shouldReturnPaginatedAwardsWithCustomParameters() throws Exception {
        // Given
        List<Award> awards = Arrays.asList(testAward);
        when(awardService.getAwardPagination(2, 5)).thenReturn(awards);

        // When & Then
        mockMvc.perform(get("/awards")
                .param("page", "2")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].awardId", is(1)));

        verify(awardService, times(1)).getAwardPagination(2, 5);
    }

    @Test
    @DisplayName("GET /awards/{id} - Should return award by id")
    void shouldReturnAwardById() throws Exception {
        // Given
        when(awardService.getAwardById(1L)).thenReturn(testAward);

        // When & Then
        mockMvc.perform(get("/awards/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.awardId", is(1)))
                .andExpect(jsonPath("$.name", is("Test Award")))
                .andExpect(jsonPath("$.description", is("Test Award Description")))
                .andExpect(jsonPath("$.inStock", is(10)))
                .andExpect(jsonPath("$.imageUrl", is("/images/test-award.png")));

        verify(awardService, times(1)).getAwardById(1L);
    }

    @Test
    @DisplayName("GET /awards/{id} - Should return 404 when award not found")
    void shouldReturn404WhenAwardNotFound() throws Exception {
        // Given
        when(awardService.getAwardById(999L)).thenThrow(new AwardNotFoundException(999L));

        // When & Then
        mockMvc.perform(get("/awards/999"))
                .andExpect(status().isNotFound());

        verify(awardService, times(1)).getAwardById(999L);
    }

    @Test
    @DisplayName("POST /awards - Should create new award")
    void shouldCreateNewAward() throws Exception {
        // Given
        when(awardService.createAward(any(AwardDto.class))).thenReturn(testAward);

        // When & Then
        mockMvc.perform(post("/awards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAwardDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.awardId", is(1)))
                .andExpect(jsonPath("$.name", is("Test Award")))
                .andExpect(jsonPath("$.description", is("Test Award Description")))
                .andExpect(jsonPath("$.inStock", is(10)));

        verify(awardService, times(1)).createAward(any(AwardDto.class));
    }

    @Test
    @DisplayName("POST /awards - Should handle invalid JSON")
    void shouldHandleInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/awards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(awardService, never()).createAward(any(AwardDto.class));
    }

    @Test
    @DisplayName("PUT /awards/{id} - Should update existing award")
    void shouldUpdateExistingAward() throws Exception {
        // Given
        Award updatedAward = Award.builder()
                .awardId(1L)
                .name("Updated Award")
                .description("Updated Description")
                .inStock(20)
                .imageUrl("/images/updated-award.png")
                .build();

        when(awardService.updateAwardDetails(eq(1L), any(AwardDto.class))).thenReturn(updatedAward);

        // When & Then
        mockMvc.perform(put("/awards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAwardDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.awardId", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Award")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.inStock", is(20)));

        verify(awardService, times(1)).updateAwardDetails(eq(1L), any(AwardDto.class));
    }

    @Test
    @DisplayName("PUT /awards/{id} - Should return 404 when updating non-existent award")
    void shouldReturn404WhenUpdatingNonExistentAward() throws Exception {
        // Given
        when(awardService.updateAwardDetails(eq(999L), any(AwardDto.class)))
                .thenThrow(new AwardNotFoundException(999L));

        // When & Then
        mockMvc.perform(put("/awards/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAwardDto)))
                .andExpect(status().isNotFound());

        verify(awardService, times(1)).updateAwardDetails(eq(999L), any(AwardDto.class));
    }

    @Test
    @DisplayName("DELETE /awards/{id} - Should delete award successfully")
    void shouldDeleteAwardSuccessfully() throws Exception {
        // Given
        doNothing().when(awardService).deleteAwardById(1L);

        // When & Then
        mockMvc.perform(delete("/awards/1"))
                .andExpect(status().isNoContent());

        verify(awardService, times(1)).deleteAwardById(1L);
    }

    @Test
    @DisplayName("DELETE /awards/{id} - Should handle deletion of non-existent award")
    void shouldHandleDeletionOfNonExistentAward() throws Exception {
        // Given
        doThrow(new AwardNotFoundException(999L)).when(awardService).deleteAwardById(999L);

        // When & Then
        mockMvc.perform(delete("/awards/999"))
                .andExpect(status().isNotFound());

        verify(awardService, times(1)).deleteAwardById(999L);
    }

    @Test
    @DisplayName("Should handle empty award list")
    void shouldHandleEmptyAwardList() throws Exception {
        // Given
        when(awardService.getAwardPagination(1, 10)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/awards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(awardService, times(1)).getAwardPagination(1, 10);
    }

    @Test
    @DisplayName("Should handle large page size")
    void shouldHandleLargePageSize() throws Exception {
        // Given
        when(awardService.getAwardPagination(1, 1000)).thenReturn(Arrays.asList(testAward));

        // When & Then
        mockMvc.perform(get("/awards")
                .param("page", "1")
                .param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(awardService, times(1)).getAwardPagination(1, 1000);
    }

    @Test
    @DisplayName("Should handle edge case page numbers")
    void shouldHandleEdgeCasePageNumbers() throws Exception {
        // Given
        when(awardService.getAwardPagination(0, 10)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/awards")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(awardService, times(1)).getAwardPagination(0, 10);
    }

    @Test
    @DisplayName("POST /awards - Should create award with minimal data")
    void shouldCreateAwardWithMinimalData() throws Exception {
        // Given
        AwardDto minimalDto = AwardDto.builder()
                .name("Minimal Award")
                .build();

        Award minimalAward = Award.builder()
                .awardId(2L)
                .name("Minimal Award")
                .build();

        when(awardService.createAward(any(AwardDto.class))).thenReturn(minimalAward);

        // When & Then
        mockMvc.perform(post("/awards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.awardId", is(2)))
                .andExpect(jsonPath("$.name", is("Minimal Award")));

        verify(awardService, times(1)).createAward(any(AwardDto.class));
    }

    @Test
    @DisplayName("Should handle very long award names and descriptions")
    void shouldHandleVeryLongAwardNamesAndDescriptions() throws Exception {
        // Given
        String longName = "A".repeat(50); // Máximo permitido
        String longDescription = "B".repeat(500); // Máximo permitido

        AwardDto longDto = AwardDto.builder()
                .name(longName)
                .description(longDescription)
                .inStock(1)
                .build();

        Award longAward = Award.builder()
                .awardId(3L)
                .name(longName)
                .description(longDescription)
                .inStock(1)
                .build();

        when(awardService.createAward(any(AwardDto.class))).thenReturn(longAward);

        // When & Then
        mockMvc.perform(post("/awards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(longName)))
                .andExpect(jsonPath("$.description", is(longDescription)));

        verify(awardService, times(1)).createAward(any(AwardDto.class));
    }

    private Award createAnotherAward() {
        return Award.builder()
                .awardId(2L)
                .name("Another Award")
                .description("Another Description")
                .inStock(5)
                .imageUrl("/images/another-award.png")
                .build();
    }
}
