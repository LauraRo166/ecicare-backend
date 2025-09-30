package edu.escuelaing.ecicare.retos.services;

import edu.escuelaing.ecicare.retos.models.dto.ModuleDTO;
import edu.escuelaing.ecicare.retos.models.entity.Challenge;
import edu.escuelaing.ecicare.retos.repositories.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import edu.escuelaing.ecicare.retos.models.entity.Module;

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

    /**
     * Persists a new module in the database.
     *
     * @param moduleDto the {@link Module} to be created
     */
    public Module createModule(ModuleDTO moduleDto) {
        Module module = Module.builder()
                .name(moduleDto.getName())
                .description(moduleDto.getDescription())
                .imageUrl(moduleDto.getImageUrl())
                .challenges(Collections.emptyList())
                .build();

        moduleRepository.save(module);
        return module;
    }

    /**
     * Retrieves all modules stored in the database.
     *
     * @return a list of {@link Module} entities
     */
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
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
     * @return a {@link Page} of {@link Module} entities
     */
    public Page<Module> getAllModulesPaginated(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return moduleRepository.findAll(pageable);
    }

    /**
     * Retrieves all challenges associated with a given module.
     *
     * @param name the unique name of the module
     * @return a list of {@link Challenge} entities belonging to the specified
     *         module
     * @throws java.util.NoSuchElementException if no module with the given name
     *                                          exists
     */
    public List<Challenge> getChallengesByModule(String name) {
        Module module = moduleRepository.findById(name).get();
        return module.getChallenges();
    }

    /**
     * Updates the description of an existing {@link Module}.
     *
     * @param moduleDto a DTO from Module, for update description and imageUrl
     * @return the updated {@link Module} entity after the change has been saved
     * @throws java.util.NoSuchElementException if no module with the given name
     *                                          exists
     */
    public Module updateModuleByName(ModuleDTO moduleDto) {
        Module module = moduleRepository.findById(moduleDto.getName()).get();
        if (moduleDto.getDescription() != null) {
            module.setDescription(moduleDto.getDescription());
        }
        if (moduleDto.getImageUrl() != null) {
            module.setImageUrl(moduleDto.getImageUrl());
        }
        moduleRepository.save(module);
        return module;
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
    public boolean deleteModule(String name) {
        Module module = moduleRepository.findById(name).get();
        if (module.getChallenges().isEmpty()) {
            moduleRepository.delete(module);
            return true;
        } else {
            return false;
        }

    }

}
