package edu.escuelaing.ecicare.retos.services;

import edu.escuelaing.ecicare.retos.models.Challenge;
import edu.escuelaing.ecicare.retos.repositories.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import edu.escuelaing.ecicare.retos.models.Module;

import java.util.List;

/**
 * Service layer for managing {@link Module} entities and their related {@link Challenge} instances.
 * This class encapsulates the business logic related to modules, providing methods to
 * create, retrieve, and delete modules, as well as to access the challenges associated
 * with a given module.
 *
 * @author Byte Programming
 */
@Service
@RequiredArgsConstructor
public class ModuleService {

    //Repository for performing CRUD operations on {@link Module} entities.
    private final ModuleRepository moduleRepository;

    /**
     * Persists a new module in the database.
     *
     * @param module the {@link Module} to be created
     */
    public void createModule(Module module) {
        moduleRepository.save(module);
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
     * Retrieves all challenges associated with a given module.
     *
     * @param name the unique name of the module
     * @return a list of {@link Challenge} entities belonging to the specified module
     * @throws java.util.NoSuchElementException if no module with the given name exists
     */
    public List<Challenge> getChallengesByModule(String name) {
        Module module = moduleRepository.findById(name).get();
        return module.getChallenges();
    }

    /**
     * Updates the description of an existing {@link Module}.
     *
     * @param name the unique identifier of the module to update
     * @param description the new description to set for the module
     * @return the updated {@link Module} entity after the change has been saved
     * @throws java.util.NoSuchElementException if no module with the given name exists
     */
    public Module updateModuleDescription(String name, String description) {
        Module module = moduleRepository.findById(name).get();
        module.setDescription(description);
        moduleRepository.save(module);
        return module;
    }

    /**
     * Deletes a module if it contains no challenges.
     *
     * @param name the unique name of the module to be deleted
     * @return {@code true} if the module was deleted successfully,
     *         {@code false} if it still contains challenges
     * @throws java.util.NoSuchElementException if no module with the given name exists
     */
    public boolean deleteModule(String name) {
        Module module = moduleRepository.findById(name).get();
        if (module.getChallenges().isEmpty()) {
            moduleRepository.delete(module);
            return true;
        }else{
            return false;
        }

    }

}
