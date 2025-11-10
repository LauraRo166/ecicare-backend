package edu.escuelaing.ecicare.utils.exceptions.notfound;

import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;

public class ChallengeNotFoundException extends ResourceNotFoundException {

    public ChallengeNotFoundException(Long id) {
        super("Challenge not found with id " + id);
    }

}
