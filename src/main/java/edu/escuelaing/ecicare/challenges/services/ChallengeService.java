package edu.escuelaing.ecicare.challenges.services;

import edu.escuelaing.ecicare.awards.models.dto.AwardDto;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.repositories.RedeemableRepository;
import edu.escuelaing.ecicare.challenges.models.dto.*;
import edu.escuelaing.ecicare.challenges.models.entity.Module;
import edu.escuelaing.ecicare.challenges.repositories.ModuleRepository;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.users.models.dto.AuthResponseDTO;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.repositories.ChallengeRepository;
import edu.escuelaing.ecicare.users.repositories.UserEcicareRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Collections;
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
     * @param challengeDto the {@link ChallengeDTO} to be created
     * @return the created challenge as {@link ChallengeResponse}
     */
    public ChallengeResponse createChallenge(ChallengeDTO challengeDto) {
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
                .requiredVerifications(
                        challengeDto.getRequiredVerifications() != null ? challengeDto.getRequiredVerifications() : 1)
                .build();
        challengeRepository.save(challenge);
        return challengeToResponse(challenge);
    }

    /**
     * Retrieves all challenges from the repository.
     *
     * @return a list of all {@link ChallengeResponse} DTOs
     */
    public List<ChallengeResponse> getAllChallenges() {
        return challengeRepository.findAll().stream()
                .map(ChallengeService::challengeToResponse)
                .toList();
    }

    /**
     * Retrieves a challenge entity by its name (for internal use).
     *
     * @param name the name of the challenge
     * @return the {@link Challenge} entity, or null if not found
     */
    public Challenge getChallengeEntityByName(String name) {
        return challengeRepository.findByName(name);
    }

    /**
     * Retrieves all challenges from the repository with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a {@link Page} of {@link ChallengeResponse} DTOs
     */
    public Page<ChallengeResponse> getAllChallengesPaginated(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return challengeRepository.findAll(pageable)
                .map(ChallengeService::challengeToResponse);
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
                    Module module = challenges.get(0).getModule();

                    // Convert challenges to DTOs
                    List<ChallengeResponse> challengeResponses = challenges.stream()
                            .map(ChallengeService::challengeToResponse)
                            .toList();

                    return ModuleWithChallengesDTO.builder()
                            .moduleName(module.getName())
                            .moduleDescription(module.getDescription())
                            .moduleImageUrl(module.getImageUrl())
                            .challenges(challengeResponses)
                            .totalChallenges(challenges.size())
                            .build();
                })
                .sorted((dto1, dto2) -> dto1.getModuleName().compareToIgnoreCase(dto2.getModuleName()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a challenge by its unique name.
     *
     * @param name the name of the challenge
     * @return the {@link ChallengeResponse} with the specified name,
     *         or {@code null} if no such challenge exists
     */
    public ChallengeResponse getChallengeByName(String name) {
        Challenge challenge = challengeRepository.findByName(name);
        return challenge != null ? challengeToResponse(challenge) : null;
    }

    /**
     * Gets all the Awards associated with a specific challenge.
     * The search is performed through the Redeemable linked to the challenge.
     *
     * @param challengeName is the unique name of the challenge to query.
     * @return a list of distinct {@link AwardDto} associated with that challenge.
     */
    public List<AwardDto> getAwardsByChallenge(String challengeName) {
        return redeemableRepository.findByChallenge_Name(challengeName).stream()
                .map(Redeemable::getAward)
                .filter(Objects::nonNull)
                .distinct()
                .map(ChallengeService::toAwardDto)
                .toList();
    }

    /**
     * Retrieves all challenges with a specific duration.
     *
     * @param duration the duration (end date/time) of the challenge
     * @return a list of {@link ChallengeResponse} DTOs matching the given duration
     *         or {@code null} if no such challenge exists
     */
    public List<ChallengeResponse> getChallengeByDuration(LocalDateTime duration) {
        return challengeRepository.findByDuration(duration).stream()
                .map(ChallengeService::challengeToResponse)
                .toList();
    }

    /**
     * Updates an existing challenge with new values for specific fields:
     * phrase, reward, and health module.
     *
     * @param challengeDto the {@link ChallengeDTO} containing updated values
     * @return the updated {@link ChallengeResponse}
     */
    public ChallengeResponse updateChallenge(ChallengeDTO challengeDto) {
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
        if (challengeDto.getRequiredVerifications() != null && challengeDto.getRequiredVerifications() > 0) {
            oldChallenge.setRequiredVerifications(challengeDto.getRequiredVerifications());
        }

        Challenge savedChallenge = challengeRepository.save(oldChallenge);
        return challengeToResponse(savedChallenge);
    }

    /**
     * Deletes a challenge by its unique name, along with its associated redeemable.
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
     * @return the updated {@link ChallengeResponse}
     */
    public ChallengeResponse addUserByEmail(String userEmail, String name) {
        Challenge challenge = challengeRepository.findByName(name);
        if (challenge == null) {
            throw new RuntimeException("Challenge not found: " + name);
        }
        List<UserEcicare> registered = challenge.getRegistered();
        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userEmail));
        if (!registered.contains(user)) {
            registered.add(user);
            challenge.setRegistered(registered);
            challengeRepository.save(challenge);
        }
        return challengeToResponse(challenge);
    }

    /**
     * Confirms a user to a specific challenge by adding them, removing from
     * registered list.
     * to the list of confirms participants.
     *
     * @param userEmail the {@link UserEcicare} to be added
     * @param challengeName      the name of the challenge
     * @return the updated {@link ChallengeResponse}
     */
    @Transactional
    public ChallengeResponse confirmUserByEmail(String userEmail, String challengeName) {

        Challenge challenge = challengeRepository.findByName(challengeName);
        if (challenge == null) {
            throw new RuntimeException("Challenge not found: " + challengeName);
        }

        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        if (!challenge.getRegistered().contains(user)) {
            throw new RuntimeException("User is not registered in the challenge");
        }

        // Obtener el conteo actual de verificaciones para este usuario
        int currentVerifications = challenge.getVerifications().getOrDefault(userEmail, 0);
        currentVerifications++;

        // Actualizar el conteo de verificaciones
        challenge.getVerifications().put(userEmail, currentVerifications);

        // Si alcanzó el número requerido de verificaciones, mover a confirmed
        if (currentVerifications >= challenge.getRequiredVerifications()) {
            challenge.getRegistered().remove(user);
            challenge.getConfirmed().add(user);
            // Limpiar las verificaciones del usuario confirmado
            challenge.getVerifications().remove(userEmail);
        }

        challengeRepository.save(challenge);

        return challengeToResponse(challenge);
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

    /**
     * Obtiene los desafíos en los que un usuario está registrado, con paginación.
     * 
     * @param userEmail email del usuario
     * @param page      página (default 0)
     * @param size      tamaño de página (default 10)
     * @return Page de ChallengeResponse (sin ordenar)
     */
    public Page<ChallengeResponse> getChallengesByUserEmailPaged(
            String userEmail, int page, int size) {

        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("userEmail no puede ser nulo o vacío");
        }

        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        // Valores seguros
        int safePage = Math.max(0, page);
        int safeSize = size > 0 ? size : 10;

        // Obtener desafíos desde el repositorio (sin ordenar)
        List<ChallengeResponse> allChallenges = challengeRepository.findByRegistered(user)
                .stream()
                .map(ChallengeService::challengeToResponse)
                .toList();

        // Paginación manual
        int start = safePage * safeSize;
        int end = Math.min(start + safeSize, allChallenges.size());

        List<ChallengeResponse> pagedList = start < allChallenges.size()
                ? allChallenges.subList(start, end)
                : Collections.emptyList();

        Pageable pageable = PageRequest.of(safePage, safeSize);

        return new PageImpl<>(pagedList, pageable, allChallenges.size());
    }

    /**
     * Searches challenges in which a user is registered by challenge name.
     *
     * @param userEmail email of the user
     * @param name      partial or full name to search for (case insensitive)
     * @return list of matching {@link ChallengeResponse}
     */
    public Page<ChallengeResponse> searchRegisteredChallengesByUserEmail(String userEmail, String name,
            Pageable pageable) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("userEmail no puede ser nulo o vacío");
        }

        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        String query = name == null ? "" : name.trim().toLowerCase();

        return challengeRepository.findRegisterChallengesByUserIdAndSearch(user.getIdEci(), query, pageable)
                .map(ChallengeService::challengeToResponse);
    }

    /**
     * Searches challenges that a user has confirmed (completed) by challenge name.
     *
     * @param userEmail email of the user
     * @param name      partial or full name to search for (case insensitive)
     * @return list of matching {@link ChallengeResponse}
     */
    public Page<ChallengeResponse> searchConfirmedChallengesByUserEmail(String userEmail, String name,
            Pageable pageable) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("userEmail no puede ser nulo o vacío");
        }

        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        String query = name == null ? "" : name.trim().toLowerCase();

        return challengeRepository.findConfirmedChallengesByUserIdAndSearch(user.getIdEci(), query, pageable)
                .map(ChallengeService::challengeToResponse);
    }

    /**
     * Maps a Challenge entity to a ChallengeResponse DTO.
     *
     * @param challenge the Challenge entity
     * @return the ChallengeResponse DTO
     */
    public static ChallengeResponse challengeToResponse(Challenge challenge) {
        List<AwardDto> redeemables = challenge.getRedeemables() != null ? challenge.getRedeemables()
                .stream()
                .map(r -> new AwardDto(
                        r.getAward().getName(),
                        r.getAward().getAwardId(),
                        r.getAward().getDescription(),
                        r.getAward().getInStock(),
                        r.getAward().getImageUrl()))
                .toList() : null;
        return new ChallengeResponse(
                challenge.getName(),
                challenge.getDescription(),
                challenge.getImageUrl(),
                challenge.getPhrase(),
                challenge.getTips(),
                challenge.getDuration(),
                challenge.getGoals(),
                challenge.getModule().getName(),
                challenge.getRequiredVerifications(),
                redeemables);
    }

    /**
     * Maps an Award entity to an AwardDto.
     *
     * @param award the Award entity
     * @return the AwardDto
     */
    private static AwardDto toAwardDto(Award award) {
        return new AwardDto(
                award.getName(),
                award.getAwardId(),
                award.getDescription(),
                award.getInStock(),
                award.getImageUrl());
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

    /**
     * Obtiene los desafíos completados por un usuario, con paginación.
     * 
     * @param userEmail email del usuario
     * @param page      página (default 0)
     * @param size      tamaño de página (default 10)
     * @return Page de ChallengeResponse (sin ordenar)
     */
    public Page<ChallengeResponse> getChallengesCompletedByUserEmailPaged(
            String userEmail, int page, int size) {

        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("userEmail no puede ser nulo o vacío");
        }

        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        // Valores seguros
        int safePage = Math.max(0, page);
        int safeSize = size > 0 ? size : 10;

        // SIN ORDENAR → directo del Set/List original
        List<ChallengeResponse> allChallenges = user.getChallengesConfirmed()
                .stream()
                .map(ChallengeService::challengeToResponse)
                .toList();

        // Paginación manual
        int start = safePage * safeSize;
        int end = Math.min(start + safeSize, allChallenges.size());

        List<ChallengeResponse> pagedList = start < allChallenges.size()
                ? allChallenges.subList(start, end)
                : Collections.emptyList();

        Pageable pageable = PageRequest.of(safePage, safeSize);

        return new PageImpl<>(pagedList, pageable, allChallenges.size());
    }

    /**
     * Retrieves emails of confirmed users for a specific challenge
     *
     * @param challengeName name of the challenge
     * @return list of confirmed user emails
     */
    public List<String> getConfirmedUsersByChallenge(String challengeName) {
        Challenge challenge = challengeRepository.findByName(challengeName);
        if (challenge == null) {
            throw new RuntimeException("Challenge not found: " + challengeName);
        }
        return challenge.getConfirmed().stream()
                .map(UserEcicare::getEmail)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves emails of registered users for a specific challenge
     *
     * @param challengeName name of the challenge
     * @return list of registered user emails
     */
    public List<String> getRegisteredUsersByChallenge(String challengeName) {
        Challenge challenge = challengeRepository.findByName(challengeName);
        if (challenge == null) {
            throw new RuntimeException("Challenge not found: " + challengeName);
        }
        return challenge.getRegistered().stream()
                .map(UserEcicare::getEmail)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves paginated registered user emails for a specific challenge.
     *
     * @param challengeName the name of the challenge
     * @param page          the page number (0-based)
     * @param size          the page size
     * @return a page of registered user name and email DTOs
     */
    public Page<UserEmailNameDTO> getRegisteredUsersByChallenge(String challengeName, int page, int size) {

        Challenge challenge = challengeRepository.findByName(challengeName);
        if (challenge == null) {
            throw new RuntimeException("Challenge not found: " + challengeName);
        }

        int requiredVerifications = challenge.getRequiredVerifications();
        Map<String, Integer> verifications = challenge.getVerifications();

        List<UserEmailNameDTO> emailsNameUser = challenge.getRegistered().stream()
                .map(user -> {
                    int currentVerifications = verifications.getOrDefault(user.getEmail(), 0);
                    return new UserEmailNameDTO(
                            user.getEmail(),
                            user.getName(),
                            currentVerifications,
                            requiredVerifications);
                })
                .toList();

        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
        if (start >= emailsNameUser.size()) {
            return Page.empty(pageable);
        }

        int end = Math.min(start + pageable.getPageSize(), emailsNameUser.size());
        List<UserEmailNameDTO> paginatedEmails = emailsNameUser.subList(start, end);

        return new PageImpl<>(paginatedEmails, pageable, emailsNameUser.size());
    }

    /**
     * Searches registered users for a specific challenge by name or email.
     *
     * @param challengeName the name of the challenge
     * @param search        the search term to match in user names or emails
     * @param pageable      pagination information
     * @return a page of {@link UserEmailNameDTO} matching the search criteria
     */
    public Page<UserEmailNameDTO> searchRegisteredUsers(
            String challengeName,
            String search,
            Pageable pageable) {
        // Reemplaza null o vacío para que el LIKE funcione siempre
        if (search == null || search.isBlank()) {
            search = "";
        }

        return challengeRepository.searchRegisteredUsers(
                challengeName,
                search,
                pageable);
    }

    /**
     * Searches challenges by name with pagination support.
     *
     * @param name     the search term to match in challenge names
     * @param pageable pagination information
     * @return a {@link Page} of {@link ChallengeResponse} matching the search
     *         criteria
     */
    /**
     * Searches challenges by name with optional module filtering and pagination
     * support.
     *
     * @param name       the search term to match in challenge names
     * @param moduleName optional module name to restrict the search to a specific
     *                   module
     * @param pageable   pagination information
     * @return a {@link Page} of {@link ChallengeResponse} matching the search
     *         criteria
     */
    public Page<ChallengeResponse> searchChallengesByName(String name, String moduleName, Pageable pageable) {

        Page<Challenge> challengePage;

        if (moduleName != null && !moduleName.isBlank()) {
            challengePage = challengeRepository.findByNameContainingIgnoreCaseAndModule_Name(name, moduleName,
                    pageable);
        } else {
            challengePage = challengeRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return challengePage.map(c -> new ChallengeResponse(
                c.getName(),
                c.getDescription(),
                c.getImageUrl(),
                c.getPhrase(),
                c.getTips(),
                c.getDuration(),
                c.getGoals(),
                c.getModule() != null ? c.getModule().getName() : null,
                c.getRequiredVerifications(),
                c.getRedeemables() != null
                        ? c.getRedeemables().stream().map(this::toDto).toList()
                        : null));
    }

    private AwardDto toDto(Redeemable redeemable) {
        if (redeemable == null || redeemable.getAward() == null) {
            return null;
        }

        Award award = redeemable.getAward();

        return AwardDto.builder()
                .name(award.getName())
                .description(award.getDescription())
                .inStock(award.getInStock())
                .imageUrl(award.getImageUrl())
                .build();
    }

    public AdminDTO getChallengeAdmin(String challengeName) {
        UserEcicare userEcicare = challengeRepository.findChallengeAdministrator(challengeName);
        if (userEcicare == null) {
            throw new RuntimeException("Challenge Administrator not found");
        }
        return new AdminDTO(userEcicare.getName(), userEcicare.getEmail());
    }

    public ChallengeUserStatus getUserChallengeStatus(String email, String challengeName) {
        UserEcicare userEcicare = userEcicareRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean registered = challengeRepository.isUserRegisteredInChallenge(challengeName, userEcicare.getIdEci());
        boolean completed = challengeRepository.isUserConfirmedInChallenge(challengeName, userEcicare.getIdEci());
        return new ChallengeUserStatus(completed, registered);
    }

    /**
     * Obtiene el estado de verificaciones de un usuario en un challenge específico.
     * 
     * @param userEmail     el email del usuario
     * @param challengeName el nombre del challenge
     * @return un mapa con: currentVerifications, requiredVerifications, isConfirmed
     */
    public Map<String, Object> getUserVerificationStatus(String userEmail, String challengeName) {
        Challenge challenge = challengeRepository.findByName(challengeName);
        if (challenge == null) {
            throw new RuntimeException("Challenge not found: " + challengeName);
        }

        UserEcicare user = userEcicareRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Map<String, Object> status = new java.util.HashMap<>();
        status.put("currentVerifications", challenge.getVerifications().getOrDefault(userEmail, 0));
        status.put("requiredVerifications", challenge.getRequiredVerifications());
        status.put("isRegistered", challenge.getRegistered().contains(user));
        status.put("isConfirmed", challenge.getConfirmed().contains(user));

        return status;
    }
}
