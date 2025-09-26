package edu.escuelaing.ecicare.premios.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AwardResponse {
    private String name;
    private String description;
    private Integer inStock;
    private String imageUrl;
}
