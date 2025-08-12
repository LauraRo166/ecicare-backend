package edu.escuelaing.ecicare.premios.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedeemableDto {
    private Long awardId;
    private Integer requiredQR;
    private Integer limitDays;
}
