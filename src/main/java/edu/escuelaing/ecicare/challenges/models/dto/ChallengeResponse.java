package edu.escuelaing.ecicare.challenges.models.dto;

import edu.escuelaing.ecicare.awards.models.dto.AwardDto;

import java.time.LocalDateTime;
import java.util.List;

public record ChallengeResponse(String name,
                                String description,
                                String imageUrl,
                                String phrase,
                                List<String>tips,
                                LocalDateTime duration,
                                List<String> goals,
                                String moduleName,
                                List<AwardDto> redeemables) {
}
