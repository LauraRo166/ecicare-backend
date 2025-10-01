package edu.escuelaing.ecicare.awards.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwardDto {
    
    private String name;
    private String description;
    private Integer inStock;
    private String imageUrl;
}
