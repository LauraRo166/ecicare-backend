package edu.escuelaing.ecicare.challenges.controllers;

import edu.escuelaing.ecicare.challenges.models.dto.ChallengeDTO;
import edu.escuelaing.ecicare.challenges.models.dto.ChallengeResponse;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleWithChallengesDTO;
import edu.escuelaing.ecicare.challenges.models.dto.UserEmailNameDTO;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.services.ChallengeService;
import edu.escuelaing.ecicare.awards.models.dto.AwardDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
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
     * @param challengeDto the {@link Challenge} to be created
     * @return the created challenge
     */
    @PostMapping("/")
    public ChallengeResponse createChallenge(@RequestBody ChallengeDTO challengeDto) {
        return challengeService.createChallenge(challengeDto);
    }

    /**
     * Retrieves all challenges with optional pagination.
     * 
     * If page and size parameters are provided, returns paginated results.
     * If no pagination parameters are provided, returns all challenges.
     *
     * @param page the page number (0-based, optional)
     * @param size the page size (optional)
     * @return ResponseEntity containing either a paginated Page of challenges or a
     *         List of all challenges
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllChallenges(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Page<ChallengeResponse> challengePage = challengeService.getAllChallengesPaginated(page, size);
            return ResponseEntity.ok(challengePage);
        }
        List<ChallengeResponse> allChallenges = challengeService.getAllChallenges();
        return ResponseEntity.ok(allChallenges);
    }

    /**
     * Search challenges by name and group them by modules - Perfect for organized
     * display.
     * Returns challenges grouped by their respective modules, showing which module
     * each challenge belongs to. This provides better UX organization.
     * 
     * @param q the search query (what the user is typing)
     * @return ResponseEntity containing:
     *         - Empty list if no search query
     *         - List<ModuleWithChallengesDTO> with challenges grouped by modules
     * 
     */
    @GetMapping("/search")
    public ResponseEntity<List<ModuleWithChallengesDTO>> searchChallenges(
            @RequestParam(required = false) String q) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<ModuleWithChallengesDTO> groupedResults = challengeService.searchChallengesGroupedByModule(q);

        return ResponseEntity.ok(groupedResults);
    }

    /**
     * Retrieves all confirmed challenges in which a user with the given email was
     * registered.
     *
     * @param userEmail email of user
     * @return a list of challenges where the user is confirmed
     */
    @GetMapping("/confirmed/{userEmail}")
    public List<ChallengeResponse> getChallengesConfirmedByUserEmail(@PathVariable String userEmail) {
        return challengeService.getChallengesCompletedByUserEmail(userEmail);
    }

    /**
     * Retrieves a challenge by its unique name.
     *
     * @param name the name of the challenge
     * @return the {@link Challenge} with the specified name,
     *         or {@code null} if not found
     */
    @GetMapping("/{name}")
    public ChallengeResponse getChallengeByName(@PathVariable String name) {
        return challengeService.getChallengeByName(name);
    }

    /**
     * Returns all awards associated to a challenge (via redeemables).
     * GET /challenges/{name}/awards
     */
    @GetMapping("/{name}/awards")
    public ResponseEntity<List<AwardDto>> getAwardsByChallenge(@PathVariable String name) {
        ChallengeResponse existing = challengeService.getChallengeByName(name);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        List<AwardDto> awards = challengeService.getAwardsByChallenge(name);
        return ResponseEntity.ok(awards);
    }

    /**
     * Retrieves challenges with a specific duration.
     *
     * @param duration the duration (end date/time) of the challenge
     * @return a list of {@link Challenge} entities matching the given duration
     *         or {@code null} if not found
     */
    @GetMapping("/duration/{duration}")
    public List<ChallengeResponse> getChallengeByDuration(@PathVariable LocalDateTime duration) {
        return challengeService.getChallengeByDuration(duration);
    }

    /**
     * Retrieves all challenges in which a user with the given email is registered.
     *
     * @param userEmail email of user
     * @return a list of challenges where the user is registered
     */
    @GetMapping("/users/{userEmail}")
    public List<ChallengeResponse> getUserChallenges(@PathVariable String userEmail) {
        return challengeService.getChallengesByUserEmail(userEmail);
    }

    /**
     * Busca en los challenges en los que el usuario está registrado (registered)
     * por nombre (coincidencia parcial, case-insensitive).
     *
     * GET /challenges/users/{email}/registered/search?name=term
     */
    @GetMapping("/users/{userEmail}/registered/search")
    public List<ChallengeResponse> searchUserRegisteredChallenges(
            @PathVariable("userEmail") String userEmail,
            @RequestParam(required = false, defaultValue = "") String name) {
        return challengeService.searchRegisteredChallengesByUserEmail(userEmail, name);
    }

    @GetMapping("/users/{email}/challenges-registered/paged")
    public ResponseEntity<Page<ChallengeResponse>> getUserRegisteredChallenges(
            @PathVariable("email") String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ChallengeResponse> result = challengeService.getChallengesByUserEmailPaged(userEmail, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves all challenges in which a user with the given email is confirmed.
     *
     * @param userEmail email of user
     * @return a list of challenges where the user is confirmed
     */
    @GetMapping("/users/{userEmail}/completed")
    public List<ChallengeResponse> getUserCompletedChallenges(@PathVariable String userEmail) {
        return challengeService.getChallengesCompletedByUserEmail(userEmail);
    }

    /**
     * Busca en los challenges completados (confirmed) por un usuario por nombre
     * (coincidencia parcial, case-insensitive).
     *
     * GET /challenges/users/{email}/completed/search?name=term
     */
    @GetMapping("/users/{userEmail}/completed/search")
    public List<ChallengeResponse> searchUserCompletedChallenges(
            @PathVariable("userEmail") String userEmail,
            @RequestParam(required = false, defaultValue = "") String name) {
        return challengeService.searchConfirmedChallengesByUserEmail(userEmail, name);
    }

    @GetMapping("/users/{email}/completed/paged")
    public ResponseEntity<Page<ChallengeResponse>> getUserCompletedChallenges(
            @PathVariable("email") String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ChallengeResponse> result = challengeService.getChallengesCompletedByUserEmailPaged(userEmail, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * Updates an existing challenge by its name.
     *
     * @param challengeDto the {@link Challenge} with updated values like:
     *                     description, imageUrl, phrase, tips, goals, Module
     * @return the updated challenge
     */
    @PutMapping("/{name}")
    public ChallengeResponse updateChallenge(@RequestBody ChallengeDTO challengeDto) {
        return challengeService.updateChallenge(challengeDto);
    }

    /**
     * Deletes a challenge by its name.
     *
     * @param name the name of the challenge to delete
     */
    @DeleteMapping("/{name}")
    public void deleteChallengeByName(@PathVariable String name) {
        challengeService.deleteChallenge(name);
    }

    /**
     * Adds a user to the list of registered participants for a challenge.
     *
     * @param userEmail the {@link UserEcicare} to be added
     * @param name      the name of the challenge
     * @return the updated {@link ChallengeResponse}
     */
    @PutMapping("/users/{userEmail}/challenges/{name}")
    public ChallengeResponse addUseByEmail(@PathVariable String userEmail, @PathVariable String name) {
        return challengeService.addUserByEmail(userEmail, name);
    }

    /**
     * Confirm a user to the list of registered participants for a challenge.
     *
     * @param userEmail the {@link UserEcicare} to be added
     * @param name      the name of the challenge
     * @return the updated {@link ChallengeResponse}
     */
    @PutMapping("/users/{userEmail}/challenges/{name}/confirm")
    public ChallengeResponse confirmUserByEmail(@PathVariable String userEmail, @PathVariable String name) {
        return challengeService.confirmUserByEmail(userEmail, name);
    }

    /**
     * Retrieves confirmed users for a specific challenge
     *
     * @param name challenge name
     * @return list of confirmed user emails
     */
    @GetMapping("/{name}/confirmed-users")
    public List<String> getConfirmedUsersByChallenge(@PathVariable String name) {
        return challengeService.getConfirmedUsersByChallenge(name);
    }

    /**
     * Retrieves registered users for a specific challenge
     *
     * @param name challenge name
     * @return list of registered user emails
     */
    @GetMapping("/{name}/registered-users")
    public List<String> getRegisteredUsersByChallenge(@PathVariable String name) {
        return challengeService.getRegisteredUsersByChallenge(name);
    }

    /**
     * * Retrieves registered users for a specific challenge with pagination
     *
     * @param challengeName challenge name
     * @param page          page number (0-based)
     * @param size          page size
     * @return paginated list of registered users with email and name
     */
    @GetMapping("/{challengeName}/registered-users/paged")
    public Page<UserEmailNameDTO> getRegisteredUsersByChallenge(
            @PathVariable String challengeName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return challengeService.getRegisteredUsersByChallenge(challengeName, page, size);
    }

    /**
     * Searches registered users for a specific challenge by name or email with
     * pagination
     *
     * @param challengeName challenge name
     * @param search        search term for name or email
     * @param pageable      pagination information
     * @return paginated list of registered users matching the search criteria
     */
    @GetMapping("/{challengeName}/users-registered/search-by-name-or-email")
    public Page<UserEmailNameDTO> getRegisteredUsersBySearch(
            @PathVariable String challengeName,
            @RequestParam(required = false, defaultValue = "") String search,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return challengeService.searchRegisteredUsers(challengeName, search, pageable);
    }

    @GetMapping("/searchContainingPaged")
    public Page<ChallengeResponse> searchChallengesWithPaged(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false) String module,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return challengeService.searchChallengesByName(name, module, pageable);
    }

}
