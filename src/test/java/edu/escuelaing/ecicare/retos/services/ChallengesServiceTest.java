package edu.escuelaing.ecicare.retos.services;

import edu.escuelaing.ecicare.retos.models.Challenge;
import edu.escuelaing.ecicare.retos.repositories.ChallengeRepository;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private Challenge createTestChallenge(String name, String healthModule) {
        return Challenge.builder()
                .name(name)
                .description("A test challenge description.")
                .phrase("Go for it!")
                .duration(LocalDateTime.now().plusDays(10))
                .reward("100 XP")
                .healthModule(healthModule)
                .registered(new ArrayList<>())
                .tips(List.of("Stay hydrated", "Warm-up first"))
                .goals(List.of("Complete the main task", "Track your progress"))
                .ratings(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should save challenge when creating a new one")
    void createChallenge_whenCalledWithChallenge_shouldSaveChallenge() {
        // Arrange
        Challenge challenge = createTestChallenge("New Fitness Challenge", "Fitness");

        // Act
        challengeService.createChallenge(challenge);

        // Assert
        verify(challengeRepository, times(1)).save(challenge);
    }

    @Test
    @DisplayName("Should return all challenges when they exist")
    void getAllChallenges_whenChallengesExist_shouldReturnChallengeList() {
        // Arrange
        Challenge challenge1 = createTestChallenge("Challenge 1", "Nutrition");
        Challenge challenge2 = createTestChallenge("Challenge 2", "Mental Health");
        when(challengeRepository.findAll()).thenReturn(List.of(challenge1, challenge2));

        // Act
        List<Challenge> result = challengeService.getAllChallenges();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(challenge1, challenge2);
        verify(challengeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return an empty list when no challenges exist")
    void getAllChallenges_whenNoChallengesExist_shouldReturnEmptyList() {
        // Arrange
        when(challengeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Challenge> result = challengeService.getAllChallenges();

        // Assert
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should return challenge when found by name")
    void getChallengeByName_whenChallengeExists_shouldReturnChallenge() {
        // Arrange
        String challengeName = "Fitness Challenge";
        Challenge expectedChallenge = createTestChallenge(challengeName, "Exercise");
        when(challengeRepository.findByName(challengeName)).thenReturn(expectedChallenge);

        // Act
        Challenge result = challengeService.getChallengeByName(challengeName);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(challengeName);
    }

    @Test
    @DisplayName("Should return null when no challenge is found by name")
    void getChallengeByName_whenChallengeDoesNotExist_shouldReturnNull() {
        // Arrange
        String challengeName = "NonExistent Challenge";
        when(challengeRepository.findByName(challengeName)).thenReturn(null);

        // Act
        Challenge result = challengeService.getChallengeByName(challengeName);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should update challenge fields when challenge exists")
    void updateChallenge_whenChallengeExists_shouldUpdateAndSaveChanges() {
        // Arrange
        String originalName = "Original Challenge";
        Challenge oldChallenge = createTestChallenge(originalName, "Old Module");
        oldChallenge.setPhrase("Old Phrase");
        oldChallenge.setReward("Old Reward");

        Challenge updates = Challenge.builder()
                .phrase("New Phrase")
                .reward("New Reward")
                .healthModule("New Module")
                .build();

        when(challengeRepository.findByName(originalName)).thenReturn(oldChallenge);

        // Act
        challengeService.updateChallenge(originalName, updates);

        // Assert
        verify(challengeRepository, times(1)).findByName(originalName);
        verify(challengeRepository, times(1)).save(oldChallenge);
        assertThat(oldChallenge.getPhrase()).isEqualTo("New Phrase");
        assertThat(oldChallenge.getReward()).isEqualTo("New Reward");
        assertThat(oldChallenge.getHealthModule()).isEqualTo("New Module");
    }

    @Test
    @DisplayName("Should not update fields with empty strings")
    void updateChallenge_whenUpdateDataHasEmptyStrings_shouldIgnoreEmptyFields() {
        // Arrange
        String originalName = "Original Challenge";
        Challenge oldChallenge = createTestChallenge(originalName, "Old Module");
        oldChallenge.setPhrase("Old Phrase");
        oldChallenge.setReward("Old Reward");

        Challenge updates = Challenge.builder()
                .phrase("") // This should be ignored
                .reward("New Reward")
                .healthModule("") // This should be ignored
                .build();

        when(challengeRepository.findByName(originalName)).thenReturn(oldChallenge);

        // Act
        challengeService.updateChallenge(originalName, updates);

        // Assert
        verify(challengeRepository, times(1)).save(oldChallenge);
        assertThat(oldChallenge.getPhrase()).isEqualTo("Old Phrase"); // Unchanged
        assertThat(oldChallenge.getReward()).isEqualTo("New Reward"); // Changed
        assertThat(oldChallenge.getHealthModule()).isEqualTo("Old Module"); // Unchanged
    }

    @Test
    @DisplayName("Should not perform update when challenge does not exist")
    void updateChallenge_whenChallengeDoesNotExist_shouldNotCallSave() {
        // Arrange
        String nonExistentName = "Ghost Challenge";
        Challenge updates = Challenge.builder().phrase("New phrase").build();
        when(challengeRepository.findByName(nonExistentName)).thenReturn(null);

        // Act
        challengeService.updateChallenge(nonExistentName, updates);

        // Assert
        verify(challengeRepository, times(1)).findByName(nonExistentName);
        verify(challengeRepository, never()).save(any(Challenge.class));
    }

    @Test
    @DisplayName("Should call deleteById when deleting a challenge")
    void deleteChallenge_whenCalledWithName_shouldCallRepositoryDelete() {
        // Arrange
        String challengeName = "Challenge To Delete";
        doNothing().when(challengeRepository).deleteById(challengeName);

        // Act
        challengeService.deleteChallenge(challengeName);

        // Assert
        verify(challengeRepository, times(1)).deleteById(challengeName);
    }

    @Test
    @DisplayName("Should add user to challenge when user is not already registered")
    void addUserByEmail_whenUserNotRegistered_shouldAddUserAndSave() {
        // Arrange
        String challengeName = "Yoga Challenge";
        UserEcicare user = new UserEcicare(); // Asumimos que UserEcicare existe
        user.setEmail("test@user.com");


        Challenge challenge = createTestChallenge(challengeName, "Wellness");

        when(challengeRepository.findByName(challengeName)).thenReturn(challenge);

        // Act
        challengeService.addUserByEmail(user.getEmail(), challengeName);

        // Assert
        assertThat(challenge.getRegistered()).hasSize(1).contains(user);
        verify(challengeRepository, times(1)).save(challenge);
    }

    @Test
    @DisplayName("Should not add user to challenge when user is already registered")
    void addUserByEmail_whenUserAlreadyRegistered_shouldNotAddUserAndNotSave() {
        // Arrange
        String challengeName = "Yoga Challenge";
        UserEcicare user = new UserEcicare();
        user.setEmail("test@user.com");

        Challenge challenge = createTestChallenge(challengeName, "Wellness");
        challenge.getRegistered().add(user); // Pre-register the user

        when(challengeRepository.findByName(challengeName)).thenReturn(challenge);

        // Act
        challengeService.addUserByEmail(user.getEmail(), challengeName);

        // Assert
        assertThat(challenge.getRegistered()).hasSize(1);
        verify(challengeRepository, never()).save(challenge);
    }

    @Test
    @DisplayName("Should return challenges for a given user")
    void getChallengesByUser_whenUserIsRegistered_shouldReturnChallengeList() {
        // Arrange
        UserEcicare user = new UserEcicare();
        user.setEmail("active@user.com");

        Challenge challenge1 = createTestChallenge("Challenge 1", "Nutrition");
        Challenge challenge2 = createTestChallenge("Challenge 2", "Fitness");
        List<Challenge> expectedChallenges = List.of(challenge1, challenge2);

        when(challengeRepository.findByRegistered(user)).thenReturn(expectedChallenges);

        // Act
        List<Challenge> result = challengeService.getChallengesByUser(user);

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactlyElementsOf(expectedChallenges);
    }
}