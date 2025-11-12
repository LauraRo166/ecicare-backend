package edu.escuelaing.ecicare.users.models.dto;

import java.time.LocalDateTime;

import edu.escuelaing.ecicare.utils.models.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserEcicareDto {

    private Long idEci;
    private String name;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime registrationDate;
    private Boolean hasMedicalApprove;

}