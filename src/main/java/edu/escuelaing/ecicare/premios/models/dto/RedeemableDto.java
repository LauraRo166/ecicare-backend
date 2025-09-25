package edu.escuelaing.ecicare.premios.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemableDto {
    private String challengeName;
    private Long awardId; 
    private Integer limitDays;
}
