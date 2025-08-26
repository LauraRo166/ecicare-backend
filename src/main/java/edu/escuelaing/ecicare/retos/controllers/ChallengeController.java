package edu.escuelaing.ecicare.retos.controllers;

import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.retos.models.Challenge;
import edu.escuelaing.ecicare.retos.services.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing {@link Challenge} entities.
 *
 * This controller exposes endpoints for creating, retrieving,
 * updating, deleting, and registering users for challenges.
 *
 * All endpoints are prefixed with {@code /challenges}.
 *
 * It delegates business logic to the {@link ChallengeService}.
 *
 * @author Byte Programming
 */
@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    /**
     * Creates a new challenge.
     *
     * @param challenge the {@link Challenge} to be created
     * @return the created challenge
     */
    @PostMapping("/")
    public Challenge createChallenge(@RequestBody Challenge challenge) {
        challengeService.createChallenge(challenge);
        return challenge;
    }

    /**
     * Retrieves all challenges.
     *
     * @return a list of all {@link Challenge} entities
     */
    @GetMapping("/all")
    public List<Challenge> getAllChallenges() {
        return challengeService.getAllChallenges();
    }

    /**
     * Retrieves a challenge by its unique name.
     *
     * @param name the name of the challenge
     * @return the {@link Challenge} with the specified name,
     *         or {@code null} if not found
     */
    @GetMapping("/find/{name}")
    public Challenge getChallengeByName(@PathVariable String name) {
        return challengeService.getChallengeByName(name);
    }

    /**
     * Retrieves challenges with a specific duration.
     *
     * @param duration the duration (end date/time) of the challenge
     * @return a list of {@link Challenge} entities matching the given duration
     *         or {@code null} if not found
     */
    @GetMapping("/find/{duration}")
    public List<Challenge> getChallengeByDuration(@PathVariable LocalDateTime duration) {
        return challengeService.getChallengeByDuration(duration);
    }

    /**
     * Retrieves challenges belonging to a specific health module.
     *
     * @param healthModule the health module (e.g., nutrition, exercise, etc.)
     * @return a list of {@link Challenge} entities matching the given module
     *         or {@code null} if not found
     */
    @GetMapping("/find/{healthModule")
    public List<Challenge> getChallengeByHealthModule(@PathVariable String healthModule) {
        return challengeService.getChallengeByHealthModule(healthModule);
    }

    /**
     * Retrieves all challenges in which a user with the given email is registered.
     *
     * <p>
     * This endpoint searches for challenges that include the user
     * identified by their email address.
     * </p>
     *
     * @param user all {@link UserEcicare} of user
     * @return a list of challenges where the user is registered
     */
    @GetMapping("/find/user/challenge")
    public List<Challenge> getAllChallengeByUser(@RequestBody UserEcicare user) {
        return challengeService.getChallengesByUser(user);
    }

    /**
     * Updates an existing challenge by its name.
     *
     * @param name      the name of the challenge to update
     * @param challenge the {@link Challenge} with updated values
     * @return the updated challenge
     */
    @PutMapping("/update/{name}")
    public Challenge updateChallenge(@PathVariable String name, @RequestBody Challenge challenge) {
        challengeService.updateChallenge(name, challenge);
        return challenge;
    }

    /**
     * Deletes a challenge by its name.
     *
     * @param name the name of the challenge to delete
     */
    @DeleteMapping("/delete/{name}")
    public void deleteChallengeByName(@PathVariable String name) {
        challengeService.deleteChallenge(name);
    }

    /**
     * Adds a user to the list of registered participants for a challenge.
     *
     * @param userEmail the {@link UserEcicare} to be added
     * @param name the name of the challenge
     */
    @PutMapping("/update/user/{name}")
    public void addUseByEmail(@PathVariable String userEmail, @PathVariable String name) {
        challengeService.addUserByEmail(userEmail, name);
    }

}
