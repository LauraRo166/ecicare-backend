package edu.escuelaing.ecicare.users.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.users.models.dto.UserEcicareDto;
import edu.escuelaing.ecicare.users.models.dto.UserEcicareResponseDTO;
import edu.escuelaing.ecicare.users.repositories.UserEcicareRepository;
import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;
import edu.escuelaing.ecicare.utils.exceptions.notfound.UserEcicareNotFoundException;
import edu.escuelaing.ecicare.utils.models.entity.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Service class for handling operations related to {@link UserEcicare}.
 * Provides methods for creating, retrieving, updating, and deleting user
 * accounts,
 * as well as managing their medical approval status.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEcicareService {

    private final UserEcicareRepository userEcicareRepository;

    /**
     * Creates a new Ecicare user in the system.
     * If the provided email already exists, throws a
     * {@link UserEcicareNotFoundException}.
     *
     * @param user The user data transfer object containing user details.
     * @return A {@link UserEcicareResponseDTO} with the created user's details.
     */
    @Transactional
    public UserEcicareResponseDTO createEcicareUser(UserEcicareDto user) {

        if (userEcicareRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserEcicareNotFoundException(user.getIdEci());
        }

        UserEcicare userEcicare = UserEcicare.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .hasMedicalApprove(false)
                .registrationDate(LocalDateTime.now())
                .build();

        UserEcicare savedUser = userEcicareRepository.save(userEcicare);
        return mapToDto(savedUser);
    }

    /**
     * Deletes a user from the system by its ID.
     * If the user does not exist, throws a {@link UserEcicareNotFoundException}.
     *
     * @param id The unique identifier of the user to delete.
     */
    @Transactional
    public void deleteEcicareUserById(Long id) {
        log.info("Deleting User with ID: {}", id);
        if (!userEcicareRepository.existsById(id)) {
            throw new UserEcicareNotFoundException(id);
        }
        userEcicareRepository.deleteById(id);
    }

    /**
     * Retrieves a user by its ID.
     * If the user does not exist, throws a {@link ResourceNotFoundException}.
     *
     * @param id The unique identifier of the user.
     * @return A {@link UserEcicareResponseDTO} with the user details.
     */

    public UserEcicareResponseDTO getUserEcicareById(Long id) {
        UserEcicare userEcicare = userEcicareRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.create("User", id));
        return mapToDto(userEcicare);
    }

    /**
     * Updates the medical approval status of a user.
     * If the user does not exist, throws a {@link ResponseStatusException}.
     *
     * @param id The unique identifier of the user to approve.
     */
    @Transactional
    public void setHasMedicalApproveUserEcicare(Long id) {
        UserEcicare userEcicare = userEcicareRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userEcicare.setHasMedicalApprove(true);
        userEcicareRepository.save(userEcicare);
    }


        /**
     * Retrieves the role of a user by their email.
     * Throws a 404 error if the user is not found.
     *
     * @param email The email of the user.
     * @return The role of the user.
     */
    public Role getUserRoleByEmail(String email) {
        UserEcicare user = userEcicareRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getRole();
    }



    /**
     * Maps a {@link UserEcicare} entity to a {@link UserEcicareResponseDTO}.
     *
     * @param userEcicare The user entity to map.
     * @return A {@link UserEcicareResponseDTO} with the user data.
     */
    public UserEcicareResponseDTO mapToDto(UserEcicare userEcicare) {
        return UserEcicareResponseDTO.builder()
                .idEci(userEcicare.getIdEci())
                .name(userEcicare.getName())
                .email(userEcicare.getEmail())
                .role(userEcicare.getRole())
                .hasMedicalApprove(userEcicare.getHasMedicalApprove())
                .registrationDate(userEcicare.getRegistrationDate())
                .build();
    }
}
