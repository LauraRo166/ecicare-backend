package edu.escuelaing.ecicare.usuarios.models.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import edu.escuelaing.ecicare.utils.models.entity.enums.Role;
import jakarta.persistence.*;
import edu.escuelaing.ecicare.retos.models.Challenge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "usersEcicare")
public class UserEcicare {

    @Id
    @Column(name = "id_eci", nullable = false, updatable = false)
    private Long idEci;

    @Size(min = 3, max = 50, message = "The name must be between 3 and 50 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false, updatable = false)
    private Role role;

    @Column(name = "registration_date", updatable = false, nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "has_medical_approve", nullable = false)
    private Boolean hasMedicalApprove;

    @ManyToMany(mappedBy = "registered")
    private List<Challenge> challengesRegistered;

    @ManyToMany(mappedBy = "confirmed")
    private List<Challenge> challengesConfirmed;
}