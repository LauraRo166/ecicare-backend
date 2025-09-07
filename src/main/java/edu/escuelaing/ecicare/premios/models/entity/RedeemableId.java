package edu.escuelaing.ecicare.premios.models.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RedeemableId implements Serializable {

    @Column(name = "challenge_name")
    private String challengeName;

    @Column(name = "award_id")
    private Long awardId;

}