package edu.escuelaing.ecicare.users.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.escuelaing.ecicare.users.models.dto.AuthResponseDTO;
import edu.escuelaing.ecicare.users.models.dto.LoginRequestDTO;
import edu.escuelaing.ecicare.users.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsible for handling authentication-related operations.
 *
 * Provides endpoints for login and session management, delegating the
 * authentication process to the {@link AuthService}.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication operations")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs the {@link AuthController} with the required {@link AuthService}.
     *
     * @param authService the service responsible for authentication logic
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles the login process by validating user credentials.
     *
     * Delegates the authentication to {@link AuthService} and returns an
     * {@link AuthResponseDTO} containing authentication details such as
     * tokens and user information.
     *
     * @param loginRequestDTO the request body containing login credentials
     * @return a {@link ResponseEntity} containing the authentication response
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        AuthResponseDTO user = authService.login(loginRequestDTO);
        return ResponseEntity.ok(user);
    }

}
