package edu.escuelaing.ecicare.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MedicalApproveException extends RuntimeException {

    public MedicalApproveException() {
        super("User does not have medical approve");
    }

}
