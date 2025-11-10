package edu.escuelaing.ecicare.challenges.models.entity;

import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "name"
)
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
    private String name;
    @Column(name="description", nullable = false)
    private String description; // Detailed description of the challenge.
    @Column(name = "image_url")
    private String imageUrl; //Url of image from challenge
    @Column(name="phrase")
    private String phrase; // Motivational phrase or slogan associated with the challenge.

    @ManyToMany
    @JoinTable(
            name = "user_challenges_registered",
            joinColumns = @JoinColumn(name = "challenge_name"),
            inverseJoinColumns = @JoinColumn(name = "user_id_eci")
    )
    private List<UserEcicare> registered; // List of users registered for the challenge.

    @ManyToMany
    @JoinTable(
            name = "user_challenges_confirmed",
            joinColumns = @JoinColumn(name = "challenge_name"),
            inverseJoinColumns = @JoinColumn(name = "user_id_eci")
    )
    private List<UserEcicare> confirmed; // List of users confirmed for the challenge done.

    @ElementCollection
    @CollectionTable(
            name = "challenge_tips",
            joinColumns = @JoinColumn(name = "challenge_name")
    )
    private List<String> tips; // List of tips related to the challenge.

    @Column(name="duration", nullable = false, updatable = false)
    private LocalDateTime duration; // Duration or deadline of the challenge, represented as a date and time.

    @ElementCollection
    @CollectionTable(
            name = "challenge_goals",
            joinColumns = @JoinColumn(name = "challenge_name")
    )
    private List<String> goals; // List of goals that participants should achieve during the challenge.

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Redeemable> redeemables;

    @ManyToOne
    @JoinColumn(name = "module_name", nullable = false)
    private Module module; // module to which the challenge belongs

    //challenge rating
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings; // Ratings and reviews provided by users for the challenge.
}
