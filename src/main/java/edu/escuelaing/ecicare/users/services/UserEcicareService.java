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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEcicareService {

    private final UserEcicareRepository userEcicareRepository;

    @Transactional
    public UserEcicareResponseDTO createEcicareUser(UserEcicareDto user) {

        if (userEcicareRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserEcicareNotFoundException(user.getIdEci());
        }

        UserEcicare userEcicare = UserEcicare.builder()
                .idEci(user.getIdEci())
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

    public void deleteEcicareUserById(Long id) {
        log.info("Deleting User with ID: {}", id);
        if (!userEcicareRepository.existsById(id)) {
            throw new UserEcicareNotFoundException(id);
        }
        userEcicareRepository.deleteById(id);
    }

    public UserEcicareResponseDTO getUserEcicareById(Long id) {
        UserEcicare userEcicare = userEcicareRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.create("User", id));
        return mapToDto(userEcicare);
    }

    public void setHasMedicalApproveUserEcicare(Long id) {
        UserEcicare userEcicare = userEcicareRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        userEcicare.setHasMedicalApprove(true);
        userEcicareRepository.save(userEcicare);
    }

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
