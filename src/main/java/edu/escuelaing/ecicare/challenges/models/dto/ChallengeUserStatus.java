package edu.escuelaing.ecicare.challenges.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChallengeUserStatus {
    private boolean completed;
    private boolean registered;
}
