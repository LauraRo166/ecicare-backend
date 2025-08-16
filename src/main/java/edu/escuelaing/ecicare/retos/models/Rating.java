package edu.escuelaing.ecicare.retos.models;

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

    //Numeric score given to the challenge.
    //This field is the primary key of the table.
    @Id
    @Column(name="rating", nullable = false, updatable = false)
    public int rating;

    @Column(name="feedback", nullable = false, updatable = false)
    public String feedback; //Textual feedback associated with the rating.
}

/*NOTA: clase no utilizada actualmente para calificacion de los retos, falta la logica para
    saber que califacion es de cada usuario inscrito; ademas de serciorarse de que el estudiante haya
    completado el reto
 */
