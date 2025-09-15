package edu.escuelaing.ecicare.premios.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import edu.escuelaing.ecicare.retos.models.Challenge;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "redeemables")
public class Redeemable {

    @EmbeddedId
    private RedeemableId id;

    @ManyToOne
    @MapsId("challengeName")
    @JoinColumn(name = "challenge_name")
    private Challenge challenge;

    @ManyToOne
    @MapsId("awardId")
    @JoinColumn(name = "award_id")
    @JsonBackReference
    private Award award;

    @Column(name = "limit_days")
    private Integer limitDays;

}