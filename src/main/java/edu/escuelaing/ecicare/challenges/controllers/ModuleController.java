package edu.escuelaing.ecicare.challenges.controllers;

import edu.escuelaing.ecicare.challenges.models.dto.ModuleDTO;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.services.ModuleService;
import edu.escuelaing.ecicare.challenges.models.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * REST controller that exposes endpoints for managing {@link Module} entities
 * and their related {@link Challenge} instances.
 *
 * @author Byte Programming
 */
@RestController
@RequestMapping("/modules")
public class ModuleController {

    // Service layer for handling module-related operations.
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
     * Retrieves the total number of modules in the database.
     * This endpoint is useful for frontend to determine whether to use pagination
     * or not.
     *
     * @return a map containing the total count of modules
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Integer>> getTotalModules() {
        int total = moduleService.getTotalModules();
        return ResponseEntity.ok(Collections.singletonMap("total", total));
    }

    /**
     * Retrieves all modules with optional pagination.
     * 
     * If page and size parameters are provided, returns paginated results.
     * If no pagination parameters are provided, returns all modules.
     *
     * @param page the page number (0-based, optional)
     * @param size the page size (optional)
     * @return ResponseEntity containing either a paginated Page of modules or a
     *         List of all modules
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllModules(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Page<Module> modulePage = moduleService.getAllModulesPaginated(page, size);
            return ResponseEntity.ok(modulePage);
        }
        List<Module> allModules = moduleService.getAllModules();
        return ResponseEntity.ok(allModules);
    }

    /**
     * Retrieves all challenges associated with a given module.
     *
     * @param name the unique name of the module
     * @return a list of {@link Challenge} entities belonging to the module
     */
    @GetMapping("/challenges/{name}")
    public List<Challenge> getModuleChallenges(@PathVariable String name) {
        return moduleService.getChallengesByModule(name);
    }

    /**
     * Updates the description of an existing module.
     *
     * @param module the new description for the module
     * @return the updated {@link Module}
     */
    @PutMapping("/")
    public Module updateModuleDescription(@RequestBody ModuleDTO module) {
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
    public void deleteModule(@PathVariable String name) {
        moduleService.deleteModule(name);
    }
}
