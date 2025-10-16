package edu.escuelaing.ecicare.awards.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedeemableResponse {
    
    private String challengeName;
    private Long awardId;
    private Integer limitDays;
}
