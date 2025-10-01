package edu.escuelaing.ecicare.users.services;

import org.springframework.stereotype.Service;

import edu.escuelaing.ecicare.users.models.dto.LoginRequestDTO;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.users.repositories.UserEcicareRepository;
import edu.escuelaing.ecicare.utils.exceptions.notfound.UserEcicareNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import edu.escuelaing.ecicare.users.models.dto.AuthResponseDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserEcicareRepository userEcicareRepository;

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {

        UserEcicare userEcicare = userEcicareRepository
                .findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UserEcicareNotFoundException("Usuario No Encontrado"));

        if (!userEcicare.getPassword().equals(loginRequestDTO.getPassword())) {
            throw new UserEcicareNotFoundException("Credencial Invalida");
        }

        return AuthResponseDTO.builder()
                // IdEci?
                .email(userEcicare.getEmail())
                .name(userEcicare.getName())
                .build();
    }
}