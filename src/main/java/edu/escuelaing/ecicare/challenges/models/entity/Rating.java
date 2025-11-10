package edu.escuelaing.ecicare.challenges.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a rating and feedback for a challenge in the Ecicare system.
 *
 * This entity stores a user's evaluation of a challenge,
 * consisting of a numeric rating and optional written feedback.
 *
 * @author Byte Programming
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "rating")
public class Rating {

    //This field is the primary key of the table.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ratingId", nullable = false, updatable = false)
    private Long id;

    //Numeric score given to the challenge.
    @Column(name="rating", nullable = false, updatable = false)
    private int rating;

    @Column(name="feedback", nullable = false, updatable = false)
    private String feedback; //Textual feedback associated with the rating.

    @ManyToOne
    @JoinColumn(name = "challenge_name", nullable = false)
    @JsonIgnore
    private Challenge challenge; //Challenge to which the rating belongs
}

/*NOTA: clase no utilizada actualmente para calificacion de los retos, falta la logica para
    saber que califacion es de cada usuario inscrito; ademas de serciorarse de que el estudiante haya
    completado el reto
 */
