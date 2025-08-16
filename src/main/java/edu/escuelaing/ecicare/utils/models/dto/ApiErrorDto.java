package edu.escuelaing.ecicare.utils.models.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
public class ApiErrorDto {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

}
