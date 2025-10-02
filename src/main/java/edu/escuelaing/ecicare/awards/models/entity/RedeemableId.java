package edu.escuelaing.ecicare.awards.models.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RedeemableId implements Serializable {

    // The name of the challenge (part of the composite primary key).
    @Column(name = "challenge_name")
    private String challengeName;

    // The ID of the award (part of the composite primary key).
    @Column(name = "award_id")
    private Long awardId;

}
