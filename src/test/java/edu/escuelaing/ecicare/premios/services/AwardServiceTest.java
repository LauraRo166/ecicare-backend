package edu.escuelaing.ecicare.premios.services;

import edu.escuelaing.ecicare.premios.models.dto.AwardDto;
import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.premios.repositories.AwardRepository;
import edu.escuelaing.ecicare.services.MapperService;
import edu.escuelaing.ecicare.utils.exceptions.notfound.AwardNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Award Service Tests")
class AwardServiceTest {

    @Mock
    private AwardRepository awardRepository;

    @Mock
    private MapperService mapperService;

    @InjectMocks
    private AwardService awardService;

    private Award testAward;
    private AwardDto testAwardDto;

    @BeforeEach
    void setUp() {

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
    @DisplayName("Should return total count of awards")
    void shouldReturnTotalCountOfAwards() {
        // Given
        List<Award> awards = Arrays.asList(testAward, createAnotherAward());
        when(awardRepository.findAll()).thenReturn(awards);

        // When
        int totalCount = awardService.getAllAwardsLength();

        // Then
        assertEquals(2, totalCount);
        verify(awardRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return zero when no awards exist")
    void shouldReturnZeroWhenNoAwardsExist() {
        // Given
        when(awardRepository.findAll()).thenReturn(Arrays.asList());

        // When
        int totalCount = awardService.getAllAwardsLength();

        // Then
        assertEquals(0, totalCount);
        verify(awardRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return paginated awards - first page")
    void shouldReturnPaginatedAwardsFirstPage() {
        // Given
        List<Award> allAwards = Arrays.asList(testAward, createAnotherAward(), createThirdAward());
        when(awardRepository.findAll()).thenReturn(allAwards);

        // When
        List<Award> result = awardService.getAwardPagination(1, 2);

        // Then
        assertEquals(2, result.size());
        assertEquals("Test Award", result.get(0).getName());
        assertEquals("Another Award", result.get(1).getName());
        verify(awardRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return paginated awards - second page")
    void shouldReturnPaginatedAwardsSecondPage() {
        // Given
        List<Award> allAwards = Arrays.asList(testAward, createAnotherAward(), createThirdAward());
        when(awardRepository.findAll()).thenReturn(allAwards);

        // When
        List<Award> result = awardService.getAwardPagination(2, 2);

        // Then
        assertEquals(1, result.size());
        assertEquals("Third Award", result.get(0).getName());
        verify(awardRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when page exceeds available awards")
    void shouldReturnEmptyListWhenPageExceedsAvailableAwards() {
        // Given
        List<Award> allAwards = Arrays.asList(testAward);
        when(awardRepository.findAll()).thenReturn(allAwards);

        // When
        List<Award> result = awardService.getAwardPagination(3, 10);

        // Then
        assertTrue(result.isEmpty());
        verify(awardRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get award by id successfully")
    void shouldGetAwardByIdSuccessfully() {
        // Given
        when(awardRepository.findById(1L)).thenReturn(Optional.of(testAward));

        // When
        Award result = awardService.getAwardById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getAwardId());
        assertEquals("Test Award", result.getName());
        verify(awardRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw AwardNotFoundException when award not found")
    void shouldThrowAwardNotFoundExceptionWhenAwardNotFound() {
        // Given
        when(awardRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        AwardNotFoundException exception = assertThrows(
            AwardNotFoundException.class,
            () -> awardService.getAwardById(999L)
        );

        assertNotNull(exception);
        verify(awardRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create award with provided image URL")
    void shouldCreateAwardWithProvidedImageUrl() {
        // Given
        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        Award result = awardService.createAward(testAwardDto);

        // Then
        assertNotNull(result);
        verify(awardRepository, times(1)).save(any(Award.class));
        
        // Verify the award was created with correct data
        verify(awardRepository).save(argThat(award -> {
            return award.getName().equals("Test Award DTO") &&
                   award.getDescription().equals("Test Award DTO Description") &&
                   award.getInStock().equals(15) &&
                   award.getImageUrl().equals("/images/test-award-dto.png");
        }));
    }

    @Test
    @DisplayName("Should create award with default image URL when not provided")
    void shouldCreateAwardWithDefaultImageUrlWhenNotProvided() {
        // Given
        AwardDto dtoWithoutImage = AwardDto.builder()
                .name("Award Without Image")
                .description("Description")
                .inStock(5)
                .build();

        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        awardService.createAward(dtoWithoutImage);

        // Then
        verify(awardRepository).save(argThat(award -> 
            award.getImageUrl().equals("/images/awards/default-award.png")
        ));
    }

    @Test
    @DisplayName("Should create award with default image URL when empty string provided")
    void shouldCreateAwardWithDefaultImageUrlWhenEmptyStringProvided() {
        // Given
        AwardDto dtoWithEmptyImage = AwardDto.builder()
                .name("Award With Empty Image")
                .description("Description")
                .inStock(5)
                .imageUrl("")
                .build();

        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        awardService.createAward(dtoWithEmptyImage);

        // Then
        verify(awardRepository).save(argThat(award -> 
            award.getImageUrl().equals("/images/awards/default-award.png")
        ));
    }

    @Test
    @DisplayName("Should create award with default image URL when whitespace provided")
    void shouldCreateAwardWithDefaultImageUrlWhenWhitespaceProvided() {
        // Given
        AwardDto dtoWithWhitespaceImage = AwardDto.builder()
                .name("Award With Whitespace Image")
                .description("Description")
                .inStock(5)
                .imageUrl("   ")
                .build();

        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        awardService.createAward(dtoWithWhitespaceImage);

        // Then
        verify(awardRepository).save(argThat(award -> 
            award.getImageUrl().equals("/images/awards/default-award.png")
        ));
    }

    @Test
    @DisplayName("Should update award details successfully")
    void shouldUpdateAwardDetailsSuccessfully() {
        // Given
        Map<String, Object> dtoMap = new HashMap<>();
        dtoMap.put("name", "Updated Name");
        dtoMap.put("description", "Updated Description");
        dtoMap.put("inStock", 20);

        when(awardRepository.findById(1L)).thenReturn(Optional.of(testAward));
        when(mapperService.covertDtoToMap(testAwardDto)).thenReturn(dtoMap);
        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        Award result = awardService.updateAwardDetails(1L, testAwardDto);

        // Then
        assertNotNull(result);
        verify(awardRepository, times(1)).findById(1L);
        verify(mapperService, times(1)).covertDtoToMap(testAwardDto);
        verify(awardRepository, times(1)).save(any(Award.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent award")
    void shouldThrowExceptionWhenUpdatingNonExistentAward() {
        // Given
        when(awardRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AwardNotFoundException.class, 
            () -> awardService.updateAwardDetails(999L, testAwardDto));
        
        verify(awardRepository, times(1)).findById(999L);
        verify(mapperService, never()).covertDtoToMap(any());
        verify(awardRepository, never()).save(any(Award.class));
    }

    @Test
    @DisplayName("Should delete award by id")
    void shouldDeleteAwardById() {
        // Given
        doNothing().when(awardRepository).deleteById(1L);

        // When
        awardService.deleteAwardById(1L);

        // Then
        verify(awardRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should save award successfully")
    void shouldSaveAwardSuccessfully() {
        // Given
        when(awardRepository.save(testAward)).thenReturn(testAward);

        // When
        awardService.saveAward(testAward);

        // Then
        verify(awardRepository, times(1)).save(testAward);
    }

    @Test
    @DisplayName("Should handle null values in update")
    void shouldHandleNullValuesInUpdate() {
        // Given
        Map<String, Object> dtoMapWithNulls = new HashMap<>();
        dtoMapWithNulls.put("name", null);
        dtoMapWithNulls.put("description", "Updated Description");

        when(awardRepository.findById(1L)).thenReturn(Optional.of(testAward));
        when(mapperService.covertDtoToMap(testAwardDto)).thenReturn(dtoMapWithNulls);
        when(awardRepository.save(any(Award.class))).thenReturn(testAward);

        // When
        Award result = awardService.updateAwardDetails(1L, testAwardDto);

        // Then
        assertNotNull(result);
        verify(awardRepository, times(1)).save(testAward);
    }

    @Test
    @DisplayName("Should handle invalid page size parameters")
    void shouldHandleInvalidPageSizeParameters() {
        // When & Then - Test page size 0
        assertThrows(IllegalArgumentException.class, () -> {
            awardService.getAwardPagination(1, 0);
        });

        // Test negative page size
        assertThrows(IllegalArgumentException.class, () -> {
            awardService.getAwardPagination(1, -1);
        });

        // Verify repository is not called when validation fails
        verify(awardRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should handle large pagination size correctly")
    void shouldHandleLargePaginationSizeCorrectly() {
        // Given
        List<Award> smallList = Arrays.asList(testAward);
        when(awardRepository.findAll()).thenReturn(smallList);

        // When
        List<Award> result = awardService.getAwardPagination(1, 1000);

        // Then
        assertEquals(1, result.size());
        assertEquals(testAward, result.get(0));
        verify(awardRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle edge case for page 0")
    void shouldHandleEdgeCaseForPageZero() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            awardService.getAwardPagination(0, 1);
        });

        // Verify repository is not called when validation fails
        verify(awardRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should handle negative page numbers")
    void shouldHandleNegativePageNumbers() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            awardService.getAwardPagination(-1, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            awardService.getAwardPagination(-5, 10);
        });

        // Verify repository is not called when validation fails
        verify(awardRepository, never()).findAll();
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

    private Award createThirdAward() {
        return Award.builder()
                .awardId(3L)
                .name("Third Award")
                .description("Third Description")
                .inStock(3)
                .imageUrl("/images/third-award.png")
                .build();
    }
}
