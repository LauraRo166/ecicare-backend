package edu.escuelaing.ecicare.users.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.escuelaing.ecicare.users.models.dto.UserEcicareDto;
import edu.escuelaing.ecicare.users.models.dto.UserEcicareResponseDTO;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.users.repositories.UserEcicareRepository;
import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;
import edu.escuelaing.ecicare.utils.exceptions.notfound.UserEcicareNotFoundException;
import edu.escuelaing.ecicare.utils.models.entity.enums.Role;

@ExtendWith(MockitoExtension.class)
class UserEcicareServiceTest {
    @Mock
    UserEcicareRepository userEcicareRepository;

    @InjectMocks
    UserEcicareService userEcicareService;

    @Test
    void createUserEcicare() {
        UserEcicareDto userEcicareDto = new UserEcicareDto();
        userEcicareDto.setIdEci(1L);
        userEcicareDto.setName("User");
        UserEcicare userEcicare = UserEcicare.builder()
                .idEci(userEcicareDto.getIdEci())
                .name(userEcicareDto.getName())
                .build();
        when(userEcicareRepository.save(any(UserEcicare.class))).thenReturn(userEcicare);
        UserEcicareResponseDTO result = userEcicareService.createEcicareUser(userEcicareDto);
        assertEquals(userEcicare.getIdEci(), result.getIdEci());
        assertEquals(userEcicare.getName(), result.getName());
        verify(userEcicareRepository, times(1)).save(any(UserEcicare.class));
    }

    @Test
    void deleteUserEcicareById() {
        Long userId = 1L;
        when(userEcicareRepository.existsById(userId)).thenReturn(true);
        userEcicareService.deleteEcicareUserById(userId);
        verify(userEcicareRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserEcicareByIdNotFound() throws UserEcicareNotFoundException {
        Long userId = 1L;
        when(userEcicareRepository.existsById(userId)).thenReturn(false);
        assertThrows(UserEcicareNotFoundException.class, () -> {
            userEcicareService.deleteEcicareUserById(userId);
        });
        verify(userEcicareRepository, times(0)).deleteById(userId);
    }

    @Test
    void getUserEcicareById() {
        UserEcicare user = UserEcicare.builder()
                .idEci(1L)
                .name("User")
                .email("test@example.com")
                .password("password")
                .hasMedicalApprove(false)
                .build();
        when(userEcicareRepository.findById(1L)).thenReturn(Optional.of(user));
        UserEcicareResponseDTO result = userEcicareService.getUserEcicareById(1L);
        assertEquals(user.getIdEci(), result.getIdEci());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getUserEcicareByIdNotFound() {
        Long userId = 1L;
        when(userEcicareRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            userEcicareService.getUserEcicareById(userId);
        });

        verify(userEcicareRepository, times(1)).findById(userId);
    }

    @Test
    void setHasMedicalApproveUserEcicare() {
        UserEcicare user = UserEcicare.builder()
                .idEci(1L)
                .name("User")
                .email("test@example.com")
                .password("password")
                .hasMedicalApprove(false)
                .build();

        when(userEcicareRepository.findById(1L)).thenReturn(Optional.of(user));
        userEcicareService.setHasMedicalApproveUserEcicare(1L);
        assertTrue(user.getHasMedicalApprove());

    }

    @Test
    void mapToDto() {
        UserEcicare user = UserEcicare.builder()
                .idEci(1L)
                .name("User")
                .email("test@example.com")
                .role(Role.STUDENT)
                .hasMedicalApprove(false)
                .registrationDate(LocalDateTime.now())
                .build();
        UserEcicareResponseDTO dto = userEcicareService.mapToDto(user);
        assertEquals(user.getIdEci(), dto.getIdEci());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getRole(), dto.getRole());
        assertFalse(dto.getHasMedicalApprove());
        assertEquals(user.getRegistrationDate(), dto.getRegistrationDate());
    }

}
