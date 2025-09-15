package edu.escuelaing.ecicare.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.escuelaing.ecicare.usuarios.controllers.UserEcicareController;
import edu.escuelaing.ecicare.usuarios.models.dto.UserEcicareDto;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.usuarios.services.UserEcicareService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import edu.escuelaing.ecicare.usuarios.models.dto.UserEcicareResponseDTO;

@WebMvcTest(UserEcicareController.class)
class UserEcicareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserEcicareService userEcicareService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createUserEcicare() throws Exception {
        UserEcicareDto userEcicare = UserEcicareDto.builder()
                .idEci(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        UserEcicareResponseDTO userResponse = UserEcicareResponseDTO.builder()
                .idEci(userEcicare.getIdEci())
                .name(userEcicare.getName())
                .email(userEcicare.getEmail())
                .build();

        when(userEcicareService.createEcicareUser(any(UserEcicareDto.class))).thenReturn(userResponse);

        mockMvc.perform(post("/ecicareusers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEcicare)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    UserEcicareResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            UserEcicareResponseDTO.class);
                    assert response.getIdEci().equals(userResponse.getIdEci());
                    assert response.getName().equals(userResponse.getName());
                    assert response.getEmail().equals(userResponse.getEmail());
                });
    }

    @Test
    void deleteUserEcicare() throws Exception {
        doNothing().when(userEcicareService).deleteEcicareUserById(1L);
        mockMvc.perform(delete("/ecicareusers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserEcicareById() throws Exception {

        UserEcicareResponseDTO userResponse = UserEcicareResponseDTO.builder()
                .idEci(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        when(userEcicareService.getUserEcicareById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/ecicareusers/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.idEci").value(userResponse.getIdEci()))
            .andExpect(jsonPath("$.name").value(userResponse.getName()))
            .andExpect(jsonPath("$.email").value(userResponse.getEmail()));

    }

    @Test
    void medicalApproveUserEcicare() throws Exception {
        doNothing().when(userEcicareService).setHasMedicalApproveUserEcicare(1L);
        mockMvc.perform(patch("/ecicareusers/1/approve"))
                .andExpect(status().isNoContent());
        verify(userEcicareService, times(1)).setHasMedicalApproveUserEcicare(1L);
    }



}
