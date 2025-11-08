package edu.escuelaing.ecicare.challenges.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEmailNameDTO {
    private String email;
    private String name;
}
