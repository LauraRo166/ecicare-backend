package edu.escuelaing.ecicare.challenges.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailNameDTO {
    private String email;
    private String name;
    private Integer currentVerifications;
    private Integer requiredVerifications;

    // Constructor for backward compatibility
    public UserEmailNameDTO(String email, String name) {
        this.email = email;
        this.name = name;
        this.currentVerifications = 0;
        this.requiredVerifications = 0;
    }
}
