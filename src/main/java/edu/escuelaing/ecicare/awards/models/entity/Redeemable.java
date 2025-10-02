package edu.escuelaing.ecicare.awards.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
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
    @JoinColumn(name = "challenge_name", nullable = false)
    private Challenge challenge;

    @ManyToOne
    @MapsId("awardId")
    @JoinColumn(name = "award_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private Award award;

    @Column(name = "limit_days", nullable = false)
    private Integer limitDays;

}