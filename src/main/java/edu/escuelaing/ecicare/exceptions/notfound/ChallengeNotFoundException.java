package edu.escuelaing.ecicare.exceptions.notfound;

import edu.escuelaing.ecicare.exceptions.ResourceNotFoundException;

public class ChallengeNotFoundException extends ResourceNotFoundException {

    public ChallengeNotFoundException(Long id) {
        super("Challenge not found with id " + id);
    }

}
