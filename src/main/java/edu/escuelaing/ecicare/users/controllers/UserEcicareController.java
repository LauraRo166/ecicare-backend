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
import jakarta.validation.*;

@RestController
@RequestMapping("/ecicareusers")
public class UserEcicareController {

    private final UserEcicareService userEcicareService;

    public UserEcicareController(UserEcicareService userEcicareService) {
        this.userEcicareService = userEcicareService;
    }

    @PostMapping
    public ResponseEntity<UserEcicareResponseDTO> createEciCareUser(
            @Valid @RequestBody UserEcicareDto userEcicareDto) {
        UserEcicareResponseDTO createdUser = userEcicareService.createEcicareUser(userEcicareDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEciCareUserById(
            @Parameter(description = "User to be deleted", required = true) @PathVariable Long id) {
        userEcicareService.deleteEcicareUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEcicareResponseDTO> getEcicareUserById(@PathVariable Long id) {
        UserEcicareResponseDTO eciCareUser = userEcicareService.getUserEcicareById(id);
        return ResponseEntity.ok(eciCareUser);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> medicalApproveUserEcicare(@PathVariable Long id) {
        userEcicareService.setHasMedicalApproveUserEcicare(id);
        return ResponseEntity.noContent().build();
    }

}