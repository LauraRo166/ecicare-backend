package edu.escuelaing.ecicare.users.services;

import org.springframework.stereotype.Service;

import edu.escuelaing.ecicare.users.models.dto.LoginRequestDTO;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.users.repositories.UserEcicareRepository;
import edu.escuelaing.ecicare.utils.exceptions.notfound.UserEcicareNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import edu.escuelaing.ecicare.users.models.dto.AuthResponseDTO;

/**
 * Service class that handles authentication logic for {@link UserEcicare}.
 * This class provides methods to authenticate users and return an
 * {@link AuthResponseDTO} upon successful login.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserEcicareRepository userEcicareRepository;

    /**
     * Authenticates a user based on email and password.
     *
     * @param loginRequestDTO The login request containing email and password.
     * @return An {@link AuthResponseDTO} containing basic user information
     *         if authentication is successful.
     * @throws UserEcicareNotFoundException if the user is not found
     *                                      or if the provided credentials are invalid.
     */
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {

        UserEcicare userEcicare = userEcicareRepository
                .findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UserEcicareNotFoundException("User not found"));

        if (!userEcicare.getPassword().equals(loginRequestDTO.getPassword())) {
            throw new UserEcicareNotFoundException("Invalid credentials");
        }

        return AuthResponseDTO.builder()
                .idEci(userEcicare.getIdEci())
                .email(userEcicare.getEmail())
                .name(userEcicare.getName())
                .build();
    }
}
