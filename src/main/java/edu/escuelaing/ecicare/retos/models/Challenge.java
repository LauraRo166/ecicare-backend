package edu.escuelaing.ecicare.retos.models;

import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a challenge within the Ecicare system.
 * This entity models a health, wellness, or fitness challenge
 * that can be stored in the database. It contains details such as
 * name, description, participants, tips, goals, and rewards.
 *
 * It is mapped to the {@code challenge} table via JPA annotations.
 *
 * @author ByteProgramming
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "challenge")
public class Challenge {

    // Unique name of the challenge.
    // This field is the primary key of the table.
    @Id
    @Column(name="name", nullable = false, updatable = false)
    public String name;
    @Column(name="description", nullable = false, updatable = false)
    public String description; // Detailed description of the challenge.
    @Column(name="phrase")
    public String phrase; // Motivational phrase or slogan associated with the challenge.
    @Column(name="registered")
    @ManyToMany(mappedBy = "registered", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //cuadrar con el modulo de usuarios para hacer el join con algun atributo.
    public List<UserEcicare> registered; // List of users registered for the challenge.

    @Column(name="tips", nullable = false)
    public List<String> tips; // List of tips related to the challenge.
    @Column(name="duration", nullable = false, updatable = false)
    public LocalDateTime duration; // Duration or deadline of the challenge, represented as a date and time.
    @Column(name="goals", nullable = false)
    public List<String> goals; // List of goals that participants should achieve during the challenge.
    @Column(name="reward")
    public String reward; // Reward given upon completing the challenge.
    @Column(name = "healthModule")
    public String healthModule; // Health module to which the challenge belongs

    //challenge rating
    @Column(name = "ratings")
    public List<Rating> ratings; // Ratings and reviews provided by users for the challenge.
}
