package edu.escuelaing.ecicare.awards.services;

import edu.escuelaing.ecicare.awards.models.dto.AwardDto;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.awards.repositories.AwardRepository;
import edu.escuelaing.ecicare.services.MapperService;
import edu.escuelaing.ecicare.utils.exceptions.notfound.AwardNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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
        when(awardRepository.findById(1L)).thenReturn(Optional.of(testAward));
        doNothing().when(awardRepository).delete(testAward);

        // When
        awardService.deleteAwardById(1L);

        // Then
        verify(awardRepository, times(1)).delete(testAward);
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

    @Test
    @DisplayName("Should default page to 0 when negative page is provided")
    void shouldDefaultPageToZeroWhenNegativePageProvided() {
        // Given
        when(awardRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testAward)));

        // When
        Page<Award> result = awardService.searchAwardsByNamePaginated("test", -1, 5);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(awardRepository, times(1))
                .findByNameContainingIgnoreCase(eq("test"), any(Pageable.class));
    }

    @Test
    @DisplayName("Should default size to 8 when size is zero, negative or greater than 100")
    void shouldDefaultSizeToEightWhenInvalid() {
        // Given
        when(awardRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testAward)));

        // When
        awardService.searchAwardsByNamePaginated("test", 0, 0);
        awardService.searchAwardsByNamePaginated("test", 0, -5);
        awardService.searchAwardsByNamePaginated("test", 0, 200);

        // Then
        verify(awardRepository, times(3))
                .findByNameContainingIgnoreCase(eq("test"), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty page when search query is null or blank")
    void shouldReturnEmptyPageWhenSearchQueryIsNullOrBlank() {
        // When
        Page<Award> nullResult = awardService.searchAwardsByNamePaginated(null, 0, 5);
        Page<Award> emptyResult = awardService.searchAwardsByNamePaginated("   ", 0, 5);

        // Then
        assertTrue(nullResult.isEmpty());
        assertTrue(emptyResult.isEmpty());
        verify(awardRepository, never()).findByNameContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should trim and truncate search query when longer than 50 characters")
    void shouldTrimAndTruncateSearchQueryWhenLongerThan50() {
        // Given
        String longQuery = "a".repeat(60); // 60 'a's
        when(awardRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(testAward)));

        // When
        Page<Award> result = awardService.searchAwardsByNamePaginated(longQuery, 0, 10);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(awardRepository, times(1))
                .findByNameContainingIgnoreCase(eq("a".repeat(50)), any(Pageable.class));
    }

    @Test
    @DisplayName("Should search awards by name successfully with valid query, page and size")
    void shouldSearchAwardsByNameSuccessfully() {
        // Given
        when(awardRepository.findByNameContainingIgnoreCase(eq("test"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testAward)));

        // When
        Page<Award> result = awardService.searchAwardsByNamePaginated(" test ", 0, 5);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Award", result.getContent().get(0).getName());
        verify(awardRepository, times(1))
                .findByNameContainingIgnoreCase(eq("test"), any(Pageable.class));
    }

}
