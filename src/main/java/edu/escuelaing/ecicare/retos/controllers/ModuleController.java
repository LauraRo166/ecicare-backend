package edu.escuelaing.ecicare.retos.controllers;

import edu.escuelaing.ecicare.retos.models.dto.ModuleDTO;
import edu.escuelaing.ecicare.retos.models.entity.Challenge;
import edu.escuelaing.ecicare.retos.services.ModuleService;
import edu.escuelaing.ecicare.retos.models.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes endpoints for managing {@link Module} entities
 * and their related {@link Challenge} instances.
 *
 * @author Byte Programming
 */
@RestController
@RequestMapping("/modules")
public class ModuleController {

    //Service layer for handling module-related operations.
    @Autowired
    private ModuleService moduleService;

    /**
     * Creates a new module and persists it in the database.
     *
     * @param module the {@link Module} object to be created
     * @return the created {@link Module}
     */
    @PostMapping("/")
    public Module createModule(@RequestBody ModuleDTO module) {
        return moduleService.createModule(module);
    }

    /**
     * Retrieves all modules stored in the database.
     *
     * @return a list of {@link Module} entities
     */
    @GetMapping("")
    public List<ModuleDTO> getAllModules() {
        return moduleService.getAllModules();
    }

    /**
     * Retrieves all challenges associated with a given module.
     *
     * @param name the unique name of the module
     * @return a list of {@link Challenge} entities belonging to the module
     */
    @GetMapping("/challenge/{name}")
    public List<Challenge> getModuleChallenges(@PathVariable String name) {
        return moduleService.getChallengesByModule(name);
    }

    /**
     * Updates the description of an existing module.
     *
     * @param module the new description for the module
     * @return the updated {@link Module}
     */
    @PutMapping
    public ModuleDTO updateModule(@RequestBody ModuleDTO module) {
        return moduleService.updateModuleByName(module);
    }

    /**
     * Deletes a module if it has no associated challenges.
     *
     * @param name the unique name of the module to be deleted
     * @return {@code true} if the module was successfully deleted,
     *         {@code false} if it still contains challenges
     */
    @DeleteMapping("/{name}")
    public boolean deleteModule(@PathVariable String name) {
        return moduleService.deleteModule(name);
    }
}
