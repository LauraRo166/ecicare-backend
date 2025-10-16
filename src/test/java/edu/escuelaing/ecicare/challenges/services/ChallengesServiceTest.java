package edu.escuelaing.ecicare.challenges.services;

import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.models.entity.RedeemableId;
import edu.escuelaing.ecicare.challenges.models.dto.ChallengeDTO;
import edu.escuelaing.ecicare.challenges.models.dto.ChallengeResponse;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleWithChallengesDTO;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.models.entity.Module;
import edu.escuelaing.ecicare.challenges.repositories.ChallengeRepository;
import edu.escuelaing.ecicare.challenges.repositories.ModuleRepository;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.users.repositories.UserEcicareRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

        @Mock
        private ChallengeRepository challengeRepository;

        @Mock
        private ModuleRepository moduleRepository;

        @Mock
        private UserEcicareRepository userEcicareRepository;

        @InjectMocks
        private ChallengeService challengeService;

        private ChallengeDTO createTestChallengeDto(String name, String module) {
                return ChallengeDTO.builder()
                                .name(name)
                                .description("A test challenge description.")
                                .imageUrl("imageUrl")
                                .phrase("Go for it!")
                                .duration(LocalDateTime.now().plusDays(10))
                                .moduleName(module)
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
        @DisplayName("Should save challenge when creating a new one")
        void createChallenge_whenCalledWithChallenge_shouldSaveChallenge() {
                // Arrange
                ChallengeDTO challengeDto = createTestChallengeDto("New Fitness Challenge", "Fitness");
                Module module = new Module("Fitness");
                when(moduleRepository.findById("Fitness")).thenReturn(Optional.of(module));

                // Act
                challengeService.createChallenge(challengeDto);

                // Assert
                verify(challengeRepository).save(
                                argThat(savedChallenge -> savedChallenge.getName().equals("New Fitness Challenge") &&
                                                savedChallenge.getModule().equals(module)));
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
                Module module = new Module("Module1", "Desc", null, null);
                Challenge existingChallenge = new Challenge("Challenge1", "Old description", null, null, null, null,
                                null, null, null, null, module, null);

                ChallengeDTO dto = new ChallengeDTO();
                dto.setName("Challenge1");
                dto.setDescription("A test challenge description."); // <- lo que realmente mandas
                dto.setModuleName("Module1");

                when(challengeRepository.findByName("Challenge1")).thenReturn(existingChallenge);
                when(moduleRepository.findById("Module1")).thenReturn(Optional.of(module));
                when(challengeRepository.save(any(Challenge.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                Challenge updated = challengeService.updateChallenge(dto);

                // Assert
                assertEquals("A test challenge description.", updated.getDescription());
                verify(challengeRepository).save(argThat(c -> c.getName().equals("Challenge1") &&
                                c.getDescription().equals("A test challenge description.") && // corregido
                                c.getModule().equals(module)));
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
                                .moduleName("") // This should be ignored
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
                ChallengeDTO updates = ChallengeDTO.builder()
                                .name(nonExistentName)
                                .phrase("New phrase")
                                .build();

                // Solo necesitas stubear la búsqueda por nombre, porque es lo que usa tu lógica
                when(challengeRepository.findByName(nonExistentName)).thenReturn(null);

                // Act
                try {
                        challengeService.updateChallenge(updates);
                } catch (RuntimeException e) {
                        // expected -> no existe challenge
                }

                // Assert
                verify(challengeRepository, never()).save(any(Challenge.class));
        }

        // @Test
        // @DisplayName("Should call deleteById when deleting a challenge")
        // void deleteChallenge_whenCalledWithName_shouldCallRepositoryDelete() {
        // Arrange
        // String challengeName = "Challenge To Delete";
        // doNothing().when(challengeRepository).deleteById(challengeName);

        // Act
        // challengeService.deleteChallenge(challengeName);

        // Assert
        // verify(challengeRepository, times(1)).deleteById(challengeName);
        // }

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
                List<ChallengeResponse> expected = expectedChallenges.stream()
                                .map(ChallengeService::challengeToResponse)
                                .toList();
                when(challengeRepository.findByRegistered(user)).thenReturn(expectedChallenges);
                when(userEcicareRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

                // Act
                List<ChallengeResponse> result = challengeService.getChallengesByUserEmail(user.getEmail());

                // Assert
                assertThat(result)
                                .isNotNull()
                                .hasSize(2)
                                .containsExactlyElementsOf(expected);
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

        @Test
        @DisplayName("Should throw exception when creating challenge with missing module")
        void createChallenge_whenModuleNotFound_shouldThrowException() {
                ChallengeDTO dto = ChallengeDTO.builder()
                                .name("Run 5K")
                                .moduleName("Invalid")
                                .build();

                when(moduleRepository.findById("Invalid")).thenReturn(Optional.empty());

                assertThrows(RuntimeException.class, () -> challengeService.createChallenge(dto));
        }

        @Test
        @DisplayName("Should throw exception when search term is null or blank")
        void searchChallengesGroupedByModule_shouldThrowException() {
                assertThrows(IllegalArgumentException.class,
                                () -> challengeService.searchChallengesGroupedByModule(null));
                assertThrows(IllegalArgumentException.class,
                                () -> challengeService.searchChallengesGroupedByModule("   "));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing challenge")
        void updateChallenge_whenChallengeNotFound_shouldThrowException() {
                ChallengeDTO dto = ChallengeDTO.builder().name("Invalid").build();

                when(challengeRepository.findByName("Invalid")).thenReturn(null);

                assertThrows(RuntimeException.class, () -> challengeService.updateChallenge(dto));
        }

        @Test
        @DisplayName("Should throw exception when search term is null or empty")
        void searchChallengesGroupedByModule_whenNameIsInvalid_shouldThrowException() {
                assertThrows(IllegalArgumentException.class,
                                () -> challengeService.searchChallengesGroupedByModule(null));

                assertThrows(IllegalArgumentException.class,
                                () -> challengeService.searchChallengesGroupedByModule("   "));
        }

        @Test
        @DisplayName("Should return grouped challenges by module when search term matches")
        void searchChallengesGroupedByModule_whenValidName_shouldReturnGroupedResults() {
                Module module1 = Module.builder().name("Module A").build();
                Challenge ch1 = Challenge.builder().name("Alpha").module(module1).build();
                Challenge ch2 = Challenge.builder().name("Alpine").module(module1).build();

                when(challengeRepository.findByNameContainingIgnoreCaseOrderByNameAsc("Al"))
                                .thenReturn(List.of(ch1, ch2));

                List<ModuleWithChallengesDTO> result = challengeService.searchChallengesGroupedByModule("Al");

                assertEquals(1, result.size());
                assertEquals("Module A", result.get(0).getModule().getName());
                assertEquals(2, result.get(0).getTotalChallenges());
        }

        @Test
        @DisplayName("Should ignore challenges with null module")
        void searchChallengesGroupedByModule_whenChallengeHasNullModule_shouldExcludeIt() {
                Challenge ch1 = Challenge.builder().name("Test1").module(null).build();

                when(challengeRepository.findByNameContainingIgnoreCaseOrderByNameAsc("Test"))
                                .thenReturn(List.of(ch1));

                List<ModuleWithChallengesDTO> result = challengeService.searchChallengesGroupedByModule("Test");

                assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return results sorted by module name")
        void searchChallengesGroupedByModule_shouldReturnSortedModules() {
                Module module1 = Module.builder().name("Beta").build();
                Module module2 = Module.builder().name("Alpha").build();

                Challenge ch1 = Challenge.builder().name("X").module(module1).build();
                Challenge ch2 = Challenge.builder().name("Y").module(module2).build();

                when(challengeRepository.findByNameContainingIgnoreCaseOrderByNameAsc("X"))
                                .thenReturn(List.of(ch1, ch2));

                List<ModuleWithChallengesDTO> result = challengeService.searchChallengesGroupedByModule("X");

                assertEquals(2, result.size());
                assertEquals("Alpha", result.get(0).getModule().getName());
                assertEquals("Beta", result.get(1).getModule().getName());
        }

        @Test
        void getAllChallengesPaginated_shouldReturnPageOfChallenges() {
                // Arrange
                Challenge challenge = new Challenge();
                Page<Challenge> challengePage = new PageImpl<>(List.of(challenge));
                when(challengeRepository.findAll(any(Pageable.class))).thenReturn(challengePage);

                // Act
                Page<Challenge> result = challengeService.getAllChallengesPaginated(0, 5);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
                verify(challengeRepository).findAll(any(Pageable.class));
        }

        @Test
        void searchChallengesGroupedByModule_shouldReturnGroupedModules() {
                // Arrange
                Module module = Module.builder().name("Module1").build();
                Challenge challenge = Challenge.builder()
                                .name("Challenge1")
                                .module(module)
                                .build();

                when(challengeRepository.findByNameContainingIgnoreCaseOrderByNameAsc("Challenge"))
                                .thenReturn(List.of(challenge));

                // Act
                List<ModuleWithChallengesDTO> result = challengeService.searchChallengesGroupedByModule("Challenge");

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("Module1", result.get(0).getModule().getName());
                assertEquals(1, result.get(0).getTotalChallenges());
        }

        @Test
        void updateChallenge_whenChallengeExists_shouldUpdateFields() {
                // Arrange
                Challenge oldChallenge = Challenge.builder()
                                .name("Challenge1")
                                .description("Old Desc")
                                .build();

                ChallengeDTO dto = new ChallengeDTO();
                dto.setName("Challenge1");
                dto.setDescription("New Desc");

                // OJO: usar el método real que implementaste en el servicio
                when(challengeRepository.findByName("Challenge1"))
                                .thenReturn(oldChallenge);

                when(challengeRepository.save(any(Challenge.class)))
                                .thenAnswer(inv -> inv.getArgument(0));

                // Act
                Challenge result = challengeService.updateChallenge(dto);

                // Assert
                assertEquals("New Desc", result.getDescription());
                verify(challengeRepository).save(result);
        }

        @Test
        void updateChallenge_whenChallengeDoesNotExist_shouldThrowException() {
                ChallengeDTO dto = new ChallengeDTO();
                dto.setName("NonExisting");

                when(challengeRepository.findByName("NonExisting"))
                                .thenReturn(null);

                assertThrows(RuntimeException.class,
                                () -> challengeService.updateChallenge(dto));
        }

        @Test
        void updateChallenge_shouldUpdateAllFields_whenDtoHasValues() {
                // Arrange
                Challenge oldChallenge = Challenge.builder()
                                .name("Challenge1")
                                .description("Old Desc")
                                .imageUrl("old.png")
                                .phrase("Old phrase")
                                .build();

                Module oldModule = new Module();
                oldModule.setName("Module1");

                ChallengeDTO dto = new ChallengeDTO();
                dto.setName("Challenge1");
                dto.setDescription("New Desc");
                dto.setImageUrl("new.png");
                dto.setPhrase("New phrase");
                dto.setTips(new ArrayList<>());
                dto.setGoals(new ArrayList<>());
                dto.setModuleName("Module1");

                when(challengeRepository.findByName("Challenge1"))
                                .thenReturn(oldChallenge);

                when(moduleRepository.findById("Module1"))
                                .thenReturn(Optional.of(oldModule));

                when(challengeRepository.save(any(Challenge.class)))
                                .thenAnswer(inv -> inv.getArgument(0));

                // Act
                Challenge result = challengeService.updateChallenge(dto);

                // Assert
                assertEquals("New Desc", result.getDescription());
                assertEquals("new.png", result.getImageUrl());
                assertEquals("New phrase", result.getPhrase());
                assertEquals(oldModule, result.getModule());
                verify(challengeRepository).save(result);
        }

        @Test
        @DisplayName("Should return confirmed challenges when user exists")
        void shouldReturnConfirmedChallengesWhenUserExists() {
                String userEmail = "test@eci.edu.co";
                UserEcicare user = UserEcicare.builder()
                                .email(userEmail)
                                .challengesConfirmed(Arrays.asList(
                                                Challenge.builder().name("Challenge1").module(Module
                                                                .builder()
                                                                .name("Module1")
                                                                .build())
                                                                .build()))
                                .build();

                when(userEcicareRepository.findByEmail(userEmail))
                                .thenReturn(Optional.of(user));

                List<ChallengeResponse> result = challengeService.getChallengesCompletedByUserEmail(userEmail);
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("Challenge1", result.get(0).name());
        }
}