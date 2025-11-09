package edu.escuelaing.ecicare.challenges.controllers;

import edu.escuelaing.ecicare.challenges.models.dto.ChallengeResponse;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleDTO;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleResponse;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleChallengesUsersDTO;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleAdministratorDTO;
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

import edu.escuelaing.ecicare.challenges.models.dto.ModuleGenResponse;
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
    public ModuleResponse createModule(@RequestBody ModuleDTO module) {
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
    @GetMapping("/modulesWithChallenges")
    public ResponseEntity<?> getAllModules(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String view) {
        if (page != null && size != null) {
            Page<ModuleResponse> modulePage = moduleService.getAllModulesPaginated(page, size);
            return ResponseEntity.ok(modulePage);
        }
        if (view != null && view.equals("MOBILE")) {
            return ResponseEntity.ok(moduleService.getModules());
        }
        List<ModuleResponse> allModules = moduleService.getAllModules();
        return ResponseEntity.ok(allModules);
    }

    @GetMapping("/gen-modules")
    public ResponseEntity<?> getAllGenModules(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String view) {
        Page<ModuleGenResponse> allModules = moduleService.getAllGenModulesPaginated(page, size);
        return ResponseEntity.ok(allModules);
    }

    /**
     * Retrieves all challenges associated with a given module.
     *
     * @param name the unique name of the module
     * @return a list of {@link Challenge} entities belonging to the module
     */
    @GetMapping("/challenges/{name}")
    public List<ChallengeResponse> getModuleChallenges(@PathVariable String name) {
        return moduleService.getChallengesByModule(name);
    }

    @GetMapping("/{name}/challenges")
    public Page<ChallengeResponse> getModuleChallengesPaged(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @PathVariable String name) {
        return moduleService.getChallengesByModulePaged(name, page, size);
    }

    /**
     * Retrieves all modules with their associated challenges and registered users.
     * Only returns modules where the requesting user is the administrator.
     *
     * @param email the email of the user requesting the modules
     * @return a list of ModuleChallengesUsersDTO containing modules, their
     *         challenges, and registered users
     */
    @GetMapping("/with-challenges-and-users")
    public ResponseEntity<List<ModuleChallengesUsersDTO>> getAllModulesWithChallengesAndUsers(
            @RequestParam String email) {
        return ResponseEntity.ok(moduleService.getAllModulesWithChallengesAndUsers(email));
    }

    /**
     * Retrieves modules where the user is an administrator.
     *
     * @param email the user's email
     * @return a list of modules with only name and image
     */
    @GetMapping("/administrated-by-user")
    public ResponseEntity<Page<ModuleGenResponse>> getModulesByAdministrator(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<ModuleGenResponse> modules = moduleService.getModulesByAdministrator(email, page, size);

        return ResponseEntity.ok(modules);
    }

    /**
     * Updates the description of an existing module.
     *
     * @param module the new description for the module
     * @return the updated {@link Module}
     */
    @PutMapping("/")
    public ModuleResponse updateModuleDescription(@RequestBody ModuleDTO module) {
        return moduleService.updateModuleByName(module);
    }

    /**
     * Updates the administrator of a module.
     *
     * @param moduleName the name of the module to update
     * @param adminDto   the DTO containing the new administrator email
     * @return the updated ModuleResponse
     */
    @PutMapping("/{moduleName}/administrator")
    public ResponseEntity<ModuleResponse> updateModuleAdministrator(
            @PathVariable String moduleName,
            @RequestBody ModuleAdministratorDTO adminDto) {
        ModuleResponse updatedModule = moduleService.updateModuleAdministrator(
                moduleName, adminDto.getAdministratorEmail());
        return ResponseEntity.ok(updatedModule);
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

    @GetMapping("/{name}")
    public ModuleResponse getModuleByName(@PathVariable String name) {
        return moduleService.getModuleByName(name);
    }
}