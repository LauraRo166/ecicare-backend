package edu.escuelaing.ecicare.premios.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwardDto {
    
    private String name;
    private String description;
    private Integer inStock;
    private String imageUrl;
    private Long updatedBy;
}
