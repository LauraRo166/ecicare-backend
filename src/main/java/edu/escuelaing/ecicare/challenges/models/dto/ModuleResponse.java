package edu.escuelaing.ecicare.challenges.models.dto;

import java.util.List;

public record ModuleResponse(
        String name,
        String description,
        String imageUrl,
        List<ChallengeResponse> challenges
) {}
