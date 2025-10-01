package edu.escuelaing.ecicare.awards.services;

import edu.escuelaing.ecicare.awards.models.dto.RedeemableDto;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.models.entity.RedeemableId;
import edu.escuelaing.ecicare.awards.repositories.RedeemableRepository;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.services.ChallengeService;
import edu.escuelaing.ecicare.utils.exceptions.notfound.RedeemableNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redeemable Service Tests")
class RedeemableServiceTest {

    @Mock
    private RedeemableRepository redeemableRepository;

    @Mock
    private AwardService awardService;

    @Mock
    private ChallengeService challengeService;

    @InjectMocks
    private RedeemableService redeemableService;

    private Redeemable testRedeemable;
    private RedeemableDto testRedeemableDto;
    private Challenge testChallenge;
    private Award testAward;
    private RedeemableId testRedeemableId;

    @BeforeEach
    void setUp() {

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
                .challengeName("Test Challenge")
                .awardId(1L)
                .limitDays(45)
                .build();
    }

    @Test
    @DisplayName("Should return all redeemables")
    void shouldReturnAllRedeemables() {
        // Given
        List<Redeemable> redeemables = Arrays.asList(testRedeemable, createAnotherRedeemable());
        when(redeemableRepository.findAll()).thenReturn(redeemables);

        // When
        List<Redeemable> result = redeemableService.getAllRedeemables();

        // Then
        assertEquals(2, result.size());
        assertEquals("Test Challenge", result.get(0).getId().getChallengeName());
        assertEquals("Another Challenge", result.get(1).getId().getChallengeName());
        verify(redeemableRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no redeemables exist")
    void shouldReturnEmptyListWhenNoRedeemablesExist() {
        // Given
        when(redeemableRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Redeemable> result = redeemableService.getAllRedeemables();

        // Then
        assertTrue(result.isEmpty());
        verify(redeemableRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get redeemable by id successfully")
    void shouldGetRedeemableByIdSuccessfully() {
        // Given
        when(redeemableRepository.findById(any(RedeemableId.class))).thenReturn(Optional.of(testRedeemable));

        // When
        Redeemable result = redeemableService.getRedeemableById("Test Challenge", 1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Challenge", result.getId().getChallengeName());
        assertEquals(1L, result.getId().getAwardId());
        assertEquals(30, result.getLimitDays());
        verify(redeemableRepository, times(1)).findById(any(RedeemableId.class));
    }

    @Test
    @DisplayName("Should throw RedeemableNotFoundException when redeemable not found")
    void shouldThrowRedeemableNotFoundExceptionWhenRedeemableNotFound() {
        // Given
        when(redeemableRepository.findById(any(RedeemableId.class))).thenReturn(Optional.empty());

        // When & Then
        RedeemableNotFoundException exception = assertThrows(
            RedeemableNotFoundException.class,
            () -> redeemableService.getRedeemableById("Nonexistent Challenge", 999L)
        );

        assertNotNull(exception);
        verify(redeemableRepository, times(1)).findById(any(RedeemableId.class));
    }

    @Test
    @DisplayName("Should create redeemables to challenge successfully")
    void shouldCreateRedeemablesToChallengeSuccessfully() {
        // Given
        List<RedeemableDto> redeemableDtos = Arrays.asList(
                RedeemableDto.builder().challengeName("Test Challenge").awardId(1L).limitDays(30).build(),
                RedeemableDto.builder().challengeName("Test Challenge").awardId(2L).limitDays(60).build()
        );

        when(challengeService.getChallengeByName("Test Challenge")).thenReturn(testChallenge);
        when(awardService.getAwardById(1L)).thenReturn(testAward);
        when(awardService.getAwardById(2L)).thenReturn(createAnotherAward());
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(testRedeemable);
        when(redeemableRepository.saveAll(anyList())).thenReturn(Arrays.asList(testRedeemable, createAnotherRedeemable()));

        // When
        List<Redeemable> result = redeemableService.createRedeemablesToChallenge(redeemableDtos);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(awardService, times(2)).getAwardById(any());
        verify(redeemableRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should create single redeemable to challenge successfully")
    void shouldCreateSingleRedeemableToChallenge() {
        // Given
        when(challengeService.getChallengeByName("Test Challenge")).thenReturn(testChallenge);
        when(awardService.getAwardById(1L)).thenReturn(testAward);
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(testRedeemable);

        // When
        Redeemable result = redeemableService.createRedeemableToChallenge(testRedeemableDto);

        // Then
        assertNotNull(result);
        assertEquals("Test Challenge", result.getId().getChallengeName());
        assertEquals(1L, result.getId().getAwardId());
        verify(awardService, times(1)).getAwardById(1L);
        verify(redeemableRepository, times(1)).save(any(Redeemable.class));
    }

    @Test
    @DisplayName("Should update redeemable successfully")
    void shouldUpdateRedeemableSuccessfully() {
        // Given
        when(redeemableRepository.findById(any(RedeemableId.class))).thenReturn(Optional.of(testRedeemable));
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(testRedeemable);

        RedeemableDto updateDto = RedeemableDto.builder()
                .challengeName("Test Challenge")
                .awardId(1L)
                .limitDays(90)
                .build();

        // When
        Redeemable result = redeemableService.updateRedeemable("Test Challenge", 1L, updateDto);

        // Then
        assertNotNull(result);
        verify(redeemableRepository, times(1)).findById(any(RedeemableId.class));
        verify(redeemableRepository, times(1)).save(any(Redeemable.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent redeemable")
    void shouldThrowExceptionWhenUpdatingNonExistentRedeemable() {
        // Given
        when(redeemableRepository.findById(any(RedeemableId.class))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RedeemableNotFoundException.class,
            () -> redeemableService.updateRedeemable("Nonexistent Challenge", 999L, testRedeemableDto));

        verify(redeemableRepository, times(1)).findById(any(RedeemableId.class));
        verify(redeemableRepository, never()).save(any(Redeemable.class));
    }

    @Test
    @DisplayName("Should delete redeemable successfully")
    void shouldDeleteRedeemableSuccessfully() {
        // Given
        when(redeemableRepository.findById(any(RedeemableId.class))).thenReturn(Optional.of(testRedeemable));
        doNothing().when(redeemableRepository).delete(testRedeemable);

        // When
        redeemableService.deleteRedeemable("Test Challenge", 1L);

        // Then
        verify(redeemableRepository, times(1)).findById(any(RedeemableId.class));
        verify(redeemableRepository, times(1)).delete(testRedeemable);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent redeemable")
    void shouldThrowExceptionWhenDeletingNonExistentRedeemable() {
        // Given
        when(redeemableRepository.findById(any(RedeemableId.class))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RedeemableNotFoundException.class,
            () -> redeemableService.deleteRedeemable("Nonexistent Challenge", 999L));

        verify(redeemableRepository, times(1)).findById(any(RedeemableId.class));
        verify(redeemableRepository, never()).delete(any(Redeemable.class));
    }

    @Test
    @DisplayName("Should handle empty redeemable list creation")
    void shouldHandleEmptyRedeemableListCreation() {
        // Given
        List<RedeemableDto> emptyList = Arrays.asList();
        when(redeemableRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        // When
        List<Redeemable> result = redeemableService.createRedeemablesToChallenge(emptyList);

        // Then
        assertTrue(result.isEmpty());
        verify(redeemableRepository, times(1)).saveAll(anyList());
        verify(awardService, never()).getAwardById(any());
    }

    @Test
    @DisplayName("Should create redeemable with correct composite key")
    void shouldCreateRedeemableWithCorrectCompositeKey() {
        // Given
        when(challengeService.getChallengeByName("Test Challenge")).thenReturn(testChallenge);
        when(awardService.getAwardById(1L)).thenReturn(testAward);
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(testRedeemable);

        // When
        redeemableService.createRedeemableToChallenge(testRedeemableDto);

        // Then
        verify(redeemableRepository).save(argThat(redeemable -> {
            RedeemableId id = redeemable.getId();
            return id != null &&
                   id.getChallengeName().equals("Test Challenge") &&
                   id.getAwardId().equals(1L) &&
                   redeemable.getChallenge().equals(testChallenge) &&
                   redeemable.getAward().equals(testAward) &&
                   redeemable.getLimitDays().equals(45);
        }));
    }

    @Test
    @DisplayName("Should update only limit days field")
    void shouldUpdateOnlyLimitDaysField() {
        // Given
        Redeemable existingRedeemable = Redeemable.builder()
                .id(testRedeemableId)
                .challenge(testChallenge)
                .award(testAward)
                .limitDays(30)
                .build();

        when(redeemableRepository.findById(any(RedeemableId.class))).thenReturn(Optional.of(existingRedeemable));
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(existingRedeemable);

        RedeemableDto updateDto = RedeemableDto.builder()
                .challengeName("Test Challenge")
                .awardId(1L)
                .limitDays(100)
                .build();

        // When
        redeemableService.updateRedeemable("Test Challenge", 1L, updateDto);

        // Then
        verify(redeemableRepository).save(argThat(redeemable -> 
            redeemable.getLimitDays().equals(100) &&
            redeemable.getId().equals(testRedeemableId) &&
            redeemable.getChallenge().equals(testChallenge) &&
            redeemable.getAward().equals(testAward)
        ));
    }

    @Test
    @DisplayName("Should handle null limit days in DTO")
    void shouldHandleNullLimitDaysInDto() {
        // Given
        RedeemableDto dtoWithNullLimit = RedeemableDto.builder()
                .challengeName("Test Challenge")
                .awardId(1L)
                .limitDays(null)
                .build();

        when(challengeService.getChallengeByName("Test Challenge")).thenReturn(testChallenge);
        when(awardService.getAwardById(1L)).thenReturn(testAward);
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(testRedeemable);

        // When
        redeemableService.createRedeemableToChallenge(dtoWithNullLimit);

        // Then
        verify(redeemableRepository).save(argThat(redeemable -> 
            redeemable.getLimitDays() == null
        ));
    }

    @Test
    @DisplayName("Should handle zero limit days")
    void shouldHandleZeroLimitDays() {
        // Given
        RedeemableDto dtoWithZeroLimit = RedeemableDto.builder()
                .challengeName("Test Challenge")
                .awardId(1L)
                .limitDays(0)
                .build();

        when(challengeService.getChallengeByName("Test Challenge")).thenReturn(testChallenge);
        when(awardService.getAwardById(1L)).thenReturn(testAward);
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(testRedeemable);

        // When
        redeemableService.createRedeemableToChallenge(dtoWithZeroLimit);

        // Then
        verify(redeemableRepository).save(argThat(redeemable -> 
            redeemable.getLimitDays().equals(0)
        ));
    }

    @Test
    @DisplayName("Should handle very large limit days")
    void shouldHandleVeryLargeLimitDays() {
        // Given
        RedeemableDto dtoWithLargeLimit = RedeemableDto.builder()
                .challengeName("Test Challenge")
                .awardId(1L)
                .limitDays(Integer.MAX_VALUE)
                .build();

        when(challengeService.getChallengeByName("Test Challenge")).thenReturn(testChallenge);
        when(awardService.getAwardById(1L)).thenReturn(testAward);
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(testRedeemable);

        // When
        redeemableService.createRedeemableToChallenge(dtoWithLargeLimit);

        // Then
        verify(redeemableRepository).save(argThat(redeemable -> 
            redeemable.getLimitDays().equals(Integer.MAX_VALUE)
        ));
    }

    @Test
    @DisplayName("Should verify correct redeemable id construction")
    void shouldVerifyCorrectRedeemableIdConstruction() {
        // Given
        String challengeName = "Special Challenge";
        Long awardId = 123L;
        
        Redeemable specialRedeemable = Redeemable.builder()
                .id(RedeemableId.builder()
                        .challengeName(challengeName)
                        .awardId(awardId)
                        .build())
                .challenge(testChallenge)
                .award(testAward)
                .limitDays(10)
                .build();
        
        when(redeemableRepository.findById(any(RedeemableId.class))).thenReturn(Optional.of(specialRedeemable));

        // When
        redeemableService.getRedeemableById(challengeName, awardId);

        // Then
        verify(redeemableRepository).findById(argThat(id -> 
            id.getChallengeName().equals(challengeName) &&
            id.getAwardId().equals(awardId)
        ));
    }

    @Test
    @DisplayName("Should handle challenge with special characters in name")
    void shouldHandleChallengeWithSpecialCharactersInName() {
        // Given
        Challenge specialChallenge = Challenge.builder()
                .name("Challenge-With_Special.Chars@123")
                .description("Special Challenge")
                .build();

        RedeemableDto dto = RedeemableDto.builder()
                .challengeName("Challenge-With_Special.Chars@123")
                .awardId(1L)
                .limitDays(15)
                .build();

        when(challengeService.getChallengeByName("Challenge-With_Special.Chars@123")).thenReturn(specialChallenge);
        when(awardService.getAwardById(1L)).thenReturn(testAward);
        when(redeemableRepository.save(any(Redeemable.class))).thenReturn(testRedeemable);

        // When
        redeemableService.createRedeemableToChallenge(dto);

        // Then
        verify(redeemableRepository).save(argThat(redeemable -> 
            redeemable.getId().getChallengeName().equals("Challenge-With_Special.Chars@123")
        ));
    }

    private Redeemable createAnotherRedeemable() {
        Challenge anotherChallenge = Challenge.builder()
                .name("Another Challenge")
                .description("Another Description")
                .build();

        Award anotherAward = createAnotherAward();

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
