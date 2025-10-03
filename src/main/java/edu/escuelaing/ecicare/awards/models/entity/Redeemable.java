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

    // Composite primary key for Redeemable (challengeName + awardId).
    @EmbeddedId
    private RedeemableId id;

    // Reference to the Challenge associated with this redeemable.
    // Uses challengeName as part of the composite key.
    @ManyToOne
    @MapsId("challengeName")
    @JoinColumn(name = "challenge_name", nullable = false)
    @JsonIgnore
    private Challenge challenge;

    // Reference to the Award associated with this redeemable.
    // Uses awardId as part of the composite key.
    // Marked with JsonBackReference/JsonIgnore to prevent circular serialization.
    @ManyToOne
    @MapsId("awardId")
    @JoinColumn(name = "award_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private Award award;

    // Number of days allowed for redeeming this award after being linked to the challenge.
    @Column(name = "limit_days", nullable = false)
    private Integer limitDays;
}