package edu.escuelaing.ecicare.usuarios.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import edu.escuelaing.ecicare.usuarios.controllers.AuthController;
import edu.escuelaing.ecicare.usuarios.models.dto.AuthResponseDTO;
import edu.escuelaing.ecicare.usuarios.models.dto.LoginRequestDTO;
import edu.escuelaing.ecicare.usuarios.services.AuthService;
import edu.escuelaing.ecicare.utils.exceptions.notfound.UserEcicareNotFoundException;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void shouldReturnOkWhenLogin() throws Exception {
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .idEci(1L)
                .email("test@example.com")
                .name("TestUser")
                .build();

        when(authService.login(loginRequest)).thenReturn(authResponse);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                              "email": "test@example.com",
                              "password": "password123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEci").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("TestUser"));

    }

    // Incomplete test for invalid login
    @Test
    void shouldReturnUnauthorizedWhenLoginFails() throws Exception {

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new UserEcicareNotFoundException("User not found"));
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                              "email": "test@example.com",
                              "password": "password123"
                            }
                        """))
                .andExpect(status().isNotFound());

    }
}
