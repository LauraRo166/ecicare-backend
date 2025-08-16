package edu.escuelaing.ecicare.services;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.escuelaing.ecicare.usuarios.models.dto.AuthResponseDTO;
import edu.escuelaing.ecicare.usuarios.models.dto.LoginRequestDTO;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.usuarios.repositories.UserEcicareRepository;
import edu.escuelaing.ecicare.usuarios.services.AuthService;
import edu.escuelaing.ecicare.utils.exceptions.notfound.UserEcicareNotFoundException;
import edu.escuelaing.ecicare.utils.models.entity.enums.Role;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserEcicareRepository userEcicareRepository;

    @InjectMocks
    AuthService authService;

    @Test
    void loginUser() {
        UserEcicare user = UserEcicare.builder()
                .idEci(1L)
                .name("Test")
                .email("test@example.com")
                .password("password123")
                .build();

        when(userEcicareRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        AuthResponseDTO response = authService.login(loginRequest);
        // Verify id?
        assertEquals(response.getEmail(), user.getEmail());
        assertEquals(response.getName(), user.getName());
    }

    @Test
    void loginUserWithInvalidCredentials() {
        UserEcicare user = UserEcicare.builder()
                .idEci(1L)
                .name("Test")
                .email("test@example.com")
                .password("password123")
                .build();

        when(userEcicareRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("test@example.com")
                .password("password")
                .build();

        assertThrows(UserEcicareNotFoundException.class, () -> {
            authService.login(loginRequest);
        });
    }
}
