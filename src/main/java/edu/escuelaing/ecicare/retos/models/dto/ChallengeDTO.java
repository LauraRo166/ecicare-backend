package edu.escuelaing.ecicare.retos.models.dto;

import edu.escuelaing.ecicare.retos.models.entity.Module;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeDTO {
    private String name;
    private String description;
    private String imageUrl;
    private String phrase;
    private List<String> tips;
    private LocalDateTime duration;
    private List<String> goals;
    private String moduleName;
}
