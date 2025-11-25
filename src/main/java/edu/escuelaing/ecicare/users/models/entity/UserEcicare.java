package edu.escuelaing.ecicare.users.models.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.escuelaing.ecicare.utils.models.entity.enums.Role;
import jakarta.persistence.*;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Represents a user within the Ecicare system.
 * This entity stores authentication, authorization, and participation details
 * of a user in different challenges.
 *
 * The class is mapped to the "usersEcicare" database table.
 */
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "idEci"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "usersEcicare")
public class UserEcicare {

    // Unique identifier for the Ecicare user (primary key).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_eci", nullable = false, updatable = false)
    private Long idEci;

    // Full name of the user, must be between 3 and 50 characters.
    @Size(min = 3, max = 50, message = "The name must be between 3 and 50 characters")
    @Column(name = "name", nullable = false)
    private String name;

    // Unique email address used for login and identification. Cannot be updated after creation.
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, updatable = false, unique = true)
    private String email;

    // Encrypted password for user authentication.
    @Column(name = "password", nullable = false)
    private String password;

    // Role of the user (e.g., ADMIN, USER, DOCTOR). Cannot be changed after registration.
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // Date and time of the user’s registration in the system. Cannot be modified.
    @Column(name = "registration_date", updatable = false, nullable = false)
    private LocalDateTime registrationDate;

    // Indicates if the user has received medical approval.
    @Column(name = "has_medical_approve", nullable = false)
    private Boolean hasMedicalApprove;

    // Challenges in which the user is registered but not yet confirmed.
    @ManyToMany(mappedBy = "registered")
    private List<Challenge> challengesRegistered;

    // Challenges in which the user has confirmed participation.
    @ManyToMany(mappedBy = "confirmed")
    private List<Challenge> challengesConfirmed;
}