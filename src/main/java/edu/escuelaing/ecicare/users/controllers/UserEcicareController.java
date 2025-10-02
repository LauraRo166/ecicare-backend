package edu.escuelaing.ecicare.users.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.escuelaing.ecicare.users.models.dto.UserEcicareDto;
import edu.escuelaing.ecicare.users.models.dto.UserEcicareResponseDTO;
import edu.escuelaing.ecicare.users.services.UserEcicareService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

/**
 * REST controller for managing {@code UserEcicare} entities.
 *
 * Provides endpoints for creating, retrieving, deleting, and approving
 * EciCare users. Delegates the business logic to {@link UserEcicareService}.
 */
@RestController
@RequestMapping("/ecicareusers")
public class UserEcicareController {

    private final UserEcicareService userEcicareService;

    /**
     * Constructs the {@link UserEcicareController} with the required service.
     *
     * @param userEcicareService the service handling business logic for EciCare users
     */
    public UserEcicareController(UserEcicareService userEcicareService) {
        this.userEcicareService = userEcicareService;
    }

    /**
     * Creates a new EciCare user.
     *
     * @param userEcicareDto the user details to be created
     * @return a {@link ResponseEntity} containing the created user with
     *         {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<UserEcicareResponseDTO> createEciCareUser(
            @Valid @RequestBody UserEcicareDto userEcicareDto) {
        UserEcicareResponseDTO createdUser = userEcicareService.createEcicareUser(userEcicareDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Deletes an EciCare user by their unique identifier.
     *
     * @param id the ID of the user to be deleted
     * @return a {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT} when deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEciCareUserById(
            @Parameter(description = "User to be deleted", required = true) @PathVariable Long id) {
        userEcicareService.deleteEcicareUserById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves an EciCare user by their unique identifier.
     *
     * @param id the ID of the user
     * @return a {@link ResponseEntity} containing the requested user
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserEcicareResponseDTO> getEcicareUserById(@PathVariable Long id) {
        UserEcicareResponseDTO eciCareUser = userEcicareService.getUserEcicareById(id);
        return ResponseEntity.ok(eciCareUser);
    }

    /**
     * Approves a user's medical status.
     *
     * This endpoint updates a flag indicating that the user has been medically
     * approved.
     *
     * @param id the ID of the user to approve
     * @return a {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT} when approved
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> medicalApproveUserEcicare(@PathVariable Long id) {
        userEcicareService.setHasMedicalApproveUserEcicare(id);
        return ResponseEntity.noContent().build();
    }

}
