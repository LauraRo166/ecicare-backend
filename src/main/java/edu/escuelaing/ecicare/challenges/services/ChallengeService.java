package edu.escuelaing.ecicare.challenges.services;

import edu.escuelaing.ecicare.awards.models.dto.AwardDto;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.repositories.RedeemableRepository;
import edu.escuelaing.ecicare.challenges.models.dto.ChallengeDTO;
import edu.escuelaing.ecicare.challenges.models.dto.ChallengeResponse;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleWithChallengesDTO;
import edu.escuelaing.ecicare.challenges.models.entity.Module;
import edu.escuelaing.ecicare.challenges.repositories.ModuleRepository;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.repositories.ChallengeRepository;
import edu.escuelaing.ecicare.users.repositories.UserEcicareRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class that provides business logic for managing {@link Challenge}
 * entities.
 *
 * This service interacts with the {@link ChallengeRepository} to handle
 * creation, retrieval, updating, and deletion of challenges, as well as
 * user registration for specific challenges.
 *
 * It is annotated with {@link Service} to indicate that it belongs to
 * the service layer of the application. The {@link RequiredArgsConstructor}
 * Lombok annotation generates a constructor for injecting the repository
 * dependency.
 *
 * @author Byte Programming
 */
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserEcicareRepository userEcicareRepository;
    private final ModuleRepository moduleRepository;
    private final RedeemableRepository redeemableRepository;

    /**
     * Creates and saves a new challenge in the repository.
     *
     * @param challengeDto the {@link Challenge} to be created
     */
    public Challenge createChallenge(ChallengeDTO challengeDto) {
        Module module = moduleRepository.findById(challengeDto.getModuleName())
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
        Challenge challenge = Challenge.builder()
                .name(challengeDto.getName())
                .description(challengeDto.getDescription())
                .imageUrl(challengeDto.getImageUrl())
                .phrase(challengeDto.getPhrase())
                .tips(challengeDto.getTips())
                .duration(challengeDto.getDuration())
                .goals(challengeDto.getGoals())
                .module(module)
                .build();
        challengeRepository.save(challenge);
        return challenge;
    }

    /**
     * Retrieves all challenges from the repository.
     *
     * @return a list of all {@link Challenge} entities
     */
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    /**
     * Retrieves all challenges from the repository with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a {@link Page} of {@link Challenge} entities
     */
    public Page<Challenge> getAllChallengesPaginated(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return challengeRepository.findAll(pageable);
    }

    /**
     * Searches challenges by name and groups them by their modules.
     * 
     * @param name the search term to match in challenge names (required)
     * @return a {@link List} of {@link ModuleWithChallengesDTO} containing modules
     *         with their matching challenges
     * @throws IllegalArgumentException if name is null or empty
     */
    public List<ModuleWithChallengesDTO> searchChallengesGroupedByModule(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }

        List<Challenge> matchingChallenges = challengeRepository
                .findByNameContainingIgnoreCaseOrderByNameAsc(name.trim());

        Map<String, List<Challenge>> challengesByModule = matchingChallenges.stream()
                .filter(challenge -> challenge.getModule() != null)
                .collect(Collectors.groupingBy(challenge -> challenge.getModule().getName()));

        return challengesByModule.entrySet().stream()
                .map(entry -> {
                    List<Challenge> challenges = entry.getValue();

                    var module = challenges.get(0).getModule();

                    return ModuleWithChallengesDTO.builder()
                            .module(module)
                            .challenges(challenges)
                            .totalChallenges(challenges.size())
                            .build();
                })
                .sorted((dto1, dto2) -> dto1.getModule().getName().compareToIgnoreCase(dto2.getModule().getName()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a challenge by its unique name.
     *
     * @param name the name of the challenge
     * @return the {@link Challenge} with the specified name,
     *         or {@code null} if no such challenge exists
     */
    public Challenge getChallengeByName(String name) {
        return challengeRepository.findByName(name);
    }

    /**
     * Gets all the Awards associated with a specific challenge.
     * The search is performed through the Redeemables linked to the challenge.
     *
     * @param challengeName is the unique name of the challenge to query.
     * @return a list of distinct Awards associated with that challenge.
     */
    public List<Award> getAwardsByChallenge(String challengeName) {
        return redeemableRepository.findByChallenge_Name(challengeName).stream()
                .map(Redeemable::getAward)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * Retrieves all challenges with a specific duration.
     *
     * @param duration the duration (end date/time) of the challenge
     * @return a list of {@link Challenge} entities matching the given duration
     *         or {@code null} if no such challenge exists
     */
    public List<Challenge> getChallengeByDuration(LocalDateTime duration) {
        return challengeRepository.findByDuration(duration);
    }

    /**
     * Updates an existing challenge with new values for specific fields:
     * phrase, reward, and health module.
     *
     * @param challengeDto the {@link Challenge} containing updated values
     */
    public Challenge updateChallenge(ChallengeDTO challengeDto) {
        Challenge oldChallenge = challengeRepository.findByName(challengeDto.getName());
        if (oldChallenge == null) {
            throw new RuntimeException("Challenge no encontrado");
        }

        if (challengeDto.getDescription() != null && !challengeDto.getDescription().isBlank()) {
            oldChallenge.setDescription(challengeDto.getDescription());
        }
        if (challengeDto.getImageUrl() != null && !challengeDto.getImageUrl().isBlank()) {
            oldChallenge.setImageUrl(challengeDto.getImageUrl());
        }
        if (challengeDto.getPhrase() != null && !challengeDto.getPhrase().isBlank()) {
            oldChallenge.setPhrase(challengeDto.getPhrase());
        }
        if (challengeDto.getTips() != null) {
            oldChallenge.setTips(challengeDto.getTips());
        }
        if (challengeDto.getGoals() != null) {
            oldChallenge.setGoals(challengeDto.getGoals());
        }
        if (challengeDto.getModuleName() != null && !challengeDto.getModuleName().isBlank()) {
            Module module = moduleRepository.findById(challengeDto.getModuleName())
                    .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
            oldChallenge.setModule(module);
        }

        return challengeRepository.save(oldChallenge);
    }

    /**
     * Deletes a challenge by its unique name, along with its associated redeemables.
     *
     * @param name the name of the challenge to delete
     */
    @Transactional
    public void deleteChallenge(String name) {
        Challenge challenge = challengeRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // Borrar todos los redeemables asociados a este challenge
        redeemableRepository.deleteAllByChallenge(challenge);

        // Ahora sí eliminar el challenge
        challengeRepository.delete(challenge);
    }

    /**
     * Registers a user to a specific challenge by adding them
     * to the list of registered participants.
     *
     * @param userEmail the {@link UserEcicare} to be added
     * @param name      the name of the challenge
     */
    public Challenge addUserByEmail(String userEmail, String name) {
        Challenge challenge = getChallengeByName(name);
        List<UserEcicare> registered = challenge.getRegistered();
        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userEmail));
        if (!registered.contains(user)) {
            registered.add(user);
            challenge.setRegistered(registered);
            challengeRepository.save(challenge);
        }
        return challenge;
    }

    /**
     * Confirms a user to a specific challenge by adding them, removing from
     * registered list.
     * to the list of confirms participants.
     *
     * @param userEmail the {@link UserEcicare} to be added
     * @param name      the name of the challenge
     */
    public Challenge confirmUserByEmail(String userEmail, String name) {
        Challenge challenge = getChallengeByName(name);
        List<UserEcicare> registered = challenge.getRegistered();
        List<UserEcicare> confirmed = challenge.getConfirmed();
        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userEmail));
        if (registered.contains(user)) {
            registered.remove(user);
            confirmed.add(user);
            challenge.setRegistered(registered);
            challenge.setConfirmed(confirmed);
            challengeRepository.save(challenge);
        }
        return challenge;
    }

    /**
     * Retrieves all challenges in which a specific user is registered.
     *
     * @param userEmail the user whose challenges should be retrieved
     * @return a list of {@link Challenge} entities containing the user
     */
    public List<ChallengeResponse> getChallengesByUserEmail(String userEmail) {
        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userEmail));
        List<Challenge> challenges = challengeRepository.findByRegistered(user);
        return challenges.stream()
                .map(ChallengeService::challengeToResponse)
                .toList();
    }
    public static ChallengeResponse challengeToResponse(Challenge challenge){
        List<AwardDto> redeemables = challenge.getRedeemables() != null ? challenge.getRedeemables()
                .stream()
                .map(r -> new AwardDto(r.getAward().getName(), r.getAward().getDescription(),
                        r.getAward().getInStock(), r.getAward().getImageUrl()))
                .toList(): null;
        return new ChallengeResponse(challenge.getName(), challenge.getDescription(), challenge.getImageUrl(),
                challenge.getPhrase(), challenge.getTips(), challenge.getDuration(),challenge.getGoals(), challenge.getModule().getName(), redeemables);
    }
    /**
     * Retrieves all challenges completes in which a specific user is registered.
     *
     * @param userEmail the user whose challenges should be retrieved
     * @return a list of {@link Challenge} entities
     */
    public List<ChallengeResponse> getChallengesCompletedByUserEmail(String userEmail) {
        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userEmail));
        return user.getChallengesConfirmed()
                .stream()
                .map(ChallengeService::challengeToResponse)
                .toList();
    }
}
