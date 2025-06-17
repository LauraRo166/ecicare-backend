package edu.escuelaing.ecicare.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RedeemAwardException extends RuntimeException {

    public RedeemAwardException(String message) {
        super(message);
    }

}
