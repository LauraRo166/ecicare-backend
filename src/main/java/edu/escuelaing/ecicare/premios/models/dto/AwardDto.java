package edu.escuelaing.ecicare.premios.models.dto;

import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
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
    private UserEcicare updatedBy;
}
