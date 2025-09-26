package edu.escuelaing.ecicare.retos.models.dto;

import edu.escuelaing.ecicare.premios.models.dto.AwardResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChallengeResponse {
    private String name;
    private String description;
    private String imageUrl;
    private String phrase;
    private List<String> tips;
    private LocalDateTime duration;
    private List<String> goals;
    private String moduleName;
    private List<AwardResponse> redeemables;
}