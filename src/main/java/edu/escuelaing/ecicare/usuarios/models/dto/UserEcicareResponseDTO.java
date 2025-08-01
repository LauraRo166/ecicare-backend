package edu.escuelaing.ecicare.usuarios.models.dto;

import java.time.LocalDateTime;
import edu.escuelaing.ecicare.models.entity.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserEcicareResponseDTO {

    private Long idEci;
    private String name;
    private String email;
    private LocalDateTime registrationDate;
    private Role role;
    private Boolean hasMedicalApprove;

}