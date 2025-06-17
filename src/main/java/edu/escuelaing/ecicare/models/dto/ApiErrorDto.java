package edu.escuelaing.ecicare.models.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorDto {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

}
