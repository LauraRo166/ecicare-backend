package edu.escuelaing.ecicare.challenges.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO that groups a Module with its associated Challenges that match search
 * criteria.
 * Perfect for displaying search results organized by modules.
 * 
 * This allows the frontend to show challenges grouped by their respective
 * modules,
 * providing better organization and user experience.
 * 
 * @author Byte Programming
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleWithChallengesDTO {

    /**
     * The module name
     */
    private String moduleName;

    /**
     * The module description
     */
    private String moduleDescription;

    /**
     * The module image URL
     */
    private String moduleImageUrl;

    /**
     * List of challenges belonging to this module that match the search criteria
     */
    private List<ChallengeResponse> challenges;

    /**
     * Total count of challenges in this module that match the search criteria.
     * Useful for pagination and displaying counts to users.
     */
    private int totalChallenges;
}