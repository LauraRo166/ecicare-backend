package edu.escuelaing.ecicare.challenges.services;

import edu.escuelaing.ecicare.challenges.models.dto.ChallengeResponse;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleDTO;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleResponse;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.repositories.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import edu.escuelaing.ecicare.challenges.models.entity.Module;

import java.util.Collections;
import java.util.List;

/**
 * Service layer for managing {@link Module} entities and their related
 * {@link Challenge} instances.
 * This class encapsulates the business logic related to modules, providing
 * methods to
 * create, retrieve, and delete modules, as well as to access the challenges
 * associated
 * with a given module.
 *
 * @author Byte Programming
 */
@Service
@RequiredArgsConstructor
public class ModuleService {

    // Repository for performing CRUD operations on {@link Module} entities.
    private final ModuleRepository moduleRepository;
    private final ChallengeService challengeService;

    /**
     * Persists a new module in the database.
     *
     * @param moduleDto the {@link ModuleDTO} to be created
     * @return the created module as {@link ModuleResponse}
     */
    public ModuleResponse createModule(ModuleDTO moduleDto) {
        Module module = Module.builder()
                .name(moduleDto.getName())
                .description(moduleDto.getDescription())
                .imageUrl(moduleDto.getImageUrl())
                .challenges(Collections.emptyList())
                .build();

        moduleRepository.save(module);
        return toModuleResponse(module);
    }

    /**
     * Retrieves all modules stored in the database.
     *
     * @return a list of {@link ModuleResponse} DTOs
     */
    public List<ModuleResponse> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::toModuleResponse)
                .toList();
    }

    public List<ModuleResponse> getModules(){
        List<Module> modules = moduleRepository.findAll();
        return modules.stream()
                .map(m -> new ModuleResponse(m.getName(), m.getDescription(), m.getImageUrl(),
                        m.getChallenges()
                                .stream()
                                .map(ChallengeService::challengeToResponse)
                                .toList()
                ))
                .toList();
    }
    /**
     * Retrieves the total number of modules in the database.
     *
     * @return the total count of modules
     */
    public int getTotalModules() {
        return (int) moduleRepository.count();
    }

    /**
     * Retrieves all modules from the repository with pagination.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @return a {@link Page} of {@link ModuleResponse} DTOs
     */
    public Page<ModuleResponse> getAllModulesPaginated(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return moduleRepository.findAll(pageable)
                .map(this::toModuleResponse);
    }

    /**
     * Retrieves all challenges associated with a given module.
     *
     * @param name the unique name of the module
     * @return a list of {@link ChallengeResponse} DTOs belonging to the specified
     *         module
     * @throws java.util.NoSuchElementException if no module with the given name
     *                                          exists
     */
    public List<ChallengeResponse> getChallengesByModule(String name) {
        Module module = moduleRepository.findById(name).get();
        return module.getChallenges().stream()
                .map(ChallengeService::challengeToResponse)
                .toList();
    }

    /**
     * Updates the description of an existing {@link Module}.
     *
     * @param moduleDto a DTO from Module, for update description and imageUrl
     * @return the updated {@link ModuleResponse} DTO after the change has been saved
     * @throws java.util.NoSuchElementException if no module with the given name
     *                                          exists
     */
    public ModuleResponse updateModuleByName(ModuleDTO moduleDto) {
        Module module = moduleRepository.findById(moduleDto.getName()).get();
        if (moduleDto.getDescription() != null) {
            module.setDescription(moduleDto.getDescription());
        }
        if (moduleDto.getImageUrl() != null) {
            module.setImageUrl(moduleDto.getImageUrl());
        }
        Module savedModule = moduleRepository.save(module);
        return toModuleResponse(savedModule);
    }

    /**
     * Deletes a module if it contains no challenges.
     *
     * @param name the unique name of the module to be deleted
     * @return {@code true} if the module was deleted successfully,
     *         {@code false} if it still contains challenges
     * @throws java.util.NoSuchElementException if no module with the given name
     *                                          exists
     */
    public void deleteModule(String name) {
        Module module = moduleRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        if (module.getChallenges() != null && !module.getChallenges().isEmpty()) {
            for (Challenge challenge : module.getChallenges()) {
                challengeService.deleteChallenge(challenge.getName());
            }
        }
        moduleRepository.delete(module);
    }

    /**
     * Maps a Module entity to a ModuleResponse DTO.
     *
     * @param module the Module entity
     * @return the ModuleResponse DTO
     */
    private ModuleResponse toModuleResponse(Module module) {
        List<ChallengeResponse> challenges = module.getChallenges() != null
                ? module.getChallenges().stream()
                        .map(ChallengeService::challengeToResponse)
                        .toList()
                : Collections.emptyList();

        return new ModuleResponse(
                module.getName(),
                module.getDescription(),
                module.getImageUrl(),
                challenges
        );
    }
}
