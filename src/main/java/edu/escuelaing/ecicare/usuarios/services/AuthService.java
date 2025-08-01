package edu.escuelaing.ecicare.usuarios.services;

import org.springframework.stereotype.Service;

import edu.escuelaing.ecicare.exceptions.notfound.UserEcicareNotFoundException;
import edu.escuelaing.ecicare.usuarios.models.dto.LoginRequestDTO;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.usuarios.repositories.UserEcicareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import edu.escuelaing.ecicare.usuarios.models.dto.AuthResponseDTO;

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