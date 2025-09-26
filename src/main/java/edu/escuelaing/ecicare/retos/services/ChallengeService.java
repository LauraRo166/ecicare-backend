package edu.escuelaing.ecicare.retos.services;

import edu.escuelaing.ecicare.premios.models.dto.AwardDto;
import edu.escuelaing.ecicare.premios.models.dto.AwardResponse;
import edu.escuelaing.ecicare.retos.models.dto.ChallengeDTO;
import edu.escuelaing.ecicare.retos.models.dto.ChallengeResponse;
import edu.escuelaing.ecicare.retos.models.entity.Module;
import edu.escuelaing.ecicare.retos.repositories.ModuleRepository;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.retos.models.entity.*;
import edu.escuelaing.ecicare.retos.repositories.ChallengeRepository;
import edu.escuelaing.ecicare.usuarios.repositories.UserEcicareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Service class that provides business logic for managing {@link Challenge} entities.
 *
 * This service interacts with the {@link ChallengeRepository} to handle
 * creation, retrieval, updating, and deletion of challenges, as well as
 * user registration for specific challenges.
 *
 * It is annotated with {@link Service} to indicate that it belongs to
 * the service layer of the application. The {@link RequiredArgsConstructor}
 * Lombok annotation generates a constructor for injecting the repository dependency.
 *
 * @author Byte Programming
 */
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserEcicareRepository userEcicareRepository;
    private final ModuleRepository moduleRepository;

    /**
     * Creates and saves a new challenge in the repository.
     *
     * @param challengeDto the {@link Challenge} to be created
     */
    public ChallengeDTO createChallenge(ChallengeDTO challengeDto) {
        Module module = moduleRepository.findById(challengeDto.getModuleName())
                .orElseThrow(() -> new RuntimeException("Module not found"));

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
        return challengeToDTO(challenge);
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
    public ChallengeDTO updateChallenge(ChallengeDTO challengeDto) {
        Challenge oldChallenge = getChallengeByName(challengeDto.getName());
        if (oldChallenge != null) {
            if (!Objects.equals(challengeDto.getDescription(), "")) {
                oldChallenge.setDescription(challengeDto.getDescription());
            }
            if (!Objects.equals(challengeDto.getImageUrl(), "")) {
                oldChallenge.setImageUrl(challengeDto.getImageUrl());
            }
            if (!Objects.equals(challengeDto.getPhrase(), "")) {
                oldChallenge.setPhrase(challengeDto.getPhrase());
            }
            if (!Objects.equals(challengeDto.getTips(), null)) {
                oldChallenge.setTips(challengeDto.getTips());
            }
            if (!Objects.equals(challengeDto.getGoals(), null)) {
                oldChallenge.setGoals(challengeDto.getGoals());
            }
            if (challengeDto.getModuleName() != null) {
                Module module = moduleRepository.findById(challengeDto.getModuleName()).orElse(null);
                if(module != null) {
                    oldChallenge.setModule(module);
                }
            }
            challengeRepository.save(oldChallenge);
        }
        return challengeToDTO(oldChallenge);
    }

    public static ChallengeDTO challengeToDTO(Challenge challenge) {
        if (challenge == null) {
            return null;
        }

        ChallengeDTO dto = new ChallengeDTO();
        dto.setName(challenge.getName());
        dto.setDescription(challenge.getDescription());
        dto.setImageUrl(challenge.getImageUrl());
        dto.setPhrase(challenge.getPhrase());
        dto.setTips(challenge.getTips());
        dto.setDuration(challenge.getDuration());
        dto.setGoals(challenge.getGoals());

        // Evitamos traer todo el objeto Module, solo el nombre
        if (challenge.getModule() != null) {
            dto.setModuleName(challenge.getModule().getName());
        }

        return dto;
    }

    public static ChallengeResponse challengeToResponseDTO(Challenge challenge) {
        if (challenge == null) {
            return null;
        }

        List<AwardResponse> redeeemables = challenge.getRedeemables()
                .stream()
                .map(r -> new AwardResponse(r.getAward().getName(),
                        r.getAward().getDescription(),
                        r.getLimitDays(),
                        r.getAward().getImageUrl()))
                .toList();

        ChallengeResponse dto = new ChallengeResponse();
        dto.setName(challenge.getName());
        dto.setDescription(challenge.getDescription());
        dto.setImageUrl(challenge.getImageUrl());
        dto.setPhrase(challenge.getPhrase());
        dto.setTips(challenge.getTips());
        dto.setDuration(challenge.getDuration());
        dto.setGoals(challenge.getGoals());
        dto.setRedeemables(redeeemables);

        if (challenge.getModule() != null) {
            dto.setModuleName(challenge.getModule().getName());
        }

        return dto;
    }


    /**
     * Deletes a challenge by its unique name.
     *
     * @param name the name of the challenge to delete
     */
    public void deleteChallenge(String name) {
        challengeRepository.deleteById(name);
    }

    /**
     * Registers a user to a specific challenge by adding them
     * to the list of registered participants.
     *
     * @param userEmail the {@link UserEcicare} to be added
     * @param name the name of the challenge
     */
    public ChallengeDTO addUserByEmail(String userEmail, String name) {
        Challenge challenge = getChallengeByName(name);
        List<UserEcicare> registered = challenge.getRegistered();
        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userEmail));
        if (!registered.contains(user)) {
            registered.add(user);
            challenge.setRegistered(registered);
            challengeRepository.save(challenge);
        }
        return challengeToDTO(challenge);
    }

    /**
     * Confirms a user to a specific challenge by adding them, removing from registered list.
     * to the list of confirms participants.
     *
     * @param userEmail the {@link UserEcicare} to be added
     * @param name the name of the challenge
     */
    public ChallengeDTO confirmUserByEmail(String userEmail, String name) {
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
        return challengeToDTO(challenge);
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
        return challengeRepository.findByRegistered(user)
                .stream()
                .map(ChallengeService::challengeToResponseDTO)
                .toList();
    }
}
