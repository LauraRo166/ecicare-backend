package edu.escuelaing.ecicare.retos.services;

import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import edu.escuelaing.ecicare.premios.models.entity.RedeemableId;
import edu.escuelaing.ecicare.retos.models.dto.ChallengeDTO;
import edu.escuelaing.ecicare.retos.models.entity.Challenge;
import edu.escuelaing.ecicare.retos.models.entity.Module;
import edu.escuelaing.ecicare.retos.repositories.ChallengeRepository;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.usuarios.repositories.UserEcicareRepository;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;


    @Mock
    private UserEcicareRepository userEcicareRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private ChallengeDTO createTestChallengeDto(String name, Module module) {
        return ChallengeDTO.builder()
                .name(name)
                .description("A test challenge description.")
                .imageUrl("imageUrl")
                .phrase("Go for it!")
                .duration(LocalDateTime.now().plusDays(10))
                .module(module)
                .tips(List.of("Stay hydrated", "Warm-up first"))
                .goals(List.of("Complete the main task", "Track your progress"))
                .build();
    }

    private Challenge createTestChallenge(String name, Module module) {
        return Challenge.builder()
                .name(name)
                .description("A test challenge description.")
                .phrase("Go for it!")
                .duration(LocalDateTime.now().plusDays(10))
                .redeemables(createTestRedeemable(name))
                .module(module)
                .registered(new ArrayList<>())
                .tips(List.of("Stay hydrated", "Warm-up first"))
                .goals(List.of("Complete the main task", "Track your progress"))
                .ratings(new ArrayList<>())
                .build();
    }

    private Set<Redeemable> createTestRedeemable(String name){
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
    @DisplayName("Should save challenge when creating a new one")
    void createChallenge_whenCalledWithChallenge_shouldSaveChallenge() {
        // Arrange
        ChallengeDTO challengeDto = createTestChallengeDto("New Fitness Challenge", new Module("Fitness"));

        // Act
        challengeService.createChallenge(challengeDto);

        // Assert
        verify(challengeRepository).save(argThat(savedChallenge ->
                savedChallenge.getName().equals(challengeDto.getName()) &&
                        savedChallenge.getDescription().equals(challengeDto.getDescription()) &&
                        savedChallenge.getPhrase().equals(challengeDto.getPhrase()) &&
                        savedChallenge.getTips().equals(challengeDto.getTips()) &&
                        savedChallenge.getGoals().equals(challengeDto.getGoals()) &&
                        savedChallenge.getModule().getName().equals("Fitness")
        ));
    }

    @Test
    @DisplayName("Should return all challenges when they exist")
    void getAllChallenges_whenChallengesExist_shouldReturnChallengeList() {
        // Arrange
        Challenge challenge1 = createTestChallenge("Challenge 1", new Module("Nutrition"));
        Challenge challenge2 = createTestChallenge("Challenge 2", new Module("Mental Health"));
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
        Challenge expectedChallenge = createTestChallenge(challengeName, new Module("Excercise"));
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
        Challenge oldChallenge = createTestChallenge(originalName, new Module("Old Module"));
        oldChallenge.setPhrase("Old Phrase");
        Set<Redeemable> redeemables = createTestRedeemable(originalName);
        oldChallenge.setRedeemables(redeemables);

        ChallengeDTO updates = ChallengeDTO.builder()
                .name(originalName)
                .phrase("New Phrase")
                .module(new Module("New Module"))
                .tips(new ArrayList<>(List.of("tip1", "tip2")))
                .goals(new ArrayList<>(List.of("goal1", "goal2")))
                .build();

        when(challengeRepository.findByName(originalName)).thenReturn(oldChallenge);

        // Act
        challengeService.updateChallenge(updates);

        // Assert
        verify(challengeRepository, times(1)).findByName(originalName);
        verify(challengeRepository, times(1)).save(oldChallenge);
        assertThat(oldChallenge.getPhrase()).isEqualTo("New Phrase");
        assertThat(oldChallenge.getRedeemables()).isEqualTo(redeemables);
        assertThat(oldChallenge.getModule()).isEqualTo(new Module("New Module"));
        assertThat(oldChallenge.getTips()).isEqualTo(List.of("tip1", "tip2"));
        assertThat(oldChallenge.getGoals()).isEqualTo(List.of("goal1", "goal2"));
    }

    @Test
    @DisplayName("Should not update fields with empty strings")
    void updateChallenge_whenUpdateDataHasEmptyStrings_shouldIgnoreEmptyFields() {
        // Arrange
        String originalName = "Original Challenge";
        Challenge oldChallenge = createTestChallenge(originalName, new Module("Old Module"));
        oldChallenge.setPhrase("Old Phrase");
        Set<Redeemable> redeemables = createTestRedeemable(originalName);
        oldChallenge.setRedeemables(redeemables);

        ChallengeDTO updates = ChallengeDTO.builder()
                .name(originalName)
                .phrase("new phrase")
                .module(null) // This should be ignored
                .build();

        when(challengeRepository.findByName(originalName)).thenReturn(oldChallenge);

        // Act
        challengeService.updateChallenge(updates);

        // Assert
        verify(challengeRepository, times(1)).save(oldChallenge);
        assertThat(oldChallenge.getPhrase()).isEqualTo("new phrase"); // Changed
        assertThat(oldChallenge.getRedeemables()).isEqualTo(redeemables); // Unchanged
        assertThat(oldChallenge.getModule()).isEqualTo(new Module("Old Module")); // Unchanged
    }

    @Test
    @DisplayName("Should not perform update when challenge does not exist")
    void updateChallenge_whenChallengeDoesNotExist_shouldNotCallSave() {
        // Arrange
        String nonExistentName = "Ghost Challenge";
        ChallengeDTO updates = ChallengeDTO.builder().name(nonExistentName).phrase("New phrase").build();
        when(challengeRepository.findByName(nonExistentName)).thenReturn(null);

        // Act
        challengeService.updateChallenge(updates);

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

        Challenge challenge = createTestChallenge(challengeName, new Module("Wellness"));

        when(challengeRepository.findByName(challengeName)).thenReturn(challenge);
        when(userEcicareRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        Challenge result = challengeService.addUserByEmail(user.getEmail(), challengeName);

        // Assert
        assertThat(result.getRegistered()).hasSize(1).contains(user);
        verify(challengeRepository, times(1)).save(challenge);
    }

    @Test
    @DisplayName("Should not add user to challenge when user is already registered")
    void addUserByEmail_whenUserAlreadyRegistered_shouldNotAddUserAndNotSave() {
        // Arrange
        String challengeName = "Yoga Challenge";
        UserEcicare user = new UserEcicare();
        user.setEmail("test@user.com");

        Challenge challenge = createTestChallenge(challengeName, new Module("Wellness"));
        challenge.getRegistered().add(user); // Pre-register the user

        when(challengeRepository.findByName(challengeName)).thenReturn(challenge);
        when(userEcicareRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        Challenge result = challengeService.addUserByEmail(user.getEmail(), challengeName);

        // Assert
        assertThat(result.getRegistered()).hasSize(1);
        verify(challengeRepository, never()).save(challenge);
    }

    @Test
    @DisplayName("Should return challenges for a given user")
    void getChallengesByUser_whenUserIsRegistered_shouldReturnChallengeList() {
        // Arrange
        UserEcicare user = new UserEcicare();
        user.setEmail("active@user.com");

        Challenge challenge1 = createTestChallenge("Challenge 1", new Module("Nutrition"));
        challenge1.getRegistered().add(user);
        Challenge challenge2 = createTestChallenge("Challenge 2", new Module("Fitness"));
        challenge2.getRegistered().add(user);
        List<Challenge> expectedChallenges = List.of(challenge1, challenge2);

        when(challengeRepository.findByRegistered(user)).thenReturn(expectedChallenges);
        when(userEcicareRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        List<Challenge> result = challengeService.getChallengesByUserEmail(user.getEmail());

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactlyElementsOf(expectedChallenges);
    }

    @Test
    @DisplayName("Should add user by email when not already registered")
    void addUserByEmail_shouldRegisterUser() {
        UserEcicare user = new UserEcicare();
        user.setEmail("test@example.com");

        Challenge challenge = Challenge.builder()
                .name("Challenge1")
                .registered(new ArrayList<>())
                .module(new Module("Nutrition"))
                .build();

        when(challengeRepository.findByName("Challenge1")).thenReturn(challenge);
        when(userEcicareRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(challengeRepository.save(any(Challenge.class))).thenReturn(challenge);

        Challenge result = challengeService.addUserByEmail("test@example.com", "Challenge1");

        assertThat(result.getRegistered()).contains(user);
        verify(challengeRepository).save(challenge);
    }

    @Test
    @DisplayName("Should confirm user by email when already registered")
    void confirmUserByEmail_shouldMoveUserToConfirmed() {
        UserEcicare user = new UserEcicare();
        user.setEmail("test@example.com");

        Challenge challenge = Challenge.builder()
                .name("Challenge1")
                .module(new Module("Nutrition"))
                .registered(new ArrayList<>(List.of(user)))
                .confirmed(new ArrayList<>())
                .build();

        when(challengeRepository.findByName("Challenge1")).thenReturn(challenge);
        when(userEcicareRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(challengeRepository.save(any(Challenge.class))).thenReturn(challenge);

        Challenge result = challengeService.confirmUserByEmail("test@example.com", "Challenge1");

        assertThat(result.getRegistered()).doesNotContain(user);
        assertThat(result.getConfirmed()).contains(user);
        verify(challengeRepository).save(challenge);
    }

    @Test
    @DisplayName("Should return challenges by duration")
    void getChallengeByDuration_shouldReturnList() {
        LocalDateTime duration = LocalDateTime.now().plusDays(7);
        Challenge challenge = Challenge.builder()
                .name("Challenge1")
                .duration(duration)
                .module(new Module("Nutrition"))
                .build();

        when(challengeRepository.findByDuration(duration)).thenReturn(List.of(challenge));

        List<Challenge> result = challengeService.getChallengeByDuration(duration);

        assertThat(result).hasSize(1).contains(challenge);
        verify(challengeRepository).findByDuration(duration);
    }
}