package edu.escuelaing.ecicare.retos.services;

import edu.escuelaing.ecicare.retos.repositories.ModuleRepository;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.retos.models.Challenge;
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
     * @param challenge the {@link Challenge} to be created
     */
    public void createChallenge(Challenge challenge) {
        challengeRepository.save(challenge);
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
     * @param name      the name of the challenge to update
     * @param challenge the {@link Challenge} containing updated values
     */
    public Challenge updateChallenge(String name, Challenge challenge) {
        Challenge oldChallenge = getChallengeByName(name);
        if (oldChallenge != null) {
            if (!Objects.equals(challenge.getPhrase(), "")) {
                oldChallenge.setPhrase(challenge.getPhrase());
            }
            if (!Objects.equals(challenge.getReward(), "")){
                oldChallenge.setReward(challenge.getReward());
            }
            if (challenge.getModule() != null) {
                oldChallenge.setModule(challenge.getModule());
            }
            challengeRepository.save(oldChallenge);
        }
        return oldChallenge;
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
     * Retrieves all challenges in which a specific user is registered.
     *
     * @param user the user whose challenges should be retrieved
     * @return a list of {@link Challenge} entities containing the user
     */
    public List<Challenge> getChallengesByUser(UserEcicare user) {
        return challengeRepository.findByRegistered(user);
    }
}
