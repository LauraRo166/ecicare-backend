package edu.escuelaing.ecicare.awards.models.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AwardResponse {

    private Long id;
    private String name;
    private String description;
    private Integer inStock;
    private String imageUrl;
    private List<RedeemableResponse> redeemables;
}